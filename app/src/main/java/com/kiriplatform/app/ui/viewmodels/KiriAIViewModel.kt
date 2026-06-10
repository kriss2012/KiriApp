package com.kiriplatform.app.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.AiMessageRequest
import com.kiriplatform.app.data.remote.models.AiMessageResponse
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.net.Uri
import android.content.Context
import android.util.Base64
import java.io.InputStream

data class KiriMessage(
    val content: String,
    val role: String, // "user" or "assistant"
    val timestamp: Long = System.currentTimeMillis()
)

class KiriAIViewModel : ViewModel() {
    private val _messages = mutableStateListOf<KiriMessage>()
    val messages: List<KiriMessage> get() = _messages

    private val _selectedFileUri = MutableStateFlow<Uri?>(null)
    val selectedFileUri: StateFlow<Uri?> = _selectedFileUri

    private val _selectedFileName = MutableStateFlow<String?>(null)
    val selectedFileName: StateFlow<String?> = _selectedFileName

    private val _currentSpecialization = MutableStateFlow("GENERAL")
    val currentSpecialization: StateFlow<String> = _currentSpecialization

    private val _currentLanguage = MutableStateFlow("English")
    val currentLanguage: StateFlow<String> = _currentLanguage

    fun setLanguage(language: String) {
        _currentLanguage.value = language
    }

    init {
        loadHistory()
    }

    fun setSpecialization(specialization: String) {
        _currentSpecialization.value = specialization
        viewModelScope.launch {
            try {
                ApiClient.service.updateSpecialization(mapOf("specialization" to specialization))
            } catch (e: Exception) {
                // Silently fail, prompt will be updated next message anyway
            }
        }
    }

    fun onFileSelected(context: Context, uri: Uri) {
        _selectedFileUri.value = uri
        // Basic name extraction
        val name = uri.path?.split("/")?.lastOrNull() ?: "Document.jpg"
        _selectedFileName.value = name
    }

    fun clearFileSelection() {
        _selectedFileUri.value = null
        _selectedFileName.value = null
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
                        timestamp = System.currentTimeMillis()
                    )
                })
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    private fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sendMessage(content: String, context: Context? = null) {
        val currentUri = _selectedFileUri.value
        val fileName = _selectedFileName.value
        
        // Optimistic update
        val displayContent = if (fileName != null) "$content\n📎 $fileName" else content
        _messages.add(KiriMessage(displayContent, "user"))
        
        viewModelScope.launch {
            try {
                var base64Data: String? = null
                var mimeType: String? = null

                if (currentUri != null && context != null) {
                    base64Data = uriToBase64(context, currentUri)
                    mimeType = context.contentResolver.getType(currentUri) ?: "image/jpeg"
                    clearFileSelection()
                }

                val response = ApiClient.service.sendAiMessage(
                    AiMessageRequest(
                        content = content,
                        fileData = base64Data,
                        mimeType = mimeType,
                        language = _currentLanguage.value
                    )
                )
                _messages.add(KiriMessage(response.content, response.role))
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: "HTTP ${e.code()}"
                _messages.add(KiriMessage("Server error: $errorBody", "assistant"))
            } catch (e: Exception) {
                _messages.add(KiriMessage("Connection error: ${e.localizedMessage ?: "ASG brain connection timed out"}. Please check backend logs.", "assistant"))
            }
        }
    }
}
