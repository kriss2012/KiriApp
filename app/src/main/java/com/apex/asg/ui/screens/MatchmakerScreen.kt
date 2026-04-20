package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*
import com.apex.asg.ui.viewmodels.MatchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.remote.models.MatchSuggestionDto

@Composable
fun MatchmakerScreen(vm: MatchViewModel = viewModel()) {
    Scaffold(containerColor = BgCream) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text("AI Matchmaker", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                Text("Predictive matching based on your skills & goals", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            if (vm.suggestions.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(vm.suggestions) { suggestion ->
                        MatchCard(suggestion)
                    }
                }
            }
        }
    }
}

@Composable
fun MatchCard(suggestion: MatchSuggestionDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(50.dp).clip(CircleShape).background(OrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(suggestion.fullName.take(1), fontWeight = FontWeight.Black, color = OrangePrimary)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(suggestion.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary)
                    Text("${suggestion.role} • ${suggestion.college ?: "Regional Hub"}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Surface(
                    color = GreenSuccess.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("${suggestion.matchScore}% MATCH", modifier = Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = GreenSuccess, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text("Top Skills", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                suggestion.skills.take(3).forEach { skill ->
                    Surface(color = BgCream, shape = RoundedCornerShape(8.dp), border = BorderStroke(0.5.dp, BorderColor)) {
                        Text(skill, modifier = Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                    }
                }
            }
            
            Spacer(Modifier.height(20.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { /* onNavigateToChat */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("CONNECT")
                }
                OutlinedButton(
                    onClick = { /* onNavigateToProfile */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Text("VIEW PORTFOLIO", color = TextPrimary)
                }
            }
        }
    }
}
