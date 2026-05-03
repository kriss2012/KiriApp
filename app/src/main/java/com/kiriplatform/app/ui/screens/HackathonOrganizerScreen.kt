package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.components.KiriPrimaryButton
import com.kiriplatform.app.ui.theme.*

@Composable
fun HackathonOrganizerScreen() {
    var currentStep by remember { mutableStateOf(1) }
    val totalSteps = 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(18.dp)
            .padding(bottom = 80.dp)
    ) {
        // Header
        Text("Organise a Hackathon", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(24.dp))

        // Stepper
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { index ->
                val step = index + 1
                StepIndicator(
                    number = step.toString(),
                    isCompleted = step < currentStep,
                    isActive = step == currentStep
                )
                if (index < totalSteps - 1) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                        color = if (step < currentStep) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                        thickness = 2.dp
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))

        // Form Fields (Placeholder for Step 1)
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Step $currentStep: Basic Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Event Name", style = MaterialTheme.typography.bodyMedium) },
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("College/Venue", style = MaterialTheme.typography.bodyMedium) },
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        KiriPrimaryButton(
            text = if (currentStep < totalSteps) "Next Step →" else "Publish to ASG →",
            onClick = { if (currentStep < totalSteps) currentStep++ else { /* Publish */ } }
        )
    }
}

@Composable
fun StepIndicator(number: String, isCompleted: Boolean, isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (isCompleted || isActive) MaterialTheme.colorScheme.primary else Color.Transparent)
            .border(if (isCompleted || isActive) 0.dp else 1.dp, if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(
                number,
                style = MaterialTheme.typography.labelSmall,
                color = if (isActive) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
