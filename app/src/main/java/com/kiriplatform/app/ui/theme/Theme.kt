package com.kiriplatform.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.LocalIndication
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppTheme(
    val lightScheme: ColorScheme,
    val darkScheme: ColorScheme
) {
    NOTION(
        lightScheme = lightColorScheme(
            primary = NotionPrimary,
            onPrimary = NotionOnPrimary,
            primaryContainer = NotionTintLavender,
            onPrimaryContainer = NotionBrandPurple800,
            secondary = NotionLinkBlue,
            onSecondary = NotionOnPrimary,
            background = NotionCanvas,
            onBackground = NotionInk,
            surface = NotionSurface,
            onSurface = NotionInk,
            surfaceVariant = NotionSurfaceSoft,
            onSurfaceVariant = NotionCharcoal,
            outline = NotionHairlineStrong,
            outlineVariant = NotionHairline,
            error = NotionError,
            onError = NotionOnPrimary
        ),
        darkScheme = darkColorScheme(
            primary = NotionLinkBlue,
            onPrimary = NotionPrimaryDeep,
            primaryContainer = NotionBrandPurple800,
            onPrimaryContainer = NotionBrandPurple300,
            secondary = NotionLinkBlue,
            onSecondary = NotionOnPrimary,
            background = NotionBrandNavyDeep,
            onBackground = NotionOnDark,
            surface = NotionBrandNavy,
            onSurface = NotionOnDark,
            surfaceVariant = NotionBrandNavyMid,
            onSurfaceVariant = NotionOnDarkMuted,
            outline = NotionStone,
            outlineVariant = NotionSlate,
            error = NotionError,
            onError = NotionOnPrimary
        )
    )
}

@Composable
fun KiriAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.NOTION,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) appTheme.darkScheme else appTheme.lightScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes
    ) {
        // Force use of a safe Indication (IndicationNodeFactory) to prevent crash in Compose 1.7+
        // This bypasses the strict check for legacy indications while version skew is present.
        CompositionLocalProvider(
            LocalIndication provides ripple(),
            content = content
        )
    }
}
