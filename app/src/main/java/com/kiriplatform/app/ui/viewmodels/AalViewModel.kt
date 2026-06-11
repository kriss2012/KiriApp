package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiriplatform.app.data.remote.AalRepository
import com.kiriplatform.app.data.remote.models.AalActivityDto
import com.kiriplatform.app.data.remote.models.AalOnboardingDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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
class AalViewModel @Inject constructor(
    private val repository: AalRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<AalState>(AalState.Loading)
    val uiState: StateFlow<AalState> = _uiState.asStateFlow()

    fun loadAalData(userId: String) {
        viewModelScope.launch {
            _uiState.value = AalState.Loading
            
            try {
                // Combine data from repository flows
                var currentOnboarding: AalOnboardingDto? = null
                var currentActivities: List<AalActivityDto> = emptyList()

                kotlinx.coroutines.coroutineScope {
                    launch {
                        repository.getOnboarding(userId)
                            .catch { /* Fallback handled by repository cache flow */ }
                            .collect { onboarding ->
                                currentOnboarding = onboarding
                                _uiState.value = AalState.Success(currentOnboarding, currentActivities)
                            }
                    }

                    launch {
                        repository.getActivities(userId)
                            .catch { e ->
                                if (_uiState.value !is AalState.Success) {
                                    _uiState.value = AalState.Error(e.message ?: "Failed to load activities")
                                }
                            }
                            .collect { activities ->
                                currentActivities = activities
                                _uiState.value = AalState.Success(currentOnboarding, currentActivities)
                            }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AalState.Error(e.message ?: "Failed to load AAL data")
            }
        }
    }
}
