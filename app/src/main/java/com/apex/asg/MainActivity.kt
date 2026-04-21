package com.apex.asg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.apex.asg.ui.theme.ASGAppTheme
import com.apex.asg.ui.navigation.ASGNavGraph
import com.apex.asg.ui.navigation.MainScaffold
import dagger.hilt.android.AndroidEntryPoint

import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.ApiClient

import com.apex.asg.data.remote.SocketHandler
import com.apex.asg.utils.NotificationHelper
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Restore session
        val sessionManager = SessionManager.getInstance(this)
        ApiClient.setToken(sessionManager.getToken())

        // Init Sockets & Notifications
        NotificationHelper.createNotificationChannel(this)
        SocketHandler.setSocket(com.apex.asg.utils.AppConfig.SOCKET_URL)
        SocketHandler.establishConnection()
        
        // Request Notification Permission (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            ASGAppTheme {
                val navController = rememberNavController()
                MainScaffold(navController = navController) { padding ->
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.background
                    ) {
                        ASGNavGraph(navController = navController)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.closeConnection()
    }
}
