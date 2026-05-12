package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onNavigateBack: () -> Unit = {}
) {
    val events = listOf(
        EventItem("Summer Hackathon", "HACKATHON", "R1 Open", "Campus Hub", "2026-06-15"),
        EventItem("AI Meetup", "MEETUP", "R2 Only", "Virtual", "2026-05-20"),
        EventItem("Tech Quiz", "QUIZ", "All", "Seminar Hall", "2026-05-25"),
        EventItem("Open Day", "OPEN_DAY", "All", "Main Grounds", "2026-06-01")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Events & Meetups", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(events) { event ->
                EventCard(event)
            }
        }
    }
}

data class EventItem(
    val title: String,
    val type: String,
    val tag: String,
    val location: String,
    val date: String
)

@Composable
fun EventCard(event: EventItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(event.tag, color = MaterialTheme.colorScheme.onSecondary, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Type: ${event.type}", style = MaterialTheme.typography.bodySmall)
            Text("Location: ${event.location}", style = MaterialTheme.typography.bodySmall)
            Text("Date: ${event.date}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* Register */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Register")
            }
        }
    }
}
