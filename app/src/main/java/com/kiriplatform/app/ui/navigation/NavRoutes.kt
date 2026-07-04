package com.kiriplatform.app.ui.navigation

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
    object Chats : Screen("chats", "Chats", Icons.Default.Chat)
    object Network : Screen("network", "Network", Icons.Default.People)
    object Events : Screen("events", "Events", Icons.Default.DateRange)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object EditProfile : Screen("edit_profile", "Edit Profile")
    object Notifications : Screen("notifications", "Notifications")
    
    // Sub-screens
    object PublicProfile : Screen("public_profile/{userId}") {
        fun createRoute(userId: String) = "public_profile/$userId"
    }
    object Connections : Screen("connections", "Connections", Icons.Default.Link)
    object Chat : Screen("chat/{receiverId}") {
        fun createRoute(receiverId: String) = "chat/$receiverId"
    }
    object AIAgent : Screen("ai_agent", "Kiri AI", Icons.Default.AutoAwesome)
    object Jobs : Screen("jobs", "Jobs")
    object AddEvent : Screen("add_event", "Add Event")
    object EventDetails : Screen("event_details/{eventJson}") {
        fun createRoute(eventJson: String) = "event_details/${java.net.URLEncoder.encode(eventJson, "UTF-8")}"
    }
    object InnovationHub : Screen("innovation_hub", "Innovation Hub")
    object MindsetDiscovery : Screen("mindset_discovery", "Mindset Discovery")
    object LiveInput : Screen("live_input", "AI Persona")
    object CommitteeManagement : Screen("committee_management", "Committee")
    
    // Innovation Hub routes
    object Marketplace : Screen("marketplace", "Marketplace")
    object Matchmaker : Screen("matchmaker", "Matchmaker")
    object Mentorship : Screen("mentorship", "Mentorship")
    object Investor : Screen("investor", "Investor Intel")
    object Vault : Screen("vault", "IP Vault")
    object Organization : Screen("organization", "Kiri Organization")
    object Admin : Screen("admin", "System Portal")
    object MarketTrends : Screen("market_trends", "Market Intelligence")
    object ResumeBuilder : Screen("resume_builder", "Resume Builder")
    object Badges : Screen("badges", "Badges")
    object ProjectShowcase : Screen("project_showcase", "Project Showcase")
    object Leaderboard : Screen("leaderboard", "Leaderboard")
    object InterviewSandbox : Screen("interview_sandbox", "Interview Sandbox")
    
    // Bottom Bar Screens
    object Prepare : Screen("prepare", "Prepare", Icons.Default.Book)
    object Participate : Screen("participate", "Participate", Icons.Default.HowToReg)
    object Opportunities : Screen("opportunities", "Opportunities", Icons.Default.Work)
}

val BottomNavItems = listOf(
    Screen.Home,
    Screen.Prepare,
    Screen.Participate,
    Screen.Opportunities
)
