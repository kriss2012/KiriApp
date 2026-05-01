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
    OCEAN(
        lightScheme = lightColorScheme(primary = OceanPrimary, secondary = OceanSecondary, background = BgCream),
        darkScheme = darkColorScheme(primary = OceanPrimary, secondary = OceanSecondary, background = Color(0xFF0A192F))
    ),
    PURPLE(
        lightScheme = lightColorScheme(primary = PurplePremium, secondary = PurpleSecondary, background = BgCream),
        darkScheme = darkColorScheme(primary = PurplePremium, secondary = PurpleSecondary, background = Color(0xFF1A0B2E))
    ),
    FOREST(
        lightScheme = lightColorScheme(primary = ForestPrimary, secondary = ForestSecondary, background = BgCream),
        darkScheme = darkColorScheme(primary = ForestPrimary, secondary = ForestSecondary, background = Color(0xFF0D1F17))
    ),
    SLATE(
        lightScheme = lightColorScheme(primary = SlatePrimary, secondary = SlateSecondary, background = BgCream),
        darkScheme = darkColorScheme(primary = SlatePrimary, secondary = SlateSecondary, background = Color(0xFF1B1B2F))
    ),
    AMBER(
        lightScheme = lightColorScheme(primary = AmberPrimary, secondary = AmberSecondary, background = BgCream),
        darkScheme = darkColorScheme(primary = AmberPrimary, secondary = AmberSecondary, background = Color(0xFF1F1A0D))
    )
}

fun ColorScheme.toAmoled(): ColorScheme {
    return this.copy(
        background = AmoledBlack,
        surface = AmoledSurface,
        surfaceContainer = AmoledSurface
    )
}

private object SafeIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return object : Modifier.Node() {}
    }
    
    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = System.identityHashCode(this)
}

@Composable
fun KiriAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    appTheme: AppTheme = AppTheme.PURPLE,
    isAmoledTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseColorScheme = if (darkTheme) appTheme.darkScheme else appTheme.lightScheme
    
    val colorScheme = if (darkTheme && isAmoledTheme) {
        baseColorScheme.toAmoled()
    } else {
        baseColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity
            activity?.window?.let { window ->
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    // Material 3 Expressive Theme logic
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        // Force use of a safe Indication (IndicationNodeFactory) to prevent crash in Compose 1.7+
        // This bypasses the strict check for legacy indications while version skew is present.
        CompositionLocalProvider(
            LocalIndication provides SafeIndication,
            content = content
        )
    }
}
