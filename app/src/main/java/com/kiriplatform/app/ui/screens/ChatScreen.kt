package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.models.MessageDto
import com.kiriplatform.app.ui.viewmodels.ChatState
import com.kiriplatform.app.ui.viewmodels.ChatViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*

import org.json.JSONObject
import com.kiriplatform.app.data.remote.SocketHandler
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.ui.components.ClickableUrlText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    receiverId: String = "admin-support", // Default
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val currentUserId = sessionManager.getUserId() ?: ""
    
    var messageText by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    var connectionStatus by remember { mutableStateOf<String?>(null) }
    var receiverName by remember { mutableStateOf("User") }
    val coroutineScope = rememberCoroutineScope()
    
    val roomId = remember(currentUserId, receiverId) {
        listOf(currentUserId, receiverId).sorted().joinToString("_")
    }

    LaunchedEffect(currentUserId, receiverId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.fetchHistory(context, currentUserId, receiverId)
            
            // Socket Integration
            SocketHandler.joinRoom(roomId)
            SocketHandler.listenForMessages { data ->
                val senderId = data.optString("senderId")
                
                // Only add if it's from the other person (to avoid double entry)
                if (senderId != currentUserId) {
                    val content = data.optString("content")
                    viewModel.addMessageLocally(
                        MessageDto(
                            _id = System.currentTimeMillis().toString(),
                            _senderId = senderId,
                            _receiverId = currentUserId,
                            _content = content,
                            _createdAt = java.util.Date().toString(),
                            _isRead = false
                        )
                    )
                }
            }

            try {
                val receiver = ApiClient.service.getProfile(receiverId)
                receiverName = receiver.fullName
                
                // Fetch connection status
                val connections = ApiClient.service.getUserConnections(currentUserId)
                val existing = connections.find { 
                    (it.senderId == currentUserId && it.receiverId == receiverId) || 
                    (it.senderId == receiverId && it.receiverId == currentUserId)
                }
                connectionStatus = existing?.status
            } catch (e: Exception) {}
        }
    }

    Scaffold(
        containerColor = BgCream,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(receiverName.take(1).uppercase(), color = OrangePrimary, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(receiverName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                if (connectionStatus == "ACCEPTED") "Connected" else "Message Request",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (connectionStatus == "ACCEPTED") Color(0xFF4CAF50) else TextSecondary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(androidx.compose.material.icons.Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White, 
                tonalElevation = 8.dp,
                modifier = Modifier
                    .navigationBarsPadding()
                    .imePadding()
            ) {
                Column {
                    // Pending Request Banner
                    if (connectionStatus == "PENDING") {
                        Surface(
                            color = OrangePrimary.copy(alpha = 0.05f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp, 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("This is a message request", fontSize = 12.sp, color = TextSecondary)
                                TextButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            try {
                                                // PERMANENT FIX: Send the actual Connection ID instead of raw user IDs
                                                val connections = ApiClient.service.getUserConnections(currentUserId)
                                                val existing = connections.find { 
                                                    (it.senderId == currentUserId && it.receiverId == receiverId) || 
                                                    (it.senderId == receiverId && it.receiverId == currentUserId)
                                                }
                                                existing?.let {
                                                    ApiClient.service.acceptConnectionRequest(mapOf("connectionId" to it.id))
                                                    connectionStatus = "ACCEPTED"
                                                }
                                            } catch (e: Exception) {}
                                        }
                                    }
                                ) {
                                    Text("Accept", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            placeholder = { Text("Message...", color = TextSecondary) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageText.isNotEmpty()) {
                                    viewModel.sendMessage(currentUserId, receiverId, messageText)
                                    SocketHandler.sendMessage(roomId, currentUserId, receiverId, messageText)
                                    messageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(OrangePrimary),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(BgCream)) {
            when (val state = uiState) {
                is ChatState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = OrangePrimary)
                }
                is ChatState.Success -> {
                    if (state.messages.isEmpty()) {
                        EmptyChatState(receiverName)
                    } else {
                        val listState = rememberLazyListState()
                        
                        LaunchedEffect(state.messages.size) {
                            if (state.messages.isNotEmpty()) {
                                listState.animateScrollToItem(state.messages.size - 1)
                            }
                        }

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                        ) {
                            items(state.messages, key = { it.id }) { msg ->
                                androidx.compose.animation.AnimatedVisibility(
                                    visible = true,
                                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically()
                                ) {
                                    ModernMessageBubble(msg, currentUserId)
                                }
                            }
                        }
                    }
                }
                is ChatState.Error -> {
                    Text(state.message, color = Color.Red, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun EmptyChatState(neighborName: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "🤝",
            fontSize = 48.sp
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Start a conversation with $neighborName",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Your email and phone are hidden\nuntil they accept your connection.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun ModernMessageBubble(message: MessageDto, currentUserId: String) {
    val isFromMe = message.senderId == currentUserId
    val isSystem = message.content.contains("\ud83e\udd1d")
    
    val timeStr = try {
        // Simple formatting helper
        val parts = message.createdAt.split("T")
        if (parts.size > 1) {
            val timePart = parts[1].split(".")
            timePart[0].substring(0, 5) // "HH:mm"
        } else {
            ""
        }
    } catch (e: Exception) { "" }

    if (isSystem) {
        Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(16.dp, 8.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF2E7D32),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        return
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isFromMe) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start) {
            Surface(
                color = if (isFromMe) OrangePrimary else Color.White,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isFromMe) 20.dp else 4.dp,
                    bottomEnd = if (isFromMe) 4.dp else 20.dp
                ),
                shadowElevation = 0.5.dp
            ) {
                ClickableUrlText(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isFromMe) Color.White else TextPrimary,
                        lineHeight = 20.sp
                    ),
                    linkColor = if (isFromMe) Color.White else OrangePrimary
                )
            }
            if (timeStr.isNotEmpty()) {
                Text(
                    text = timeStr,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}
