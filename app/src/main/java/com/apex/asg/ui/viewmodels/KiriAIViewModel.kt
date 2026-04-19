package com.apex.asg.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.AiMessageRequest
import com.apex.asg.data.remote.AiMessageResponse
import kotlinx.coroutines.launch

data class KiriMessage(
    val content: String,
    val role: String, // "user" or "assistant"
    val timestamp: Long = System.currentTimeMillis()
)

class KiriAIViewModel : ViewModel() {
    private val _messages = mutableStateListOf<KiriMessage>()
    val messages: List<KiriMessage> get() = _messages

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            try {
                val history = ApiClient.service.getAiHistory()
                _messages.clear()
                _messages.addAll(history.map { resp ->
                    KiriMessage(
                        content = resp.content,
                        role = resp.role,
                        timestamp = try { 
                            // Convert ISO date if needed, or just use current as placeholder
                            System.currentTimeMillis() 
                        } catch (e: Exception) { 
                            System.currentTimeMillis() 
                        }
                    )
                })
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        // Optimistic update
        val userMsg = KiriMessage(content, "user")
        _messages.add(userMsg)
        
        viewModelScope.launch {
            try {
                val response = ApiClient.service.sendAiMessage(AiMessageRequest(content))
                _messages.add(KiriMessage(response.content, response.role))
            } catch (e: Exception) {
                _messages.add(KiriMessage("Connection error. Kiri is having trouble reaching the ASG brain. Please check your internet.", "assistant"))
            }
        }
    }
}
