package lt.university.diabetescalculator.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import lt.university.diabetescalculator.navigation.AppDestination

/**
 * Apačios navigacijos juosta kompaktiškiems ekranams (telefonai).
 * Naudojama, kai WindowWidthSizeClass == Compact.
 */
@Composable
fun BottomNavBar(current: AppDestination, onSelect: (AppDestination) -> Unit) {
    NavigationBar {
        AppDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == current,
                onClick = { onSelect(destination) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) }
            )
        }
    }
}

/**
 * Šoninis navigacijos skydelis platesniems ekranams (planšetės, sulankstomi,
 * horizontali orientacija). Naudojama, kai WindowWidthSizeClass != Compact —
 * tai realizuoja "kintami ekrano dydžiai" (adaptyvus UI) bonus reikalavimą.
 */
@Composable
fun SideNavRail(current: AppDestination, onSelect: (AppDestination) -> Unit) {
    NavigationRail {
        AppDestination.entries.forEach { destination ->
            NavigationRailItem(
                selected = destination == current,
                onClick = { onSelect(destination) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun isCompactWidth(widthSizeClass: WindowWidthSizeClass): Boolean =
    widthSizeClass == WindowWidthSizeClass.Compact
