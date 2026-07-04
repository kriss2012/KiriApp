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

            // 1. Load from Cache immediately
            try {
                val user = com.kiriplatform.app.data.CacheManager.getCache(context, "profile_$userId", object : com.google.gson.reflect.TypeToken<UserDto>() {})
                val events = com.kiriplatform.app.data.CacheManager.getCache(context, "home_events", object : com.google.gson.reflect.TypeToken<List<EventDto>>() {})
                
                if (user != null) {
                    _uiState.value = HomeState.Success(
                        user = user,
                        upcomingEvents = featuredEvents + (events ?: emptyList()).filter { it.id != "1" && it.id != "2" && it.id != "3" }
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
                var currentEvents: List<EventDto> = (uiState.value as? HomeState.Success)?.upcomingEvents ?: featuredEvents
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
                    val rawEvents = eventsDeferred.await()
                    val today = java.time.LocalDate.now().toString()
                    val networkEvents = rawEvents.filter { it.date >= today && it.id != "1" && it.id != "2" && it.id != "3" }
                    val mergedEvents = (featuredEvents + networkEvents).take(3)
                    currentEvents = mergedEvents
                    currentUser?.let {
                        _uiState.value = HomeState.Success(it, mergedEvents, currentOnboarding, currentActivities)
                    }
                    com.kiriplatform.app.data.CacheManager.saveCache(context, "home_events", mergedEvents)
                } catch (e: Exception) {
                    android.util.Log.e("HomeViewModel", "Events fetch failed", e)
                    currentEvents = featuredEvents
                    currentUser?.let {
                        _uiState.value = HomeState.Success(it, featuredEvents, currentOnboarding, currentActivities)
                    }
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
