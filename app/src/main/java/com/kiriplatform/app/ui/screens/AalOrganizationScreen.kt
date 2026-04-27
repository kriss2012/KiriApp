package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
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
                title = { Text("Kiri Organization", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgCream)
            )
        },
        containerColor = BgCream
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("INTERNSHIP PROGRESS", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
                        Spacer(Modifier.height(8.dp))
                        Text("3 / 7 Activities", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = 3f/7f,
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = OrangePrimary,
                            trackColor = Color.White.copy(alpha = 0.1f),
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
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) Color.White else Color.White.copy(alpha = 0.6f)
        ),
        onClick = onClick,
        border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, OrangePrimary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(
                    if (isCompleted) GreenSuccess.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                    RoundedCornerShape(12.dp)
                ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, null, tint = GreenSuccess)
                } else {
                    Icon(Icons.Default.Info, null, tint = Color.Gray)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrent) FontWeight.Black else FontWeight.Bold,
                    color = if (isCurrent) TextPrimary else TextSecondary
                )
                Text(
                    if (isCompleted) "VERIFIED" else if (isCurrent) "START NOW" else "LOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCompleted) GreenSuccess else if (isCurrent) OrangePrimary else TextSecondary
                )
            }
        }
    }
}
