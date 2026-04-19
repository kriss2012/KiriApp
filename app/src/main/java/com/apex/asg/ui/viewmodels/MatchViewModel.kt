package com.apex.asg.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.MatchSuggestionDto
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
