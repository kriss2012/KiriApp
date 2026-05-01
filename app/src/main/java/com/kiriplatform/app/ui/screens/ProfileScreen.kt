package com.kiriplatform.app.ui.screens

import com.kiriplatform.app.ui.components.ClickableUrlText

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.components.SectionHeader
import com.kiriplatform.app.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.models.UserDto
import com.kiriplatform.app.ui.viewmodels.ProfileState
import com.kiriplatform.app.ui.viewmodels.ProfileViewModel
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToEdit: () -> Unit = {},
    onNavigateToConnections: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    // Stable initial fetch — only re-runs when userId actually changes.
    // No DisposableEffect ON_RESUME observer to prevent infinite request loops.
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchProfile(context, userId)
        }
    }

    // Pull-to-refresh state (Material3 1.3.x API)
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        if (userId.isNotEmpty()) {
            viewModel.fetchProfile(context, userId)
        }
    }

    // Stop the indicator once the network call resolves
    LaunchedEffect(uiState) {
        if (uiState !is ProfileState.Loading) {
            isRefreshing = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgCream,
        bottomBar = { Spacer(Modifier.height(0.dp)) }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val state = uiState) {
                is ProfileState.Loading -> {
                    // Show spinner only on first load (no cached data visible yet)
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OrangePrimary
                    )
                }
                is ProfileState.Success -> {
                    ProfileContent(
                        user = state.user,
                        onNavigateToEdit = onNavigateToEdit,
                        onNavigateToConnections = onNavigateToConnections,
                        onNavigateToActivity = onNavigateToActivity,
                        onLogout = {
                            sessionManager.logout()
                            onLogout()
                        }
                    )
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchProfile(context, userId) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: UserDto,
    onNavigateToEdit: () -> Unit,
    onNavigateToConnections: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 100.dp) // Space for floating nav
    ) {
        item { 
            ProfileHeroSection(
                user = user, 
                onNavigateToEdit = onNavigateToEdit
            ) 
        }

        // New Resources Section with Clickable Links
        if (!user.website.isNullOrEmpty() || !user.githubUrl.isNullOrEmpty() || !user.linkedInUrl.isNullOrEmpty()) {
            item { SectionHeader(title = "Resources & Social", actionText = "", onActionClick = {}) }
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        user.website?.let {
                            ResourceRow("🌐 Website", it)
                        }
                        user.githubUrl?.let {
                            ResourceRow("💻 GitHub", it)
                        }
                        user.linkedInUrl?.let {
                            ResourceRow("🔗 LinkedIn", it)
                        }
                    }
                }
            }
        }
        
        // Digital Persona (AI-Generated)
        if (!user.digitalPersona.isNullOrEmpty()) {
            item { SectionHeader(title = "AI Digital Persona", actionText = "Live Analysis", onActionClick = {}) }
            item {
                Card(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = user.digitalPersona,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Generated by KIRI AI based on your activities.",
                            style = MaterialTheme.typography.labelSmall,
                            color = OrangePrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item { SectionHeader(title = "My ASG Dashboard", actionText = "", onActionClick = {}) }
        item { 
            DashboardMenu(
                onNavigateToConnections = onNavigateToConnections,
                onNavigateToActivity = onNavigateToActivity
            ) 
        }
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp, 24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Secure Logout", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Composable
fun ResourceRow(label: String, url: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        ClickableUrlText(
            text = url,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

@Composable
fun ProfileHeroSection(
    user: UserDto, 
    onNavigateToEdit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp, 0.dp, 32.dp, 32.dp))
            .background(Brush.linearGradient(listOf(OrangePrimary, Color(0xFFD94D08))))
            .padding(24.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(user.fullName.take(1).uppercase(), style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Black)
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.fullName, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black)
                    }
                    Text(user.role, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.8f))
                    if (!user.department.isNullOrEmpty()) {
                        Text(user.department, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
                    }
                }
                
                IconButton(onClick = onNavigateToEdit) {
                    Icon(
                        Icons.Default.Edit, 
                        contentDescription = "Edit Profile", 
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Real Stats Row (Permanent Fix)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBox(user.eventsCount.toString(), "Events")
                StatBox(user.connectionsCount.toString(), "Connections")
                StatBox(user.points.toString(), "Score")
            }
        }
    }
}

@Composable
fun RowScope.StatBox(value: String, label: String) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.65f), fontWeight = FontWeight.SemiBold, fontSize = 8.sp)
        }
    }
}

@Composable
fun DashboardMenu(
    onNavigateToConnections: () -> Unit,
    onNavigateToActivity: () -> Unit
) {
    val menuItems = listOf(
        DashboardMenuItem("🚀", "My Startup Profile", OrangeLight, "Coming Soon"),
        DashboardMenuItem("🤝", "Team Requests", GreenLight),
        DashboardMenuItem("📋", "My Community Activity", BlueInfo)
    )

    Column(modifier = Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        if (menuItems.isEmpty()) {
            Text("No dashboard items available", modifier = Modifier.padding(16.dp), color = TextSecondary)
        } else {
            menuItems.forEach { item ->
                DashboardMenuCard(
                    item = item,
                    onClick = {
                        when (item.label) {
                            "Team Requests" -> onNavigateToConnections()
                            "My Community Activity" -> onNavigateToActivity()
                        }
                    }
                )
            }
        }
    }
}

data class DashboardMenuItem(val icon: String, val label: String, val color: Color, val badge: String? = null)

@Composable
fun DashboardMenuCard(item: DashboardMenuItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.color),
                contentAlignment = Alignment.Center
            ) {
                Text(item.icon, fontSize = 13.sp)
            }
            Text(item.label, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 11.sp)
            
            if (item.badge != null) {
                Surface(color = OrangeLight, shape = RoundedCornerShape(5.dp)) {
                    Text(item.badge, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                }
            } else {
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFCCCCCC))
            }
        }
    }
}

