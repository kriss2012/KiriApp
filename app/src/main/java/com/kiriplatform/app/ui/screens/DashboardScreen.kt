package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToRepository: () -> Unit = {},
    onNavigateToVoice: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {},
    onNavigateToLms: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ASG Organization Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Welcome, Innovator!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item { DashboardCard("Internal Chat", "Chat with founders & mentors") { /* Navigate to Chat */ } }
                item { DashboardCard("Job Board", "Find startup opportunities") { /* Navigate to Jobs */ } }
                item { DashboardCard("Repositories", "Alumni, Faculty & Students") { onNavigateToRepository() } }
                item { DashboardCard("Events Wall", "Community meetups & SIH") { onNavigateToEvents() } }
                item { DashboardCard("Mentorship", "Find initial handholding") {} }
                item { DashboardCard("AAL LMS", "AI Launchpad & Internship") { onNavigateToLms() } }
                item { DashboardCard("AI Agent", "Linkages & Funding assist") { onNavigateToVoice() } }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
