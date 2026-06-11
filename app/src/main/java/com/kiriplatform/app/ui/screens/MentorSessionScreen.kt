package com.kiriplatform.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.MentorSessionDto
import com.kiriplatform.app.data.remote.models.MentorSessionRequest
import com.kiriplatform.app.data.remote.models.UserResponse
import com.kiriplatform.app.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorSessionScreen(
    onBack: () -> Unit = {},
    onNavigateToProfile: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager.getInstance(context) }
    val currentUserId = sessionManager.getUserId() ?: ""

    var activeTab by remember { mutableStateOf(0) }
    var activeSprints by remember { mutableStateOf<List<MentorSessionDto>>(emptyList()) }
    var availableMentors by remember { mutableStateOf<List<UserResponse>>(emptyList()) }
    
    var isLoadingActiveSprints by remember { mutableStateOf(false) }
    var isLoadingMentors by remember { mutableStateOf(false) }

    // Booking Dialog state
    var showBookingDialog by remember { mutableStateOf(false) }
    var selectedMentorForBooking by remember { mutableStateOf<UserResponse?>(null) }
    var topicText by remember { mutableStateOf("") }
    var dateTimeText by remember { mutableStateOf("") }
    var isSubmittingBooking by remember { mutableStateOf(false) }

    fun loadActiveSprints() {
        scope.launch {
            isLoadingActiveSprints = true
            try {
                activeSprints = ApiClient.service.getMentorHistory()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to load active sprints: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoadingActiveSprints = false
            }
        }
    }

    fun loadMentors() {
        scope.launch {
            isLoadingMentors = true
            try {
                val results = ApiClient.service.getUsers(role = "MENTOR")
                availableMentors = results.filter { it.id != currentUserId }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to load mentors: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoadingMentors = false
            }
        }
    }

    // Load active tab data
    LaunchedEffect(activeTab) {
        if (activeTab == 0) {
            loadActiveSprints()
        } else {
            loadMentors()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mentorship", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text("GLOBAL MENTOR LINK", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp)
                Text("Expert Sessions", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Black)
                Text("Book 1:1 mentorship sprints with global mentors", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Tabs
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[activeTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }) {
                    Text("ACTIVE SPRINTS", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }) {
                    Text("BROWSE EXPERTS", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))

            if (activeTab == 0) {
                // Active Sprints Tab
                if (isLoadingActiveSprints) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (activeSprints.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No active sprints scheduled yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(activeSprints) { session ->
                            val isMentor = session.mentor?.id == currentUserId
                            val otherPersonName = if (isMentor) {
                                session.founder?.fullName ?: "Founder"
                            } else {
                                session.mentor?.fullName ?: "Mentor"
                            }
                            val otherPersonInitial = otherPersonName.take(1).uppercase()

                            MentorSprintCard(
                                otherPersonName = otherPersonName,
                                otherPersonInitial = otherPersonInitial,
                                topic = session._topic ?: "Mentorship Session",
                                status = session._status ?: "PENDING",
                                time = session.scheduledAt ?: "Date/Time not set",
                                showActionButtons = isMentor && session._status == "PENDING",
                                onAccept = {
                                    scope.launch {
                                        try {
                                            ApiClient.service.updateMentorSessionStatus(
                                                sessionId = session._id ?: "",
                                                request = mapOf("status" to "APPROVED")
                                            )
                                            Toast.makeText(context, "Session request approved!", Toast.LENGTH_SHORT).show()
                                            loadActiveSprints()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(context, "Failed to approve: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                onDecline = {
                                    scope.launch {
                                        try {
                                            ApiClient.service.updateMentorSessionStatus(
                                                sessionId = session._id ?: "",
                                                request = mapOf("status" to "REJECTED")
                                            )
                                            Toast.makeText(context, "Session request declined", Toast.LENGTH_SHORT).show()
                                            loadActiveSprints()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(context, "Failed to decline: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // Browse Experts Tab
                if (isLoadingMentors) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (availableMentors.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No global experts registered yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(availableMentors) { mentor ->
                            MentorCard(
                                mentorName = mentor.fullName,
                                mentorInitial = mentor.fullName.take(1).uppercase(),
                                roleText = mentor.role + (if (!mentor.college.isNullOrBlank()) " | ${mentor.college}" else ""),
                                bio = mentor.bio ?: "Experienced industry expert and mentor.",
                                onClick = { onNavigateToProfile(mentor.id) },
                                onBookSession = {
                                    selectedMentorForBooking = mentor
                                    showBookingDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Booking Request Dialog
    if (showBookingDialog && selectedMentorForBooking != null) {
        AlertDialog(
            onDismissRequest = { if (!isSubmittingBooking) showBookingDialog = false },
            title = {
                Text(
                    text = "Request 1:1 Sprint",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Propose a mentorship sprint with ${selectedMentorForBooking!!.fullName}.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = topicText,
                        onValueChange = { topicText = it },
                        label = { Text("Topic / Help Needed") },
                        placeholder = { Text("e.g. Seed funding pitch deck review") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dateTimeText,
                        onValueChange = { dateTimeText = it },
                        label = { Text("Proposed Date & Time") },
                        placeholder = { Text("e.g. Next Tuesday, 4:00 PM") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val topic = topicText.trim()
                        val dateTime = dateTimeText.trim()
                        if (topic.isEmpty()) {
                            Toast.makeText(context, "Please provide a session topic", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (isSubmittingBooking) return@Button
                        isSubmittingBooking = true
                        scope.launch {
                            try {
                                ApiClient.service.requestMentorSession(
                                    MentorSessionRequest(
                                        mentorId = selectedMentorForBooking!!.id,
                                        topic = topic,
                                        scheduledAt = if (dateTime.isNotEmpty()) dateTime else null
                                    )
                                )
                                Toast.makeText(context, "Sprint requested successfully!", Toast.LENGTH_LONG).show()
                                showBookingDialog = false
                                topicText = ""
                                dateTimeText = ""
                                activeTab = 0 // Return to Sprints to view status
                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(context, "Request failed: ${e.message}", Toast.LENGTH_SHORT).show()
                            } finally {
                                isSubmittingBooking = false
                            }
                        }
                    },
                    enabled = !isSubmittingBooking
                ) {
                    if (isSubmittingBooking) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Send Request")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showBookingDialog = false },
                    enabled = !isSubmittingBooking
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun MentorSprintCard(
    otherPersonName: String,
    otherPersonInitial: String,
    topic: String,
    status: String,
    time: String,
    showActionButtons: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(otherPersonInitial, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(otherPersonName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(topic, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                
                // Harmonized status colors
                val (chipBg, chipTextColor, displayStatus) = when (status.uppercase()) {
                    "APPROVED", "SCHEDULED" -> Triple(Color(0xFFE2F6EA), Color(0xFF0F5132), "SCHEDULED")
                    "REJECTED", "DECLINED" -> Triple(Color(0xFFFDE8E8), Color(0xFF9B1C1C), "DECLINED")
                    else -> Triple(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), MaterialTheme.colorScheme.primary, "PENDING")
                }

                Surface(
                    color = chipBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = displayStatus,
                        modifier = Modifier.padding(8.dp, 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = chipTextColor
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                
                Spacer(Modifier.weight(1f))

                if (showActionButtons) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onDecline,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFFDE8E8), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Decline", tint = Color(0xFF9B1C1C), modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = onAccept,
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE2F6EA), CircleShape)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color(0xFF0F5132), modifier = Modifier.size(16.dp))
                        }
                    }
                } else if (status.uppercase() == "APPROVED" || status.uppercase() == "SCHEDULED") {
                    TextButton(onClick = { /* Join Link placeholder */ }) {
                        Text("JOIN SESSION", style = MaterialTheme.typography.titleSmall, color = NotionLinkBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun MentorCard(
    mentorName: String,
    mentorInitial: String,
    roleText: String,
    bio: String,
    onClick: () -> Unit,
    onBookSession: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(mentorInitial, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(mentorName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(roleText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClick) {
                    Text("VIEW PROFILE", style = MaterialTheme.typography.labelMedium)
                }
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = onBookSession,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("BOOK SESSION", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
