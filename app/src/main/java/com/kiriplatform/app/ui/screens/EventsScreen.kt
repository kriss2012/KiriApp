package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    text = { Text("Broadcast Event", fontWeight = FontWeight.Bold) },
                    shape = RoundedCornerShape(16.dp)
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
            Row(
                modifier = Modifier.padding(24.dp, 20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Upcoming Events",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Black
                )
            }

            // Tabs
            LazyRow(
                modifier = Modifier.padding(bottom = 16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedTab == tab
                    Column(
                        modifier = Modifier.clickable { selectedTab = tab },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .height(3.dp)
                                    .width(16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(vertical = 8.dp)
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
    val location = event.description.take(45) + "..."
    val type = event.type ?: "General"
    val prize = event.prize ?: "TBD"

    Surface(
        onClick = onDetailsClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column {
            if (!event.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = "Event Banner",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Large Date Block
                Surface(
                    modifier = Modifier.size(48.dp, 52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            day,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            month,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 7.sp
                        )
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            type,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        location,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp, 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Prize: ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(prize, style = MaterialTheme.typography.titleSmall, color = GreenSuccess, fontWeight = FontWeight.Bold)
                }
                Text(
                    "Details →",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
