# KiriApp — Dark/Light Mode & Insets Audit (v2 branch)

## Scope of this pass
Your brief covers 12 categories (build errors, full UI redesign, responsiveness,
navigation unification, theming, alignment, performance, animation, accessibility,
architecture, error handling, testing). That's a multi-week team effort, and several
parts of it (Gradle build verification, runtime testing, emulator/device checks) need
an actual Android build environment — I don't have the Android SDK or a device/emulator
here, so I can't compile the project or claim "zero errors/zero warnings" honestly.

What I *can* do reliably from source alone: find every place a real, reproducible bug
pattern repeats, and fix it consistently. That's what this batch is — a repo-wide sweep
for the two confirmed bug classes from your screenshots (dark-mode color bugs, and the
navigation-insets bug), applied everywhere they occur, not just on the Home screen.

## Files changed

| File | Bug | Fix |
|---|---|---|
| `ui/theme/Color.kt` | No dark-mode counterparts for pastel card tints | Added `*Dark` variants + `NotionOnTintDark(Muted)` text colors |
| `ui/screens/HomeScreen.kt` | 7 cards hardcoded to light-only tints; rank number/badge invisible in dark mode; no status-bar inset | Theme-aware tint switching, higher-contrast accent color, `.statusBarsPadding()` |
| `ui/navigation/MainScaffold.kt` | `contentWindowInsets` zeroed, so only the floating nav pill got real insets | Removed the override so screen content gets real system-bar insets |
| `ui/screens/InnovationPitchesScreen.kt` | Category chip hardcoded to light tint | Theme-aware tint |
| `ui/screens/ChatScreen.kt` | System-message bubble hardcoded to light tint | Theme-aware tint |
| `ui/screens/SplashScreen.kt` | Badge pill hardcoded to light tint | Theme-aware tint |
| `ui/screens/EventDetailsScreen.kt` | Event type badge hardcoded to light tint | Theme-aware tint |
| `ui/screens/ProfileScreen.kt` | "SECURE LOGOUT" text hardcoded dark-gray on themed background | Switched to `MaterialTheme.colorScheme.onSurface` |
| `ui/screens/RegisterScreen.kt` | Two body-text lines hardcoded dark-gray on themed background | Switched to `MaterialTheme.colorScheme.onSurface` |

Apply via `git apply *.patch` from the repo root, or drop the `.kt` files in directly.

## What I did NOT touch (and why)
- **Build errors / dependency conflicts / deprecated APIs**: needs an actual Gradle
  sync + build log to diagnose real compile errors — I can't invent fixes for errors
  I can't reproduce.
- **Full Naukri-style visual redesign** (typography, spacing, animations, empty states):
  this is genuine design/build work across every screen — happy to do it screen by
  screen if you tell me which ones to prioritize, since doing all of it blind risks
  breaking things that already work.
- **Navigation architecture unification** (gesture vs bottom-nav approaches): I found
  and fixed the specific insets bug causing the glitch you saw, but a full nav-stack
  audit (back-stack handling, deep links, per-tab state restoration) needs the actual
  `NavGraph.kt` routes traced against real navigation events — worth a dedicated pass.
- **Performance, accessibility, testing, architecture refactor**: real, valuable work,
  but each needs its own focused session with actual profiling/build tools rather than
  a blind pass.

## Suggested next step
Pick 1–2 items from the list above (e.g. "just the Chats screen visual redesign" or
"paste me your actual Gradle build error log") and I'll go deep on that, verified
against real source, the same way I did here — rather than a shallow pass across
everything at once.
