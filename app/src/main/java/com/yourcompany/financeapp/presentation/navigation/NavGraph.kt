package com.yourcompany.financeapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yourcompany.financeapp.presentation.screens.home.HomeScreen
import com.yourcompany.financeapp.presentation.screens.transactions.AddTransactionScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction")
    object Transactions : Screen("transactions")
    object TransactionDetail : Screen("transaction_detail/{id}") {
        fun createRoute(id: Long) = "transaction_detail/$id"
    }
    object Budgets : Screen("budgets")
    object Reports : Screen("reports")
    object Settings : Screen("settings")
}
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    Scaffold(
        bottomBar = {
            FinanceBottomNavigation(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onAddTransactionClick = {
                        navController.navigate(Screen.AddTransaction.route)
                    },
                    onViewTransactionsClick = {
                        navController.navigate(Screen.Transactions.route)
                    },
                    onViewBudgetsClick = {
                        navController.navigate(Screen.Budgets.route)
                    },
                    onViewReportsClick = {
                        navController.navigate(Screen.Reports.route)
                    }
                )
            }
            
            composable(Screen.AddTransaction.route) {
                AddTransactionScreen(navController = navController)
            }
            
            composable(Screen.Transactions.route) {
                TransactionsScreen(navController = navController)
            }
            
            composable(
                route = Screen.TransactionDetail.route,
                arguments = listOf(navArgument("id") { type = NavType.LongType })
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("id") ?: 0L
                TransactionDetailScreen(
                    transactionId = transactionId,
                    navController = navController
                )
            }
            
            // Placeholder screens
            composable(Screen.Budgets.route) {
                PlaceholderScreen(title = "Budgets", navController = navController)
            }
            
            composable(Screen.Reports.route) {
                PlaceholderScreen(title = "Reports", navController = navController)
            }
            
            composable(Screen.Settings.route) {
                PlaceholderScreen(title = "Settings", navController = navController)
            }
        }
    }
}

@Composable
fun PlaceholderScreen(
    title: String,
    navController: androidx.navigation.NavController? = null
) {
    Column(
        modifier = androidx.compose.ui.Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        androidx.compose.material3.Text(
            text = "$title Screen",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
        )
        androidx.compose.ui.Modifier.height(16.dp)
        androidx.compose.material3.Button(
            onClick = { navController?.popBackStack() }
        ) {
            androidx.compose.material3.Text("Go Back")
        }
    }
}
