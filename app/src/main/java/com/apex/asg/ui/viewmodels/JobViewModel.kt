package com.apex.asg.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.CreateJobRequest
import com.apex.asg.data.remote.models.JobDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class JobState {
    object Loading : JobState()
    data class Success(val jobs: List<JobDto>) : JobState()
    data class Error(val message: String) : JobState()
}

class JobViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<JobState>(JobState.Loading)
    val uiState: StateFlow<JobState> = _uiState.asStateFlow()

    init {
        // Fetch is now initiated by the screen with context
    }

    fun fetchJobs(context: android.content.Context) {
        viewModelScope.launch {
            // Load from cache first for immediate offline view
            val cached = com.apex.asg.data.CacheManager.getCache(context, "jobs_list", object : com.google.gson.reflect.TypeToken<List<JobDto>>() {})
            if (cached != null) {
                _uiState.value = JobState.Success(cached)
            } else {
                _uiState.value = JobState.Loading
            }

            try {
                val jobs = ApiClient.service.getJobs()
                // Save to cache
                com.apex.asg.data.CacheManager.saveCache(context, "jobs_list", jobs)
                _uiState.value = JobState.Success(jobs)
            } catch (e: Exception) {
                if (_uiState.value !is JobState.Success) {
                    _uiState.value = JobState.Error(e.message ?: "Failed to fetch jobs")
                }
            }
        }
    }

    fun postJob(context: android.content.Context, title: String, description: String, location: String?, type: String, posterId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                ApiClient.service.createJob(CreateJobRequest(title, description, location, type, posterId))
                fetchJobs(context) // Refresh after successful post
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
