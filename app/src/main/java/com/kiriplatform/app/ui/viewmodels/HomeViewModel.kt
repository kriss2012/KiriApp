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

import com.kiriplatform.app.data.remote.models.*

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

class HomeViewModel : ViewModel() {
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
                
                val (cachedUser, cachedEvents) = cachedData
                
                if (cachedUser != null && cachedEvents != null) {
                    _uiState.value = HomeState.Success(cachedUser, cachedEvents)
                } else {
                    _uiState.value = HomeState.Loading
                }
            } catch (e: Exception) {
                // Ignore cache errors and proceed to network
                _uiState.value = HomeState.Loading
            }

            try {
                // Retrofit handles its own thread switching, but we keep it inside the scope
                val user = ApiClient.service.getProfile(userId)
                val events = ApiClient.service.getEvents().take(3)
                
                // Fetch AAL data
                var onboarding: AalOnboardingDto? = null
                var activities: List<AalActivityDto> = emptyList()
                try {
                    // Fetch AAL data if userId is valid
                    if (userId.isNotEmpty()) {
                        onboarding = ApiClient.service.getAalOnboarding(userId)
                        activities = ApiClient.service.getAalActivities(userId)
                    }
                } catch (e: Exception) { /* AAL not available for this user */ }
                
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
