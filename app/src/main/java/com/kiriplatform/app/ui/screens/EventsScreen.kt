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

    val userRole = sessionManager.getUserRole() ?: "STUDENT"

    LaunchedEffect(Unit) {
        viewModel.fetchEvents(context, userRole, userId)
    }

    LaunchedEffect(uiState) {
        if (uiState is EventsState.Error) {
            errorMessage = (uiState as EventsState.Error).message
            showErrorDialog = true
        }
    }

    Scaffold(
        containerColor = BgCream,
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
                    containerColor = OrangePrimary,
                    contentColor = Color.White,
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
                .background(BgCream)
                .padding(bottom = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.padding(18.dp, 10.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Upcoming Events", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
            }

            // Tabs
            LazyRow(
                modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(tabs) { tab ->
                    val isSelected = selectedTab == tab
                    Column(
                        modifier = Modifier.clickable { selectedTab = tab },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = tab,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) OrangePrimary else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .height(2.dp)
                                    .width(20.dp)
                                    .background(OrangePrimary)
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
                            color = OrangePrimary
                        )
                    }
                    is EventsState.Success -> {
                        val filteredEvents = if (selectedTab == "All") state.events else state.events.filter { it.title.contains(selectedTab, ignoreCase = true) }
                        
                        if (filteredEvents.isEmpty()) {
                            Text("No related data found", modifier = Modifier.align(Alignment.Center), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        } else {
                            EventsList(filteredEvents, navController)
                        }
                    }
                    is EventsState.Error -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(state.message, color = Color.Red, fontSize = 14.sp)
                            Button(onClick = { viewModel.fetchEvents(context, userRole, userId) }) {
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
        modifier = Modifier.padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Text("ACTIVE EVENTS", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(vertical = 5.dp)) }
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
    val dateParts = event.date.split("T").first().split("-")
    val day = dateParts.lastOrNull() ?: "01"
    val month = when(dateParts.getOrNull(1)) {
        "01" -> "Jan"
        "02" -> "Feb"
        "03" -> "Mar"
        "04" -> "Apr"
        "05" -> "May"
        "06" -> "Jun"
        "07" -> "Jul"
        "08" -> "Aug"
        "09" -> "Sep"
        "10" -> "Oct"
        "11" -> "Nov"
        "12" -> "Dec"
        else -> "EVENT"
    }
    
    val title = event.title
    val organizer = event.coordinatorName ?: "Kiri Community"
    val location = event.location
    val type = event.type ?: "General"
    val typeBg = when(type.uppercase()) {
        "HACKATHON" -> Color(0xFFFFEFE0)
        "WORKSHOP" -> Color(0xFFE0F7FA)
        "MEETUP" -> Color(0xFFF3E5F5)
        else -> OrangeLight
    }
    val typeText = when(type.uppercase()) {
        "HACKATHON" -> OrangeDark
        "WORKSHOP" -> Color(0xFF006064)
        "MEETUP" -> Color(0xFF4A148C)
        else -> OrangeDark
    }
    val prize = event.prize ?: "TBD"

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onDetailsClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            if (!event.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = event.imageUrl,
                    contentDescription = "Event Banner",
                    modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Large Date Block
                Box(
                    modifier = Modifier
                        .size(44.dp, 48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(OrangeLight),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(day, style = MaterialTheme.typography.headlineSmall, color = OrangePrimary, fontWeight = FontWeight.Black, fontSize = 18.sp, lineHeight = 18.sp)
                        Text(month.uppercase(), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Surface(color = typeBg, shape = RoundedCornerShape(6.dp)) {
                        Text(type, modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp), style = MaterialTheme.typography.labelSmall, color = typeText, fontWeight = FontWeight.Bold, fontSize = 8.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text(organizer, style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                    Text("ℹ️ $location", style = MaterialTheme.typography.labelSmall, color = TextSecondary, fontSize = 10.sp)
                }
            }
            HorizontalDivider(color = BorderColor)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Prize: $prize", style = MaterialTheme.typography.bodySmall, color = GreenSuccess, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { onDetailsClick() },
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text("Details →", style = MaterialTheme.typography.labelSmall, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}
