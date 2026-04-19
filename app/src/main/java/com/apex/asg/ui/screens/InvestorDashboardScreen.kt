package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.apex.asg.ui.viewmodels.InvestorViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.remote.InvestorPitchDto

@Composable
fun InvestorDashboardScreen(vm: InvestorViewModel = viewModel()) {
    Scaffold(containerColor = BgCream) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text("INVESTOR COMMAND", style = MaterialTheme.typography.labelSmall, color = BluePrimary, letterSpacing = 2.sp)
                Text("Market Insights", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                Text("Data-driven talent discovery for Jalgaon region", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // Market Trends Row
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Top Trending Sectors", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        vm.trends.take(2).forEach { trend ->
                            TrendBox(trend.category, "${trend._count["id"] ?: 0} Projects")
                        }
                    }
                }

                item {
                    Text("High Potential Pitches", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                }

                items(vm.highPotential) { pitch ->
                    InvestorPitchCard(pitch)
                }
            }
        }
    }
}

@Composable
fun TrendBox(label: String, value: String) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = BluePrimary)
        }
    }
}

@Composable
fun InvestorPitchCard(pitch: InvestorPitchDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = BluePrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(pitch.category, modifier = Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = BluePrimary, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Text("HEALTH SCORE: ${pitch.healthScore}%", style = MaterialTheme.typography.labelSmall, color = if (pitch.healthScore > 70) GreenSuccess else OrangePrimary, fontWeight = FontWeight.Bold)
            }
            
            Spacer(Modifier.height(12.dp))
            Text(pitch.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(pitch.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 2)

            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { /* onNavigateToPitchDetails */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("ANALYZE ROI")
                }
                OutlinedButton(
                    onClick = { /* onNavigateToFounder */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Text("FOUNDER INTEL", color = TextPrimary)
                }
            }
        }
    }
}
