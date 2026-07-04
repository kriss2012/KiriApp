package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.HeartBroken
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*

data class Hackathon(
    val id: String,
    val title: String,
    val organizer: String,
    val teamSize: String,
    val mode: String,
    val tags: List<String>,
    val date: String,
    val timeLeft: String,
    val category: String, // e.g. "Applied AI", "Web3", "Others"
    val isFavorite: Boolean = false,
    val logoText: String = "AI"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipateScreen(
    onNavigateToAddEvent: () -> Unit = {},
    onNavigateToEventDetails: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    
    val initialHackathons = remember {
        listOf(
            Hackathon(
                id = "1",
                title = "AI for Bharat Hackathon 2026 – Build AI Solutions f...",
                organizer = "Indraprastha Institute of Information Technology, Delhi",
                teamSize = "Individual Participation",
                mode = "Online",
                tags = listOf("Applied AI", "Undergraduate", "+6"),
                date = "Jul 1, 2026",
                timeLeft = "10 days left",
                category = "Applied AI",
                logoText = "AB"
            ),
            Hackathon(
                id = "2",
                title = "MSOC 2026 — MSTC Summer of Code Data...",
                organizer = "Dhirubhai Ambani Institute of Information Technology, Gandhinagar",
                teamSize = "1 - 2 Members",
                mode = "Online",
                tags = listOf("Applied AI", "Data Science", "+1"),
                date = "Jul 1, 2026",
                timeLeft = "8 days left",
                category = "Applied AI",
                logoText = "MS"
            ),
            Hackathon(
                id = "3",
                title = "Grand Hack IPEC",
                organizer = "HackShastra - Inderprastha Engineering College, Ghaziabad",
                teamSize = "2 - 4 Members",
                mode = "Inderprastha Engineering College, Ghaziabad, UP, India",
                tags = listOf("Others", "Everyone can apply"),
                date = "Jun 27, 2026",
                timeLeft = "2 months left",
                category = "Others",
                logoText = "GH"
            ),
            Hackathon(
                id = "4",
                title = "Port Mortem 2026 | Code Resurrection Hackathon",
                organizer = "Hackathon Raptors",
                teamSize = "1 - 4 Members",
                mode = "Online",
                tags = listOf("Others", "Everyone can apply"),
                date = "Jun 25, 2026",
                timeLeft = "27 days left",
                category = "Others",
                logoText = "PM"
            )
        )
    }

    var hackathons by remember { mutableStateOf(initialHackathons) }

    val filteredList = remember(searchQuery, selectedFilter, hackathons) {
        hackathons.filter { hackathon ->
            val matchesSearch = hackathon.title.contains(searchQuery, ignoreCase = true) ||
                                hackathon.organizer.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedFilter == "ALL" || hackathon.category == selectedFilter
            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Participate in Hackathons", 
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    ) 
                },
                actions = {
                    IconButton(onClick = onNavigateToAddEvent) {
                        Icon(Icons.Default.Add, contentDescription = "Add Hackathon", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0),
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Search Input
            com.kiriplatform.app.ui.components.UnifiedSearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search Hackathons...",
                customModifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Filter Pills Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text("Filters (2)") },
                        leadingIcon = { Icon(Icons.Default.FilterList, null, modifier = Modifier.size(16.dp)) }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "Applied AI",
                        onClick = { selectedFilter = if (selectedFilter == "Applied AI") "ALL" else "Applied AI" },
                        label = { Text("Applied AI") }
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == "Others",
                        onClick = { selectedFilter = if (selectedFilter == "Others") "ALL" else "Others" },
                        label = { Text("Others") }
                    )
                }
            }

            // Hackathons List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (filteredList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No hackathons found matching filters.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    items(filteredList) { hackathon ->
                        HackathonCard(
                            hackathon = hackathon,
                            onFavoriteToggle = { id ->
                                hackathons = hackathons.map {
                                    if (it.id == id) it.copy(isFavorite = !it.isFavorite) else it
                                }
                            },
                            onClick = {
                                // Navigate to Detail using mock event serialization
                                val mockJson = """{"title":"${hackathon.title}","description":"Organizer: ${hackathon.organizer}. Mode: ${hackathon.mode}. Team Size: ${hackathon.teamSize}.","date":"${hackathon.date}","location":"${hackathon.mode}","imageUrl":"https://images.unsplash.com/photo-1504384308090-c894fdcc538d?w=800","registrationLink":"https://forms.gle/4w7H8L9J1o2P3q4R5"}"""
                                onNavigateToEventDetails(mockJson)
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HackathonCard(
    hackathon: Hackathon,
    onFavoriteToggle: (String) -> Unit,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hackathon Organizer Logo Placeholder
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = hackathon.logoText,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Title and Organizer Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hackathon.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = hackathon.organizer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata: Team Size & Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = "Team Size",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = hackathon.teamSize,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Mode",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = if (hackathon.mode.length > 25) "Venue: Offline" else hackathon.mode,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tags (Applied AI, Undergraduate, etc.)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                hackathon.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            Spacer(modifier = Modifier.height(12.dp))

            // Date & Time Left + Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = hackathon.date,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassEmpty,
                            contentDescription = "Time Left",
                            tint = NotionBrandPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = hackathon.timeLeft,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = NotionBrandPink
                        )
                    }
                }

                IconButton(
                    onClick = { onFavoriteToggle(hackathon.id) }
                ) {
                    Icon(
                        imageVector = if (hackathon.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Bookmark",
                        tint = if (hackathon.isFavorite) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
