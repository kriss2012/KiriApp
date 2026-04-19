package com.apex.asg.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.ui.graphics.vector.ImageVector
// Trailing imports will be handled by Kotlin compiler or Icons.Default

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object AIAgent : Screen("ai_agent", "Kiri AI", Icons.Default.SmartToy)
    object Repository : Screen("repository", "Community", Icons.Default.Group)
    object Events : Screen("events", "Events", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object EditProfile : Screen("edit_profile", "Edit Profile")
    object Notifications : Screen("notifications", "Notifications")
    
    // Sub-screens
    object PublicProfile : Screen("public_profile/{userId}") {
        fun createRoute(userId: String) = "public_profile/$userId"
    }
    object Connections : Screen("connections", "Connections", Icons.Default.Link)
    object Chat : Screen("chat", "Chat")
    object Jobs : Screen("jobs", "Jobs")
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Search,
    Screen.AIAgent,
    Screen.Repository,
    Screen.Profile
)
