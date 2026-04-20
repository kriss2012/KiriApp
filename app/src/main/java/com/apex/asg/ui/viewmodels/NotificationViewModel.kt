package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.NotificationDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationState {
    object Loading : NotificationState()
    data class Success(val notifications: List<NotificationDto>) : NotificationState()
    data class Error(val message: String) : NotificationState()
}

class NotificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<NotificationState>(NotificationState.Loading)
    val uiState: StateFlow<NotificationState> = _uiState.asStateFlow()

    fun fetchNotifications(userId: String) {
        viewModelScope.launch {
            _uiState.value = NotificationState.Loading
            try {
                val notifications = ApiClient.service.getNotifications(userId)
                _uiState.value = NotificationState.Success(notifications)
            } catch (e: Exception) {
                _uiState.value = NotificationState.Error(e.message ?: "Failed to fetch notifications")
            }
        }
    }

    fun markAsRead(notificationId: String, userId: String) {
        viewModelScope.launch {
            try {
                ApiClient.service.markNotificationAsRead(notificationId)
                fetchNotifications(userId) // Refresh
            } catch (e: Exception) {
                // Silently fail or log
            }
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            try {
                ApiClient.service.markAllNotificationsAsRead(userId)
                fetchNotifications(userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun acceptConnection(notificationId: String, connectionId: String, userId: String) {
        viewModelScope.launch {
            try {
                // 1. Accept the connection
                ApiClient.service.acceptConnectionRequest(mapOf("connectionId" to connectionId, "userId" to userId))
                // 2. Mark notification as read
                ApiClient.service.markNotificationAsRead(notificationId)
                // 3. Refresh list
                fetchNotifications(userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
