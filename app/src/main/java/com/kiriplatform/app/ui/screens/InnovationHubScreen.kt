package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.utils.glassmorphism

data class HubItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val route: String
)

@Composable
fun InnovationHubScreen(
    onNavigate: (String) -> Unit
) {
    val hubItems = listOf(
        HubItem("Marketplace", "Startup intelligence", Icons.Default.ShoppingCart, MaterialTheme.colorScheme.primary, "marketplace"),
        HubItem("Matchmaker", "Neural co-founder matching", Icons.Default.Person, MaterialTheme.colorScheme.secondary, "matchmaker"),
        HubItem("Mentorship", "Expert-led sprints", Icons.Default.DateRange, MaterialTheme.colorScheme.tertiary, "mentorship"),
        HubItem("Investor Intel", "Real-time KPI tracking", Icons.Default.TrendingUp, MaterialTheme.colorScheme.primary, "investor"),
        HubItem("IP Vault", "Institutional Repos", Icons.Default.Build, MaterialTheme.colorScheme.secondary, "vault"),
        HubItem("Kiri Organization", "7 Core AI Activities", Icons.Default.RocketLaunch, MaterialTheme.colorScheme.primary, "organization"),
        HubItem("System Portal", "Network orchestration", Icons.Default.Settings, Color.Gray, "admin")
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Hero Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer)
                        )
                    )
                    .padding(24.dp)
            ) {
                // Background decoration
                Box(
                    modifier = Modifier
                        .size(300.dp)
                        .offset(x = 100.dp, y = (-50).dp)
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(150.dp))
                )

                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                    ) {
                        Text(
                            "KIRI INTELLIGENCE LAYER",
                            modifier = Modifier.padding(12.dp, 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        "Ecosystem Hub",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Scaling the Innovation Economy with AI.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Grid of Features
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Network Nodes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )

                hubItems.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { item ->
                            HubCard(item, modifier = Modifier.weight(1f), onClick = { onNavigate(item.route) })
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun HubCard(item: HubItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(item.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, null, tint = item.color, modifier = Modifier.size(24.dp))
            }
            
            Column {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    item.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}
