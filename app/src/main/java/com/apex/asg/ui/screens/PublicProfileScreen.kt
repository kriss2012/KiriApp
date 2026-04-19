package com.apex.asg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.UserDto
import com.apex.asg.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    userId: String,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val currentUserId = sessionManager.getUserId() ?: ""
    
    var isLoading by remember { mutableStateOf(true) }
    var isPending by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf<UserDto?>(null) }
    var connectionStatus by remember { mutableStateOf<String?>(null) } // "PENDING", "ACCEPTED", null
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        try {
            user = ApiClient.service.getProfile(userId)
            // Check current connection status
            val connections = ApiClient.service.getUserConnections(currentUserId)
            val existing = connections.find { 
                (it.senderId == currentUserId && it.receiverId == userId) || 
                (it.senderId == userId && it.receiverId == currentUserId)
            }
            connectionStatus = existing?.status
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = BgCream,
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCream)
            )
        }
    ) { padding ->
        // Smooth Fade-in Transition for Content
        val alpha by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (isLoading) 0f else 1f,
            animationSpec = androidx.compose.animation.core.tween(600),
            label = "fade"
        )
        
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = OrangePrimary)
            }
        } else if (user != null) {
            val u = user!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .alpha(alpha), // Apply smooth fade
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Spacer(Modifier.height(32.dp))
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = u.fullName.take(1).uppercase(),
                            color = OrangePrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 48.sp
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(u.fullName, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text(u.role, color = OrangePrimary, style = MaterialTheme.typography.titleMedium)
                    if (u.college != null) {
                        Text(u.college, color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    if (userId == currentUserId) {
                        Surface(color = OrangeLight, shape = RoundedCornerShape(8.dp)) {
                            Text("Your Public View", modifier = Modifier.padding(12.dp, 6.dp), color = OrangeDark, style = MaterialTheme.typography.labelSmall)
                        }
                    } else if (connectionStatus == "ACCEPTED") {
                        Button(
                            onClick = { /* Navigate to Chat */ },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Email, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Send Message")
                        }
                    } else if (connectionStatus == "PENDING") {
                        Button(
                            onClick = { },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = ButtonDefaults.buttonColors(containerColor = BorderColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Request Pending")
                        }
                    } else {
                        Button(
                            onClick = { 
                                isPending = true
                                coroutineScope.launch {
                                    try {
                                        ApiClient.service.sendConnectionRequest(
                                            mapOf("senderId" to currentUserId, "receiverId" to userId)
                                        )
                                        connectionStatus = "PENDING"
                                    } catch (e: Exception) {
                                        isPending = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !isPending,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPending) BorderColor else OrangePrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PersonAdd, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Connect with ${u.fullName.split(" ")[0]}")
                        }
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    ProfileSection("About", u.bio ?: "No bio provided")
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Contact Info (Blurred if not connected)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Contact Info", fontWeight = FontWeight.Bold, color = TextPrimary)
                            Spacer(Modifier.height(8.dp))
                            if (connectionStatus == "ACCEPTED" || userId == currentUserId) {
                                Text("Email: ${u.email}", style = MaterialTheme.typography.bodyMedium)
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .background(BorderColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Connect to view contact info", color = TextSecondary, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(100.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        Text(content, color = TextSecondary, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
    }
}
