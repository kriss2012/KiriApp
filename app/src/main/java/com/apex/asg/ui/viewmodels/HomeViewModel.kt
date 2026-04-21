package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.EventDto
import com.apex.asg.data.remote.models.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeState {
    object Loading : HomeState()
    data class Success(val user: UserDto, val upcomingEvents: List<EventDto>) : HomeState()
    data class Error(val message: String) : HomeState()
}

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<HomeState>(HomeState.Loading)
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    fun loadHomeData(context: android.content.Context, userId: String) {
        viewModelScope.launch {
            // First, try to load from cache for immediate offline view
            val cachedUser = com.apex.asg.data.CacheManager.getCache(context, "profile_$userId", object : com.google.gson.reflect.TypeToken<UserDto>() {})
            val cachedEvents = com.apex.asg.data.CacheManager.getCache(context, "home_events", object : com.google.gson.reflect.TypeToken<List<EventDto>>() {})
            
            if (cachedUser != null && cachedEvents != null) {
                _uiState.value = HomeState.Success(cachedUser, cachedEvents)
            } else {
                _uiState.value = HomeState.Loading
            }

            try {
                val user = ApiClient.service.getProfile(userId)
                val events = ApiClient.service.getEvents().take(3)
                
                // SAVE to cache for next time
                com.apex.asg.data.CacheManager.saveCache(context, "profile_$userId", user)
                com.apex.asg.data.CacheManager.saveCache(context, "home_events", events)
                
                _uiState.value = HomeState.Success(user, events)
            } catch (e: Exception) {
                if (_uiState.value !is HomeState.Success) {
                    _uiState.value = HomeState.Error(e.message ?: "Failed to load home data")
                }
            }
        }
    }
}
