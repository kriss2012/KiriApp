package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.UpdateProfileRequest
import com.kiriplatform.app.data.remote.models.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val user: UserDto) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    fun fetchProfile(context: android.content.Context, userId: String) {
        viewModelScope.launch {
            // Load cached data immediately so the screen isn't blank offline
            val cached = com.kiriplatform.app.data.CacheManager.getCache(
                context, "profile_$userId",
                object : com.google.gson.reflect.TypeToken<UserDto>() {}
            )
            if (cached != null) {
                _uiState.value = ProfileState.Success(cached)
            } else {
                _uiState.value = ProfileState.Loading
            }

            // Then refresh from network in background
            try {
                val user = ApiClient.service.getProfile(userId)
                com.kiriplatform.app.data.CacheManager.saveCache(context, "profile_$userId", user)
                _uiState.value = ProfileState.Success(user)
            } catch (e: Exception) {
                // Keep cached data visible if already showing success
                if (_uiState.value !is ProfileState.Success) {
                    _uiState.value = ProfileState.Error(e.message ?: "Failed to fetch profile")
                }
            }
        }
    }

    fun updateProfile(
        context: android.content.Context,
        userId: String,
        fullName: String,
        role: String,
        bio: String? = null,
        department: String? = null,
        college: String? = null,
        year: String? = null,
        phoneNumber: String? = null,
        website: String? = null,
        githubUrl: String? = null,
        linkedInUrl: String? = null,
        avatarUrl: String? = null,
        bannerUrl: String? = null,
        services: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            // --- Client-side validation ---
            if (fullName.isBlank()) {
                _uiState.value = ProfileState.Error("Full Name cannot be empty.")
                return@launch
            }
            val urlFields = mapOf(
                "Website" to website, 
                "GitHub URL" to githubUrl, 
                "LinkedIn URL" to linkedInUrl, 
                "Avatar URL" to avatarUrl,
                "Banner URL" to bannerUrl
            )
            for ((label, url) in urlFields) {
                if (!url.isNullOrBlank() && !url.startsWith("http://") && !url.startsWith("https://")) {
                    _uiState.value = ProfileState.Error("$label must start with https://")
                    return@launch
                }
            }

            _uiState.value = ProfileState.Loading
            try {
                // Use typed DTO — fixes the Retrofit wildcard serialization crash
                val request = UpdateProfileRequest(
                    fullName = fullName,
                    role = role,
                    bio = bio,
                    department = department,
                    college = college,
                    year = year,
                    phoneNumber = phoneNumber,
                    website = website,
                    githubUrl = githubUrl,
                    linkedInUrl = linkedInUrl,
                    avatarUrl = avatarUrl,
                    bannerUrl = bannerUrl,
                    services = services
                )

                val updatedUser = ApiClient.service.updateProfile(userId, request)

                // Sync session BEFORE emitting success so Dashboard header
                // reflects name changes on the same navigation frame
                val sessionManager = com.kiriplatform.app.data.SessionManager.getInstance(context)
                sessionManager.saveUserName(updatedUser.fullName)
                sessionManager.saveUserRole(updatedUser.role)
                sessionManager.saveUserAvatar(updatedUser.avatarUrl)
                sessionManager.saveUserBanner(updatedUser.bannerUrl)
                sessionManager.setCanCreateEvents(updatedUser.canCreateEvents)

                // Persist to cache so it survives offline
                com.kiriplatform.app.data.CacheManager.saveCache(context, "profile_$userId", updatedUser)

                _uiState.value = ProfileState.Success(updatedUser)
            } catch (e: Exception) {
                val friendlyMessage = when {
                    e.message?.contains("403") == true -> "Permission denied. You can only edit your own profile."
                    e.message?.contains("404") == true -> "User not found. Please log in again."
                    e.message?.contains("Unable to resolve host") == true -> "No internet connection."
                    else -> e.message ?: "Failed to update profile"
                }
                _uiState.value = ProfileState.Error(friendlyMessage)
            }
        }
    }
}
