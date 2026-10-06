package lt.university.diabetescalculator.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import lt.university.diabetescalculator.navigation.AppDestination
import lt.university.diabetescalculator.ui.theme.Ink
import lt.university.diabetescalculator.ui.theme.InkFaint
import lt.university.diabetescalculator.ui.theme.NightCard
import lt.university.diabetescalculator.ui.theme.Paper
import lt.university.diabetescalculator.ui.theme.Persimmon
import androidx.compose.foundation.isSystemInDarkTheme

/** Tamsi "rašalo" juosta su persimonų spalvos pasirinkimo indikatoriumi. */
@Composable
private fun navContainerColor() = if (isSystemInDarkTheme()) NightCard else Ink

/**
 * Apačios navigacijos juosta kompaktiškiems ekranams (telefonai).
 * Naudojama, kai WindowWidthSizeClass == Compact.
 */
@Composable
fun BottomNavBar(current: AppDestination, onSelect: (AppDestination) -> Unit) {
    NavigationBar(containerColor = navContainerColor(), tonalElevation = 0.dp) {
        AppDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = destination == current,
                onClick = { onSelect(destination) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label, style = MaterialTheme.typography.labelLarge) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Paper,
                    selectedTextColor = Paper,
                    indicatorColor = Persimmon,
                    unselectedIconColor = InkFaint,
                    unselectedTextColor = InkFaint
                )
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
    NavigationRail(containerColor = navContainerColor()) {
        Spacer(Modifier.height(12.dp))
        AppDestination.entries.forEach { destination ->
            NavigationRailItem(
                selected = destination == current,
                onClick = { onSelect(destination) },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label, style = MaterialTheme.typography.labelLarge) },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = Paper,
                    selectedTextColor = Paper,
                    indicatorColor = Persimmon,
                    unselectedIconColor = InkFaint,
                    unselectedTextColor = InkFaint
                )
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun isCompactWidth(widthSizeClass: WindowWidthSizeClass): Boolean =
    widthSizeClass == WindowWidthSizeClass.Compact
