package com.kiriplatform.app.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.ActivityDto
import kotlinx.coroutines.launch

class NAACViewModel : ViewModel() {
    private val _activities = mutableStateListOf<ActivityDto>()
    val activities: List<ActivityDto> get() = _activities

    fun loadActivities(userId: String) {
        viewModelScope.launch {
            try {
                val data = ApiClient.service.getActivities(userId)
                _activities.clear()
                _activities.addAll(data)
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
