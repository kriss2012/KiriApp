package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.ui.viewmodels.AalState
import com.kiriplatform.app.ui.viewmodels.AalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AalOrganizationScreen(
    onBack: () -> Unit = {},
    viewModel: AalViewModel = hiltViewModel()
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

    val state by viewModel.uiState.collectAsState()
    
    // TODO: Get actual userId from SessionManager
    LaunchedEffect(Unit) {
        viewModel.loadAalData("default_user") 
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Kiri Organization", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = MaterialTheme.colorScheme.onSurface) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        when (val currentState = state) {
            is AalState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NotionPrimary)
                }
            }
            is AalState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${currentState.message}", color = Color.Red)
                }
            }
            is AalState.Success -> {
                val completedCount = currentState.activities.count { it.status?.name == "COMPLETED" || it.status?.name == "VERIFIED" }
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.large // 12dp
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text("INTERNSHIP PROGRESS", color = NotionOnDark.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
                                Spacer(Modifier.height(8.dp))
                                Text("$completedCount / 7 Activities", color = NotionOnDark, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(16.dp))
                                LinearProgressIndicator(
                                    progress = { completedCount.toFloat() / 7f },
                                    modifier = Modifier.fillMaxWidth().height(6.dp),
                                    color = NotionOnDark,
                                    trackColor = NotionOnDark.copy(alpha = 0.2f),
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                        }
                    }

                    item {
                        Text("Your Learning Journey", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 12.dp))
                    }

                    items(activities.indices.toList()) { index ->
                        val activityNumber = index + 1
                        val activityData = currentState.activities.find { it.activityNumber == activityNumber }
                        val isCompleted = activityData?.status?.name == "COMPLETED" || activityData?.status?.name == "VERIFIED"
                        val isCurrent = activityData?.status?.name == "IN_PROGRESS" || (activityData == null && index == completedCount)
                        
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
    }
}

@Composable
fun ActivityCard(title: String, isCompleted: Boolean, isCurrent: Boolean, onClick: () -> Unit) {
    val dark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium, // 8dp
        color = if (isCurrent) {
            (if (dark) NotionTintLavenderDark else NotionTintLavender).copy(alpha = 0.3f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        onClick = onClick,
        border = if (isCurrent) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(
                    if (isCompleted) {
                        MaterialTheme.colorScheme.primary.copy(alpha = if (dark) 0.25f else 0.1f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                    },
                    MaterialTheme.shapes.small // 6dp
                ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
                } else {
                    Icon(Icons.Default.Info, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    if (isCompleted) "VERIFIED" else if (isCurrent) "START NOW" else "LOCKED",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCompleted || isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
