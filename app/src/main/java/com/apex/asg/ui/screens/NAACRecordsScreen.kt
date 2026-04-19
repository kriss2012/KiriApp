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

import com.apex.asg.ui.viewmodels.NAACViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.SessionManager
import androidx.compose.ui.platform.LocalContext

import com.apex.asg.data.services.CertificateService

@Composable
fun NAACRecordsScreen(vm: NAACViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val userName = sessionManager.getUserName() ?: "Student"
    
    var selectedTab by remember { mutableStateOf("Student Data") }
    val tabs = listOf("Student Data", "Events Log", "Connectivity", "Innovation")

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            vm.loadActivities(userId)
        }
    }

    Scaffold(
        containerColor = BgCream,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { CertificateService.generateInnovationReport(context, userName, vm.activities) },
                containerColor = OrangePrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("DOWNLOAD PORTFOLIO") },
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // ... (keep Header and Tabs from original code) ...
            
            // Header
            Column(modifier = Modifier.padding(18.dp, 10.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("Innovation Portfolio", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                }
                Text("Automated records for NAAC & NEP documentation", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            // Tabs (same as original)
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

            // Records List (REAL DATA)
            val filteredActivities = when(selectedTab) {
                "Student Data" -> vm.activities
                "Events Log" -> vm.activities.filter { it.type == "EVENT_JOIN" }
                "Connectivity" -> vm.activities.filter { it.type == "CONNECTION" }
                "Innovation" -> vm.activities.filter { it.type == "AI_SESSION" }
                else -> vm.activities
            }

            LazyColumn(
                modifier = Modifier.padding(horizontal = 14.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                if (filteredActivities.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Text("No activity records found for this category.", 
                                modifier = Modifier.padding(32.dp).fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = TextSecondary)
                        }
                    }
                }

                items(filteredActivities) { activity ->
                    RecordCard(
                        title = activity.title,
                        date = activity.createdAt.split("T").firstOrNull() ?: activity.createdAt,
                        status = "Verified",
                        statusColor = GreenSuccess
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
