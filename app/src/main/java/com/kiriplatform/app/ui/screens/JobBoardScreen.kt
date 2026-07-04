package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.models.JobDto
import com.kiriplatform.app.ui.viewmodels.JobState
import com.kiriplatform.app.ui.viewmodels.JobViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*

import com.kiriplatform.app.ui.components.ClickableUrlText

@Composable
fun JobBoardScreen(
    navController: androidx.navigation.NavController,
    viewModel: JobViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userRole = sessionManager.getUserRole() ?: "STUDENT"
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchJobs(context)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Opportunity Board", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) },
                actions = {
                    IconButton(onClick = { navController.navigate("market_trends") }) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Market Intelligence",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            if (userRole != "STUDENT") {
                FloatingActionButton(
                    onClick = { /* TODO: Open Post Job Dialog */ },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Post Opportunity")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is JobState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = MaterialTheme.colorScheme.primary)
                }
                is JobState.Success -> {
                    JobBoardContent(state.jobs)
                }
                is JobState.Error -> {
                    Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        Button(onClick = { viewModel.fetchJobs(context) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JobBoardContent(jobs: List<JobDto>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "Find Your Next Startup Role",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
        if (jobs.isEmpty()) {
            item {
                Text("No opportunities found", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(jobs) { job ->
                JobCard(job)
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun JobCard(job: JobDto) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = job.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = job.poster?.fullName ?: "ASG Recruiter",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Experience
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BusinessCenter,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (job.type.contains("Intern", ignoreCase = true)) "0-1 Yrs" else "0-2 Yrs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Salary
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "₹",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Stipend / Competitive",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = job.location ?: "Hybrid",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Key Skills:",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tags = listOf(job.type, "AI", "Startup")
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ClickableUrlText(
                text = job.description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Posted: 3 days ago • Active",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { /* Apply */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Apply Now", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}
