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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
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

    val events = listOf(
        EventDto(
            _id = "1",
            _title = "Kiri AI Innovation Summit 2026",
            type = "HACKATHON",
            _description = "Join developers, founders, and creators to showcase next-generation AI platforms, agents, and local language models.",
            _date = "2026-07-07T10:00:00.000Z",
            _location = "Virtual / Kiri Hub",
            coordinatorName = "Aditi Sharma",
            coordinatorPhone = "+91 98765 43210",
            prize = "₹5,00,000 + Incubation",
            imageUrl = "https://images.unsplash.com/photo-1540575467063-178a50c2df87?w=800&auto=format&fit=crop",
            registrationLink = "https://forms.gle/KiriSummit2026"
        ),
        EventDto(
            _id = "2",
            _title = "Global Builders Hackathon",
            type = "COMPETITION",
            _description = "A 48-hour virtual hackathon focused on building open-source projects, peer review, and developer collaboration.",
            _date = "2026-07-24T14:00:00.000Z",
            _location = "Kiri Sandbox / Discord",
            coordinatorName = "Rohan Verma",
            coordinatorPhone = "+91 99999 88888",
            prize = "$10,000 Seed Grant",
            imageUrl = "https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800&auto=format&fit=crop",
            registrationLink = "https://forms.gle/KiriHackathon2026"
        ),
        EventDto(
            _id = "3",
            _title = "Startup Pitch Deck Workshop",
            type = "WORKSHOP",
            _description = "Pitch your idea to global investors and get a chance to secure seed funding.",
            _date = "2026-08-05T14:00:00.000Z",
            _location = "Main Auditorium",
            coordinatorName = "ASG Core Team",
            coordinatorPhone = null,
            prize = "$5000 AWS Credits",
            imageUrl = "https://images.unsplash.com/photo-1515187029135-18ee286d815b?w=800&auto=format&fit=crop",
            registrationLink = "https://forms.gle/KiriWorkshop2026"
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
        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
        java.net.URLDecoder.decode(this.replace("+", " "), "UTF-8")
    } catch (e: Exception) {
        this.replace("+", " ")
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSystemInDarkTheme()) NotionBrandNavyMid.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column {
            if (!event.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = "Event Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(16.dp)) {
                // Top Row: Category and Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                val dark = isSystemInDarkTheme()
                Surface(
                    color = if (dark) NotionTintLavenderDark else NotionTintLavender,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = (event.type ?: "GENERAL").uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (dark) NotionOnTintDark else NotionBrandPurple800,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                
                Text(
                    text = if (event.type == "HACKATHON") "01 EVENT" else "ACTIVE",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(Modifier.height(14.dp))

            // Title
            Text(
                text = event.title.clean(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (isSystemInDarkTheme()) NotionBrandYellow else NotionPrimary,
                letterSpacing = (-0.3).sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(6.dp))

            // Description
            Text(
                text = event.description.clean(),
                style = MaterialTheme.typography.bodySmall,
                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                lineHeight = 18.sp,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(
                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.1f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), 
                thickness = 0.5.dp
            )
            Spacer(Modifier.height(16.dp))

            // Bottom Row: Reward and View Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reward Section - Using a Column or a Row with weight to ensure no overlap
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "REWARD: ",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = event.prize?.clean() ?: "TBD",
                        style = MaterialTheme.typography.labelSmall,
                        color = NotionLinkBlue,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.width(16.dp)) // Guaranteed gap

                Text(
                    text = "VIEW DETAILS →",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSystemInDarkTheme()) NotionBrandYellow else MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
            }
        }
    }
}
}
