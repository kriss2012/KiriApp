package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.ConnectionDto
import com.kiriplatform.app.data.remote.models.UserDto
import com.kiriplatform.app.ui.theme.*
import kotlinx.coroutines.launch
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkScreen(
    onNavigateToProfile: (String) -> Unit,
    onNavigateToChat: (String) -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val currentUserId = sessionManager.getUserId() ?: ""
    val scope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("ALL") }
    val roles = listOf("ALL", "STUDENT", "FOUNDER", "MENTOR", "SERVICE_PROVIDER")

    var isConnectionsLoading by remember { mutableStateOf(true) }
    var isSearchLoading by remember { mutableStateOf(false) }

    var connections by remember { mutableStateOf<List<ConnectionDto>>(emptyList()) }
    var searchResults by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var connectionStatusMap by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var hiddenIds by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Helper to refresh connections list and update mapping
    fun loadConnections() {
        scope.launch {
            isConnectionsLoading = true
            try {
                val conns = ApiClient.service.getUserConnections(currentUserId)
                connections = conns
                connectionStatusMap = conns.associate { conn ->
                    val otherId = if (conn.senderId == currentUserId) conn.receiverId ?: "" else conn.senderId ?: ""
                    otherId to conn.status
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isConnectionsLoading = false
            }
        }
    }

    // Helper to load search results
    fun executeSearch() {
        scope.launch {
            isSearchLoading = true
            try {
                // Call standard users API search by name/role
                val resultsResponse = try {
                    ApiClient.service.getUsers(
                        name = if (searchQuery.isNotBlank()) searchQuery else null,
                        role = if (selectedRole != "ALL") selectedRole else null
                    )
                } catch (e: Exception) {
                    emptyList()
                }

                // Call chat query search by name/email/github (only if query not empty)
                val resultsSearch = try {
                    if (searchQuery.isNotBlank()) {
                        ApiClient.service.searchUsers(searchQuery)
                    } else {
                        emptyList()
                    }
                } catch (e: Exception) {
                    emptyList()
                }

                // Merge lists by ID
                val mergedList = mutableListOf<UserDto>()
                mergedList.addAll(resultsSearch)
                resultsResponse.forEach { resp ->
                    if (mergedList.none { it.id == resp.id }) {
                        mergedList.add(
                            UserDto(
                                _id = resp.id,
                                _fullName = resp.fullName,
                                _role = resp.role,
                                college = resp.college,
                                bio = resp.bio,
                                avatarUrl = resp.avatarUrl,
                                githubUrl = null
                            )
                        )
                    }
                }

                // Filter out current user and match role filter
                searchResults = mergedList.filter { user ->
                    user.id != currentUserId && (
                        selectedRole == "ALL" || 
                        user.role.equals(selectedRole, ignoreCase = true) || 
                        user.userCategory.equals(selectedRole, ignoreCase = true)
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSearchLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadConnections()
    }

    LaunchedEffect(searchQuery, selectedRole) {
        executeSearch()
    }

    val pendingIncoming = connections.filter {
        it.status == "PENDING" && it.receiverId == currentUserId && it.id !in hiddenIds
    }

    val acceptedConnections = connections.filter { it.status == "ACCEPTED" }.mapNotNull { conn ->
        val otherUser = if (conn.senderId == currentUserId) conn.receiver else conn.sender
        otherUser
    }.filter { user ->
        selectedRole == "ALL" || 
        user.role.equals(selectedRole, ignoreCase = true) || 
        user.userCategory.equals(selectedRole, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        // ─── Header ───────────────────────────────────────────────
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 0.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                Text(
                    "Network",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Grow and manage your professional nodes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "Search by name or GitHub handle…",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(Modifier.height(16.dp))

            // Role filters LazyRow
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(roles) { role ->
                    FilterChip(
                        selected = selectedRole == role,
                        onClick = { selectedRole = role },
                        label = {
                            Text(
                                role.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        border = null,
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // Main Content Area
            if (isConnectionsLoading || isSearchLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    item {
                        Text(
                            "Discover Professionals",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    if (searchResults.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No members found to discover",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else {
                        items(searchResults) { user ->
                            val status = connectionStatusMap[user.id]
                            DiscoverUserCard(
                                user = user,
                                connectionStatus = status,
                                onClick = { onNavigateToProfile(user.id) },
                                onConnect = {
                                    scope.launch {
                                        try {
                                            ApiClient.service.sendConnectionRequest(
                                                mapOf("senderId" to currentUserId, "receiverId" to user.id)
                                            )
                                            connectionStatusMap = connectionStatusMap + (user.id to "PENDING")
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                },
                                onMessage = { onNavigateToChat(user.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PendingRequestCard(
    senderName: String,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    senderName.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    senderName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    "Wants to connect with you",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onDecline, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Close,
                    null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(onClick = onAccept, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Check,
                    null,
                    tint = Color(0xFF2E7D32),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun DiscoverUserCard(
    user: UserDto,
    connectionStatus: String?,
    onClick: () -> Unit,
    onConnect: () -> Unit,
    onMessage: () -> Unit
) {
    val gitUsername = remember(user.githubUrl) {
        val url = user.githubUrl
        if (!url.isNullOrBlank()) {
            val name = url.removeSuffix("/").substringAfterLast("/").trim()
            if (name.isNotEmpty() && name != "github.com") name else null
        } else null
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!user.avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val firstChar = user.fullName.trim().firstOrNull()?.toString()?.uppercase() ?: "?"
                    Text(
                        text = firstChar,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.fullName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    buildString {
                        append(user.role.lowercase().replaceFirstChar { it.uppercase() })
                        if (!user.college.isNullOrEmpty()) append(" · ${user.college}")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                if (gitUsername != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "github: @$gitUsername",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            when (connectionStatus) {
                "ACCEPTED" -> {
                    IconButton(
                        onClick = onMessage,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Message",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                "PENDING" -> {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            "Pending",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                else -> {
                    OutlinedButton(
                        onClick = onConnect,
                        modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        border = null,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.PersonAdd,
                            null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Connect",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
