package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.ConnectionDto
import com.kiriplatform.app.data.remote.models.UserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ConnectionsState {
    object Idle : ConnectionsState()
    object Loading : ConnectionsState()
    data class Success(val connections: List<ConnectionDto>) : ConnectionsState()
    data class Error(val message: String) : ConnectionsState()
}

class ConnectionsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ConnectionsState>(ConnectionsState.Idle)
    val uiState: StateFlow<ConnectionsState> = _uiState.asStateFlow()

    fun fetchConnections(userId: String) {
        // PERMANENT FIX: Listen for real-time acceptance to keep list fresh
        com.kiriplatform.app.data.remote.SocketHandler.setupGlobalListeners(
            onNotification = {},
            onMessage = {},
            onConnectionAccepted = { fetchConnections(userId) },
            onMatchSuggested = {}
        )

        viewModelScope.launch {
            _uiState.value = ConnectionsState.Loading
            try {
                val connections = ApiClient.service.getUserConnections(userId)
                _uiState.value = ConnectionsState.Success(connections)
            } catch (e: Exception) {
                _uiState.value = ConnectionsState.Error(e.message ?: "Failed to fetch connections")
            }
        }
    }

    fun acceptRequest(connectionId: String, userId: String) {
        viewModelScope.launch {
            try {
                ApiClient.service.acceptConnectionRequest(mapOf("connectionId" to connectionId, "userId" to userId))
                // Refresh list
                fetchConnections(userId)
            } catch (e: Exception) {
                // Handle error (e.g., show a toast or update state)
            }
        }
    }
}
