package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.models.NotificationDto
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.ui.viewmodels.NotificationState
import com.kiriplatform.app.ui.viewmodels.NotificationViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NotificationScreen(
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToEvents: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    // Notification Permission Handling
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
        val permissionState = rememberPermissionState(
            android.Manifest.permission.POST_NOTIFICATIONS
        )
        LaunchedEffect(Unit) {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchNotifications(context, userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is NotificationState.Success && (uiState as NotificationState.Success).notifications.any { !it.isRead }) {
                        TextButton(onClick = { viewModel.markAllAsRead(context, userId) }) {
                            Text("Mark all read", color = OrangePrimary, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            )
        },
        containerColor = BgCream
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is NotificationState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = OrangePrimary)
                }
                is NotificationState.Success -> {
                    if (state.notifications.isEmpty()) {
                        EmptyNotifications(modifier = Modifier.align(Alignment.Center))
                    } else {
                        NotificationList(
                            notifications = state.notifications,
                            userId = userId,
                            onMarkRead = { id -> viewModel.markAsRead(context, id, userId) },
                            onAccept = { notifId, connId -> viewModel.acceptConnection(context, notifId, connId, userId) },
                            onNavigate = { type, relatedId ->
                                when (type) {
                                    "MESSAGE" -> relatedId?.let { onNavigateToChat(it) }
                                    "REQUEST" -> relatedId?.let { onNavigateToProfile(it) }
                                    "EVENT" -> onNavigateToEvents()
                                }
                            }
                        )
                    }
                }
                is NotificationState.Error -> {
                    Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun NotificationList(
    notifications: List<NotificationDto>,
    userId: String,
    onMarkRead: (String) -> Unit,
    onAccept: (String, String) -> Unit,
    onNavigate: (String, String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(notifications, key = { it.id }) { notification ->
            NotificationItem(
                notification = notification, 
                onClick = { 
                    onMarkRead(notification.id)
                    onNavigate(notification.type, notification.relatedId)
                },
                onAccept = { connId -> onAccept(notification.id, connId) },
                onDecline = { onMarkRead(notification.id) }
            )
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationDto, 
    onClick: () -> Unit,
    onAccept: (String) -> Unit,
    onDecline: () -> Unit
) {
    val icon = when (notification.type) {
        "EVENT" -> Icons.Default.CalendarMonth
        "REQUEST" -> Icons.Default.PersonAdd
        "MESSAGE" -> Icons.Default.Chat
        else -> Icons.Default.Notifications
    }
    
    val color = when (notification.type) {
        "EVENT" -> OrangePrimary
        "REQUEST" -> Color(0xFF1D9E75)
        "MESSAGE" -> Color(0xFF378ADD)
        else -> TextSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) Color.White.copy(alpha = 0.6f) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 0.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = notification.content,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                    Text(
                        text = getRelativeTime(notification.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (!notification.isRead) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary)
                    )
                }
            }

            // Action Buttons for Requests
            if (notification.type == "REQUEST" && !notification.isRead && notification.relatedId != null) {
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { notification.relatedId?.let { onAccept(it) } },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Accept", style = MaterialTheme.typography.labelSmall, color = Color.White)
                    }
                    OutlinedButton(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, BorderColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Ignore", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
        }
    }
}

fun getRelativeTime(dateStr: String): String {
    return try {
        // Simple relative time logic for demo
        val parts = dateStr.split("T")
        if (parts.size > 1) {
            val time = parts[1].substring(0, 5)
            "at $time"
        } else "Recent"
    } catch (e: Exception) { "Just now" }
}

@Composable
fun EmptyNotifications(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = TextSecondary.copy(alpha = 0.1f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "You're all caught up!", 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            "We'll alert you when something happens.", 
            style = MaterialTheme.typography.bodySmall, 
            color = TextSecondary.copy(alpha = 0.7f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
    }
}
