package lt.university.diabetescalculator.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(val route: String, val label: String, val icon: ImageVector) {
    CALCULATOR("calculator", "Skaičiuoklė", Icons.Filled.Calculate),
    HISTORY("history", "Istorija", Icons.Filled.List),
    INFO("info", "Info", Icons.Filled.Info)
}
