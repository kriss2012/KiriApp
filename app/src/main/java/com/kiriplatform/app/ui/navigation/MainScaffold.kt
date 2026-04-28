package com.kiriplatform.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
            // Unified Global Alert Hub
            SocketHandler.setupGlobalListeners(
                onNotification = { data ->
                    val title = data.optString("title", "ASG Alert")
                    val content = data.optString("content", "")
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, title, content)
                },
                onMessage = { data ->
                    val senderName = data.optJSONObject("sender")?.optString("fullName") ?: "New Message"
                    val content = data.optString("content", "")
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, senderName, content)
                },
                onConnectionAccepted = { data ->
                    val receiverName = data.optJSONObject("receiver")?.optString("fullName") ?: "Kiri Community"
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, "Connection Accepted", "You are now connected with $receiverName!")
                },
                onMatchSuggested = { data ->
                    val reason = data.optString("matchReason", "The AI Agent found a new resource for you.")
                    com.kiriplatform.app.utils.NotificationHelper.showNotification(context, "New Resource Match 🚀", reason)
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
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BgCream
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Main Content
                content(padding)

                // Floating Bottom Navigation
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding() // Keep above gesture line
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
            .padding(horizontal = 20.dp)
            .height(64.dp)
            .wrapContentWidth()
            .glassmorphism(cornerRadius = 32.dp, alpha = 0.85f),
        shape = RoundedCornerShape(32.dp),
        color = Color.Transparent,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
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
    val backgroundColor by animateColorAsState(
        targetValue = if (selected) OrangePrimary.copy(alpha = 0.15f) else Color.Transparent,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tabBackground"
    )
    
    val iconColor by animateColorAsState(
        targetValue = if (selected) OrangePrimary else TextSecondary.copy(alpha = 0.7f),
        label = "iconColor"
    )

    Surface(
        onClick = onClick,
        color = backgroundColor,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .height(48.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (screen.icon != null) {
                Icon(
                    imageVector = screen.icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor
                )
            }
            
            AnimatedVisibility(
                visible = selected,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                Text(
                    text = screen.title,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = OrangePrimary,
                    maxLines = 1
                )
            }
        }
    }
}
