package lt.university.diabetescalculator.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Persimmon,
    onPrimary = PaperCard,
    primaryContainer = PersimmonSoft,
    onPrimaryContainer = Ink,
    secondary = Sage,
    onSecondary = PaperCard,
    secondaryContainer = SageSoft,
    onSecondaryContainer = Ink,
    tertiary = Plum,
    onTertiary = PaperCard,
    tertiaryContainer = PlumSoft,
    onTertiaryContainer = Ink,
    background = Paper,
    onBackground = Ink,
    surface = Paper,
    onSurface = Ink,
    surfaceVariant = PaperDeep,
    onSurfaceVariant = InkSoft,
    surfaceContainerLowest = PaperCard,
    surfaceContainerLow = PaperCard,
    surfaceContainer = PaperCard,
    surfaceContainerHigh = PaperDeep,
    surfaceContainerHighest = PaperDeep,
    inverseSurface = Ink,
    inverseOnSurface = Paper,
    inversePrimary = PersimmonBright,
    outline = InkFaint,
    outlineVariant = PaperDeep,
    error = StatusHigh
)

private val DarkColors = darkColorScheme(
    primary = PersimmonBright,
    onPrimary = Night,
    primaryContainer = DarkPersimmonContainer,
    onPrimaryContainer = NightText,
    secondary = SageSoft,
    onSecondary = Night,
    secondaryContainer = DarkSageContainer,
    onSecondaryContainer = NightText,
    tertiary = PlumSoft,
    onTertiary = Night,
    tertiaryContainer = DarkPlumContainer,
    onTertiaryContainer = NightText,
    background = Night,
    onBackground = NightText,
    surface = Night,
    onSurface = NightText,
    surfaceVariant = NightRaised,
    onSurfaceVariant = NightTextSoft,
    surfaceContainerLowest = NightCard,
    surfaceContainerLow = NightCard,
    surfaceContainer = NightCard,
    surfaceContainerHigh = NightRaised,
    surfaceContainerHighest = NightRaised,
    inverseSurface = NightText,
    inverseOnSurface = Night,
    inversePrimary = Persimmon,
    outline = NightTextSoft,
    outlineVariant = NightRaised,
    error = PersimmonBright
)

/**
 * Programos tema. Sąmoningai NEnaudojamos Android 12+ dinaminės spalvos —
 * programa turi savo atpažįstamą "užrašų knygutės" paletę, kuri nesikeičia
 * pagal telefono fono paveikslėlį.
 */
@Composable
fun DiabetesCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = (if (darkTheme) NightCard else Ink).toArgb()
            val insets = WindowCompat.getInsetsController(window, view)
            insets.isAppearanceLightStatusBars = !darkTheme
            insets.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
