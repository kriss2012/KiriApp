package com.kiriplatform.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.kiriplatform.app.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MainState(
    val currentColorTheme: AppTheme = AppTheme.NOTION,
    val isDarkTheme: Boolean? = null, // null follows system
    val isAmoledTheme: Boolean = false,
    val isLiquidGlassEnabled: Boolean = true,
    val isLoggedIn: Boolean = true
)

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MainState())
    val uiState: StateFlow<MainState> = _uiState.asStateFlow()

    fun setTheme(theme: AppTheme) {
        _uiState.value = _uiState.value.copy(currentColorTheme = theme)
    }

    fun toggleDarkMode(isDark: Boolean?) {
        _uiState.value = _uiState.value.copy(isDarkTheme = isDark)
    }

    fun toggleAmoled(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isAmoledTheme = enabled)
    }

    fun toggleLiquidGlass(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isLiquidGlassEnabled = enabled)
    }

    fun setLoggedIn(loggedIn: Boolean) {
        _uiState.value = _uiState.value.copy(isLoggedIn = loggedIn)
    }
}
