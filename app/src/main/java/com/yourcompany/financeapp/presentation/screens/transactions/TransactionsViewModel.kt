package com.yourcompany.financeapp.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.financeapp.data.local.entities.TransactionEntity
import com.yourcompany.financeapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionsUiState(
    val isLoading: Boolean = true,
    val transactions: List<TransactionEntity> = emptyList(),
    val totalAmount: Double = 0.0,
    val incomeTotal: Double = 0.0,
    val expenseTotal: Double = 0.0,
    val error: String? = null
)

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()
    
    init {
        loadTransactions()
    }
    
    fun loadTransactions() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                // Collect transactions from Flow
                repository.getAllTransactions()
                    .collectLatest { transactions ->
                        val income = transactions
                            .filter { it.type == TransactionEntity.TYPE_INCOME }
                            .sumOf { it.amount }
                        
                        val expenses = transactions
                            .filter { it.type == TransactionEntity.TYPE_EXPENSE }
                            .sumOf { it.amount }
                        
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                transactions = transactions,
                                totalAmount = income - expenses,
                                incomeTotal = income,
                                expenseTotal = expenses
                            )
                        }
                    }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load transactions: ${e.message}"
                    )
                }
            }
        }
    }
    
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        try {
            repository.deleteTransaction(transaction)
            loadTransactions()
        } catch (e: Exception) {
            _uiState.update { it.copy(error = "Failed to delete: ${e.message}") }
        }
    }
    
    suspend fun addSampleTransactions() {
        val samples = listOf(
            TransactionEntity(
                amount = 150.75,
                type = TransactionEntity.TYPE_EXPENSE,
                category = "Groceries",
                notes = "Weekly shopping"
            ),
            TransactionEntity(
                amount = 65.00,
                type = TransactionEntity.TYPE_EXPENSE,
                category = "Entertainment",
                notes = "Movie tickets"
            ),
            TransactionEntity(
                amount = 2500.00,
                type = TransactionEntity.TYPE_INCOME,
                category = "Salary",
                notes = "Monthly salary"
            ),
            TransactionEntity(
                amount = 120.50,
                type = TransactionEntity.TYPE_EXPENSE,
                category = "Transport",
                notes = "Fuel and parking"
            ),
            TransactionEntity(
                amount = 300.00,
                type = TransactionEntity.TYPE_EXPENSE,
                category = "Utilities",
                notes = "Electricity bill"
            )
        )
        
        samples.forEach { repository.insertTransaction(it) }
        loadTransactions()
    }
}
