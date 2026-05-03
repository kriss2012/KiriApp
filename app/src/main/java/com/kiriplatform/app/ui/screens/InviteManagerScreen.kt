package com.kiriplatform.app.ui.screens

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
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.data.remote.models.InviteCodeDto

@Composable
fun InviteManagerScreen() {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Generate Invite Dialog */ }, containerColor = MaterialTheme.colorScheme.primary, contentColor = MaterialTheme.colorScheme.onPrimary) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text("GOVERNANCE HUB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp)
                Text("Professional Invites", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Black)
                Text("Manage onboarding for Prof Admins & Mentors", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Stats row
            Row(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChip("Active", "12", color = MaterialTheme.colorScheme.primary)
                StatChip("Used", "45", color = NotionSuccess)
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
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(8.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = color, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
fun InviteRow(code: String, role: String, isUsed: Boolean) {
    ListItem(
        headlineContent = { Text(code, fontWeight = FontWeight.Black, color = if(isUsed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary, letterSpacing = 1.sp) },
        supportingContent = { Text("Target Role: $role", style = MaterialTheme.typography.labelSmall) },
        trailingContent = { 
            if(isUsed) {
                Icon(Icons.Default.CheckCircle, "Used", tint = NotionSuccess, modifier = Modifier.size(20.dp))
            } else {
                Surface(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text("ACTIVE", modifier = Modifier.padding(8.dp, 4.dp), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                }
            }
        },
        colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surface)
    )
}
