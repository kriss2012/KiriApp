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

    fun loadHistory() {
        viewModelScope.launch {
            try {
                // val history = ApiClient.service.getAiHistory()
                // _messages.clear()
                // _messages.addAll(history.map { KiriMessage(it.content, it.role) })
                
                // For now, if no history, start with empty
                if (_messages.isEmpty()) {
                    // _messages.add(KiriMessage("SYSTEM_READY", "assistant"))
                }
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        
        val userMsg = KiriMessage(content, "user")
        _messages.add(userMsg)
        
        viewModelScope.launch {
            try {
                // Simulate AI Thinking
                // val response = ApiClient.service.sendAiMessage(AiMessageRequest(content))
                // _messages.add(KiriMessage(response.content, "assistant"))
                
                // Mock response based on "Second Brain" logic
                val aiResponse = when {
                    content.contains("hackathon", ignoreCase = true) -> 
                        "I've analyzed your profile. As a Student at GCOEJ, you should check out the Jalgoan-Smart-City Hackathon next month. I've logged this in your 'Opportunity Tracker'."
                    content.contains("college", ignoreCase = true) -> 
                        "You are currently registered under GH Raisoni Institute. Would you like to see upcoming events specifically for your campus?"
                    else -> "Logic active. Data synchronized with your Jalgaon community profile. How can your second brain assist you further?"
                }
                
                _messages.add(KiriMessage(aiResponse, "assistant"))
            } catch (e: Exception) {
                _messages.add(KiriMessage("Connection error. I'm still your second brain, but I need a stable link to the ASG servers.", "assistant"))
            }
        }
    }
}
