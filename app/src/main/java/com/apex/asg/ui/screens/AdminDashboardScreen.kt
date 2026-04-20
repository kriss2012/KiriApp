package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.ActivityDto
import kotlinx.coroutines.launch

@Composable
fun AdminDashboardScreen(collegeName: String) {
    var activities by remember { mutableStateOf<List<ActivityDto>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(collegeName) {
        scope.launch {
            try {
                // val data = ApiClient.service.getCollegeActivity(collegeName)
                // activities = data
            } catch (e: Exception) {}
        }
    }

    Scaffold(containerColor = BgCream) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text(collegeName.uppercase(), style = MaterialTheme.typography.labelSmall, color = OrangePrimary, letterSpacing = 2.sp)
                Text("Institutional Panel", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                Text("Monitoring Innovation Records", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // Stats row (Simulated)
            Row(modifier = Modifier.padding(horizontal = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBox("Total Students", "142", BluePrimary)
                StatBox("Pitches", "12", GreenSuccess)
            }

            Spacer(Modifier.height(16.dp))

            // Activity Log
            Text("Regional Activity Log", modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(5) { index ->
                    AdminActivityCard(
                        studentName = "Student #$index",
                        activity = "Innovation Pitch Submitted",
                        date = "Today"
                    )
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun AdminActivityCard(studentName: String, activity: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(0.5.dp, BorderColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(studentName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text(activity, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Text(date, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}
