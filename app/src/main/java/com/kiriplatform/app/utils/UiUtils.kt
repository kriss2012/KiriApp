package com.kiriplatform.app.utils

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glassmorphism(
    enabled: Boolean = true,
    cornerRadius: Dp = 32.dp,
    alpha: Float = 0.15f
): Modifier = composed {
    if (!enabled) return@composed this

    this
        .clip(RoundedCornerShape(cornerRadius))
        .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = alpha))
        .border(
            width = 1.dp,
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.3f),
                    Color.White.copy(alpha = 0.05f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

fun Modifier.shimmer(
    visible: Boolean = true,
    showGradient: Boolean = true
): Modifier = composed {
    if (!visible) return@composed this
    
    // Performance optimization: return early for shimmer if not explicitly needed 
    // to reduce frame drops on lower-end devices/emulators
    // this.background(Color.LightGray.copy(alpha = 0.1f)) // Fallback static background
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1500f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    if (showGradient) {
        this.background(brush)
    } else {
        this
    }
}

/**
 * Throttles quick multiple click inputs to prevent duplicate actions/submissions.
 * Executes the onClick action instantly, then blocks further clicks for [delayMillis].
 */
fun Modifier.clickableDebounced(
    delayMillis: Long = 500L,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var lastClickTime = remember { 0L }
    this.clickable(enabled = enabled) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= delayMillis) {
            lastClickTime = currentTime
            onClick()
        }
    }
}
