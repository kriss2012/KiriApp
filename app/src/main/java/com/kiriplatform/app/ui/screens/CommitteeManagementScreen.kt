package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommitteeManagementScreen(
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Reps", "Repositories", "Approvals")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Committee Group", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> RepsManagement()
                1 -> RepositoryTiers()
                2 -> PendingApprovals()
            }
        }
    }
}

@Composable
fun RepsManagement() {
    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Committee Reps", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Button(
                onClick = { /* Add Rep Dialog */ },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Rep")
            }
        }

        Spacer(Modifier.height(20.dp))

        val reps = listOf(
            "Dr. Amit Sharma" to "FACULTY_REP",
            "Priya Patil" to "STUDENT_REP",
            "Rajesh Kumar" to "FACULTY_REP"
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(reps) { (name, role) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isDark = isSystemInDarkTheme()
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = if (role.contains("FACULTY")) {
                                MaterialTheme.colorScheme.primary.copy(alpha = if (isDark) 0.25f else 0.1f)
                            } else {
                                if (isDark) NotionTintPeachDark else NotionTintPeach
                            }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    if (role.contains("FACULTY")) Icons.Default.School else Icons.Default.Person,
                                    null,
                                    tint = if (role.contains("FACULTY")) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        if (isDark) NotionOnTintDark else NotionBrandOrange
                                    }
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(name, fontWeight = FontWeight.Bold)
                            Text(role.replace("_", " "), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoryTiers() {
    val tiers = listOf(
        "R1" to "Freshers & General Interest",
        "R2" to "Active Builders & Skill-focused",
        "R3" to "Project-Ready Innovators",
        "R4" to "Placement & Internship Candidates",
        "R5" to "Alumni & Mentors (Industry-ready)"
    )

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Text("Repository Mapping", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Organize your talent pool into tiers for city-wide matching.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(Modifier.height(20.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(tiers) { (tier, description) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) { Text(tier, color = MaterialTheme.colorScheme.onPrimary) }
                            Spacer(Modifier.width(12.dp))
                            Text(description, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { /* View Members */ }) {
                                Text("Manage Members →", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PendingApprovals() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.TaskAlt, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Text("No pending requests", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
