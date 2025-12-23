package com.yourcompany.financeapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.yourcompany.financeapp.data.local.converters.DateConverter
import java.util.*

@Entity(tableName = "transactions")
@TypeConverters(DateConverter::class)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val date: Date = Date(),
    val notes: String = "",
    val isRecurring: Boolean = false,
    val recurringInterval: Int = 0 // 0 = not recurring, 30 = monthly, etc.
) {
    companion object {
        const val TYPE_INCOME = "INCOME"
        const val TYPE_EXPENSE = "EXPENSE"
    }
}
