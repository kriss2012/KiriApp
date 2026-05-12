package com.kiriplatform.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(6.dp),
    medium = RoundedCornerShape(8.dp),  // Buttons, inputs (Notion's md)
    large = RoundedCornerShape(12.dp),  // Cards (Notion's lg)
    extraLarge = RoundedCornerShape(16.dp)
)
