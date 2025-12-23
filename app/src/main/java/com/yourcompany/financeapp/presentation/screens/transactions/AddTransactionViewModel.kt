package com.yourcompany.financeapp.presentation.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.financeapp.data.local.entities.TransactionEntity
import com.yourcompany.financeapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class AddTransactionUiState(
    val type: String = "Expense",
    val amount: String = "",
    val category: String = "",
    val notes: String = "",
    val date: String = SimpleDateFormat("MMM dd, yyyy").format(Date()),
    val isRecurring: Boolean = false,
    val recurringInterval: Int = 30,
    val isFormValid: Boolean = false,
    val isSaving: Boolean = false,
    val isTransactionSaved: Boolean = false,
    val amountError: String? = null,
    val categoryError: String? = null,
    val error: String? = null
)

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val repository: TransactionRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()
    
    init {
        // Validate form whenever inputs change
        combine(
            _uiState.map { it.amount },
            _uiState.map { it.category }
        ) { amount, category ->
            validateForm(amount, category)
        }.onEach { isValid ->
            _uiState.update { it.copy(isFormValid = isValid) }
        }.launchIn(viewModelScope)
    }
    
    fun onTypeChanged(type: String) {
        _uiState.update { it.copy(type = type) }
    }
    
    fun onAmountChanged(amount: String) {
        _uiState.update { 
            it.copy(
                amount = amount,
                amountError = validateAmount(amount)
            ) 
        }
    }
    
    fun onCategoryChanged(category: String) {
        _uiState.update { 
            it.copy(
                category = category,
                categoryError = validateCategory(category)
            ) 
        }
    }
    
    fun onNotesChanged(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }
    
    fun onRecurringChanged(isRecurring: Boolean) {
        _uiState.update { it.copy(isRecurring = isRecurring) }
    }
    
    fun saveTransaction() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isSaving = true, error = null) }
                
                val amount = _uiState.value.amount.toDoubleOrNull() ?: 0.0
                val transactionType = if (_uiState.value.type == "Income") 
                    TransactionEntity.TYPE_INCOME 
                else 
                    TransactionEntity.TYPE_EXPENSE
                
                val transaction = TransactionEntity(
                    amount = amount,
                    type = transactionType,
                    category = _uiState.value.category,
                    notes = _uiState.value.notes,
                    isRecurring = _uiState.value.isRecurring,
                    recurringInterval = if (_uiState.value.isRecurring) 30 else 0
                )
                
                repository.insertTransaction(transaction)
                
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        isTransactionSaved = true,
                        amount = "",
                        category = "",
                        notes = "",
                        type = "Expense"
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isSaving = false,
                        error = "Failed to save transaction: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun addSampleData() {
        viewModelScope.launch {
            try {
                // Add some sample transactions
                val samples = listOf(
                    TransactionEntity(
                        amount = 65.50,
                        type = TransactionEntity.TYPE_EXPENSE,
                        category = "Restaurant",
                        notes = "Dinner with friends"
                    ),
                    TransactionEntity(
                        amount = 129.99,
                        type = TransactionEntity.TYPE_EXPENSE,
                        category = "Shopping",
                        notes = "New clothes"
                    ),
                    TransactionEntity(
                        amount = 3000.00,
                        type = TransactionEntity.TYPE_INCOME,
                        category = "Salary",
                        notes = "Monthly salary"
                    )
                )
                
                samples.forEach { repository.insertTransaction(it) }
                
                _uiState.update { 
                    it.copy(
                        error = "Added ${samples.size} sample transactions!"
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = "Failed to add samples: ${e.message}"
                    ) 
                }
            }
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    
    private fun validateForm(amount: String, category: String): Boolean {
        return validateAmount(amount) == null && 
               validateCategory(category) == null &&
               amount.isNotEmpty() && 
               category.isNotEmpty()
    }
    
    private fun validateAmount(amount: String): String? {
        return when {
            amount.isEmpty() -> "Amount is required"
            amount.toDoubleOrNull() == null -> "Enter a valid number"
            amount.toDouble() <= 0 -> "Amount must be greater than 0"
            else -> null
        }
    }
    
    private fun validateCategory(category: String): String? {
        return when {
            category.isEmpty() -> "Category is required"
            category.length < 2 -> "Category is too short"
            else -> null
        }
    }
}
