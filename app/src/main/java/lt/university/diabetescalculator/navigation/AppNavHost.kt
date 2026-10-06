package lt.university.diabetescalculator.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import lt.university.diabetescalculator.ui.screens.CalculatorScreen
import lt.university.diabetescalculator.ui.screens.HistoryScreen
import lt.university.diabetescalculator.ui.screens.InfoScreen
import lt.university.diabetescalculator.viewmodel.DiabetesViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: DiabetesViewModel,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.CALCULATOR.route,
        modifier = modifier,
        enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(220)) { it / 6 } },
        exitTransition = { fadeOut(tween(150)) },
        popEnterTransition = { fadeIn(tween(220)) },
        popExitTransition = { fadeOut(tween(150)) + slideOutHorizontally(tween(150)) { it / 6 } }
    ) {
        composable(AppDestination.CALCULATOR.route) {
            CalculatorScreen(viewModel = viewModel)
        }
        composable(AppDestination.HISTORY.route) {
            HistoryScreen(viewModel = viewModel)
        }
        composable(AppDestination.INFO.route) {
            InfoScreen(viewModel = viewModel)
        }
    }
}
