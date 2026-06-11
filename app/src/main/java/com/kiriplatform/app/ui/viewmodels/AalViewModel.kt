package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.AalActivityDto
import com.kiriplatform.app.data.remote.models.AalOnboardingDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AalState {
    object Loading : AalState()
    data class Success(
        val onboarding: AalOnboardingDto?,
        val activities: List<AalActivityDto>
    ) : AalState()
    data class Error(val message: String) : AalState()
}

@HiltViewModel
class AalViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<AalState>(AalState.Loading)
    val uiState: StateFlow<AalState> = _uiState.asStateFlow()

    fun loadAalData(userId: String) {
        viewModelScope.launch {
            _uiState.value = AalState.Loading
            try {
                val onboarding = try {
                    ApiClient.service.getAalOnboarding(userId)
                } catch (e: Exception) {
                    null
                }
                val activities = try {
                    ApiClient.service.getAalActivities(userId)
                } catch (e: Exception) {
                    emptyList()
                }
                _uiState.value = AalState.Success(onboarding, activities)
            } catch (e: Exception) {
                _uiState.value = AalState.Error(e.message ?: "Failed to load AAL data")
            }
        }
    }
}
