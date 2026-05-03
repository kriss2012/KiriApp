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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 100.dp) // Space for floating nav
    ) {
        // Notion Style Header
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Text(
                    "NETWORK",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Community",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Network of Jalgaon's brightest minds",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Filter Chips
        LazyRow(
            modifier = Modifier.padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    onClick = { selectedFilter = filter },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            filter.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }

        // Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is RepositoryState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
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
                        Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchVerifiedUsers(context) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
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
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (users.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Text("No related data found", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        initials,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    user.role,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (!user.college.isNullOrEmpty()) {
                    Text(
                        user.college ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        lineHeight = 14.sp
                    )
                }
            }

            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
        }
    }
}

