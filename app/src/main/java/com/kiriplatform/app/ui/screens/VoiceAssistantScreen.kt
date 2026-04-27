package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceAssistantScreen(
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
             TopAppBar(
                 title = { Text("AI Voice Assistant") },
                 navigationIcon = {
                     IconButton(onClick = onBack) { Text("←") }
                 }
             )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "AI Linkage Assistant",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { /* Start Voice Listening */ },
                modifier = Modifier.size(100.dp)
            ) {
                Text("Tap")
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "I'm listening for your requirements regarding funding, teams, or incubation support...",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
