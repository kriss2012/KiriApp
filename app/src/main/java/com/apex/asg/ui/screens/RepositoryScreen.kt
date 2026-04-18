package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.components.ASGTagChip
import com.apex.asg.ui.theme.*

@Composable
fun RepositoryScreen() {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Creators", "Participants", "Organisers", "Alumni", "Professors")

    val students = listOf(
        StudentItem("PJ", "Priya Joshi", "DY Patil College, Kolhapur", listOf("Content Creator", "SIH Finalist"), 92, "Creator"),
        StudentItem("AK", "Arjun Kulkarni", "BVDU, Pune", listOf("Organiser", "DIPEX Winner"), 88, "Organiser"),
        StudentItem("SM", "Snehal More", "Shivaji University", listOf("Alumni", "Avishkar"), 85, "Alumni"),
        StudentItem("RB", "Rohan Bhosale", "KIT College, Kolhapur", listOf("Creator"), 79, "Creator"),
        StudentItem("NP", "Dr. Neha Patil", "Professor · Shivaji Univ.", listOf("Professor", "Mentor"), 96, "Professor")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(18.dp, 10.dp)) {
            Text("← Back", style = MaterialTheme.typography.labelSmall, color = OrangePrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { })
            Text("Student Repository", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Black)
            Text("5 categories · 7,800+ registered", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        // Search Bar
        Card(
            modifier = Modifier
                .padding(14.dp, 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(9.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp), tint = TextSecondary)
                Text("Search by name, college, skill...", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
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

        // Student List
        LazyColumn(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            items(students) { student ->
                StudentCard(student)
            }
        }
    }
}

data class StudentItem(val initials: String, val name: String, val college: String, val tags: List<String>, val score: Int, val category: String)

@Composable
fun StudentCard(student: StudentItem) {
    val gradient = when (student.category) {
        "Creator" -> Brush.linearGradient(colors = listOf(OrangePrimary, OrangeDark))
        "Organiser" -> Brush.linearGradient(colors = listOf(Color(0xFF1D9E75), Color(0xFF0F6E56)))
        "Alumni" -> Brush.linearGradient(colors = listOf(Color(0xFFE0742A), Color(0xFFB85A15)))
        "Professor" -> Brush.linearGradient(colors = listOf(Color(0xFF378ADD), Color(0xFF185FA5)))
        else -> Brush.linearGradient(colors = listOf(PurpleAccent, Color(0xFF4F3BB5)))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Text(student.initials, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(student.name, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(student.college, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 9.sp)
                Spacer(Modifier.height(4.dp))
                com.google.accompanist.flowlayout.FlowRow(
                    mainAxisSpacing = 3.dp,
                    crossAxisSpacing = 3.dp
                ) {
                    student.tags.forEach { tag ->
                        val (bg, txt) = when (tag) {
                            "Content Creator" -> OrangeLight to OrangeDark
                            "Organiser" -> GreenLight to GreenSuccess
                            "Alumni" -> YellowWarm to Color(0xFFB7770D)
                            "Professor" -> BlueInfo to Color(0xFF185FA5)
                            else -> PurpleLight to PurpleAccent
                        }
                        ASGTagChip(text = tag, backgroundColor = bg, textColor = txt)
                    }
                }
            }

            // Score
            Column(horizontalAlignment = Alignment.End) {
                Text(student.score.toString(), style = MaterialTheme.typography.titleMedium, color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text("AI Score", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 8.sp)
            }
        }
    }
}

