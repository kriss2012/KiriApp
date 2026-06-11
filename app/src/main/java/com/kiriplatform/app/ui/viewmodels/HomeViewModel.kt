package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.EventDto
import com.kiriplatform.app.data.remote.models.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope

import com.kiriplatform.app.data.remote.models.*

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class HomeState {
    object Loading : HomeState()
    data class Success(
        val user: UserDto, 
        val upcomingEvents: List<EventDto>,
        val aalOnboarding: AalOnboardingDto? = null,
        val aalActivities: List<AalActivityDto> = emptyList()
    ) : HomeState()
    data class Error(val message: String) : HomeState()
}

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    fun loadHomeData(context: android.content.Context, userId: String) {
        viewModelScope.launch {
            // 1. Load from Cache immediately
            try {
                val user = com.kiriplatform.app.data.CacheManager.getCache(context, "profile_$userId", object : com.google.gson.reflect.TypeToken<UserDto>() {})
                val events = com.kiriplatform.app.data.CacheManager.getCache(context, "home_events", object : com.google.gson.reflect.TypeToken<List<EventDto>>() {})
                
                if (user != null) {
                    _uiState.value = HomeState.Success(
                        user = user,
                        upcomingEvents = (events ?: emptyList()) as List<EventDto>
                    )
                }
            } catch (e: Exception) {
                // Cache failure is non-fatal
            }

            // 2. Fetch Network Data incrementally
            supervisorScope {
                val userDeferred = async { ApiClient.service.getProfile(userId) }
                val eventsDeferred = async { ApiClient.service.getEvents() }
                val onboardingDeferred = async { ApiClient.service.getAalOnboarding(userId) }
                val activitiesDeferred = async { ApiClient.service.getAalActivities(userId) }

                // Use a local copy to update state incrementally
                var currentUser: UserDto? = (uiState.value as? HomeState.Success)?.user
                var currentEvents: List<EventDto> = (uiState.value as? HomeState.Success)?.upcomingEvents ?: emptyList()
                var currentOnboarding: AalOnboardingDto? = (uiState.value as? HomeState.Success)?.aalOnboarding
                var currentActivities: List<AalActivityDto> = (uiState.value as? HomeState.Success)?.aalActivities ?: emptyList()

                // Update Profile & Events first as they are critical
                try {
                    val user = userDeferred.await()
                    currentUser = user
                    _uiState.value = HomeState.Success(user, currentEvents, currentOnboarding, currentActivities)
                    
                    // Save critical path to cache
                    com.kiriplatform.app.data.CacheManager.saveCache(context, "profile_$userId", user)
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Profile fetch failed", e)
                }

                try {
                    val events = eventsDeferred.await().take(3)
                    currentEvents = events
                    currentUser?.let {
                        _uiState.value = HomeState.Success(it, events, currentOnboarding, currentActivities)
                    }
                    com.kiriplatform.app.data.CacheManager.saveCache(context, "home_events", events)
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Events fetch failed", e)
                }

                // Update AAL data as it arrives - handle 404s specifically if needed, 
                // but supervisorScope + try-catch is already much safer.
                try {
                    currentOnboarding = onboardingDeferred.await()
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Onboarding fetch failed", e)
                }

                try {
                    currentActivities = activitiesDeferred.await()
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Activities fetch failed", e)
                }

                // Push final state update for AAL if we have a user
                currentUser?.let {
                    _uiState.value = HomeState.Success(it, currentEvents, currentOnboarding, currentActivities)
                }
            }
        }
    }
}
