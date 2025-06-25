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
import androidx.navigation.NavHostController
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
import com.example.wildwaste.viewmodels.ThemeViewModel

// --- Bottom Navigation Data Class ---
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Map : BottomNavItem("map", Icons.Default.LocationOn, "Map")
    object History : BottomNavItem("history", Icons.Default.DateRange, "History")
    object Account : BottomNavItem("account", Icons.Default.AccountCircle, "Account")
}

@Composable
fun MainScreen(
    userId: Int,
    username: String,
    authViewModel: AuthViewModel,
    appNavController: NavHostController,
    themeViewModel: ThemeViewModel
) {
    val bottomBarNavController = rememberNavController()
    val bottomNavItems = listOf(BottomNavItem.Map, BottomNavItem.History, BottomNavItem.Account)

    // --- PERUBAHAN DI SINI ---
    // Menerapkan gradien tiga warna yang sama seperti di halaman login
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2D6A44), // Hijau paling gelap
            Color(0xFF4B8E5A), // Hijau pertengahan
            Color(0xFF5CA46C)  // Hijau paling terang
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
                    val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                bottomBarNavController.navigate(screen.route) {
                                    popUpTo(bottomBarNavController.graph.findStartDestination().id) {
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
            navController = bottomBarNavController,
            startDestination = BottomNavItem.Map.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Map.route) { MapScreen(userId = userId) }
            composable(BottomNavItem.History.route) { HistoryScreen(userId = userId) }
            composable(BottomNavItem.Account.route) {
                AccountScreen(
                    userId = userId,
                    username = username,
                    themeViewModel = themeViewModel,
                    onLogoutClicked = {
                        authViewModel.logout()
                        appNavController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            WildWasteTheme(darkTheme = themeViewModel.isDarkMode.value) {
                AppNavigation(themeViewModel = themeViewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { userId, username ->
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
            route = "main/{userId}/{username}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("username") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId")
            val username = backStackEntry.arguments?.getString("username")

            if (userId != null && username != null) {
                MainScreen(
                    userId = userId,
                    username = username,
                    authViewModel = authViewModel,
                    appNavController = navController,
                    themeViewModel = themeViewModel
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}
