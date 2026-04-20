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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        androidx.core.view.WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Restore session
        val sessionManager = SessionManager.getInstance(this)
        ApiClient.setToken(sessionManager.getToken())

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
}
