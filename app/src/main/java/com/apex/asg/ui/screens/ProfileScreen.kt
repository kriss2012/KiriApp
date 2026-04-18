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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.components.SectionHeader
import com.apex.asg.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.UserDto
import com.apex.asg.ui.viewmodels.ProfileState
import com.apex.asg.ui.viewmodels.ProfileViewModel
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen(viewModel: ProfileViewModel = viewModel()) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchProfile(userId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgCream,
        bottomBar = { Spacer(Modifier.height(0.dp)) } // Padding already in MainScaffold
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = OrangePrimary
                    )
                }
                is ProfileState.Success -> {
                    ProfileContent(state.user)
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red, fontSize = 14.sp)
                        Button(onClick = { viewModel.fetchProfile(userId) }) {
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
fun ProfileContent(user: UserDto) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 20.dp)
    ) {
        item { ProfileHeroSection(user = user, isVerified = true) }
        item { AchievementChipsRow() }
        item { SectionHeader(title = "My ASG Dashboard", actionText = "", onActionClick = {}) }
        item { DashboardMenu() }
    }
}

@Composable
fun ProfileHeroSection(user: UserDto, isVerified: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(0.dp, 0.dp, 30.dp, 30.dp))
            .background(Brush.linearGradient(listOf(OrangePrimary, Color(0xFFD94D08))))
            .padding(18.dp, 18.dp, 22.dp, 18.dp) // Reduced bottom padding
    ) {
        // Decoration (Subtle)
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-50).dp)
                .background(Color.White.copy(alpha = 0.07f), CircleShape)
        )

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(user.fullName.take(1).uppercase(), style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Black, fontSize = 19.sp)
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(user.fullName, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                        if (isVerified) {
                            Spacer(Modifier.width(4.dp))
                            // Verification Badge Logo
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✓", color = OrangePrimary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Text(user.role, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                    Text("Institution Name", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.6f), fontSize = 9.sp)
                }
                
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White.copy(alpha = 0.5f))
            }

            Spacer(Modifier.height(10.dp)) // Reduced spacer

            // Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                StatBox("92", "AI Score")
                StatBox("4", "Events")
                StatBox("2", "Hackathons")
                StatBox("12", "Connections")
            }
        }
    }
}

@Composable
fun RowScope.StatBox(value: String, label: String) {
    Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(11.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, color = Color.White, fontWeight = FontWeight.Black, fontSize = 17.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.65f), fontWeight = FontWeight.SemiBold, fontSize = 8.sp)
        }
    }
}

@Composable
fun AchievementChipsRow() {
    val achievements = listOf("🥇 SIH Finalist", "🎬 100+ Vlogs", "⭐ Top Creator")
    
    LazyRow(
        modifier = Modifier.padding(10.dp, 14.dp, 10.dp, 2.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        items(achievements) { ach ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(6.dp, 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(ach, style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
            }
        }
    }
}

@Composable
fun DashboardMenu() {
    val menuItems = listOf(
        DashboardMenuItem("🚀", "My Startup Profile", OrangeLight, "Active"),
        DashboardMenuItem("🤝", "Find Team Members", GreenLight),
        DashboardMenuItem("💰", "Funding & Grants", YellowWarm),
        DashboardMenuItem("🎓", "Find a Mentor", PurpleLight),
        DashboardMenuItem("📋", "NAAC / NEP Records", BlueInfo, "New"),
        DashboardMenuItem("🗓️", "Organise a Hackathon", GreenLight)
    )

    Column(modifier = Modifier.padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        menuItems.forEach { item ->
            DashboardMenuCard(item)
        }
    }
}

data class DashboardMenuItem(val icon: String, val label: String, val color: Color, val badge: String? = null)

@Composable
fun DashboardMenuCard(item: DashboardMenuItem) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { },
        shape = RoundedCornerShape(13.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(10.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.color),
                contentAlignment = Alignment.Center
            ) {
                Text(item.icon, fontSize = 13.sp)
            }
            Text(item.label, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 11.sp)
            
            if (item.badge != null) {
                Surface(color = OrangeLight, shape = RoundedCornerShape(5.dp)) {
                    Text(item.badge, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                }
            } else {
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFCCCCCC))
            }
        }
    }
}

