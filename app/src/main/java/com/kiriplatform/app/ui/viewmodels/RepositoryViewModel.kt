package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.UserDto
import com.kiriplatform.app.data.remote.models.ProjectArtifactDto
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
        // Fetch is now initiated by the screen with context for caching support
    }

    fun fetchVerifiedUsers(context: android.content.Context) {
        viewModelScope.launch {
            // Offline view first
            val cached = com.kiriplatform.app.data.CacheManager.getCache(context, "verified_users", object : com.google.gson.reflect.TypeToken<List<UserDto>>() {})
            if (cached != null) {
                _uiState.value = RepositoryState.Success(cached)
            } else {
                _uiState.value = RepositoryState.Loading
            }

            try {
                val users = ApiClient.service.getVerifiedUsers()
                // Update Cache
                com.kiriplatform.app.data.CacheManager.saveCache(context, "verified_users", users)
                _uiState.value = RepositoryState.Success(users)
            } catch (e: Exception) {
                // If we have cached data, stay in Success state. Otherwise show error.
                if (_uiState.value !is RepositoryState.Success) {
                    _uiState.value = RepositoryState.Error(e.message ?: "Failed to fetch users")
                }
            }
        }
    }
}
