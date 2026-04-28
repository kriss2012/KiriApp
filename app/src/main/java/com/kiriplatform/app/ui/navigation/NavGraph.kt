package com.kiriplatform.app.ui.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.kiriplatform.app.ui.screens.*
import com.kiriplatform.app.data.SessionManager
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment

@Composable
fun KiriNavGraph(
    navController: NavHostController = rememberNavController(),
    hasToken: Boolean,
    paddingValues: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(0.dp)
) {
    val context = LocalContext.current
    
    NavHost(
        navController = navController,
        startDestination = if (hasToken) Screen.Home.route else Screen.Splash.route,
        modifier = Modifier.padding(paddingValues),
        enterTransition = {
            androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { 1000 },
                animationSpec = androidx.compose.animation.core.tween(500)
            ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500))
        },
        exitTransition = {
            androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { -1000 },
                animationSpec = androidx.compose.animation.core.tween(500)
            ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(500))
        },
        popEnterTransition = {
            androidx.compose.animation.slideInHorizontally(
                initialOffsetX = { -1000 },
                animationSpec = androidx.compose.animation.core.tween(500)
            ) + androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(500))
        },
        popExitTransition = {
            androidx.compose.animation.slideOutHorizontally(
                targetOffsetX = { 1000 },
                animationSpec = androidx.compose.animation.core.tween(500)
            ) + androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(500))
        }
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onJoinCommunity = { 
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                },
                onOrganization = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(com.kiriplatform.app.utils.AppConfig.WEBSITE_URL))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle potential activity not found
                    }
                },
                onSignIn = { 
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { 
                    navController.navigate(Screen.Register.route) {
                        launchSingleTop = true
                    }
                },
                onLoginSuccess = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = { 
                    navController.navigate(Screen.Login.route) {
                        launchSingleTop = true
                    }
                },
                onRegisterSuccess = { 
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = { navController.navigate(Screen.Home.route) })
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToRepository = { navController.navigate(Screen.Repository.route) },
                onNavigateToEvents = { navController.navigate(Screen.Events.route) },
                onNavigateToAddEvent = { navController.navigate(Screen.AddEvent.route) },
                onNavigateToAal = { navController.navigate(Screen.Organization.route) }
            )
        }
        
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateToProfile = { userId -> 
                    navController.navigate(Screen.PublicProfile.createRoute(userId))
                }
            )
        }

        composable(Screen.Repository.route) {
            RepositoryScreen(
                onNavigateToProfile = { userId -> 
                    navController.navigate(Screen.PublicProfile.createRoute(userId))
                }
            )
        }
        
        composable(Screen.Chats.route) {
            ChatsScreen(
                onNavigateToChat = { receiverId -> 
                    navController.navigate(Screen.Chat.createRoute(receiverId))
                },
                onNavigateToAI = {
                    navController.navigate(Screen.AIAgent.route)
                }
            )
        }

        composable(Screen.AIAgent.route) {
            AIAgentScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.PublicProfile.route) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            PublicProfileScreen(
                userId = userId,
                onBack = { navController.popBackStack() },
                onNavigateToChat = { receiverId -> 
                    navController.navigate(Screen.Chat.createRoute(receiverId))
                }
            )
        }
        
        composable(Screen.Events.route) {
            EventsScreen(navController = navController)
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateToEdit = { navController.navigate(Screen.EditProfile.route) },
                onNavigateToConnections = { navController.navigate(Screen.Connections.route) },
                onNavigateToActivity = { navController.navigate(Screen.Notifications.route) },
                onLogout = { 
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.EditProfile.route) {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBack = { navController.popBackStack() },
                onNavigateToChat = { receiverId -> 
                    navController.navigate(Screen.Chat.createRoute(receiverId))
                },
                onNavigateToProfile = { userId ->
                    navController.navigate(Screen.PublicProfile.createRoute(userId))
                },
                onNavigateToEvents = {
                    navController.navigate(Screen.Events.route)
                }
            )
        }

        composable(Screen.Connections.route) {
            ConnectionsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Chat.route) { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
            ChatScreen(
                receiverId = receiverId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Jobs.route) {
            JobBoardScreen(navController = navController)
        }

        composable(Screen.AddEvent.route) {
            AddEventScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.EventDetails.route) { backStackEntry ->
            val eventJson = backStackEntry.arguments?.getString("eventJson") ?: ""
            EventDetailsScreen(
                navController = navController,
                eventJson = eventJson
            )
        }

        composable(Screen.InnovationHub.route) {
            InnovationHubScreen(onNavigate = { route -> navController.navigate(route) })
        }

        composable(Screen.MindsetDiscovery.route) {
            MindsetDiscoveryScreen(
                onBack = { navController.popBackStack() },
                onComplete = { navController.popBackStack() }
            )
        }

        composable(Screen.LiveInput.route) {
            LiveInputScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.CommitteeManagement.route) {
            CommitteeManagementScreen(onBack = { navController.popBackStack() })
        }

        // Innovation Hub Sub-Routes
        composable(Screen.Marketplace.route) {
            InnovationPitchesScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Matchmaker.route) {
            MatchmakerScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Mentorship.route) {
            MentorSessionScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Investor.route) {
            InvestorDashboardScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Vault.route) {
            NAACRecordsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Organization.route) {
            AalOrganizationScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Admin.route) {
            AdminDashboardScreen(onBack = { navController.popBackStack() })
        }
    }
}
