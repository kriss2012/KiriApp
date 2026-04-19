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
import androidx.compose.runtime.collectAsState

import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.EventDto
import com.apex.asg.ui.viewmodels.*
import androidx.compose.ui.platform.LocalContext

@Composable
fun EventsScreen(viewModel: EventsViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Hackathon", "Competition", "Workshop", "Seminar")

    LaunchedEffect(Unit) {
        viewModel.fetchEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.padding(18.dp, 10.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Upcoming Events", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
            if (sessionManager.canCreateEvents()) {
                IconButton(onClick = { /* TODO: Open Create Event Dialog */ }) {
                    Text("+", fontSize = 24.sp, color = OrangePrimary, fontWeight = FontWeight.Bold)
                }
            }
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

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is EventsState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OrangePrimary
                    )
                }
                is EventsState.Success -> {
                    val filteredEvents = if (selectedTab == "All") state.events else state.events.filter { it.title.contains(selectedTab, ignoreCase = true) }
                    
                    if (filteredEvents.isEmpty()) {
                        Text("No events found", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                    } else {
                        EventsList(filteredEvents)
                    }
                }
                is EventsState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red, fontSize = 14.sp)
                        Button(onClick = { viewModel.fetchEvents() }) {
                            Text("Retry")
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun EventsList(events: List<EventDto>) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("ACTIVE EVENTS", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 5.dp)) }
        items(events) { event ->
            EventDetailCard(
                day = event.date.split("-").lastOrNull() ?: "01",
                month = "EVENT", 
                title = event.title,
                organizer = "Community Event",
                location = event.description.take(20) + "...", 
                type = event.category ?: "General",
                typeBg = OrangeLight,
                typeText = OrangeDark,
                prize = "TBD"
            )
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
                    Text("ℹ️ $location", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
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
                    Text("Details →", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
