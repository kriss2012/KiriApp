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
    object Repository : Screen("repository", "Repository", Icons.Default.Group)
    object AIAgent : Screen("ai_agent", "AI Agent", Icons.Default.SmartToy)
    object Events : Screen("events", "Events", Icons.Default.CalendarMonth)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    
    // Sub-screens
    object HackathonOrganizer : Screen("hackathon_organizer")
    object NAACRecords : Screen("naac_records")
    object DistrictMap : Screen("district_map")
    object Chat : Screen("chat", "Chat")
    object Jobs : Screen("jobs", "Jobs")
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Repository,
    Screen.AIAgent,
    Screen.Events,
    Screen.Profile
)
