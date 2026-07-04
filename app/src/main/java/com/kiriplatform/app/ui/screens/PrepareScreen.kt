package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepareScreen(
    onNavigateToResumeBuilder: () -> Unit = {},
    onNavigateToInterviewSandbox: () -> Unit = {},
    onNavigateToLms: () -> Unit = {},
    onNavigateToAIAgent: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Prepare & Upskill", 
                        fontWeight = FontWeight.Bold, 
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                "Skill Preparation Hub", 
                style = MaterialTheme.typography.titleLarge, 
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "Access AI tools and interactive training to get job-ready.", 
                style = MaterialTheme.typography.bodyMedium, 
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )
            
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 140.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    PrepareCard(
                        title = "Resume Builder",
                        subtitle = "Build ATS-friendly, professional resumes.",
                        icon = Icons.Default.Description,
                        cardColor = NotionTintSky,
                        onClick = onNavigateToResumeBuilder
                    )
                }
                item {
                    PrepareCard(
                        title = "Mock Interviews",
                        subtitle = "AI-powered sandbox for real-time interview practice.",
                        icon = Icons.Default.ChatBubble,
                        cardColor = NotionTintPeach,
                        onClick = onNavigateToInterviewSandbox
                    )
                }
                item {
                    PrepareCard(
                        title = "AAL LMS Journey",
                        subtitle = "Internships, tasks, and learning courses.",
                        icon = Icons.Default.School,
                        cardColor = NotionTintMint,
                        onClick = onNavigateToLms
                    )
                }
                item {
                    PrepareCard(
                        title = "AI Copilot Agent",
                        subtitle = "Get career guidance and interactive help.",
                        icon = Icons.Default.AutoAwesome,
                        cardColor = NotionTintYellow,
                        onClick = onNavigateToAIAgent
                    )
                }
            }
        }
    }
}

@Composable
fun PrepareCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    cardColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(160.dp),
        shape = RoundedCornerShape(12.dp),
        color = cardColor.copy(alpha = 0.15f),
        border = BorderStroke(1.dp, cardColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = cardColor.copy(alpha = 0.3f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = cardColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title, 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle, 
                    style = MaterialTheme.typography.bodySmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
