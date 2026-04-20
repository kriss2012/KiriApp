package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.components.AIStatusChip
import com.apex.asg.ui.components.ASGIconBadge
import com.apex.asg.ui.components.SectionHeader
import com.apex.asg.ui.theme.*
import androidx.compose.runtime.collectAsState

import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.EventDto
import com.apex.asg.data.remote.UserDto
import com.apex.asg.ui.viewmodels.HomeState
import com.apex.asg.ui.viewmodels.HomeViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToRepository: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadHomeData(userId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,
        bottomBar = { Spacer(Modifier.height(0.dp)) } // Padding in MainScaffold
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is HomeState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OrangePrimary
                    )
                }
                is HomeState.Success -> {
                    HomeContent(
                        user = state.user, 
                        events = state.upcomingEvents,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToSearch = onNavigateToSearch,
                        onNavigateToRepository = onNavigateToRepository,
                        onNavigateToEvents = onNavigateToEvents
                    )
                }
                is HomeState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red, fontSize = 14.sp)
                        Button(onClick = { viewModel.loadHomeData(userId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    user: UserDto, 
    events: List<EventDto>,
    onNavigateToNotifications: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToRepository: () -> Unit,
    onNavigateToEvents: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(bottom = 100.dp) // Space for floating nav
    ) {
        item { HomeTopBar(onNavigateToNotifications = onNavigateToNotifications) }
        item { GreetingSection(userName = user.fullName) }
        item { InnovationProgressCard(points = user.points) }
        item { DiscoverCommunityCard(onNavigateToSearch = onNavigateToSearch) }
        item { InnovationHubCard(onNavigateToHub = { /* TODO: onNavigateToHub */ }) }
        item { RepositoriesSection(onNavigateToRepository = onNavigateToRepository) }
        item { UpcomingEventsSection(events, onNavigateToEvents = onNavigateToEvents) }
    }
}

@Composable
fun InnovationHubCard(onNavigateToHub: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(14.dp, 8.dp)
            .fillMaxWidth()
            .clickable { onNavigateToHub() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BluePrimary)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("INNOVATION HUB", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), letterSpacing = 1.sp)
                Text("Ecosystem Dashboard", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black)
                Text("Access Marketplace, Matchmaker & more.", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
            }
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun InnovationProgressCard(points: Int) {
    Card(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔥", fontSize = 24.sp)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Innovation Rank", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                Text("Vanguard Level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary)
                LinearProgressIndicator(
                    progress = { (points % 1000) / 1000f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).padding(vertical = 4.dp),
                    color = OrangePrimary,
                    trackColor = OrangeLight
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(points.toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = OrangePrimary)
                Text("POINTS", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 8.sp)
            }
        }
    }
}

@Composable
fun HomeTopBar(onNavigateToNotifications: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp, 16.dp, 24.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("APEX", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = TextPrimary, letterSpacing = 2.sp)
            Text("Ecosystem", style = MaterialTheme.typography.labelSmall, color = TextSecondary, letterSpacing = 1.sp)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onNavigateToNotifications) {
                ASGIconBadge(icon = "🔔", backgroundColor = Color.White)
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Column(modifier = Modifier.padding(24.dp, 24.dp)) {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Rising high, 🌅"
            in 12..16 -> "Still building, ☀️"
            else -> "Planning tomorrow, 🌙"
        }
        Text(greeting, style = MaterialTheme.typography.labelMedium, color = OrangePrimary, fontWeight = FontWeight.Bold)
        Text(userName.split(" ").firstOrNull() ?: userName, style = MaterialTheme.typography.displaySmall, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 36.sp)
        Spacer(Modifier.height(12.dp))
        AIStatusChip()
    }
}

@Composable
fun DiscoverCommunityCard(onNavigateToSearch: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(14.dp, 12.dp)
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = OrangePrimary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(OrangePrimary, Color(0xFFE04F0A)),
                        start = androidx.compose.ui.geometry.Offset.Zero,
                        end = androidx.compose.ui.geometry.Offset.Infinite
                    )
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        "ASG COMMUNITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Connect with innovators in your region.",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                }
                
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(9.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { onNavigateToSearch() }
                ) {
                    Text(
                        "Discover Members →",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun RepositoriesSection(onNavigateToRepository: () -> Unit) {
    val repos = listOf(
        RepoItem("🎬", "Content Creators", "0", OrangeLight),
        RepoItem("🏆", "Students", "0", PurpleLight),
        RepoItem("🎯", "Organisers", "0", GreenLight)
    )

    Column {
        SectionHeader(title = "Community Repositories", actionText = "See all", onActionClick = onNavigateToRepository)
        LazyRow(
            modifier = Modifier.padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(repos) { item ->
                RepositoryCard(item, onClick = onNavigateToRepository)
            }
        }
    }
}

data class RepoItem(val icon: String, val name: String, val count: String, val color: Color)

@Composable
fun RepositoryCard(item: RepoItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.color),
                contentAlignment = Alignment.Center
            ) {
                Text(item.icon, fontSize = 15.sp)
            }
            Spacer(Modifier.height(5.dp))
            Text(
                item.name,
                style = MaterialTheme.typography.labelSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 11.sp
            )
        }
    }
}

@Composable
fun UpcomingEventsSection(events: List<EventDto>, onNavigateToEvents: () -> Unit) {
    Column {
        SectionHeader(title = "Upcoming Events", actionText = "See all", onActionClick = onNavigateToEvents)
        Column(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (events.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Text(
                        "No related data found", 
                        modifier = Modifier.padding(24.dp).fillMaxWidth(), 
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall, 
                        color = TextSecondary
                    )
                }
            } else {
                events.forEach { event ->
                    EventItemCard(
                        day = event.date.split("-").lastOrNull() ?: "01",
                        month = "Apr",
                        title = event.title,
                        location = "Jalgaon, MH",
                        tag = "Live",
                        tagBg = OrangeLight,
                        tagText = OrangeDark
                    )
                }
            }
        }
    }
}

@Composable
fun EventItemCard(day: String, month: String, title: String, location: String, tag: String, tagBg: Color, tagText: Color) {
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp, 38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(OrangeLight),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(day, style = MaterialTheme.typography.titleMedium, color = OrangePrimary, fontWeight = FontWeight.Black, lineHeight = 14.sp)
                    Text(month.uppercase(), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold, fontSize = 7.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(location, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 9.sp)
            }
            Surface(color = tagBg, shape = RoundedCornerShape(6.dp)) {
                Text(tag, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = tagText, fontWeight = FontWeight.Bold, fontSize = 8.sp)
            }
        }
    }
}