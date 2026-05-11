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
            val sessionManager = com.kiriplatform.app.data.SessionManager.getInstance(context)
            val userRole = sessionManager.getUserRole() ?: "STUDENT"

            // First, try to load from cache for immediate offline view
            val cachedUser = com.kiriplatform.app.data.CacheManager.getCache(context, "profile_$userId", object : com.google.gson.reflect.TypeToken<UserDto>() {})
            val cachedEvents = com.kiriplatform.app.data.CacheManager.getCache(context, "home_events", object : com.google.gson.reflect.TypeToken<List<EventDto>>() {})
            
            if (cachedUser != null && cachedEvents != null) {
                _uiState.value = HomeState.Success(cachedUser, cachedEvents)
            } else {
                _uiState.value = HomeState.Loading
            }

            try {
                val user = ApiClient.service.getProfile(userId)
                val rawEvents = ApiClient.service.getEvents()
                
                val today = java.time.LocalDate.now().toString()
                val filteredEvents = when (userRole) {
                    "ADMIN" -> rawEvents
                    "SPOC", "ORGANIZER" -> {
                        rawEvents.filter { it.ownerId == userId || it.date >= today }
                    }
                    else -> {
                        rawEvents.filter { it.date >= today }
                    }
                }.take(3)
                
                // Fetch AAL data
                var onboarding: AalOnboardingDto? = null
                var activities: List<AalActivityDto> = emptyList()
                try {
                    val intUserId = userId.toIntOrNull()
                    if (intUserId != null) {
                        onboarding = ApiClient.service.getAalOnboarding(intUserId)
                        activities = ApiClient.service.getAalActivities(intUserId)
                    }
                } catch (e: Exception) { /* AAL not available for this user */ }
                
                // SAVE to cache for next time
                com.kiriplatform.app.data.CacheManager.saveCache(context, "profile_$userId", user)
                com.kiriplatform.app.data.CacheManager.saveCache(context, "home_events", filteredEvents)
                
                _uiState.value = HomeState.Success(user, filteredEvents, onboarding, activities)
            } catch (e: Exception) {
                if (_uiState.value !is HomeState.Success) {
                    _uiState.value = HomeState.Error(e.message ?: "Failed to load home data")
                }
            }
        }
    }
}
