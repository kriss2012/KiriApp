package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun EventsScreen() {
    var selectedTab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Hackathon", "Competition", "Workshop", "Seminar")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(18.dp, 10.dp)) {
            Text("Upcoming Events", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
        }

        // Tabs
        LazyRow(
            modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(tabs) { tab ->
                val isSelected = selectedTab == tab
                Column(
                    modifier = Modifier.clickable { selectedTab = tab },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = tab,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) OrangePrimary else TextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .height(2.dp)
                                .width(20.dp)
                                .background(OrangePrimary)
                        )
                    }
                }
            }
        }

        // Events List
        LazyColumn(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Text("APRIL 2025", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 5.dp)) }
            items(2) { index ->
                EventDetailCard(
                    day = if (index == 0) "15" else "22",
                    month = "Apr",
                    title = if (index == 0) "Avishkar 2025 — Kolhapur" else "DIPEX Innovation Fair",
                    organizer = if (index == 0) "Shivaji University" else "DIPEX State Committee",
                    location = if (index == 0) "Kolhapur · District Level" else "Pune · State Level",
                    type = if (index == 0) "Competition" else "Hackathon",
                    typeBg = if (index == 0) OrangeLight else GreenLight,
                    typeText = if (index == 0) OrangeDark else GreenSuccess,
                    prize = "₹1,00,000"
                )
            }
        }
    }
}

@Composable
fun EventDetailCard(day: String, month: String, title: String, organizer: String, location: String, type: String, typeBg: Color, typeText: Color, prize: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Large Date Block
                Box(
                    modifier = Modifier
                        .size(44.dp, 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(day, style = MaterialTheme.typography.headlineSmall, color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, lineHeight = 18.sp)
                        Text(month.uppercase(), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Surface(color = typeBg, shape = RoundedCornerShape(6.dp)) {
                        Text(type, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = typeText, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(organizer, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                    Text("📍 $location", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                }
            }
            HorizontalDivider(color = BorderColor)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Prize: $prize", style = MaterialTheme.typography.bodySmall, color = GreenSuccess, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { },
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text("Register →", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
