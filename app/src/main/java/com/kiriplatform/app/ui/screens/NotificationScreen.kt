package com.kiriplatform.app.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
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
            com.kiriplatform.app.data.SessionBus.events.collect { event ->
                if (event == com.kiriplatform.app.data.SessionEvent.NotificationReceived) {
                    viewModel.fetchNotifications(context, userId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("NOTIFICATIONS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), letterSpacing = 1.sp)
                        Text("Recent Updates", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(36.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(18.dp))
                    }
                },
                actions = {
                    if (uiState is NotificationState.Success && (uiState as NotificationState.Success).notifications.any { !it.isRead }) {
                        TextButton(onClick = { viewModel.markAllAsRead(context, userId) }) {
                            Text("Clear all", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is NotificationState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
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
                    Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
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
    val grouped = remember(notifications) {
        notifications.groupBy { getDayBucket(it.createdAt) }
    }

    val bucketOrder = listOf("Today", "Yesterday", "This Week", "Older")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        bucketOrder.forEach { bucket ->
            val itemsInBucket = grouped[bucket] ?: emptyList()
            if (itemsInBucket.isNotEmpty()) {
                item {
                    Text(
                        text = bucket.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                        letterSpacing = 1.sp
                    )
                }
                items(itemsInBucket, key = { it.id }) { notification ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp)) {
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
        "MESSAGE" -> Icons.AutoMirrored.Filled.Chat
        "REFERRAL" -> Icons.Default.CardGiftcard
        "REWARD" -> Icons.Default.WorkspacePremium
        else -> Icons.Default.Notifications
    }
    
    val iconColor = when (notification.type) {
        "EVENT" -> MaterialTheme.colorScheme.primary
        "REQUEST" -> MaterialTheme.colorScheme.secondary
        "MESSAGE" -> MaterialTheme.colorScheme.tertiary
        "REFERRAL" -> NotionBrandPink
        "REWARD" -> NotionTintYellow
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = iconColor.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = notification.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (notification.isRead) FontWeight.Medium else FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = notification.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        lineHeight = 16.sp
                    )
                    Text(
                        text = getRelativeTime(notification.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                if (!notification.isRead) {
                    Surface(
                        modifier = Modifier.size(6.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {}
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
                        modifier = Modifier.weight(1f).height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Accept", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Button(
                        onClick = onDecline,
                        modifier = Modifier.weight(1f).height(36.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Ignore", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun getDayBucket(dateStr: String): String {
    return try {
        val isoFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
        isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = isoFormat.parse(dateStr) ?: return "Older"
        
        val now = java.util.Calendar.getInstance()
        val notifCal = java.util.Calendar.getInstance()
        notifCal.time = date
        
        val diffMillis = now.timeInMillis - notifCal.timeInMillis
        val diffDays = diffMillis / (24 * 60 * 60 * 1000)
        
        when {
            diffDays < 1 && now.get(java.util.Calendar.DAY_OF_YEAR) == notifCal.get(java.util.Calendar.DAY_OF_YEAR) -> "Today"
            diffDays < 2 && (now.get(java.util.Calendar.DAY_OF_YEAR) - notifCal.get(java.util.Calendar.DAY_OF_YEAR) == 1) -> "Yesterday"
            diffDays < 7 -> "This Week"
            else -> "Older"
        }
    } catch (e: Exception) {
        "Older"
    }
}

fun getRelativeTime(dateStr: String): String {
    return try {
        val isoFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.US)
        isoFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = isoFormat.parse(dateStr) ?: return "Recent"
        
        val now = java.util.Calendar.getInstance()
        val notifCal = java.util.Calendar.getInstance()
        notifCal.time = date
        
        val diffMillis = now.timeInMillis - notifCal.timeInMillis
        val diffMinutes = diffMillis / (60 * 1000)
        val diffHours = diffMinutes / 60
        
        when {
            diffMinutes < 1 -> "Just now"
            diffMinutes < 60 -> "$diffMinutes min ago"
            diffHours < 24 -> "$diffHours hr ago"
            else -> {
                val outFormat = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.US)
                outFormat.format(date)
            }
        }
    } catch (e: Exception) {
        "Recent"
    }
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
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "You're all caught up!", 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "We'll alert you when something happens.", 
            style = MaterialTheme.typography.bodySmall, 
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
    }
}
