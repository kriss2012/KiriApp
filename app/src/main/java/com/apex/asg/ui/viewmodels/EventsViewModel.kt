package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.CreateEventRequest
import com.apex.asg.data.remote.EventDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class EventsState {
    object Idle : EventsState()
    object Loading : EventsState()
    data class Success(val events: List<EventDto>) : EventsState()
    data class Error(val message: String) : EventsState()
}

class EventsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<EventsState>(EventsState.Idle)
    val uiState: StateFlow<EventsState> = _uiState.asStateFlow()

    fun fetchEvents() {
        viewModelScope.launch {
            _uiState.value = EventsState.Loading
            try {
                val events = ApiClient.service.getEvents()
                _uiState.value = EventsState.Success(events)
            } catch (e: Exception) {
                _uiState.value = EventsState.Error(e.message ?: "Failed to fetch events")
            }
        }
    }

    fun createEvent(title: String, description: String, date: String, location: String, ownerId: String) {
        viewModelScope.launch {
            _uiState.value = EventsState.Loading
            try {
                ApiClient.service.createEvent(
                    CreateEventRequest(title, description, date, location, ownerId)
                )
                fetchEvents() // Refresh list
            } catch (e: Exception) {
                _uiState.value = EventsState.Error(e.message ?: "Failed to create event")
            }
        }
    }
}
