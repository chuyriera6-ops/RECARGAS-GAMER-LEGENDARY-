package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val FFDarkColorScheme = darkColorScheme(
    primary = FlameOrange,
    onPrimary = TextPrimary,
    primaryContainer = FlameOrangeVariant,
    onPrimaryContainer = TextPrimary,
    secondary = DiamondCyan,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceHighlight,
    onSecondaryContainer = DiamondCyan,
    tertiary = GoldCoin,
    onTertiary = DarkBackground,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkSurfaceHighlight,
    error = StatusRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Gaming app defaults to dark gaming aesthetics
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = FFDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkBackground.toArgb()
                window.navigationBarColor = DarkBackground.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
