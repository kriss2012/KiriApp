package com.apex.asg.ui.screens
 
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
import com.apex.asg.ui.components.ASGPrimaryButton
import com.apex.asg.ui.theme.*
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = BgCream,
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
                    color = TextSecondary,
                    modifier = Modifier.clickable { onComplete() }
                )
            }
        },
        bottomBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                ASGPrimaryButton(
                    text = if (pagerState.currentPage == 2) "Continue →" else "Next →",
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        } else {
                            onComplete()
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (pagerState.currentPage == index) OrangePrimary else BorderColor)
                                .padding(horizontal = 4.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { page ->
            when (page) {
                0 -> RoleSelectionPage()
                1 -> CollegeSelectionPage()
                2 -> InterestSelectionPage()
            }
        }
    }
}

@Composable
fun RoleSelectionPage() {
    var selectedRole by remember { mutableStateOf<String?>(null) }
    val roles = listOf("🎓 Student", "🚀 Founder", "💼 Investor", "👨‍🏫 Mentor")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        Text("Who are you?", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text("Select your primary role in the ecosystem", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
                            color = if (isSelected) OrangePrimary else BorderColor,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) OrangeLight else Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(role, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
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
            .padding(22.dp)
    ) {
        Text("Select your college", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search college...", style = MaterialTheme.typography.bodyMedium) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = BorderColor,
                focusedBorderColor = OrangePrimary
            )
        )
        Spacer(Modifier.height(16.dp))
        // Scrollable list placeholder
        Text("Nearby Colleges", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        // ... list items would go here
    }
}

@Composable
fun InterestSelectionPage() {
    val interests = listOf("Content Creation", "Hackathons", "Fundraising", "Mentorship", "NAAC/NEP", "Events Organizing")
    val selectedInterests = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(22.dp)
    ) {
        Text("What are your interests?", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.height(24.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
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
                        selectedContainerColor = OrangePrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = BorderColor,
                        selectedBorderColor = OrangePrimary,
                        borderWidth = 1.dp
                    )
                )
            }
        }
    }
}

