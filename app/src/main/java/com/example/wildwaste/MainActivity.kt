package com.example.wildwaste

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wildwaste.ui.screens.AccountScreen
import com.example.wildwaste.ui.screens.HistoryScreen
import com.example.wildwaste.ui.screens.LoginScreen
import com.example.wildwaste.ui.screens.MapScreen
import com.example.wildwaste.ui.screens.RegisterScreen
import com.example.wildwaste.ui.theme.WildWasteTheme
import com.example.wildwaste.viewmodels.AuthViewModel

// --- Bottom Navigation Data Class ---
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Map : BottomNavItem("map", Icons.Default.LocationOn, "Map")
    object History : BottomNavItem("history", Icons.Default.DateRange, "History")
    object Account : BottomNavItem("account", Icons.Default.AccountCircle, "Account")
}

@Composable
fun MainScreen(userId: Int, username: String) { // CHANGE 1: Accept username
    val navController = rememberNavController()
    val bottomNavItems = listOf(BottomNavItem.Map, BottomNavItem.History, BottomNavItem.Account)

    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF4DB6AC),
            Color(0xFFA5D6A7)
        )
    )

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier.background(brush = gradientBrush)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                selectedTextColor = Color.White,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Map.route) { MapScreen(userId = userId) }
            composable(BottomNavItem.History.route) { HistoryScreen(userId = userId) }
            // CHANGE 2: Pass username to AccountScreen
            composable(BottomNavItem.Account.route) { AccountScreen(userId = userId, username = username) }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WildWasteTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                // CHANGE 3: The success callback now provides username
                onLoginSuccess = { userId, username ->
                    // CHANGE 4: Navigate with both userId and username
                    navController.navigate("main/$userId/$username") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            RegisterScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(
            // CHANGE 5: Update the route to accept username
            route = "main/{userId}/{username}",
            // CHANGE 6: Add navArgument for username
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // CHANGE 7: Extract username from arguments
            val userId = backStackEntry.arguments?.getInt("userId")
            val username = backStackEntry.arguments?.getString("username")

            if (userId != null && username != null) {
                // CHANGE 8: Pass both userId and username to MainScreen
                MainScreen(userId = userId, username = username)
            } else {
                navController.popBackStack()
            }
        }
    }
}
