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

    fun fetchProfile(userId: String) {
        viewModelScope.launch {
            _uiState.value = ProfileState.Loading
            try {
                val user = ApiClient.service.getProfile(userId)
                _uiState.value = ProfileState.Success(user)
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.message ?: "Failed to fetch profile")
            }
        }
    }

    fun updateProfile(
        userId: String, 
        fullName: String, 
        role: String, 
        bio: String? = null, 
        department: String? = null, 
        college: String? = null, 
        year: String? = null,
        phoneNumber: String? = null,
        website: String? = null,
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
                profileData["services"] = services

                val updatedUser = ApiClient.service.updateProfile(
                    userId = userId,
                    profileData = profileData
                )
                _uiState.value = ProfileState.Success(updatedUser)
            } catch (e: Exception) {
                _uiState.value = ProfileState.Error(e.message ?: "Failed to update profile")
            }
        }
    }
}
