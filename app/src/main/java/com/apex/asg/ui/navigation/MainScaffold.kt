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

@Composable
fun MainScaffold(
    navController: NavHostController = rememberNavController(),
    content: @Composable (PaddingValues) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Hide bottom bar on specific screens (Chat and AI Agent) to avoid overlap and keyboard issues
    val isChatScreen = currentRoute?.contains("chat", ignoreCase = true) == true || 
                       currentRoute?.contains("ai", ignoreCase = true) == true
                       
    val showBottomBar = currentRoute in BottomNavItems.map { it.route } && !isChatScreen
    
    // Detect keyboard visibility
    val isKeyboardOpen = WindowInsets.ime.getBottom(androidx.compose.ui.platform.LocalDensity.current) > 0

    Scaffold(
        containerColor = BgCream,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main Content
            content(padding)

            // Floating Bottom Navigation
            // Auto-hide when keyboard is open to avoid overlap and shifting
            AnimatedVisibility(
                visible = showBottomBar && !isKeyboardOpen,
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
