package com.kiriplatform.app.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.ProjectReviewRequest
import com.kiriplatform.app.data.remote.models.ProjectShowcaseDto
import com.kiriplatform.app.data.remote.models.ProjectSubmitRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectShowcaseScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var projectsList by remember { mutableStateOf<List<ProjectShowcaseDto>>(emptyList()) }
    
    var showSubmitDialog by remember { mutableStateOf(false) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedProjectForReview by remember { mutableStateOf<ProjectShowcaseDto?>(null) }

    // Submit Project Form State
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var newGithubUrl by remember { mutableStateOf("") }
    var newTechStack by remember { mutableStateOf("") }
    var isSubmittingProject by remember { mutableStateOf(false) }

    // Review Form State
    var reviewCodeQuality by remember { mutableStateOf(4) }
    var reviewDoc by remember { mutableStateOf(4) }
    var reviewArch by remember { mutableStateOf(4) }
    var reviewInnov by remember { mutableStateOf(4) }
    var reviewComment by remember { mutableStateOf("") }
    var isSubmittingReview by remember { mutableStateOf(false) }

    fun loadProjects() {
        scope.launch {
            isLoading = true
            try {
                val response = ApiClient.service.getProjects()
                if (response.success) {
                    projectsList = response.projects
                } else {
                    Toast.makeText(context, "Failed to load project showcase", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error fetching projects: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun submitNewProject() {
        if (newTitle.isBlank() || newDesc.isBlank() || newGithubUrl.isBlank()) {
            Toast.makeText(context, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
            return
        }
        val techList = newTechStack.split(",").map { it.trim() }.filter { it.isNotEmpty() }

        if (isSubmittingProject) return
        isSubmittingProject = true

        scope.launch {
            try {
                val response = ApiClient.service.submitProject(
                    ProjectSubmitRequest(
                        title = newTitle,
                        description = newDesc,
                        githubUrl = newGithubUrl,
                        techStack = techList
                    )
                )
                if (response.success) {
                    Toast.makeText(context, "Project submitted successfully!", Toast.LENGTH_SHORT).show()
                    showSubmitDialog = false
                    // Reset form
                    newTitle = ""
                    newDesc = ""
                    newGithubUrl = ""
                    newTechStack = ""
                    loadProjects()
                } else {
                    Toast.makeText(context, "Failed to submit project", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error submitting project: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isSubmittingProject = false
            }
        }
    }

    fun submitReview() {
        val project = selectedProjectForReview ?: return
        if (reviewComment.isBlank()) {
            Toast.makeText(context, "Review comment cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        if (!com.kiriplatform.app.utils.NetworkUtils.isOnline(context)) {
            scope.launch {
                try {
                    com.kiriplatform.app.data.services.OfflineSyncManager.queueAction(
                        context,
                        "PROJECT_REVIEW",
                        com.kiriplatform.app.data.services.SyncWorker.ProjectReviewPayload(
                            projectId = project.id,
                            request = ProjectReviewRequest(
                                codeQuality = reviewCodeQuality,
                                documentation = reviewDoc,
                                architecture = reviewArch,
                                innovation = reviewInnov,
                                comment = reviewComment
                            )
                        )
                    )
                    Toast.makeText(context, "Offline: Review queued to sync when connection is restored.", Toast.LENGTH_LONG).show()
                    showReviewDialog = false
                    // Reset form
                    reviewCodeQuality = 4
                    reviewDoc = 4
                    reviewArch = 4
                    reviewInnov = 4
                    reviewComment = ""
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Failed to queue review: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }

        if (isSubmittingReview) return
        isSubmittingReview = true

        scope.launch {
            try {
                val response = ApiClient.service.submitProjectReview(
                    projectId = project.id,
                    request = ProjectReviewRequest(
                        codeQuality = reviewCodeQuality,
                        documentation = reviewDoc,
                        architecture = reviewArch,
                        innovation = reviewInnov,
                        comment = reviewComment
                    )
                )
                if (response.success) {
                    Toast.makeText(context, "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                    showReviewDialog = false
                    // Reset form
                    reviewCodeQuality = 4
                    reviewDoc = 4
                    reviewArch = 4
                    reviewInnov = 4
                    reviewComment = ""
                    loadProjects()
                } else {
                    Toast.makeText(context, "Failed to submit review", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error submitting review: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isSubmittingReview = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadProjects()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Project Showcase", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text("Peer Code Reviews & Featured Portfolios", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showSubmitDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Showcase Project")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && projectsList.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (projectsList.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No Projects Submitted Yet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Be the first to submit your GitHub repository for peer feedback and recruiter visibility!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(projectsList) { project ->
                        ProjectShowcaseCard(
                            project = project,
                            onOpenGithub = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(project.githubUrl))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Invalid link or browser unavailable", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onAddReview = {
                                selectedProjectForReview = project
                                showReviewDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Submit Project Dialog
    if (showSubmitDialog) {
        AlertDialog(
            onDismissRequest = { showSubmitDialog = false },
            title = { Text("Showcase Your Project", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Project Title *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("Short Description *") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                    OutlinedTextField(
                        value = newGithubUrl,
                        onValueChange = { newGithubUrl = it },
                        label = { Text("GitHub Repo URL *") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTechStack,
                        onValueChange = { newTechStack = it },
                        label = { Text("Tech Stack (comma separated)") },
                        placeholder = { Text("e.g. Kotlin, Compose, Express") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { submitNewProject() },
                    enabled = !isSubmittingProject
                ) {
                    if (isSubmittingProject) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Submit")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Review Dialog
    if (showReviewDialog && selectedProjectForReview != null) {
        AlertDialog(
            onDismissRequest = { showReviewDialog = false },
            title = { Text("Review: ${selectedProjectForReview?.title}", fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Column {
                            Text("Code Quality: $reviewCodeQuality/5", style = MaterialTheme.typography.bodyMedium)
                            Slider(
                                value = reviewCodeQuality.toFloat(),
                                onValueChange = { reviewCodeQuality = it.toInt() },
                                valueRange = 1f..5f,
                                steps = 3
                            )
                        }
                    }
                    item {
                        Column {
                            Text("Documentation: $reviewDoc/5", style = MaterialTheme.typography.bodyMedium)
                            Slider(
                                value = reviewDoc.toFloat(),
                                onValueChange = { reviewDoc = it.toInt() },
                                valueRange = 1f..5f,
                                steps = 3
                            )
                        }
                    }
                    item {
                        Column {
                            Text("Architecture: $reviewArch/5", style = MaterialTheme.typography.bodyMedium)
                            Slider(
                                value = reviewArch.toFloat(),
                                onValueChange = { reviewArch = it.toInt() },
                                valueRange = 1f..5f,
                                steps = 3
                            )
                        }
                    }
                    item {
                        Column {
                            Text("Innovation: $reviewInnov/5", style = MaterialTheme.typography.bodyMedium)
                            Slider(
                                value = reviewInnov.toFloat(),
                                onValueChange = { reviewInnov = it.toInt() },
                                valueRange = 1f..5f,
                                steps = 3
                            )
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = reviewComment,
                            onValueChange = { reviewComment = it },
                            label = { Text("Feedback & Code Suggestions *") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { submitReview() },
                    enabled = !isSubmittingReview
                ) {
                    if (isSubmittingReview) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Submit Review")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showReviewDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProjectShowcaseCard(
    project: ProjectShowcaseDto,
    onOpenGithub: () -> Unit,
    onAddReview: () -> Unit
) {
    var expandedReviews by remember { mutableStateOf(false) }

    val avgQuality = if (project.reviews.isNotEmpty()) project.reviews.map { it.codeQuality }.average() else 0.0
    val avgDoc = if (project.reviews.isNotEmpty()) project.reviews.map { it.documentation }.average() else 0.0
    val avgArch = if (project.reviews.isNotEmpty()) project.reviews.map { it.architecture }.average() else 0.0
    val avgInnov = if (project.reviews.isNotEmpty()) project.reviews.map { it.innovation }.average() else 0.0
    val totalAvg = (avgQuality + avgDoc + avgArch + avgInnov) / 4.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: title, featured badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = project.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (project.isFeatured) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FEATURED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                    Text(
                        text = "by ${project.student.fullName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Average Score Badge
                if (project.reviews.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", totalAvg),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }

            // Description
            Text(
                text = project.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Tech Stack Badges
            if (project.techStack.isNotEmpty()) {
                FlowRowLayout(
                    modifier = Modifier.fillMaxWidth(),
                    spacing = 6.dp
                ) {
                    project.techStack.forEach { tech ->
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = tech,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onOpenGithub,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Code", fontSize = 12.sp)
                    }

                    TextButton(
                        onClick = onAddReview,
                        contentPadding = PaddingValues(horizontal = 8.dp),
                    ) {
                        Icon(imageVector = Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Review", fontSize = 12.sp)
                    }
                }

                if (project.reviews.isNotEmpty()) {
                    TextButton(onClick = { expandedReviews = !expandedReviews }) {
                        Text(
                            text = if (expandedReviews) "Hide Reviews (${project.reviews.size})" else "Show Reviews (${project.reviews.size})",
                            fontSize = 12.sp
                        )
                        Icon(
                            imageVector = if (expandedReviews) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Expandable Reviews List
            AnimatedVisibility(visible = expandedReviews) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Peer Reviews:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    project.reviews.forEach { review ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = review.reviewer.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                // Average Review Score
                                val revAvg = (review.codeQuality + review.documentation + review.architecture + review.innovation) / 4.0
                                Text(
                                    text = String.format("%.1f ★", revAvg),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                            Text(
                                text = review.comment,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlowRowLayout(
    modifier: Modifier = Modifier,
    spacing: androidx.compose.ui.unit.Dp = 8.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }

        val layoutWidth = constraints.maxWidth
        var layoutHeight = 0
        var currentX = 0
        var currentY = 0
        var maxRowHeight = 0

        val childPositions = mutableListOf<androidx.compose.ui.geometry.Offset>()

        placeables.forEach { placeable ->
            if (currentX + placeable.width > layoutWidth) {
                currentX = 0
                currentY += maxRowHeight + spacing.roundToPx()
                maxRowHeight = 0
            }
            childPositions.add(androidx.compose.ui.geometry.Offset(currentX.toFloat(), currentY.toFloat()))
            currentX += placeable.width + spacing.roundToPx()
            maxRowHeight = maxOf(maxRowHeight, placeable.height)
        }

        layoutHeight = currentY + maxRowHeight

        layout(layoutWidth, layoutHeight) {
            placeables.forEachIndexed { index, placeable ->
                val offset = childPositions[index]
                placeable.placeRelative(offset.x.toInt(), offset.y.toInt())
            }
        }
    }
}
