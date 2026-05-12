package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.data.remote.models.EventDto
import com.kiriplatform.app.ui.theme.*
import com.google.gson.Gson
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("ALL") }
    val filters = listOf("ALL", "HACKATHON", "COMPETITION", "WORKSHOP", "OFFER")

    // Mock data updated to match EventDto structure for better visual representation
    val events = listOf(
        EventDto(
            _id = "1",
            _title = "National AI Hackathon",
            type = "HACKATHON",
            _description = "A 24-hour hackathon to build AI solutions for the future of decentralized ecosystems.",
            _date = "2024-06-25T10:00:00.000Z",
            _location = "Innovation Hub, Bangalore",
            prize = "₹1,00,000 + Incubation"
        ),
        EventDto(
            _id = "2",
            _title = "Kiri Tech Summit 2024",
            type = "WORKSHOP",
            _description = "Join top engineering leads to discuss the future of AI and LLMs in production.",
            _date = "2024-07-15T09:00:00.000Z",
            _location = "Virtual via Kiri Portal",
            prize = "Free Kiri Certifications"
        ),
        EventDto(
            _id = "3",
            _title = "Startup Pitch Deck Competition",
            type = "COMPETITION",
            _description = "Pitch your idea to global investors and get a chance to secure seed funding.",
            _date = "2024-08-05T14:00:00.000Z",
            _location = "Main Auditorium",
            prize = "$5000 AWS Credits"
        )
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Section
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                Text(
                    "LIVE EVENTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Ecosystem Hub",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(Modifier.height(16.dp))

            // Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filters) { filter ->
                    FilterChip(
                        label = filter,
                        isSelected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Active Broadcasts Label
            Text(
                "ACTIVE BROADCASTS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            // Events List
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredEvents = events.filter { selectedFilter == "ALL" || it.type == selectedFilter }
                items(filteredEvents) { event ->
                    BroadcastCard(event, onClick = { 
                        val json = Gson().toJson(event)
                        onNavigateToDetail(json)
                    })
                }
            }
        }
    }
}

@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
        border = BorderStroke(1.dp, if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
    }
}

data class EventDisplayItem(
    val title: String,
    val type: String,
    val description: String,
    val count: String,
    val prize: String
)

@Composable
fun BroadcastCard(event: EventDto, onClick: () -> Unit) {
    fun String.clean(): String = try {
        URLDecoder.decode(this.replace("+", " "), "UTF-8")
    } catch (e: Exception) {
        this.replace("+", " ")
    }

    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Badge
                Surface(
                    color = NotionTintLavender,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = (event.type ?: "GENERAL").uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NotionBrandPurple800,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // Event Count / Date Preview
                Text(
                    text = if (event.type == "HACKATHON") "01 EVENT" else "ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            // Title - Highlighted visibility
            Text(
                text = event.title.clean(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (isSystemInDarkTheme()) NotionBrandYellow else NotionPrimary
            )

            Spacer(Modifier.height(4.dp))

            // Description
            Text(
                text = event.description.clean(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prize Info
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "REWARD: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        event.prize?.clean() ?: "TBD",
                        style = MaterialTheme.typography.labelSmall,
                        color = NotionLinkBlue,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                // Action Text
                Text(
                    "VIEW DETAILS →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
