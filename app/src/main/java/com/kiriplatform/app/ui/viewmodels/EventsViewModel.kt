package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.CreateEventRequest
import com.kiriplatform.app.data.remote.models.EventDto
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

    fun fetchEvents(context: android.content.Context, userRole: String, userId: String) {
        viewModelScope.launch {
            // Load from cache for immediate offline view
            val cached = com.kiriplatform.app.data.CacheManager.getCache(context, "events_list", object : com.google.gson.reflect.TypeToken<List<EventDto>>() {})
            if (cached != null) {
                _uiState.value = EventsState.Success(cached)
            } else {
                _uiState.value = EventsState.Loading
            }

            try {
                val rawEvents = ApiClient.service.getEvents()
                
                val today = java.time.LocalDate.now().toString()
                
                val events = when (userRole) {
                    "ADMIN" -> rawEvents // Admin sees all (current and past)
                    "SPOC", "ORGANIZER" -> {
                        // Organizers see their own events (all) and other upcoming events
                        rawEvents.filter { it.ownerId == userId || it.date >= today }
                    }
                    else -> {
                        // Students see only upcoming events
                        rawEvents.filter { it.date >= today }
                    }
                }
                
                // Save to cache
                com.kiriplatform.app.data.CacheManager.saveCache(context, "events_list", events)
                _uiState.value = EventsState.Success(events)
            } catch (e: Exception) {
                if (_uiState.value !is EventsState.Success) {
                    _uiState.value = EventsState.Error(e.message ?: "Failed to fetch events")
                }
            }
        }
    }

    fun createEvent(
        context: android.content.Context, 
        title: String, 
        description: String, 
        date: String, 
        location: String, 
        ownerId: String,
        coordinatorName: String,
        coordinatorPhone: String? = null,
        imageUrl: String? = null,
        registrationLink: String? = null,
        prize: String? = null,
        userRole: String
    ) {
        viewModelScope.launch {
            _uiState.value = EventsState.Loading
            try {
                ApiClient.service.createEvent(
                    CreateEventRequest(
                        title = title, 
                        description = description, 
                        date = date, 
                        location = location, 
                        ownerId = ownerId, 
                        coordinatorName = coordinatorName,
                        coordinatorPhone = coordinatorPhone,
                        imageUrl = imageUrl, 
                        type = "GENERAL", 
                        registrationLink = registrationLink, 
                        prize = prize
                    )
                )
                fetchEvents(context, userRole, ownerId) // Refresh list after successful creation
            } catch (e: Exception) {
                _uiState.value = EventsState.Error(e.message ?: "Failed to create event")
            }
        }
    }
}
