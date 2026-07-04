package com.kiriplatform.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.utils.glassmorphism

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.SocketHandler
import com.kiriplatform.app.utils.AppConfig
import kotlinx.coroutines.launch

@Composable
fun MainScaffold(
    navController: NavHostController = rememberNavController(),
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current

    // Initialize and maintain Socket.io connectivity
    DisposableEffect(userId, lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (!userId.isNullOrEmpty()) {
                    SocketHandler.setSocket(AppConfig.SOCKET_URL)
                    SocketHandler.establishConnection()
                    val roomName = "user_$userId"
                    
                    // Force join on resume to ensure we are listening
                    if (SocketHandler.getSocket()?.connected() == true) {
                        SocketHandler.joinRoom(roomName)
                    }
                    
                    SocketHandler.getSocket()?.on(io.socket.client.Socket.EVENT_CONNECT) {
                        SocketHandler.joinRoom(roomName)
                    }
                }
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)

        if (!userId.isNullOrEmpty()) {
            // Re-establish connection on app startup or userId change
            SocketHandler.setSocket(AppConfig.SOCKET_URL, sessionManager.getToken())
            SocketHandler.establishConnection()
            SocketHandler.joinRoom("user_$userId")

            // Unified Global Alert Hub
            SocketHandler.setupGlobalListeners(
                onNotification = { data ->
                    val title = data.optString("title", "ASG Alert")
                    val content = data.optString("content", "")
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, title, content)
                    scope.launch { com.kiriplatform.app.data.SessionBus.emit(com.kiriplatform.app.data.SessionEvent.NotificationReceived) }
                },
                onMessage = { data ->
                    val senderName = data.optJSONObject("sender")?.optString("fullName") ?: "New Message"
                    val content = data.optString("content", "")
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, senderName, content)
                    scope.launch { com.kiriplatform.app.data.SessionBus.emit(com.kiriplatform.app.data.SessionEvent.NotificationReceived) }
                },
                onConnectionAccepted = { data ->
                    val receiverName = data.optJSONObject("receiver")?.optString("fullName") ?: "Kiri Community"
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, "Connection Accepted", "You are now connected with $receiverName!")
                    scope.launch { com.kiriplatform.app.data.SessionBus.emit(com.kiriplatform.app.data.SessionEvent.NotificationReceived) }
                },
                onMatchSuggested = { data ->
                    val reason = data.optString("matchReason", "The AI Agent found a new resource for you.")
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, "New Resource Match 🚀", reason)
                    scope.launch { com.kiriplatform.app.data.SessionBus.emit(com.kiriplatform.app.data.SessionEvent.NotificationReceived) }
                }
            )
        }

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Hide bottom bar on actual messaging screens (Individual Chat and AI Agent) 
    val isChatSubScreen = currentRoute?.startsWith("chat/") == true || 
                          currentRoute == Screen.AIAgent.route
                       
    val showBottomBar = currentRoute in BottomNavItems.map { it.route } && !isChatSubScreen
    
    Scaffold(
        containerColor = Color.Transparent,
        // Let Scaffold compute real status-bar / navigation-bar insets instead of
        // zeroing them out. Previously only the floating nav pill applied its own
        // navigationBarsPadding(), so screen content never reserved space for the
        // status bar or the system nav bar — fine on gesture nav (thin inset),
        // but overlapping/clipped on 3-button nav (tall opaque bar) and on
        // devices with different status bar heights.
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Content — now receives real insets via `padding`
                content(padding)

                // Floating Bottom Navigation
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding() // Keep above gesture line / 3-button bar
                        .padding(bottom = 24.dp)
                ) {
                    ASGBottomNavigation(navController = navController, currentRoute = currentRoute)
                }
            }
        }
    }
}

@Composable
fun ASGBottomNavigation(navController: NavController, currentRoute: String?) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 48.dp) // Narrower width for icon-only nav
            .height(56.dp)
            .wrapContentWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItems.forEach { screen ->
                val selected = currentRoute == screen.route
                
                NavigationTab(
                    screen = screen,
                    selected = selected,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun NavigationTab(
    screen: Screen,
    selected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        label = "iconColor"
    )

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .background(if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        if (screen.icon != null) {
            Icon(
                imageVector = screen.icon,
                contentDescription = screen.title,
                modifier = Modifier.size(22.dp),
                tint = iconColor
            )
        }
    }
}
