package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.UserDto
import com.apex.asg.data.remote.models.ProjectArtifactDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RepositoryState {
    object Loading : RepositoryState()
    data class Success(val users: List<UserDto>) : RepositoryState()
    data class Error(val message: String) : RepositoryState()
}

class RepositoryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<RepositoryState>(RepositoryState.Loading)
    val uiState: StateFlow<RepositoryState> = _uiState.asStateFlow()

    init {
        fetchVerifiedUsers()
    }

    fun fetchVerifiedUsers() {
        viewModelScope.launch {
            _uiState.value = RepositoryState.Loading
            try {
                val users = ApiClient.service.getVerifiedUsers()
                _uiState.value = RepositoryState.Success(users)
            } catch (e: Exception) {
                _uiState.value = RepositoryState.Error(e.message ?: "Failed to fetch users")
            }
        }
    }
}
