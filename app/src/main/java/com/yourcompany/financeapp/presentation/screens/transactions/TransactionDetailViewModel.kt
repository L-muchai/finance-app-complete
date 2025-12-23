package com.yourcompany.financeapp.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.financeapp.data.local.entities.TransactionEntity
import com.yourcompany.financeapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionDetailUiState(
    val isLoading: Boolean = true,
    val transaction: TransactionEntity? = null,
    val error: String? = null
)

@HiltViewModel
class TransactionDetailViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TransactionDetailUiState())
    val uiState: StateFlow<TransactionDetailUiState> = _uiState.asStateFlow()
    
    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val transaction = repository.getTransactionById(id)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        transaction = transaction,
                        error = if (transaction == null) "Transaction not found" else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load transaction: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun deleteTransaction() {
        viewModelScope.launch {
            try {
                val transaction = _uiState.value.transaction
                if (transaction != null) {
                    repository.deleteTransaction(transaction)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to delete: ${e.message}") }
            }
        }
    }
    
    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
                loadTransaction(transaction.id)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Failed to update: ${e.message}") }
            }
        }
    }
}
