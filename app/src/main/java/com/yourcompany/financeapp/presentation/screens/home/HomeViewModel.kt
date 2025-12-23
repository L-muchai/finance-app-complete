package com.yourcompany.financeapp.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.financeapp.data.repository.TransactionRepository
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
    val transactionCount: Int = 0,
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Load data in parallel
                val income = repository.getTotalIncome()
                val expenses = repository.getTotalExpenses()
                val todayExpenses = repository.getTodayExpenses()
                val weeklyExpenses = repository.getWeeklyExpenses()
                val count = repository.getTransactionCount()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        totalIncome = income,
                        totalExpenses = expenses,
                        totalBalance = income - expenses,
                        todayExpenses = todayExpenses,
                        weeklyExpenses = weeklyExpenses,
                        transactionCount = count
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
        loadData()
    }
    
    suspend fun addSampleData() {
        // Add sample transactions for testing
        val sampleTransactions = listOf(
            TransactionEntity(
                amount = 2500.00,
                type = TransactionEntity.TYPE_INCOME,
                category = "Salary",
                notes = "Monthly salary"
            ),
            TransactionEntity(
                amount = 1200.00,
                type = TransactionEntity.TYPE_INCOME,
                category = "Freelance",
                notes = "Web development project"
            ),
            TransactionEntity(
                amount = 850.00,
                type = TransactionEntity.TYPE_EXPENSE,
                category = "Rent",
                notes = "Monthly rent payment"
            ),
            TransactionEntity(
                amount = 320.50,
                type = TransactionEntity.TYPE_EXPENSE,
                category = "Groceries",
                notes = "Weekly groceries"
            ),
            TransactionEntity(
                amount = 45.75,
                type = TransactionEntity.TYPE_EXPENSE,
                category = "Transport",
                notes = "Fuel and parking"
            )
        )
        
        sampleTransactions.forEach { transaction ->
            repository.insertTransaction(transaction)
        }
        
        refreshData()
    }
}
