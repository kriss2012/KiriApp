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

    fun fetchEvents(context: android.content.Context) {
        viewModelScope.launch {
            val featuredEvents = listOf(
                EventDto(
                    _id = "1",
                    _title = "Kiri AI Innovation Summit 2026",
                    type = "HACKATHON",
                    _description = "Join developers, founders, and creators to showcase next-generation AI platforms, agents, and local language models.",
                    _date = "2026-07-07T10:00:00.000Z",
                    _location = "Virtual / Kiri Hub",
                    coordinatorName = "Aditi Sharma",
                    coordinatorPhone = "+91 98765 43210",
                    prize = "₹5,00,000 + Incubation",
                    imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&auto=format&fit=crop",
                    registrationLink = "https://forms.gle/KiriSummit2026"
                ),
                EventDto(
                    _id = "2",
                    _title = "Global Builders Hackathon",
                    type = "COMPETITION",
                    _description = "A 48-hour virtual hackathon focused on building open-source projects, peer review, and developer collaboration.",
                    _date = "2026-07-24T14:00:00.000Z",
                    _location = "Kiri Sandbox / Discord",
                    coordinatorName = "Rohan Verma",
                    coordinatorPhone = "+91 99999 88888",
                    prize = "$10,000 Seed Grant",
                    imageUrl = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&auto=format&fit=crop",
                    registrationLink = "https://forms.gle/KiriHackathon2026"
                ),
                EventDto(
                    _id = "3",
                    _title = "Startup Pitch Deck Workshop",
                    type = "WORKSHOP",
                    _description = "Pitch your idea to global investors and get a chance to secure seed funding.",
                    _date = "2026-08-05T14:00:00.000Z",
                    _location = "Main Auditorium",
                    coordinatorName = "ASG Core Team",
                    coordinatorPhone = null,
                    prize = "$5000 AWS Credits",
                    imageUrl = "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800&auto=format&fit=crop",
                    registrationLink = "https://forms.gle/KiriWorkshop2026"
                )
            )

            // Load from cache for immediate offline view
            val cached = com.kiriplatform.app.data.CacheManager.getCache(context, "events_list", object : com.google.gson.reflect.TypeToken<List<EventDto>>() {})
            if (cached != null) {
                _uiState.value = EventsState.Success(featuredEvents + cached.filter { it.id != "1" && it.id != "2" && it.id != "3" })
            } else {
                _uiState.value = EventsState.Loading
            }

            try {
                val rawEvents = ApiClient.service.getEvents()
                val today = java.time.LocalDate.now().toString()
                val networkEvents = rawEvents.filter { it.date >= today && it.id != "1" && it.id != "2" && it.id != "3" }
                val mergedEvents = featuredEvents + networkEvents
                
                // Save to cache
                com.kiriplatform.app.data.CacheManager.saveCache(context, "events_list", mergedEvents)
                _uiState.value = EventsState.Success(mergedEvents)
            } catch (e: Exception) {
                if (_uiState.value !is EventsState.Success) {
                    _uiState.value = EventsState.Success(featuredEvents)
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
        prize: String? = null
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
                fetchEvents(context) // Refresh list after successful creation
            } catch (e: Exception) {
                _uiState.value = EventsState.Error(e.message ?: "Failed to create event")
            }
        }
    }
}
