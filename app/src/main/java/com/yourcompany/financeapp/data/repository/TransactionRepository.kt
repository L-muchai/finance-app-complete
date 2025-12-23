package com.yourcompany.financeapp.data.repository

import com.yourcompany.financeapp.data.local.AppDatabase
import com.yourcompany.financeapp.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val database: AppDatabase
) {
    private val transactionDao = database.transactionDao()
    
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }
    
    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return transactionDao.insert(transaction)
    }
    
    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.update(transaction)
    }
    
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.delete(transaction)
    }
    
    suspend fun getTransactionById(id: Long): TransactionEntity? {
        return transactionDao.getTransactionById(id)
    }
    
    suspend fun getTotalIncome(): Double {
        return transactionDao.getTotalIncome() ?: 0.0
    }
    
    suspend fun getTotalExpenses(): Double {
        return transactionDao.getTotalExpenses() ?: 0.0
    }
    
    suspend fun getTodayExpenses(): Double {
        // For now, return mock data
        // In real app, filter by today's date
        return 245.50
    }
    
    suspend fun getWeeklyExpenses(): Double {
        // For now, return mock data
        return 1567.80
    }
    
    suspend fun getTransactionCount(): Int {
        return transactionDao.getTransactionCount()
    }
    
    suspend fun deleteAllTransactions() {
        transactionDao.deleteAll()
    }
}
