package com.apex.asg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.apex.asg.data.remote.models.EventDto
import com.apex.asg.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    eventJson: String // We'll pass the event as a JSON string for simplicity in navigation
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val gson = com.google.gson.Gson()
    val event = try {
        gson.fromJson(eventJson, EventDto::class.java)
    } catch (e: Exception) {
        null
    }

    if (event == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Event not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = TextPrimary
                )
            )
        },
        containerColor = BgCream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Event Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color.LightGray)
            ) {
                if (!event.imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = event.imageUrl,
                        contentDescription = "Event Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No Banner Image", color = Color.Gray)
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                // Type Badge
                Surface(
                    color = OrangeLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = event.type ?: "GENERAL",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = OrangeDark,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Title
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(Modifier.height(16.dp))

                // Date & Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = OrangePrimary, size = 20.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(event.date, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = OrangePrimary, size = 20.dp)
                    Spacer(Modifier.width(8.dp))
                    Text(event.location, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }

                if (!event.prize.isNullOrEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🏆", fontSize = 24.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Prizes & Rewards", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                                Text(event.prize!!, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1B5E20))
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Description
                Text("About the Event", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    lineHeight = 24.sp
                )

                Spacer(Modifier.height(40.dp))

                // Apply Button
                Button(
                    onClick = {
                        if (!event.registrationLink.isNullOrEmpty()) {
                            uriHandler.openUri(event.registrationLink!!)
                        } else {
                            android.widget.Toast.makeText(context, "Registration link not available", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
                ) {
                    Text(
                        if (!event.registrationLink.isNullOrEmpty()) "Register Now / Apply →" else "Apply via Platform",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                
                if (!event.registrationLink.isNullOrEmpty()) {
                    Text(
                        "Note: Clicking this will open an external registration form (e.g., Google Form).",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}
