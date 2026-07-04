package com.kiriplatform.app.ui.theme

import androidx.compose.ui.graphics.Color

// Notion Design System Colors (from DESIGN.md)
val NotionPrimary = Color(0xFF5645D4)
val NotionPrimaryPressed = Color(0xFF4534B3)
val NotionPrimaryDeep = Color(0xFF3A2A99)
val NotionOnPrimary = Color(0xFFFFFFFF)

val NotionBrandNavy = Color(0xFF0A1530)
val NotionBrandNavyDeep = Color(0xFF070F24)
val NotionBrandNavyMid = Color(0xFF1A2A52)

val NotionLinkBlue = Color(0xFF0075DE)
val NotionLinkBluePressed = Color(0xFF005BAB)

val NotionBrandOrange = Color(0xFFDD5B00)
val NotionBrandOrangeDeep = Color(0xFF793400)
val NotionBrandPink = Color(0xFFFF64C8)
val NotionBrandPinkDeep = Color(0xFFA02E6D)
val NotionBrandPurple = Color(0xFF7B3FF2)
val NotionBrandPurple300 = Color(0xFFD6B6F6)
val NotionBrandPurple800 = Color(0xFF391C57)
val NotionBrandTeal = Color(0xFF2A9D99)
val NotionBrandGreen = Color(0xFF1AAE39)
val NotionBrandYellow = Color(0xFFF5D75E)
val NotionBrandBrown = Color(0xFF523410)

// Card Tints (Light mode)
val NotionTintPeach = Color(0xFFFFE8D4)
val NotionTintRose = Color(0xFFFDE0EC)
val NotionTintMint = Color(0xFFD9F3E1)
val NotionTintLavender = Color(0xFFE6E0F5)
val NotionTintSky = Color(0xFFDCECFA)
val NotionTintYellow = Color(0xFFFEF7D6)
val NotionTintYellowBold = Color(0xFFF9E79F)
val NotionTintCream = Color(0xFFF8F5E8)
val NotionTintGray = Color(0xFFF0EEEC)

// Card Tints (Dark mode) - desaturated/darkened counterparts so cards
// no longer stay bright pastel when the app is in dark theme.
val NotionTintPeachDark = Color(0xFF332008)
val NotionTintRoseDark = Color(0xFF33101F)
val NotionTintMintDark = Color(0xFF0F2A1C)
val NotionTintLavenderDark = Color(0xFF241A3D)
val NotionTintSkyDark = Color(0xFF10243D)
val NotionTintYellowDark = Color(0xFF2E2810)

// Dark-mode text colors to pair with the tints above (replace NotionCharcoal /
// NotionBrandPurple800 / etc. which are dark-only colors that vanish on dark cards)
val NotionOnTintDark = Color(0xFFF2F0EC)
val NotionOnTintDarkMuted = Color(0xFFC9C6C0)

// Surface
val NotionCanvas = Color(0xFFFFFFFF)
val NotionSurface = Color(0xFFF6F5F4)
val NotionSurfaceSoft = Color(0xFFFAFAF9)
val NotionHairline = Color(0xFFE5E3DF)
val NotionHairlineSoft = Color(0xFFEDE9E4)
val NotionHairlineStrong = Color(0xFFC8C4BE)

// Text
val NotionInkDeep = Color(0xFF000000)
val NotionInk = Color(0xFF1A1A1A)
val NotionCharcoal = Color(0xFF37352F)
val NotionSlate = Color(0xFF5D5B54)
val NotionSteel = Color(0xFF787671)
val NotionStone = Color(0xFFA4A097)
val NotionMuted = Color(0xFFBBB8B1)
val NotionOnDark = Color(0xFFFFFFFF)
val NotionOnDarkMuted = Color(0xFFA4A097)

// Semantic
val NotionSuccess = Color(0xFF1AAE39)
val NotionWarning = Color(0xFFDD5B00)
val NotionError = Color(0xFFE03131)

// Amoled (Retained from original)
val AmoledBlack = Color(0xFF000000)
val AmoledSurface = Color(0xFF050505)

// Legacy aliases for backwards compatibility
val BgCream = NotionTintCream
val GreenSuccess = NotionSuccess
val GreenLight = NotionTintMint
val OrangePrimary = NotionBrandOrange
val TextSecondary = NotionSlate
val TextPrimary = NotionInk
