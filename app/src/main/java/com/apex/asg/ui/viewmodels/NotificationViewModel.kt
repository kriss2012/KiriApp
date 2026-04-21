package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.NotificationDto
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

    fun fetchNotifications(context: android.content.Context, userId: String) {
        viewModelScope.launch {
            // Load from cache first for immediate offline view
            val cached = com.apex.asg.data.CacheManager.getCache(context, "notifications_$userId", object : com.google.gson.reflect.TypeToken<List<NotificationDto>>() {})
            if (cached != null) {
                _uiState.value = NotificationState.Success(cached)
            } else {
                _uiState.value = NotificationState.Loading
            }

            try {
                val notifications = ApiClient.service.getNotifications(userId)
                // Save to cache
                com.apex.asg.data.CacheManager.saveCache(context, "notifications_$userId", notifications)
                _uiState.value = NotificationState.Success(notifications)
            } catch (e: Exception) {
                if (_uiState.value !is NotificationState.Success) {
                    _uiState.value = NotificationState.Error(e.message ?: "Failed to fetch notifications")
                }
            }
        }
    }

    fun markAsRead(context: android.content.Context, notificationId: String, userId: String) {
        viewModelScope.launch {
            try {
                ApiClient.service.markNotificationAsRead(notificationId)
                fetchNotifications(context, userId) // Refresh
            } catch (e: Exception) {
                // Silently fail or log
            }
        }
    }

    fun markAllAsRead(context: android.content.Context, userId: String) {
        viewModelScope.launch {
            try {
                ApiClient.service.markAllNotificationsAsRead(userId)
                fetchNotifications(context, userId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun acceptConnection(context: android.content.Context, notificationId: String, connectionId: String, userId: String) {
        viewModelScope.launch {
            try {
                // 1. Accept the connection
                ApiClient.service.acceptConnectionRequest(mapOf("connectionId" to connectionId, "userId" to userId))
                // 2. Mark notification as read
                ApiClient.service.markNotificationAsRead(notificationId)
                // 3. Refresh list
                fetchNotifications(context, userId)
            } catch (e: Exception) {
                println("Accept Connection Error: ${e.message}")
                _uiState.value = NotificationState.Error("Failed to accept connection: ${e.message}")
            }
        }
    }
}
