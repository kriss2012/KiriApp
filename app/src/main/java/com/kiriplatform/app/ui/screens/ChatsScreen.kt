package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.ui.viewmodels.ChatListViewModel
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.data.remote.models.ConversationResponse
import com.kiriplatform.app.data.remote.models.UserDto
import com.kiriplatform.app.data.remote.models.AiMessageResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    onNavigateToChat: (String) -> Unit,
    onNavigateToAI: () -> Unit,
    viewModel: ChatListViewModel = viewModel()
) {
    val conversations by viewModel.conversations.collectAsState()
    val aiLatest by viewModel.aiLatest.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchChatHub()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "MESSAGES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Conversations",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(20.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.searchUsers(it) },
                    placeholder = { Text("Search community agents or people...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                    trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { viewModel.searchUsers("") }) { Icon(Icons.Default.Close, null) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    ),
                    singleLine = true
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 100.dp, top = 16.dp)
        ) {
            // Search Results Override
            if (query.isNotEmpty()) {
                item { Text("Search Results", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp)) }
                items(searchResults) { user ->
                    ChatListItem(
                        name = user.fullName,
                        lastMsg = "Tap to message ${user.role.lowercase()}",
                        time = "",
                        unreadCount = 0,
                        isAgent = false,
                        onClick = { onNavigateToChat(user.id) }
                    )
                }
                if (searchResults.isEmpty() && !isLoading) {
                    item { Text("No results for '$query'", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
            } else {
                // PINNED: Kiri AI
                item {
                    Text("Pinned Assistant", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                }
                item {
                    ChatListItem(
                        name = "Kiri AI",
                        lastMsg = aiLatest?.content ?: "Your agentic orchestrator is ready.",
                        time = "Agent",
                        unreadCount = 0,
                        isAgent = true,
                        onClick = onNavigateToAI
                    )
                }

                // Recent Chats
                if (conversations.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recent Conversations", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                    }
                    items(conversations) { convo ->
                        val otherUser = convo.otherUser
                        val lastMessage = convo.lastMessage
                        if (otherUser != null) {
                            ChatListItem(
                                name = otherUser.fullName,
                                lastMsg = lastMessage?.content ?: "",
                                time = "Active", // TODO: Format timestamp
                                unreadCount = convo.unreadCount ?: 0,
                                isAgent = false,
                                onClick = { onNavigateToChat(otherUser.id) }
                            )
                        }
                    }
                } else if (!isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.ChatBubbleOutline, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No peer chats yet.\nStart by discovering community members!", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(
    name: String,
    lastMsg: String,
    time: String,
    unreadCount: Int,
    isAgent: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isAgent) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, if (isAgent) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    if (isAgent) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.SmartToy,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(name.take(1).uppercase(), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                    }
                }
                if (isAgent) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(12.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
                    ) {
                        Box(modifier = Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF4CAF50)))
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Body
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = time.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isAgent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                        letterSpacing = 0.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lastMsg,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (unreadCount > 0) {
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(8.dp)
                ) {}
            }
        }
    }
}
