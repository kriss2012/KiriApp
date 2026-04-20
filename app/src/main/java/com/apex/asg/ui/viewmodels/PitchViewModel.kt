package com.apex.asg.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.*
import kotlinx.coroutines.launch

class PitchViewModel : ViewModel() {
    private val _pitches = mutableStateListOf<PitchDto>()
    val pitches: List<PitchDto> get() = _pitches

    init {
        loadPitches()
    }

    fun loadPitches() {
        viewModelScope.launch {
            try {
                val data = ApiClient.service.getPitches()
                _pitches.clear()
                _pitches.addAll(data)
            } catch (e: Exception) {
                // Log error
            }
        }
    }

    fun submitPitch(request: CreatePitchRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                ApiClient.service.createPitch(request)
                loadPitches()
                onSuccess()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun backPitch(pitchId: String, points: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                ApiClient.service.backPitch(mapOf("pitchId" to pitchId, "points" to points.toString()))
                loadPitches()
                onSuccess()
            } catch (e: Exception) {
                // Handle insufficient points
            }
        }
    }
}
