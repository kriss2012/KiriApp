package com.apex.asg

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.apex.asg.ui.theme.ASGAppTheme
import com.apex.asg.ui.navigation.ASGNavGraph
import com.apex.asg.ui.navigation.MainScaffold
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
