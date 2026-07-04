package com.kiriplatform.app.ui.theme

import androidx.compose.ui.graphics.Color

// Naukri Design System Colors
val NaukriNavy = Color(0xFF0F2C59) // Navy Primary
val NaukriCyan = Color(0xFF00A5EC) // Cyan Accent
val NaukriBackground = Color(0xFFF4F6F9) // Off-white canvas
val NaukriSurface = Color(0xFFFFFFFF) // White surface
val NaukriTextPrimary = Color(0xFF1E293B) // Dark slate for text
val NaukriTextSecondary = Color(0xFF64748B) // Slate gray for secondary text
val NaukriBorder = Color(0xFFE2E8F0) // Flat hairline border

// Notion Design System Colors mapped to Naukri colors
val NotionPrimary = NaukriNavy
val NotionPrimaryPressed = Color(0xFF0B2143)
val NotionPrimaryDeep = Color(0xFF081832)
val NotionOnPrimary = Color(0xFFFFFFFF)

val NotionBrandNavy = NaukriNavy
val NotionBrandNavyDeep = Color(0xFF081832)
val NotionBrandNavyMid = NaukriNavy

val NotionLinkBlue = NaukriCyan
val NotionLinkBluePressed = Color(0xFF0088C2)

val NotionBrandOrange = Color(0xFFE056FD)
val NotionBrandOrangeDeep = Color(0xFFBE2EDD)
val NotionBrandPink = Color(0xFFFF6B6B)
val NotionBrandPinkDeep = Color(0xFFEE5253)
val NotionBrandPurple = NaukriNavy
val NotionBrandPurple300 = Color(0xFF90A4AE)
val NotionBrandPurple800 = Color(0xFF081832)
val NotionBrandTeal = NaukriCyan
val NotionBrandGreen = Color(0xFF10B981)
val NotionBrandYellow = Color(0xFFFBBF24)
val NotionBrandBrown = Color(0xFF78350F)

// Card Tints
val NotionTintPeach = Color(0xFFF8FAFC)
val NotionTintRose = Color(0xFFF8FAFC)
val NotionTintMint = Color(0xFFECFDF5)
val NotionTintLavender = Color(0xFFF1F5F9)
val NotionTintSky = Color(0xFFEFF6FF)
val NotionTintYellow = Color(0xFFFEF3C7)
val NotionTintYellowBold = Color(0xFFFDE68A)
val NotionTintCream = Color(0xFFF8FAFC)
val NotionTintGray = Color(0xFFF1F5F9)

// Dark Card Tints
val NotionTintSkyDark = Color(0xFF10243D)
val NotionTintYellowDark = Color(0xFF2E2810)
val NotionTintMintDark = Color(0xFF0F2A1C)
val NotionTintLavenderDark = Color(0xFF241A3D)
val NotionTintRoseDark = Color(0xFF33101F)
val NotionTintPeachDark = Color(0xFF332008)

// Dark-mode text colors to pair with the tints above
val NotionOnTintDark = Color(0xFFF2F0EC)
val NotionOnTintDarkMuted = Color(0xFFC9C6C0)

// Surface
val NotionCanvas = NaukriBackground
val NotionSurface = NaukriSurface
val NotionSurfaceSoft = Color(0xFFF8FAFC)
val NotionHairline = NaukriBorder
val NotionHairlineSoft = Color(0xFFF1F5F9)
val NotionHairlineStrong = Color(0xFFCBD5E1)

// Text
val NotionInkDeep = Color(0xFF0F172A)
val NotionInk = NaukriTextPrimary
val NotionCharcoal = Color(0xFF334155)
val NotionSlate = NaukriTextSecondary
val NotionSteel = Color(0xFF64748B)
val NotionStone = Color(0xFF94A3B8)
val NotionMuted = Color(0xFFCBD5E1)
val NotionOnDark = Color(0xFFFFFFFF)
val NotionOnDarkMuted = Color(0xFF94A3B8)

// Semantic
val NotionSuccess = Color(0xFF10B981)
val NotionWarning = Color(0xFFF59E0B)
val NotionError = Color(0xFFEF4444)

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
