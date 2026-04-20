package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*
import com.apex.asg.data.remote.models.InviteCodeDto

@Composable
fun InviteManagerScreen() {
    Scaffold(
        containerColor = BgCream,
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Generate Invite Dialog */ }, containerColor = OrangePrimary, contentColor = Color.White) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text("GOVERNANCE HUB", style = MaterialTheme.typography.labelSmall, color = BluePrimary, letterSpacing = 2.sp)
                Text("Professional Invites", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                Text("Manage onboarding for Prof Admins & Mentors", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // Stats row
            Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip("Active", "12", color = BluePrimary)
                StatChip("Used", "45", color = GreenSuccess)
            }

            Spacer(Modifier.height(16.dp))

            // Invites List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(5) { index ->
                    InviteRow(
                        code = "ASG-${index}K9X",
                        role = if(index % 2 == 0) "SPOC" else "MENTOR",
                        isUsed = index > 3
                    )
                }
            }
        }
    }
}

@Composable
fun StatChip(label: String, value: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.05f),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(12.dp, 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(Modifier.width(8.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun InviteRow(code: String, role: String, isUsed: Boolean) {
    ListItem(
        headlineContent = { Text(code, fontWeight = FontWeight.Black, color = if(isUsed) TextSecondary else BluePrimary, letterSpacing = 1.sp) },
        supportingContent = { Text("Target Role: $role", style = MaterialTheme.typography.labelSmall) },
        trailingContent = { 
            if(isUsed) {
                Icon(Icons.Default.CheckCircle, "Used", tint = GreenSuccess, modifier = Modifier.size(20.dp))
            } else {
                Surface(color = OrangePrimary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text("ACTIVE", modifier = Modifier.padding(8.dp, 4.dp), color = OrangePrimary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.White)
    )
}
