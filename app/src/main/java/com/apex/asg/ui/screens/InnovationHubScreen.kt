package com.apex.asg.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.apex.asg.ui.theme.*

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
        HubItem("Marketplace", "Browse startup pitches", Icons.Default.ShoppingCart, OrangePrimary, "marketplace"),
        HubItem("Matchmaker", "Find your co-founder", Icons.Default.Person, BluePrimary, "matchmaker"),
        HubItem("Mentorship", "Book expert sprints", Icons.Default.DateRange, GreenSuccess, "mentorship"),
        HubItem("Investor Intel", "High-potential KPIs", Icons.Default.TrendingUp, BluePrimary, "investor"),
        HubItem("IP Vault", "Manage Smart Repos", Icons.Default.Build, OrangePrimary, "vault"),
        HubItem("SPOC Portal", "Institutional stats", Icons.Default.Settings, Color.Gray, "admin")
    )

    Scaffold(
        containerColor = BgCream
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
                    .height(260.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(BluePrimary, BluePrimary.copy(alpha = 0.8f))
                        )
                    )
                    .padding(24.dp)
            ) {
                Column(modifier = Modifier.align(Alignment.BottomStart)) {
                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "REGIONAL ECOSYSTEM",
                            modifier = Modifier.padding(12.dp, 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            letterSpacing = 2.sp
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Innovation Hub",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "Orchestrating the Jalgaon startup economy.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Grid of Features
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "Ecosystem Layers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Using Column/Row instead of Grid for better scroll control in simple layout
                hubItems.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowItems.forEach { item ->
                            HubCard(item, modifier = Modifier.weight(1f), onClick = { onNavigate(item.route) })
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun HubCard(item: HubItem, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier.height(180.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
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
                    color = TextPrimary
                )
                Text(
                    item.description,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}
