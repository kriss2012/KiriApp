package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.MessageDto
import com.apex.asg.data.remote.SendMessageRequest
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

    fun sendMessage(senderId: String, receiverId: String, content: String) {
        viewModelScope.launch {
            try {
                ApiClient.service.sendMessage(SendMessageRequest(senderId, receiverId, content))
                // Refresh local history
                fetchHistory(senderId, receiverId) 
            } catch (e: Exception) {
                // Handle silent failure or update UI with error
            }
        }
    }
}
