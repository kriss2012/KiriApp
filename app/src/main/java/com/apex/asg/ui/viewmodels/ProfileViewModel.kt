package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.UserDto
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
            // Offline view first
            val cached = com.apex.asg.data.CacheManager.getCache(context, "profile_$userId", object : com.google.gson.reflect.TypeToken<UserDto>() {})
            if (cached != null) {
                _uiState.value = ProfileState.Success(cached)
            } else {
                _uiState.value = ProfileState.Loading
            }

            try {
                val user = ApiClient.service.getProfile(userId)
                com.apex.asg.data.CacheManager.saveCache(context, "profile_$userId", user)
                _uiState.value = ProfileState.Success(user)
            } catch (e: Exception) {
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
        services: List<String> = emptyList()
    ) {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            try {
                val profileData = mutableMapOf<String, Any?>(
                    "fullName" to fullName,
                    "role" to role
                )
                bio?.let { profileData["bio"] = it }
                department?.let { profileData["department"] = it }
                college?.let { profileData["college"] = it }
                year?.let { profileData["year"] = it }
                phoneNumber?.let { profileData["phoneNumber"] = it }
                website?.let { profileData["website"] = it }
                githubUrl?.let { profileData["githubUrl"] = it }
                linkedInUrl?.let { profileData["linkedInUrl"] = it }
                profileData["services"] = services

                val updatedUser = ApiClient.service.updateProfile(
                    userId = userId,
                    profileData = profileData
                )
                
                // PERMANENT SYNC: Save to Cache AND SessionManager
                com.apex.asg.data.CacheManager.saveCache(context, "profile_$userId", updatedUser)
                val sessionManager = com.apex.asg.data.SessionManager.getInstance(context)
                sessionManager.saveUserName(updatedUser.fullName)
                sessionManager.saveUserRole(updatedUser.role)
                sessionManager.setCanCreateEvents(updatedUser.canCreateEvents)
                
                _uiState.value = ProfileState.Success(updatedUser)
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }
}
