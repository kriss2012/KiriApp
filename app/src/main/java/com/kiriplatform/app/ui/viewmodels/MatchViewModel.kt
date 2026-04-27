package com.kiriplatform.app.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.MatchSuggestionDto
import kotlinx.coroutines.launch

class MatchViewModel : ViewModel() {
    private val _suggestions = mutableStateListOf<MatchSuggestionDto>()
    val suggestions: List<MatchSuggestionDto> get() = _suggestions

    init {
        loadSuggestions()
    }

    fun loadSuggestions() {
        viewModelScope.launch {
            try {
                val data = ApiClient.service.getMatchSuggestions()
                _suggestions.clear()
                _suggestions.addAll(data)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
