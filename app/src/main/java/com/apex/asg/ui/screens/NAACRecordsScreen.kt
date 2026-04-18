package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
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

@Composable
fun NAACRecordsScreen() {
    var selectedTab by remember { mutableStateOf("Student Data") }
    val tabs = listOf("Student Data", "Events Log", "Visiting Guests", "Innovation")

    Scaffold(
        containerColor = BgCream,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = OrangePrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(18.dp, 10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("NAAC & NEP Records", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                }
                Text("Maintain college data for accreditation", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // Tabs
            LazyRow(
                modifier = Modifier.padding(vertical = 5.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedTab == tab
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        label = { Text(tab) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = OrangePrimary,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = BorderColor,
                            selectedBorderColor = OrangePrimary,
                            borderWidth = 1.dp
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // Records List
            LazyColumn(
                modifier = Modifier.padding(horizontal = 14.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                items(5) { index ->
                    RecordCard(
                        title = "Monthly Innovation Report",
                        date = "March 2025",
                        status = if (index % 2 == 0) "Verified" else "Pending",
                        statusColor = if (index % 2 == 0) GreenSuccess else OrangePrimary
                    )
                }
            }
        }
    }
}

@Composable
fun RecordCard(title: String, date: String, status: String, statusColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(modifier = Modifier.size(32.dp).background(BlueInfo, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Text("📄", fontSize = 14.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(date, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 9.sp)
            }
            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(status, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 8.sp)
            }
        }
    }
}
