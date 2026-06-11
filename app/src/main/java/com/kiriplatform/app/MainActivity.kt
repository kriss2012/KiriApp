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
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Handle the result if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        
        // 1. Load session data off-main thread
        val sessionManager = SessionManager.getInstance(this)
        lifecycleScope.launch(Dispatchers.IO) {
            ApiClient.init(sessionManager)
            
            // 2. Async Init Sockets
            SocketHandler.setSocket(com.kiriplatform.app.utils.AppConfig.SOCKET_URL, sessionManager.getToken())
            SocketHandler.establishConnection()
            
            // 3. Permission checks
            withContext(Dispatchers.Main) {
                // Set initial login state
                // Note: We'll need a way to inject MainViewModel or set its state
                // Since MainViewModel is hilt-managed in setContent, 
                // we'll handle initial state there or via a shared repository.

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        }

        setContent {
            val mainViewModel: com.kiriplatform.app.ui.viewmodels.MainViewModel = androidx.hilt.navigation.compose.hiltViewModel()
            val uiState by mainViewModel.uiState.collectAsState()
            
            // Sync initial and future login states
            LaunchedEffect(Unit) {
                mainViewModel.setLoggedIn(sessionManager.getToken() != null)
            }
            
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
                        // Pass token-check as a derived state or from VM later, 
                        // for now use sessionManager directly but outside critical hot paths if possible.
                        KiriNavGraph(
                            navController = navController,
                            hasToken = uiState.isLoggedIn,
                            paddingValues = padding
                        )
                    }
                }
            }
        }
        handleDeepLink(intent)
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDeepLink(intent: android.content.Intent) {
        val data = intent.data
        if (data != null && data.scheme == "kiriapp" && data.host == "github-connect") {
            android.widget.Toast.makeText(this, "GitHub connection completed! Refreshing profile...", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketHandler.closeConnection()
    }
}
