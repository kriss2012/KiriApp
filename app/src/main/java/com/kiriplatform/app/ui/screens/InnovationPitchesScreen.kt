package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
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
import com.kiriplatform.app.ui.viewmodels.PitchViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.remote.models.PitchDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InnovationPitchesScreen(
    onBack: () -> Unit = {},
    vm: PitchViewModel = viewModel()
) {
    var showSubmitDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Marketplace", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BgCream)
            )
        },
        containerColor = BgCream,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showSubmitDialog = true },
                containerColor = OrangePrimary,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, null) },
                text = { Text("PITCH IDEA") },
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header
            Column(modifier = Modifier.padding(24.dp, 16.dp)) {
                Text("Innovation Marketplace", style = MaterialTheme.typography.headlineSmall, color = TextPrimary, fontWeight = FontWeight.Black)
                Text("Discovery and back regional startup pitches", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(vm.pitches) { pitch ->
                    PitchCard(pitch = pitch, onBack = { vm.backPitch(pitch.id, 10, {}) })
                }
            }
        }
    }

    if (showSubmitDialog) {
        // Simple Submit Dialog (Implementation omitted for brevity, would be a full form)
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Submit Your Pitch") },
            text = { Text("The submission form is coming soon to the regional portal. Engage with Kiri AI to refine your pitch first!") },
            confirmButton = { Button(onClick = { showSubmitDialog = false }) { Text("OK") } }
        )
    }
}

@Composable
fun PitchCard(pitch: PitchDto, onBack: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = OrangeLight,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(pitch.category, modifier = Modifier.padding(8.dp, 4.dp), style = MaterialTheme.typography.labelSmall, color = OrangeDark, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.weight(1f))
                Text(pitch.status, style = MaterialTheme.typography.labelSmall, color = if (pitch.status == "OPEN") GreenSuccess else TextSecondary)
            }
            
            Spacer(Modifier.height(12.dp))
            Text(pitch.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black, color = TextPrimary)
            Text(pitch.description, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 3)
            
            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text("Goal", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text("₹${pitch.fundingGoal.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
                Column {
                    Text("Backers", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    Text((pitch._count?.backers ?: 0).toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                }
               
                Spacer(Modifier.weight(1f))
                
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = BorderColor.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, modifier = Modifier.size(16.dp), tint = OrangePrimary)
                    Spacer(Modifier.width(6.dp))
                    Text("BACK IT", style = MaterialTheme.typography.labelSmall, color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 0.5.dp)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pitched by ${pitch.founder?.fullName ?: "Unknown"}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }
    }
}
