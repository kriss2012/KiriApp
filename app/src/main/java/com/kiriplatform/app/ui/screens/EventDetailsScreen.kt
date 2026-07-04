package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.kiriplatform.app.data.remote.models.EventDto
import com.kiriplatform.app.ui.components.KiriPrimaryButton
import com.kiriplatform.app.ui.theme.*
import java.net.URLDecoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsScreen(
    navController: NavController,
    eventJson: String
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
            Text("Event not found", color = MaterialTheme.colorScheme.onSurface)
        }
        return
    }

    // Helper functions for cleaning and formatting
    fun String.clean(): String = try {
        URLDecoder.decode(this.replace("+", " "), "UTF-8")
    } catch (e: Exception) {
        this.replace("+", " ")
    }

    fun String.formatDate(): String = try {
        val zonedDateTime = ZonedDateTime.parse(this)
        zonedDateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy • hh:mm a"))
    } catch (e: Exception) {
        this.clean()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Event Details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) NotionBrandNavyDeep else MaterialTheme.colorScheme.background,
                    titleContentColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = if (isSystemInDarkTheme()) NotionBrandNavyDeep else MaterialTheme.colorScheme.background
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
                    .height(240.dp)
                    .background(if (isSystemInDarkTheme()) NotionBrandNavyMid else MaterialTheme.colorScheme.surfaceVariant)
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.4f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("No Banner Image Provided", 
                                color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant, 
                                style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                // Type Badge
                val dark = isSystemInDarkTheme()
                Surface(
                    color = if (dark) NotionTintLavenderDark else NotionTintLavender,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = (event.type ?: "GENERAL").uppercase(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (dark) NotionOnTintDark else NotionBrandPurple800,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Title
                Text(
                    text = event.title.clean(),
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (isSystemInDarkTheme()) NotionBrandYellow else MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black,
                    lineHeight = 34.sp
                )

                Spacer(Modifier.height(20.dp))

                // Date & Location
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, 
                        tint = if (isSystemInDarkTheme()) NotionBrandYellow else MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(event.date.formatDate(), 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, 
                        tint = if (isSystemInDarkTheme()) NotionBrandYellow else MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(event.location.clean(), 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.onSurface)
                }

                Spacer(Modifier.height(24.dp))
                
                // Coordinator Section
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSystemInDarkTheme()) NotionBrandNavyMid else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("EVENT COORDINATOR", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, null, 
                                tint = if (isSystemInDarkTheme()) NotionBrandYellow else MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(event.coordinatorName?.clean() ?: "ASG Core Team", 
                                fontWeight = FontWeight.Bold, 
                                color = if (isSystemInDarkTheme()) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }

                if (!event.prize.isNullOrEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = if (isSystemInDarkTheme()) NotionTintMintDark else NotionTintMint,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = NotionBrandGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Prizes & Rewards", fontWeight = FontWeight.Bold, color = NotionBrandGreen)
                                Text(event.prize.clean(), style = MaterialTheme.typography.bodySmall, color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.8f) else NotionBrandGreen)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Description
                Text("About the Event", 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Black, 
                    color = if (isSystemInDarkTheme()) NotionBrandYellow else MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = event.description.clean(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSystemInDarkTheme()) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = 26.sp
                )

                Spacer(Modifier.height(48.dp))

                // Apply Button
                var isRegistered by remember { 
                    mutableStateOf(context.getSharedPreferences("event_regs", android.content.Context.MODE_PRIVATE).getBoolean("reg_${event.id}", false)) 
                }

                KiriPrimaryButton(
                    text = if (isRegistered) "Registered ✓" else if (!event.registrationLink.isNullOrEmpty()) "Register Now →" else "Apply via Platform",
                    enabled = !isRegistered,
                    onClick = {
                        if (!event.registrationLink.isNullOrEmpty()) {
                            event.registrationLink?.let { uriHandler.openUri(it) }
                            context.getSharedPreferences("event_regs", android.content.Context.MODE_PRIVATE)
                                .edit().putBoolean("reg_${event.id}", true).apply()
                            isRegistered = true
                        } else {
                            android.widget.Toast.makeText(context, "Registration link not available", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
