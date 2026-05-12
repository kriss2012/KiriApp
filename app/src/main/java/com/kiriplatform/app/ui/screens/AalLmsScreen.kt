package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AalLmsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val activities = listOf(
        LmsActivity("1", "Mindset Questionnaire", "COMPLETED"),
        LmsActivity("2", "AI Ethics Module", "COMPLETED"),
        LmsActivity("3", "Prompt Engineering", "IN_PROGRESS"),
        LmsActivity("4", "Data Labeling Project", "LOCKED"),
        LmsActivity("5", "Model Fine-tuning", "LOCKED"),
        LmsActivity("6", "Final Project", "LOCKED")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AAL LMS Track", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Your Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = 0.33f,
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("33% Completed", style = MaterialTheme.typography.bodySmall)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Activities", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activities) { activity ->
                    ActivityCard(activity)
                }
            }
        }
    }
}

data class LmsActivity(
    val id: String,
    val title: String,
    val status: String
)

@Composable
fun ActivityCard(activity: LmsActivity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Activity ${activity.id}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                Text(activity.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .background(
                        when (activity.status) {
                            "COMPLETED" -> MaterialTheme.colorScheme.primary
                            "IN_PROGRESS" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        },
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    activity.status,
                    color = when (activity.status) {
                        "COMPLETED" -> MaterialTheme.colorScheme.onPrimary
                        "IN_PROGRESS" -> MaterialTheme.colorScheme.onSecondary
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                    fontSize = 12.sp
                )
            }
        }
    }
}
