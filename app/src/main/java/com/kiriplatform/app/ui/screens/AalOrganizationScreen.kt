package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AalOrganizationScreen(
    onBack: () -> Unit = {}
) {
    val activities = remember {
        listOf(
            "1. Mindset Discovery",
            "2. Problem Identification",
            "3. Market Analysis",
            "4. Solution Architecture",
            "5. Prototype Blueprint",
            "6. Pitch Deconstruction",
            "7. Ecosystem Integration"
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kiri Organization", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = NotionInk) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = NotionInk) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = NotionCanvas)
            )
        },
        containerColor = NotionCanvas
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = NotionInkDeep,
                    shape = MaterialTheme.shapes.large // 12dp
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("INTERNSHIP PROGRESS", color = NotionOnDark.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.height(8.dp))
                        Text("3 / 7 Activities", color = NotionOnDark, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { 3f/7f },
                            modifier = Modifier.fillMaxWidth().height(6.dp),
                            color = NotionPrimary,
                            trackColor = NotionOnDark.copy(alpha = 0.1f),
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                    }
                }
            }

            item {
                Text("Your Learning Journey", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 12.dp))
            }

            items(activities.indices.toList()) { index ->
                val isCompleted = index < 3
                val isCurrent = index == 3
                ActivityCard(
                    title = activities[index],
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    onClick = { /* Navigate to activity submission */ }
                )
            }
        }
    }
}

@Composable
fun ActivityCard(title: String, isCompleted: Boolean, isCurrent: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, // 8dp
        color = if (isCurrent) NotionTintLavender.copy(alpha = 0.3f) else NotionSurface,
        onClick = onClick,
        border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.dp, NotionPrimary) else BorderStroke(1.dp, NotionHairline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(
                    if (isCompleted) NotionPrimary.copy(alpha = 0.1f) else NotionSlate.copy(alpha = 0.1f),
                    MaterialTheme.shapes.small // 6dp
                ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, null, tint = NotionPrimary)
                } else {
                    Icon(Icons.Default.Info, null, tint = NotionSteel)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = NotionInk
                )
                Text(
                    if (isCompleted) "VERIFIED" else if (isCurrent) "START NOW" else "LOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCompleted) NotionPrimary else if (isCurrent) NotionPrimary else NotionSteel
                )
            }
        }
    }
}
