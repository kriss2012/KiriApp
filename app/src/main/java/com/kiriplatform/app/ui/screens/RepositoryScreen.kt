package com.kiriplatform.app.ui.screens

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
import androidx.compose.material.icons.filled.ArrowForward
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
import com.kiriplatform.app.ui.components.ASGTagChip
import com.kiriplatform.app.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.remote.models.UserDto
import com.kiriplatform.app.ui.viewmodels.RepositoryState
import com.kiriplatform.app.ui.viewmodels.RepositoryViewModel

@Composable
fun RepositoryScreen(
    onNavigateToProfile: (String) -> Unit,
    viewModel: RepositoryViewModel = viewModel()
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "STUDENT", "FOUNDER", "INVESTOR", "MENTOR", "SPOC", "ADMIN")
    val uiState by viewModel.uiState.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchVerifiedUsers(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 100.dp) // Space for floating nav
    ) {
        // Premium Gradient Header — matches ProfileHeroSection design token
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                .background(
                    Brush.linearGradient(listOf(OrangePrimary, Color(0xFFD94D08)))
                )
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    "Community",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Network of Jalgaon's brightest minds",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(bottom = 10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 10.sp) },
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

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is RepositoryState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = OrangePrimary)
                }
                is RepositoryState.Success -> {
                    val filteredUsers = if (selectedFilter == "All") state.users else state.users.filter { it.role == selectedFilter }
                    RepositoryContent(filteredUsers, onNavigateToProfile)
                }
                is RepositoryState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = Color.Red, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchVerifiedUsers(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RepositoryContent(users: List<UserDto>, onNavigateToProfile: (String) -> Unit) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        if (users.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Text("No related data found", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
        } else {
            items(users) { user ->
                StudentCard(user = user, onClick = { onNavigateToProfile(user.id) })
            }
        }
    }
}

@Composable
fun StudentCard(user: UserDto, onClick: () -> Unit) {
    val initials = user.fullName.split(" ").filter { it.isNotEmpty() }.take(2).map { it[0] }.joinToString("")
    
    val gradient = when (user.role) {
        "FOUNDER" -> Brush.linearGradient(colors = listOf(OrangePrimary, OrangeDark))
        "SPOC", "ADMIN" -> Brush.linearGradient(colors = listOf(Color(0xFF1D9E75), Color(0xFF0F6E56)))
        "MENTOR" -> Brush.linearGradient(colors = listOf(Color(0xFFE0742A), Color(0xFFB85A15)))
        "INVESTOR" -> Brush.linearGradient(colors = listOf(Color(0xFF378ADD), Color(0xFF185FA5)))
        else -> Brush.linearGradient(colors = listOf(PurpleAccent, Color(0xFF4F3BB5)))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
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
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(user.fullName, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(user.role, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                if (!user.college.isNullOrEmpty()) {
                    Text(user.college ?: "", style = MaterialTheme.typography.labelSmall, color = TextSecondary.copy(alpha = 0.7f), fontSize = 8.sp)
                }
            }

            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFCCCCCC))
        }
    }
}

