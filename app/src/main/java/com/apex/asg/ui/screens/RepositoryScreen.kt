package com.apex.asg.ui.screens

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
import com.apex.asg.ui.components.ASGTagChip
import com.apex.asg.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.remote.UserDto
import com.apex.asg.ui.viewmodels.RepositoryState
import com.apex.asg.ui.viewmodels.RepositoryViewModel

@Composable
fun RepositoryScreen(viewModel: RepositoryViewModel = viewModel()) {
    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "STUDENT", "FOUNDER", "INVESTOR", "MENTOR", "SPOC", "ADMIN")
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(18.dp, 10.dp)) {
            Text("Student Repository", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, fontWeight = FontWeight.Black)
            Text("Live community directory", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }

        // Search Bar (Static for now)
        Card(
            modifier = Modifier
                .padding(14.dp, 10.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(9.dp, 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(20.dp), tint = TextSecondary)
                Text("Search community members...", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                    RepositoryContent(filteredUsers)
                }
                is RepositoryState.Error -> {
                    Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun RepositoryContent(users: List<UserDto>) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        if (users.isEmpty()) {
            item {
                Text("No members found in this category", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        } else {
            items(users) { user ->
                StudentCard(user)
            }
        }
    }
}


@Composable
fun StudentCard(user: UserDto) {
    val initials = user.fullName.split(" ").filter { it.isNotEmpty() }.take(2).map { it[0] }.joinToString("")
    
    val gradient = when (user.role) {
        "FOUNDER" -> Brush.linearGradient(colors = listOf(OrangePrimary, OrangeDark))
        "SPOC", "ADMIN" -> Brush.linearGradient(colors = listOf(Color(0xFF1D9E75), Color(0xFF0F6E56)))
        "MENTOR" -> Brush.linearGradient(colors = listOf(Color(0xFFE0742A), Color(0xFFB85A15)))
        "INVESTOR" -> Brush.linearGradient(colors = listOf(Color(0xFF378ADD), Color(0xFF185FA5)))
        else -> Brush.linearGradient(colors = listOf(PurpleAccent, Color(0xFF4F3BB5)))
    }

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
            horizontalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(gradient),
                contentAlignment = Alignment.Center
            ) {
                Text(initials, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Black, fontSize = 13.sp)
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(user.fullName, style = MaterialTheme.typography.bodySmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                Text(user.role, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 9.sp)
                Spacer(Modifier.height(4.dp))
                ASGTagChip(text = "Verified Member", backgroundColor = GreenLight, textColor = GreenSuccess)
            }

            // Score (Mock for production feel)
            Column(horizontalAlignment = Alignment.End) {
                Text("90", style = MaterialTheme.typography.titleMedium, color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 15.sp)
                Text("Trust Score", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 8.sp)
            }
        }
    }
}

