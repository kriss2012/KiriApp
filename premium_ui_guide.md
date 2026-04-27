# Kiri Store Premium UI Implementation Guide

This guide details the architecture and implementation of the premium UI system found in Kiri Store, including advanced theme switching, "Liquid Glass" effects, and expressive animations. Use this as a blueprint to port these features to other apps (like ASGApp).

## 1. Advanced Theme System Architecture

The theme system is built on **Material 3 Expressive**, allowing for dynamic personality shifts through multiple color schemes and motion behaviors.

### Key Components:
- **`AppTheme` (Enum)**: Defines available themes (OCEAN, PURPLE, FOREST, SLATE, AMBER).
- **`KiriStoreTheme` (Composable)**: The entry point that wraps the entire app. It handles:
  - Dark/Light Mode selection.
  - Amoled (Pure Black) backgrounds.
  - Dynamic Color (Android 12+) integration.
  - Expressive Motion Schemes.

### Implementation:
```kotlin
@Composable
fun KiriStoreTheme(
    isDarkTheme: Boolean = false,
    appTheme: AppTheme = AppTheme.OCEAN,
    isAmoledTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val baseColorScheme = if (isDarkTheme) appTheme.darkScheme else appTheme.lightScheme
    
    val colorScheme = if (isDarkTheme && isAmoledTheme) {
        baseColorScheme.toAmoled()
    } else {
        baseColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
```

---

## 2. Premium UI Effects

### A. Liquid Glass (Glassmorphism)
The "Liquid Glass" effect creates a frosted glass look with real-time refraction and saturation shifts.

**Implementation Details:**
- **Library**: Uses `io.github.fletchmckee.liquid` for the frost effect.
- **Modifier**: `Modifier.glassmorphism()`
- **Mechanism**:
  - Clips to a rounded shape.
  - Adds a semi-transparent surface background.
  - Applies a `liquid` shader with `frost`, `refraction`, and `dispersion` parameters.
  - Adds a vertical gradient border to simulate light reflection on edges.

```kotlin
fun Modifier.glassmorphism(enabled: Boolean = true): Modifier {
    if (!enabled) return this.border(...) // Fallback
    
    return this
        .clip(RoundedCornerShape(32.dp))
        .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.15f))
        .liquid(liquidState) {
            this.frost = 16.dp
            this.refraction = 0.1f
            this.saturation = 0.7f
        }
        .border(...)
}
```

### B. Shimmer Animation
A custom shimmer effect used for loading states, providing a "skeleton" preview that feels alive.

**Mechanism:**
- Uses `InfiniteTransition` to animate a `Float` from 0 to 1500.
- Applies a `LinearGradient` brush that shifts its `start` and `end` offsets based on the animation.

---

## 3. Dynamic Theme Switching

The theme is not static; it responds to user actions via a central state.

### State Management (MainViewModel):
```kotlin
data class MainState(
    val currentColorTheme: AppTheme = AppTheme.OCEAN,
    val isDarkTheme: Boolean? = null, // null follows system
    val isAmoledTheme: Boolean = false,
    val isLiquidGlassEnabled: Boolean = true
)
```

### Persistence:
Settings are persisted using **DataStore** or a similar repository, allowing the theme to remain consistent across app launches.

---

## 4. Porting to Other Apps (Step-by-Step)

To implement this in **ASGApp**:

1.  **Dependency Setup**: Add the `liquid` library for shaders and ensure Material 3 Expressive APIs are enabled.
2.  **Define Color Schemes**: Create a `Color.kt` with sets of colors for each theme personality (Primary, Secondary, Surface, etc.).
3.  **Upgrade `Theme.kt`**: Replace the basic `MaterialTheme` with a dynamic wrapper that accepts `AppTheme` as a parameter.
4.  **Create Utility Modifiers**: Copy the `glassmorphism` and `shimmer` modifiers to a `utils` package.
5.  **Global State**: Update the `MainViewModel` to expose the current theme state from a settings repository.
6.  **Apply Modifiers**: Replace standard `Card` or `Surface` components with those using `.glassmorphism()` or `.premiumGradient()`.

---

## 5. Visual Hierarchy & Animations
- **Expressive Motion**: Use `MotionScheme.expressive()` to make transitions feel fluid and bouncy.
- **Depth**: Use varying levels of glassmorphism alpha to create a sense of layers (e.g., Background -> Glass Card -> Floating Button).
- **Micro-interactions**: Use `isLiquidGlassEnabled` as a global flag to toggle effects on/off for performance or user preference.
