package com.yourcompany.financeapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = true,
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val todayExpenses: Double = 0.0,
    val weeklyExpenses: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadInitialData()
    }
    
    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                // Simulate loading
                _uiState.update { it.copy(isLoading = true) }
                
                // Mock data for now - will replace with real database calls
                delay(1000)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalBalance = 12543.75,
                        totalIncome = 20000.00,
                        totalExpenses = 7456.25,
                        todayExpenses = 245.50,
                        weeklyExpenses = 1567.80
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load data"
                    )
                }
            }
        }
    }
    
    fun refreshData() {
        loadInitialData()
    }
    
    // Helper function for testing
    private suspend fun delay(timeMillis: Long) {
        kotlinx.coroutines.delay(timeMillis)
    }
}
