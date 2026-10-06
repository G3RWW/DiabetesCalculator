package lt.university.diabetescalculator

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import lt.university.diabetescalculator.navigation.AppDestination
import lt.university.diabetescalculator.navigation.AppNavHost
import lt.university.diabetescalculator.ui.components.AppTopBar
import lt.university.diabetescalculator.ui.components.BottomNavBar
import lt.university.diabetescalculator.ui.components.SideNavRail
import lt.university.diabetescalculator.ui.components.isCompactWidth
import lt.university.diabetescalculator.viewmodel.DiabetesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScaffold(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val viewModel: DiabetesViewModel = viewModel()
    val snackbarHostState = remember { SnackbarHostState() }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    val current = AppDestination.entries.firstOrNull { dest ->
        currentDestination?.hierarchy?.any { it.route == dest.route } == true
    } ?: AppDestination.CALCULATOR

    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeSnackbarMessage()
        }
    }

    fun navigateTo(destination: AppDestination) {
        navController.navigate(destination.route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val compact = isCompactWidth(windowSizeClass.widthSizeClass)

    Scaffold(
        topBar = { AppTopBar(currentScreenTitle = current.label) },
        bottomBar = {
            if (compact) BottomNavBar(current = current, onSelect = ::navigateTo)
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        }
    ) { innerPadding ->
        Row(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (!compact) {
                SideNavRail(current = current, onSelect = ::navigateTo)
            }
            AppNavHost(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
