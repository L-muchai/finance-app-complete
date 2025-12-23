package com.yourcompany.financeapp.presentation.screens.transactions

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.yourcompany.financeapp.data.local.entities.TransactionEntity
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController? = null,
    viewModel: AddTransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    LaunchedEffect(uiState.isTransactionSaved) {
        if (uiState.isTransactionSaved) {
            Toast.makeText(context, "Transaction saved!", Toast.LENGTH_SHORT).show()
            navController?.popBackStack()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.saveTransaction() },
                        enabled = uiState.isFormValid
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            // Type Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        listOf("Income", "Expense").forEach { type ->
                            FilterChip(
                                selected = uiState.type == type,
                                onClick = { viewModel.onTypeChanged(type) },
                                label = { Text(type) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = if (type == "Income") 
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else 
                                        MaterialTheme.colorScheme.error.copy(alpha = 0.2f),
                                    selectedLabelColor = if (type == "Income")
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            )
                        }
                    }
                }
            }
            
            // Amount Input
            item {
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = viewModel::onAmountChanged,
                    label = { Text("Amount") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.amountError != null,
                    placeholder = { Text("0.00") }
                )
                uiState.amountError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            
            // Category Input
            item {
                OutlinedTextField(
                    value = uiState.category,
                    onValueChange = viewModel::onCategoryChanged,
                    label = { Text("Category") },
                    leadingIcon = { Icon(Icons.Default.Category, null) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.categoryError != null,
                    placeholder = { Text("e.g., Food, Rent, Salary") }
                )
                uiState.categoryError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }
            }
            
            // Notes Input
            item {
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChanged,
                    label = { Text("Notes (Optional)") },
                    leadingIcon = { Icon(Icons.Default.Description, null) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
            
            // Date Picker Section
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Transaction Date",
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(
                                uiState.date,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        IconButton(onClick = { /* Show date picker */ }) {
                            Icon(Icons.Default.CalendarToday, "Pick Date")
                        }
                    }
                }
            }
            
            // Recurring Transaction Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Recurring Transaction", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Repeat this transaction regularly",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.isRecurring,
                        onCheckedChange = viewModel::onRecurringChanged
                    )
                }
            }
            
            // Save Button
            item {
                Button(
                    onClick = { viewModel.saveTransaction() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = uiState.isFormValid
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Save Transaction", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            
            // Add Sample Data Button (for testing)
            item {
                OutlinedButton(
                    onClick = { viewModel.addSampleData() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.DataUsage, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Sample Data")
                }
            }
        }
    }
    
    // Show error dialog
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.clearError() },
            title = { Text("Error") },
            text = { Text(uiState.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }
}
