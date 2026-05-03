package com.kiriplatform.app.ui.screens

import com.kiriplatform.app.ui.components.ClickableUrlText

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
        containerColor = MaterialTheme.colorScheme.background,
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
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
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
                        Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchProfile(context, userId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
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
            .padding(bottom = 100.dp) // Space for floating nav
    ) {
        item { 
            ProfileHeroSection(
                user = user, 
                onNavigateToEdit = onNavigateToEdit
            ) 
        }

        // Bio / About Section
        if (!user.bio.isNullOrEmpty()) {
            item { SectionHeader(title = "About", actionText = "", onActionClick = {}) }
            item {
                Text(
                    text = user.bio,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Skills / Services Section
        if (!user.services.isNullOrEmpty()) {
            item { SectionHeader(title = "Expertise & Services", actionText = "", onActionClick = {}) }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    user.services.forEach { service ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Text(
                                text = service.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        // Resources & Social Section
        
        // Digital Persona (AI-Generated)
        if (!user.digitalPersona.isNullOrEmpty()) {
            item { SectionHeader(title = "AI Digital Persona", actionText = "", onActionClick = {}) }
            item {
                Surface(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
                    shape = MaterialTheme.shapes.large, // 12dp
                    color = NotionTintYellow,
                    border = BorderStroke(1.dp, NotionHairline)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "ANALYSIS",
                            style = MaterialTheme.typography.labelSmall,
                            color = NotionCharcoal,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = user.digitalPersona,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NotionCharcoal,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                modifier = Modifier.size(6.dp),
                                shape = CircleShape,
                                color = NotionPrimary
                            ) {}
                            Text(
                                "Generated by Kiri Intelligence",
                                style = MaterialTheme.typography.labelSmall,
                                color = NotionCharcoal.copy(alpha = 0.4f),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item { SectionHeader(title = "Kiri Dashboard", actionText = "", onActionClick = {}) }
        item { 
            DashboardMenu(
                onNavigateToConnections = onNavigateToConnections,
                onNavigateToActivity = onNavigateToActivity
            ) 
        }
        item {
            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .height(44.dp),
                border = BorderStroke(1.dp, NotionHairline),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    "SECURE LOGOUT", 
                    color = NotionCharcoal.copy(alpha = 0.6f), 
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}


@Composable
fun ResourceRow(label: String, url: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        ClickableUrlText(
            text = url,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            linkColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ProfileHeroSection(
    user: UserDto, 
    onNavigateToEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Avatar - Notion style: simple, clean
            Surface(
                modifier = Modifier.size(72.dp),
                shape = MaterialTheme.shapes.medium, // 8dp
                color = NotionCanvas,
                border = BorderStroke(1.dp, NotionHairline)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        user.fullName.take(1).uppercase(),
                        style = MaterialTheme.typography.displaySmall,
                        color = NotionInk,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.fullName,
                    style = MaterialTheme.typography.titleLarge,
                    color = NotionInk,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    user.role,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NotionCharcoal.copy(alpha = 0.6f)
                )
                if (!user.department.isNullOrEmpty() || !user.college.isNullOrEmpty()) {
                    Text(
                        text = listOfNotNull(user.department, user.college).joinToString(" • "),
                        style = MaterialTheme.typography.labelSmall,
                        color = NotionCharcoal.copy(alpha = 0.4f)
                    )
                }
            }
            
            IconButton(
                onClick = onNavigateToEdit,
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, NotionHairline, MaterialTheme.shapes.small)
            ) {
                Icon(
                    Icons.Default.Edit, 
                    contentDescription = "Edit Profile", 
                    tint = NotionCharcoal,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Stats Row - Subtle Notion blocks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(user.eventsCount.toString(), "Events")
            StatBox(user.connectionsCount.toString(), "Nodes")
            StatBox(user.points.toString(), "Score")
        }
    }
}

@Composable
fun RowScope.StatBox(value: String, label: String) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = MaterialTheme.shapes.medium, // 8dp
        color = NotionCanvas,
        border = BorderStroke(1.dp, NotionHairline)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                color = NotionInk,
                fontWeight = FontWeight.Bold
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = NotionCharcoal.copy(alpha = 0.5f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DashboardMenu(
    onNavigateToConnections: () -> Unit,
    onNavigateToActivity: () -> Unit
) {
    val menuItems = listOf(
        DashboardMenuItem("🚀", "My Startup Profile", "Coming Soon"),
        DashboardMenuItem("🤝", "Team Requests", null),
        DashboardMenuItem("📋", "My Community Activity", null)
    )

    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (menuItems.isEmpty()) {
            Text("No dashboard items available", modifier = Modifier.padding(24.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
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

data class DashboardMenuItem(val icon: String, val label: String, val badge: String? = null)

@Composable
fun DashboardMenuCard(item: DashboardMenuItem, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(item.icon, fontSize = 20.sp)
            
            Text(
                item.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            
            if (item.badge != null) {
                Surface(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        item.badge,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

