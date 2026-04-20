package com.apex.asg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.UserResponse
import com.apex.asg.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(onNavigateToProfile: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }
    var userResults by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val categories = listOf("ALL", "STUDENT", "FOUNDER", "INVESTOR", "MENTOR", "SERVICE_PROVIDER")

    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.apex.asg.data.SessionManager.getInstance(context) }
    val currentUserId = sessionManager.getUserId() ?: ""

    fun performSearch() {
        coroutineScope.launch {
            isLoading = true
            try {
                val results = ApiClient.service.getUsers(
                    name = if (searchQuery.isNotEmpty()) searchQuery else null,
                    role = if (selectedCategory != "ALL") selectedCategory else null
                )
                userResults = results.filter { it.id != currentUserId }
            } catch (e: Exception) {
                userResults = emptyList()
            }
            isLoading = false
        }
    }

    // Initial load and on parameter change
    LaunchedEffect(selectedCategory) {
        performSearch()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 24.dp, start = 24.dp, end = 24.dp)
    ) {
        Text(
            "Community Discovery",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Text(
            "Find and connect with fellow students and mentors in Jalgaon",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { 
                searchQuery = it 
                performSearch()
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search members, colleges...", style = MaterialTheme.typography.bodySmall) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            maxLines = 1,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = BorderColor
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Chips
        androidx.compose.foundation.lazy.LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { 
                        Text(
                            category.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall
                        ) 
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = BorderColor,
                        enabled = true,
                        selected = selectedCategory == category
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (userResults.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No related data found", color = TextSecondary)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(userResults) { user ->
                    UserSearchItem(user = user, onClick = { onNavigateToProfile(user.id) })
                }
            }
        }
    }
}

@Composable
fun UserSearchItem(user: UserResponse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(OrangePrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.fullName.take(1).uppercase(),
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = user.role,
                    style = MaterialTheme.typography.bodySmall,
                    color = OrangePrimary
                )
                if (user.college != null) {
                    Text(
                        text = user.college,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
