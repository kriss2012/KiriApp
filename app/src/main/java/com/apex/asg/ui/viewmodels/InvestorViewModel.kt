package com.apex.asg.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.InvestorPitchDto
import com.apex.asg.data.remote.MarketTrendDto
import kotlinx.coroutines.launch

class InvestorViewModel : ViewModel() {
    private val _highPotential = mutableStateListOf<InvestorPitchDto>()
    val highPotential: List<InvestorPitchDto> get() = _highPotential

    private val _trends = mutableStateListOf<MarketTrendDto>()
    val trends: List<MarketTrendDto> get() = _trends

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            try {
                _highPotential.clear()
                _highPotential.addAll(ApiClient.service.getHighPotentialPitches())
                
                _trends.clear()
                _trends.addAll(ApiClient.service.getMarketTrends())
            } catch (e: Exception) {
                // Log error
            }
        }
    }
}
