package lt.university.diabetescalculator.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = SurfaceLight,
    primaryContainer = TealPrimaryLight,
    secondary = AccentBlue,
    surface = SurfaceLight,
    background = SurfaceLight
)

private val DarkColors = darkColorScheme(
    primary = TealPrimaryLight,
    onPrimary = SurfaceDark,
    primaryContainer = TealPrimaryDark,
    secondary = AccentBlue,
    surface = SurfaceDark,
    background = SurfaceDark
)

/**
 * Programos tema. Palaiko šviesų/tamsų rėžimą ir Android 12+ dinamines spalvas,
 * kai jos prieinamos, kitu atveju naudojama rankiniu būdu apibrėžta teal paletė.
 */
@Composable
fun DiabetesCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
