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
            try {
                // First, try to load from cache on IO thread to prevent main-thread lag
                val cachedData = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    val user = com.kiriplatform.app.data.CacheManager.getCache(context, "profile_$userId", object : com.google.gson.reflect.TypeToken<UserDto>() {})
                    val events = com.kiriplatform.app.data.CacheManager.getCache(context, "home_events", object : com.google.gson.reflect.TypeToken<List<EventDto>>() {})
                    user to events
                }
                
                val cachedUser = cachedData.first
                val cachedEvents = cachedData.second
                
                if (cachedUser is UserDto && cachedEvents is List<*>) {
                    @Suppress("UNCHECKED_CAST")
                    val validEvents = cachedEvents as List<EventDto>
                    _uiState.value = HomeState.Success(cachedUser, validEvents)
                } else {
                    _uiState.value = HomeState.Loading
                }
            } catch (e: Exception) {
                // Ignore cache errors and proceed to network
                _uiState.value = HomeState.Loading
            }

            try {
                // Fetch all 4 APIs in parallel on the coroutine scope (concurrent execution)
                val userDeferred = async { ApiClient.service.getProfile(userId) }
                val eventsDeferred = async { ApiClient.service.getEvents() }
                val onboardingDeferred = async {
                    if (userId.isNotEmpty()) ApiClient.service.getAalOnboarding(userId) else null
                }
                val activitiesDeferred = async {
                    if (userId.isNotEmpty()) ApiClient.service.getAalActivities(userId) else emptyList()
                }

                // Await results
                val user = userDeferred.await()
                val events = eventsDeferred.await().take(3)
                
                var onboarding: AalOnboardingDto? = null
                var activities: List<AalActivityDto> = emptyList()
                try {
                    onboarding = onboardingDeferred.await()
                } catch (e: Exception) { /* AAL not available or fails */ }
                try {
                    activities = activitiesDeferred.await()
                } catch (e: Exception) { /* AAL activities not available or fails */ }
                
                // SAVE to cache on IO thread
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                    com.kiriplatform.app.data.CacheManager.saveCache(context, "profile_$userId", user)
                    com.kiriplatform.app.data.CacheManager.saveCache(context, "home_events", events)
                }
                
                _uiState.value = HomeState.Success(user, events, onboarding, activities)
            } catch (e: Exception) {
                if (_uiState.value !is HomeState.Success) {
                    _uiState.value = HomeState.Error(e.message ?: "Failed to load home data")
                }
            }
        }
    }
}
