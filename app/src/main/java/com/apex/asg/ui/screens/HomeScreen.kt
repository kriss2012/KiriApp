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

import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.EventDto
import com.apex.asg.data.remote.UserDto
import com.apex.asg.ui.viewmodels.HomeState
import com.apex.asg.ui.viewmodels.HomeViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*

@Composable
fun HomeScreen(viewModel: HomeViewModel = viewModel()) {
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
        containerColor = BgCream,
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
                    HomeContent(user = state.user, events = state.upcomingEvents)
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
fun HomeContent(user: UserDto, events: List<EventDto>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 20.dp)
    ) {
        item { HomeTopBar() }
        item { GreetingSection(userName = user.fullName) }
        item { HeroOpportunityCard() }
        item { RepositoriesSection() }
        item { UpcomingEventsSection(events) }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp, 10.dp, 18.dp, 0.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Mini Triangle Logo
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(OrangePrimary) // Simplified triangle
            )
            Text("ASG", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ASGIconBadge(icon = "🔍", backgroundColor = OrangeLight)
            ASGIconBadge(icon = "🔔", backgroundColor = OrangeLight)
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Column(modifier = Modifier.padding(18.dp, 10.dp)) {
        Text("Good Morning 👋", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        Text(userName, style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(6.dp))
        AIStatusChip()
    }
}

@Composable
fun HeroOpportunityCard() {
    Card(
        modifier = Modifier
            .padding(14.dp, 12.dp)
            .fillMaxWidth()
            .height(160.dp),
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
            // Glassmorphism circle decoration
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-40).dp)
                    .background(Color.White.copy(alpha = 0.08f), CircleShape)
            )

            Column(modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        "🚀 FEATURED OPPORTUNITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.75f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Smart India Hackathon 2025 is live!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "₹1 lakh prize · Deadline: Apr 20",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(9.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.35f)),
                    modifier = Modifier.clickable { }
                ) {
                    Text(
                        "Apply Now →",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                horizontalAlignment = Alignment.End
            ) {
                Text("2.4k", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Black)
                Text("registered", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun RepositoriesSection() {
    val repos = listOf(
        RepoItem("🎬", "Content Creators", "1,240", OrangeLight),
        RepoItem("🏆", "Participants", "3,180", PurpleLight),
        RepoItem("🎯", "Organisers", "860", GreenLight),
        RepoItem("🎓", "Alumni", "2,100", YellowWarm),
        RepoItem("👨‍🏫", "Professors", "420", BlueInfo)
    )

    Column {
        SectionHeader(title = "5 Repositories", actionText = "View all", onActionClick = {})
        LazyRow(
            modifier = Modifier.padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(repos) { item ->
                RepositoryCard(item)
            }
        }
    }
}

data class RepoItem(val icon: String, val name: String, val count: String, val color: Color)

@Composable
fun RepositoryCard(item: RepoItem) {
    Card(
        modifier = Modifier
            .width(90.dp)
            .height(110.dp),
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
            Text(item.count + " members", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 8.sp)
        }
    }
}

@Composable
fun UpcomingEventsSection(events: List<EventDto>) {
    Column {
        SectionHeader(title = "Upcoming Events", actionText = "See all", onActionClick = {})
        Column(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            if (events.isEmpty()) {
                Text("No upcoming events found", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            } else {
                events.forEach { event ->
                    EventItemCard(
                        day = event.date.split("-").lastOrNull() ?: "01",
                        month = "Apr",
                        title = event.title,
                        location = "ASG Community",
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

