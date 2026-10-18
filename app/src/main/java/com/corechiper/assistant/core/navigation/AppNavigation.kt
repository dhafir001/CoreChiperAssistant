package com.corechiper.assistant.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.corechiper.assistant.ui.MainViewModel
import com.corechiper.assistant.ui.explore.ExploreScreen
import com.corechiper.assistant.ui.home.HomeScreen
import com.corechiper.assistant.ui.insights.InsightsScreen
import com.corechiper.assistant.ui.modules.document.DocumentScreen
import com.corechiper.assistant.ui.modules.emergency.EmergencyScreen
import com.corechiper.assistant.ui.modules.finance.FinanceScreen
import com.corechiper.assistant.ui.modules.health.HealthScreen
import com.corechiper.assistant.ui.modules.learning.LearningScreen
import com.corechiper.assistant.ui.modules.productivity.ProductivityScreen
import com.corechiper.assistant.ui.profile.ProfileScreen
import com.corechiper.assistant.ui.quickadd.QuickAddScreen

data class BottomNavItem(val route: String, val labelId: String, val labelEn: String, val icon: ImageVector)

@Composable
fun CoreChiperMainScaffold(vm: MainViewModel) {
    val navController = rememberNavController()
    val prefs by vm.preferences.collectAsState()
    val statusMsg by vm.statusMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(statusMsg) {
        statusMsg?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearStatusMessage()
        }
    }

    val bottomItems = listOf(
        BottomNavItem("home", "Beranda", "Home", Icons.Default.Home),
        BottomNavItem("explore", "Jelajah", "Explore", Icons.Default.GridView),
        BottomNavItem("quick_add", "Tambah", "Quick Add", Icons.Default.AddCircle),
        BottomNavItem("insights", "Statistik", "Insights", Icons.Default.Insights),
        BottomNavItem("profile", "Profil", "Profile", Icons.Default.Person)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (currentRoute in bottomItems.map { it.route }) {
                NavigationBar {
                    bottomItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.labelId) },
                            label = { Text(if (prefs.languageCode == "en") item.labelEn else item.labelId) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(vm = vm, onNavigateToModule = { navController.navigate(it) })
            }
            composable("explore") {
                ExploreScreen(onNavigateToModule = { navController.navigate(it) })
            }
            composable("quick_add") {
                QuickAddScreen(vm = vm, onNavigateToModule = { navController.navigate(it) })
            }
            composable("insights") {
                InsightsScreen(vm = vm)
            }
            composable("profile") {
                ProfileScreen(vm = vm)
            }
            composable("module_health") {
                HealthScreen(vm = vm, onBack = { navController.popBackStack() })
            }
            composable("module_finance") {
                FinanceScreen(vm = vm, onBack = { navController.popBackStack() })
            }
            composable("module_productivity") {
                ProductivityScreen(vm = vm, onBack = { navController.popBackStack() })
            }
            composable("module_document") {
                DocumentScreen(vm = vm, onBack = { navController.popBackStack() })
            }
            composable("module_learning") {
                LearningScreen(vm = vm, onBack = { navController.popBackStack() })
            }
            composable("module_emergency") {
                EmergencyScreen(vm = vm, onBack = { navController.popBackStack() })
            }
        }
    }
}
