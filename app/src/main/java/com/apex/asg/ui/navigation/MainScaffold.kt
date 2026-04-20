package com.apex.asg.ui.navigation

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
import com.apex.asg.ui.theme.*

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.SocketHandler
import com.apex.asg.utils.AppConfig
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

    // Initialize Socket.io globally
    LaunchedEffect(userId) {
        if (!userId.isNullOrEmpty()) {
            SocketHandler.setSocket(AppConfig.SOCKET_URL)
            SocketHandler.establishConnection()
            
            // Wait for connection and then join room
            SocketHandler.getSocket()?.on(io.socket.client.Socket.EVENT_CONNECT) {
                SocketHandler.joinRoom("user_$userId")
            }
            
            // Unified Global Alert Hub
            SocketHandler.setupGlobalListeners(
                onNotification = { data ->
                    val title = data.optString("title", "New Alert")
                    val content = data.optString("content", "")
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "🔔 $title: $content",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                onMessage = { data ->
                    val senderId = data.optJSONObject("sender")?.optString("fullName") ?: "Someone"
                    val content = data.optString("content", "sent a message")
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "💬 $senderId: $content",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                onConnectionAccepted = { data ->
                    val receiverName = data.optJSONObject("receiver")?.optString("fullName") ?: "User"
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "🤝 Connection accepted by $receiverName!",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            )
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
            .wrapContentWidth(),
        shape = RoundedCornerShape(32.dp),
        color = Color.White.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
        shadowElevation = 8.dp
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
            Icon(
                imageVector = screen.icon!!,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = iconColor
            )
            
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
