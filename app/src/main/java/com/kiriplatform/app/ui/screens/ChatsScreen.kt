package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.*
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
        containerColor = BgCream,
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Messages",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Search Bar
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.searchUsers(it) },
                    placeholder = { Text("Search community agents or people...", color = TextSecondary.copy(alpha = 0.5f), fontSize = 14.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = OrangePrimary) },
                    trailingIcon = { if (query.isNotEmpty()) IconButton(onClick = { viewModel.searchUsers("") }) { Icon(Icons.Default.Close, null) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BgCream.copy(alpha = 0.5f),
                        unfocusedContainerColor = BgCream.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = OrangePrimary.copy(alpha = 0.3f)
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
                item { Text("Search Results", style = MaterialTheme.typography.labelMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp)) }
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
                    item { Text("No results for '$query'", modifier = Modifier.padding(16.dp), color = TextSecondary) }
                }
            } else {
                // PINNED: Kiri AI
                item {
                    Text("Pinned Assistant", style = MaterialTheme.typography.labelMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
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
                        Text("Recent Conversations", style = MaterialTheme.typography.labelMedium, color = TextSecondary, modifier = Modifier.padding(bottom = 4.dp))
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
                                Icon(Icons.Default.ChatBubbleOutline, null, modifier = Modifier.size(48.dp), tint = TextSecondary.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("No peer chats yet.\nStart by discovering community members!", color = TextSecondary, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 0.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = CircleShape,
                    color = if (isAgent) OrangePrimary.copy(alpha = 0.1f) else BgCream
                ) {
                    if (isAgent) {
                        Icon(
                            Icons.Default.SmartToy,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = OrangePrimary
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(name.take(1).uppercase(), color = OrangePrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                    }
                }
                if (isAgent) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
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
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isAgent) OrangePrimary else TextSecondary.copy(alpha = 0.6f)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = lastMsg,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (unreadCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = OrangePrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(20.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(unreadCount.toString(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}
