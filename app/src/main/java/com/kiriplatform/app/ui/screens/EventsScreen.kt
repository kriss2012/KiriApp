package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.models.EventDto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector
import com.kiriplatform.app.ui.viewmodels.*
import androidx.compose.ui.platform.LocalContext
import com.kiriplatform.app.ui.components.ErrorDialog
import com.kiriplatform.app.ui.components.SuccessDialog
import com.kiriplatform.app.ui.navigation.Screen
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@Composable
fun EventsScreen(
    navController: androidx.navigation.NavController,
    viewModel: EventsViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    var selectedTab by remember { mutableStateOf("All") }
    val tabs = listOf("All", "Hackathon", "Competition", "Workshop", "Seminar")
    
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchEvents(context)
    }

    LaunchedEffect(uiState) {
        if (uiState is EventsState.Error) {
            errorMessage = (uiState as EventsState.Error).message
            showErrorDialog = true
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            val userRole = sessionManager.getUserRole() ?: ""
            val canBroadcast = sessionManager.canCreateEvents() 
                || userRole == "SPOC" 
                || userRole == "ADMIN"
            if (canBroadcast) {
                ExtendedFloatingActionButton(
                    onClick = { 
                        android.widget.Toast.makeText(context, "Opening Broadcast Window...", android.widget.Toast.LENGTH_SHORT).show()
                        navController.navigate("add_event") 
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = { Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("BROADCAST", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                    shape = RoundedCornerShape(8.dp),
                    elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp, 0.dp, 0.dp)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 20.dp)
        ) {
            // Header
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text(
                    "LIVE EVENTS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    letterSpacing = 1.sp
                )
                Text(
                    "Ecosystem Hub",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            // Tabs
            LazyRow(
                modifier = Modifier.padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedTab == tab
                    Surface(
                        modifier = Modifier.clickable { selectedTab = tab },
                        shape = MaterialTheme.shapes.medium, // 8dp
                        color = if (isSelected) NotionInkDeep else NotionCanvas,
                        border = if (isSelected) null else BorderStroke(1.dp, NotionHairline)
                    ) {
                        Text(
                            text = tab,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isSelected) NotionOnDark else NotionSteel,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

    Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is EventsState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    is EventsState.Success -> {
                        val filteredEvents = if (selectedTab == "All") state.events else state.events.filter { it.title.contains(selectedTab, ignoreCase = true) }
                        
                        if (filteredEvents.isEmpty()) {
                            Text("No events found in this category", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            EventsList(filteredEvents, navController)
                        }
                    }
                    is EventsState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { viewModel.fetchEvents(context) }) {
                                Text("Retry")
                            }
                        }
                    }
                    else -> {}
                }
            }
        }

        if (showErrorDialog) {
            ErrorDialog(
                title = "Events Error",
                message = errorMessage,
                onDismiss = { showErrorDialog = false }
            )
        }
    }
}

@Composable
fun EventsList(events: List<EventDto>, navController: androidx.navigation.NavController) {
    LazyColumn(
        modifier = Modifier.padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item { 
            Text(
                "ACTIVE BROADCASTS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        items(events) { event ->
            EventDetailCard(
                event = event,
                onDetailsClick = {
                    val gson = com.google.gson.Gson()
                    val eventJson = gson.toJson(event)
                    navController.navigate(Screen.EventDetails.createRoute(eventJson))
                }
            )
        }
    }
}


@Composable
fun EventDetailCard(event: EventDto, onDetailsClick: () -> Unit) {
    val day = event.date.split("-").lastOrNull()?.split("T")?.firstOrNull() ?: "01"
    val month = "EVENT"
    val title = event.title
    val description = event.description.take(60) + "..."
    val type = event.type ?: "General"
    val prize = event.prize ?: "TBD"

    Surface(
        onClick = onDetailsClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large, // 12dp
        color = NotionCanvas,
        border = BorderStroke(1.dp, NotionHairline)
    ) {
        Column {
            if (!event.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = "Event Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )
            }
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = NotionTintLavender,
                        shape = MaterialTheme.shapes.small, // 6dp
                        border = BorderStroke(1.dp, NotionPrimary.copy(alpha = 0.1f))
                    ) {
                        Text(
                            type.uppercase(),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NotionBrandPurple800,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    
                    Text(
                        "$day ${month.uppercase()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = NotionSteel,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NotionInk,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NotionCharcoal.copy(alpha = 0.6f),
                    lineHeight = 18.sp
                )
                
                Spacer(Modifier.height(16.dp))
                
                HorizontalDivider(color = NotionHairline)
                
                Spacer(Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Prize:", style = MaterialTheme.typography.labelSmall, color = NotionSteel)
                        Text(prize, style = MaterialTheme.typography.labelSmall, color = NotionPrimary, fontWeight = FontWeight.Bold)
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "View Details",
                            style = MaterialTheme.typography.labelSmall,
                            color = NotionInk,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = NotionInk
                        )
                    }
                }
            }
        }
    }
}
