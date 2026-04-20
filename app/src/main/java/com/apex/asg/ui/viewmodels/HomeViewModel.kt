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

    fun loadHomeData(userId: String) {
        viewModelScope.launch {
            _uiState.value = HomeState.Loading
            try {
                val user = ApiClient.service.getProfile(userId)
                val events = ApiClient.service.getEvents()
                _uiState.value = HomeState.Success(user, events.take(3))
            } catch (e: Exception) {
                _uiState.value = HomeState.Error(e.message ?: "Failed to load home data")
            }
        }
    }
}
