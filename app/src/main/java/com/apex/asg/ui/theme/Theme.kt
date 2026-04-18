package com.apex.asg.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ASGColorScheme = lightColorScheme(
    primary = OrangePrimary,
    onPrimary = BgCream,
    primaryContainer = OrangeLight,
    onPrimaryContainer = OrangeDark,
    secondary = PurpleAccent,
    onSecondary = BgCream,
    secondaryContainer = PurpleLight,
    onSecondaryContainer = PurpleAccent,
    background = BgCream,
    onBackground = TextPrimary,
    surface = BgSurface,
    onSurface = TextPrimary,
    outline = BorderColor,
    surfaceVariant = OrangeLight,
    onSurfaceVariant = OrangeDark
)

@Composable
fun ASGAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is disabled to strictly follow the ASG brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = ASGColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
