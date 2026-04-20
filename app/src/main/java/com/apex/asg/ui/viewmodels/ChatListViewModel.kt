package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.ConversationResponse
import com.apex.asg.data.remote.models.UserDto
import com.apex.asg.data.remote.models.AiMessageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {
    private val _conversations = MutableStateFlow<List<ConversationResponse>>(emptyList())
    val conversations: StateFlow<List<ConversationResponse>> = _conversations

    private val _aiLatest = MutableStateFlow<AiMessageResponse?>(null)
    val aiLatest: StateFlow<AiMessageResponse?> = _aiLatest

    private val _searchResults = MutableStateFlow<List<UserDto>>(emptyList())
    val searchResults: StateFlow<List<UserDto>> = _searchResults

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchChatHub() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Fetch Peer Conversations
                val convos = ApiClient.service.getConversations()
                _conversations.value = convos

                // Fetch Latest AI message for the pinned card
                val aiHistory = ApiClient.service.getAiHistory()
                if (aiHistory.isNotEmpty()) {
                    _aiLatest.value = aiHistory.first()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchUsers(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val results = ApiClient.service.searchUsers(query)
                _searchResults.value = results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
