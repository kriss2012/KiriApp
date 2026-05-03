package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
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
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.UserDto
import com.kiriplatform.app.ui.components.KiriPrimaryButton
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.ui.components.ClickableUrlText
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(
    userId: String,
    onBack: () -> Unit,
    onNavigateToChat: (String) -> Unit
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
        containerColor = NotionCanvas,
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.titleMedium, color = NotionInk) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = NotionInk)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NotionCanvas)
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
                            .size(100.dp)
                            .clip(MaterialTheme.shapes.large) // Notion square-ish look
                            .background(NotionCanvas)
                            .border(1.dp, NotionHairline, MaterialTheme.shapes.large),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = u.fullName.take(1).uppercase(),
                            color = NotionInk,
                            fontWeight = FontWeight.Bold,
                            fontSize = 40.sp
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
                        Surface(color = NotionTintLavender, shape = MaterialTheme.shapes.medium, border = BorderStroke(1.dp, NotionPrimary.copy(alpha = 0.1f))) {
                            Text("Your Public View", modifier = Modifier.padding(12.dp, 6.dp), color = NotionBrandPurple800, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        val btnText = when (connectionStatus) {
                            "ACCEPTED" -> "Send Message"
                            "PENDING" -> "Message Request"
                            else -> "Connect & Message"
                        }
                        KiriPrimaryButton(
                            text = btnText,
                            onClick = { onNavigateToChat(userId) }
                        )
                    }
                    
                    Spacer(Modifier.height(32.dp))
                    
                    Spacer(Modifier.height(32.dp))
                    
                    if (u.bio != null) {
                        ProfileSection("Expertise & About", u.bio)
                    }

                    if (!u.services.isNullOrEmpty()) {
                        Spacer(Modifier.height(24.dp))
                        ServicesSection(u.services ?: emptyList())
                    }
                    
                    Spacer(Modifier.height(24.dp))
                    
                    // Contact & Links Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large, // 12dp
                        color = NotionCanvas,
                        border = BorderStroke(1.dp, NotionHairline)
                    ) {
                        Column(Modifier.padding(20.dp)) {
                            Text("Professional Details", fontWeight = FontWeight.Bold, color = NotionInk, fontSize = 16.sp)
                            Spacer(Modifier.height(16.dp))
                            
                            DetailItem(Icons.Default.Email, "Email", u.email)
                            DetailItem(androidx.compose.material.icons.Icons.Default.Phone, "Phone", u.phoneNumber ?: "Not provided")
                            
                            if (u.website != null) {
                                HorizontalDivider(Modifier.padding(vertical = 12.dp), color = NotionHairline)
                                DetailItem(androidx.compose.material.icons.Icons.Default.Language, "Website", u.website, isLink = true)
                            }
                        }
                    }

                    Spacer(Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
fun ServicesSection(services: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Services Offered", fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            services.forEach { service ->
                Surface(
                    color = OrangePrimary.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OrangePrimary.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = service,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = OrangePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    content: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}

@Composable
fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, isLink: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isLink) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(18.dp), tint = if (isLink) Color(0xFF1976D2) else TextSecondary)
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, color = TextSecondary, fontSize = 12.sp)
            if (isLink) {
                ClickableUrlText(
                    text = value,
                    style = androidx.compose.ui.text.TextStyle(
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                )
            } else {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(title, fontWeight = FontWeight.Bold, color = TextPrimary, style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(8.dp))
        ClickableUrlText(
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary, lineHeight = 20.sp)
        )
    }
}
