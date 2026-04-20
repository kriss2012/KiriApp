package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.MessageDto
import com.apex.asg.data.remote.models.SendMessageRequest
import com.apex.asg.data.remote.SocketHandler
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

    fun fetchHistory(user1: String, user2: String) {
        viewModelScope.launch {
            _uiState.value = ChatState.Loading
            try {
                val history = ApiClient.service.getChatHistory(user1, user2)
                _uiState.value = ChatState.Success(history)
            } catch (e: Exception) {
                _uiState.value = ChatState.Error(e.message ?: "Failed to fetch chat history")
            }
        }
    }

    fun addMessageLocally(message: MessageDto) {
        val currentState = _uiState.value
        if (currentState is ChatState.Success) {
            _uiState.value = ChatState.Success(currentState.messages + message)
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
