package com.yourcompany.financeapp.data.local.dao

import androidx.room.*
import com.yourcompany.financeapp.data.local.entities.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long
    
    @Update
    suspend fun update(transaction: TransactionEntity)
    
    @Delete
    suspend fun delete(transaction: TransactionEntity)
    
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    suspend fun getTotalIncome(): Double?
    
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    suspend fun getTotalExpenses(): Double?
    
    @Query("DELETE FROM transactions")
    suspend fun deleteAll()
    
    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int
}
