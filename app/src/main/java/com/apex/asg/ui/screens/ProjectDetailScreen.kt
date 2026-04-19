package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*
import com.apex.asg.data.remote.ProjectArtifactDto

@Composable
fun ProjectDetailScreen(projectName: String) {
    Scaffold(
        containerColor = BgCream,
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Add Artifact */ }, containerColor = OrangePrimary, contentColor = Color.White) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text("Project Repository", style = MaterialTheme.typography.labelSmall, color = OrangePrimary, letterSpacing = 2.sp)
                Text(projectName, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                Text("Smart IP & Artifact Vault", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // AI Insight Card
            AIInsightCard(
                insight = "Kiri Intelligence: This project aligns with 3 NAAC innovation criteria and has a high synergy with the regional Agritech hub."
            )

            Spacer(Modifier.height(16.dp))

            // Artifacts List
            Text("Technical Artifacts", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(3) { index ->
                    ArtifactCard(
                        title = "Documentation v$index.0",
                        type = "PDF / Architecture",
                        date = "April 19, 2026"
                    )
                }
            }
        }
    }
}

@Composable
fun AIInsightCard(insight: String) {
    Card(
        modifier = Modifier.padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BluePrimary.copy(alpha = 0.05f)),
        border = BorderStroke(1.dp, BluePrimary.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, null, tint = BluePrimary, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(insight, style = MaterialTheme.typography.bodySmall, color = TextPrimary, lineHeight = 18.sp)
        }
    }
}

@Composable
fun ArtifactCard(title: String, type: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(type, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Text(date, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}
