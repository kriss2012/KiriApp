package com.kiriplatform.app.ui.screens
 
import androidx.compose.foundation.ExperimentalFoundationApi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "Skip",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.clickable { onComplete() }
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                KiriPrimaryButton(
                    text = "Finish →",
                    onClick = { onComplete() }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            InterestSelectionPage()
        }
    }
}

@Composable
fun RoleSelectionPage() {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    val roles = listOf("Student", "Founder", "Investor", "Mentor")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Who are you?", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(8.dp))
        Text("Select your primary role in the ecosystem", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(roles) { role ->
                val isSelected = selectedRole == role
                Card(
                    modifier = Modifier
                        .height(120.dp)
                        .clickable { selectedRole = role }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(role, style = MaterialTheme.typography.titleMedium, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
fun CollegeSelectionPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Select your college", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search college...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )
        )
        Spacer(Modifier.height(16.dp))
        // Scrollable list placeholder
        Text("Nearby Colleges", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        // ... list items would go here
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InterestSelectionPage() {
    val interests = listOf("Content Creation", "Hackathons", "Fundraising", "Mentorship", "NAAC/NEP", "Events Organizing")
    val selectedInterests = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("What are your interests?", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurface)
        Spacer(Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            interests.forEach { interest ->
                val isSelected = selectedInterests.contains(interest)
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        if (isSelected) selectedInterests.remove(interest) else selectedInterests.add(interest)
                    },
                    label = { Text(interest) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = MaterialTheme.colorScheme.outlineVariant,
                        selectedBorderColor = MaterialTheme.colorScheme.primary,
                        borderWidth = 1.dp
                    )
                )
            }
        }
    }
}

