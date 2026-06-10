package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.MessageDto
import com.kiriplatform.app.data.remote.models.SendMessageRequest
import com.kiriplatform.app.data.remote.SocketHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ChatState {
    object Loading : ChatState()
    data class Success(val messages: List<MessageDto>) : ChatState()
    data class Error(val message: String) : ChatState()
}

class ChatViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<ChatState>(ChatState.Loading)
    val uiState: StateFlow<ChatState> = _uiState.asStateFlow()

    fun fetchHistory(context: android.content.Context, user1: String, user2: String) {
        viewModelScope.launch {
            val cacheKey = "chat_${user1}_${user2}"
            // 1. Load from cache for immediate offline view
            val cached = com.kiriplatform.app.data.CacheManager.getCache(
                context, 
                cacheKey, 
                object : com.google.gson.reflect.TypeToken<List<MessageDto>>() {}
            )
            if (cached != null) {
                _uiState.value = ChatState.Success(cached)
            } else {
                _uiState.value = ChatState.Loading
            }

            // 2. Refresh from network
            try {
                val history = ApiClient.service.getChatHistory(user1, user2)
                // Update cache
                com.kiriplatform.app.data.CacheManager.saveCache(context, cacheKey, history)
                _uiState.value = ChatState.Success(history)
            } catch (e: Exception) {
                // Keep cached data visible if already showing success
                if (_uiState.value !is ChatState.Success) {
                    _uiState.value = ChatState.Error("Offline: Cannot load new messages.")
                }
            }
        }
    }

    fun addMessageLocally(message: MessageDto) {
        val currentState = _uiState.value
        if (currentState is ChatState.Success) {
            val exists = currentState.messages.any { it.id == message.id }
            if (!exists) {
                _uiState.value = ChatState.Success(currentState.messages + message)
            }
        }
    }

    fun sendMessage(senderId: String, receiverId: String, content: String) {
        viewModelScope.launch {
            try {
                val response = ApiClient.service.sendMessage(SendMessageRequest(senderId, receiverId, content))
                addMessageLocally(response)
            } catch (e: Exception) {
                _uiState.value = ChatState.Error("Message failed to send: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        SocketHandler.closeConnection()
    }
}
