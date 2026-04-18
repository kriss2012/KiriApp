package com.apex.asg.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.apex.asg.ui.screens.*

@Composable
fun ASGNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onJoinCommunity = { navController.navigate(Screen.Register.route) },
                onLaunchpad = { navController.navigate(Screen.AIAgent.route) },
                onSignIn = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { navController.navigate(Screen.Home.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = { navController.navigate(Screen.Login.route) },
                onRegisterSuccess = { navController.navigate(Screen.Onboarding.route) }
            )
        }
        
        composable(Screen.Onboarding.route) {
            OnboardingScreen(onComplete = { navController.navigate(Screen.Home.route) })
        }
        
        composable(Screen.Home.route) {
            HomeScreen()
        }
        
        composable(Screen.Repository.route) {
            RepositoryScreen()
        }
        
        composable(Screen.AIAgent.route) {
            AIAgentScreen()
        }
        
        composable(Screen.Events.route) {
            EventsScreen()
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(Screen.HackathonOrganizer.route) {
            HackathonOrganizerScreen()
        }

        composable(Screen.NAACRecords.route) {
            NAACRecordsScreen()
        }

        composable(Screen.DistrictMap.route) {
            DistrictMapScreen()
        }

        composable(Screen.Chat.route) {
            ChatScreen()
        }

        composable(Screen.Jobs.route) {
            JobBoardScreen()
        }
    }
}
