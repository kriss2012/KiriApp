package com.kiriplatform.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.kiriplatform.app.ui.theme.KiriAppTheme
import com.kiriplatform.app.ui.navigation.KiriNavGraph
import com.kiriplatform.app.ui.navigation.MainScaffold
import dagger.hilt.android.AndroidEntryPoint

import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.ApiClient

import com.kiriplatform.app.data.remote.SocketHandler
import com.kiriplatform.app.utils.NotificationHelper
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle the result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Keep this first
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        
        // Restore session
        val sessionManager = SessionManager.getInstance(this)
        ApiClient.setToken(sessionManager.getToken())

        // Init Sockets & Notifications
        NotificationHelper.createNotificationChannel(this)
        SocketHandler.setSocket(com.kiriplatform.app.utils.AppConfig.SOCKET_URL)
        SocketHandler.establishConnection()
        
        // Request Notification Permission (Android 13+)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            val mainViewModel: com.kiriplatform.app.ui.viewmodels.MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            val uiState by mainViewModel.uiState.collectAsState()
            
            KiriAppTheme(
                darkTheme = uiState.isDarkTheme ?: androidx.compose.foundation.isSystemInDarkTheme(),
                appTheme = uiState.currentColorTheme,
                isAmoledTheme = uiState.isAmoledTheme
            ) {
                val navController = rememberNavController()
                MainScaffold(navController = navController) { padding ->
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = androidx.compose.material3.MaterialTheme.colorScheme.background
                    ) {
                        KiriNavGraph(navController = navController)
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
