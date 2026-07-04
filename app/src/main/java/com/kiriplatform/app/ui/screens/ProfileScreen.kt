package com.kiriplatform.app.ui.screens

import android.widget.Toast
import com.kiriplatform.app.ui.components.ClickableUrlText
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.components.SectionHeader
import com.kiriplatform.app.ui.theme.*

import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.UserDto
import com.kiriplatform.app.ui.viewmodels.ProfileState
import com.kiriplatform.app.ui.viewmodels.ProfileViewModel
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onNavigateToEdit: () -> Unit = {},
    onNavigateToConnections: () -> Unit = {},
    onNavigateToActivity: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    // Refresh profile on resume to catch GitHub OAuth completion
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, userId) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                if (userId.isNotEmpty()) {
                    viewModel.fetchProfile(context, userId)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Pull-to-refresh state (Material3 1.3.x API)
    var isRefreshing by remember { mutableStateOf(false) }
    val onRefresh: () -> Unit = {
        isRefreshing = true
        if (userId.isNotEmpty()) {
            viewModel.fetchProfile(context, userId)
        }
    }

    // Stop the indicator once the network call resolves
    LaunchedEffect(uiState) {
        if (uiState !is ProfileState.Loading) {
            isRefreshing = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            when (val state = uiState) {
                is ProfileState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is ProfileState.Success -> {
                    ProfileContent(
                        user = state.user,
                        onNavigateToEdit = onNavigateToEdit,
                        onNavigateToConnections = onNavigateToConnections,
                        onNavigateToActivity = onNavigateToActivity,
                        onLogout = {
                            sessionManager.logout()
                            ApiClient.setToken(null, null)
                            onLogout()
                        }
                    )
                }
                is ProfileState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchProfile(context, userId) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Text("Retry", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun ProfileContent(
    user: UserDto,
    onNavigateToEdit: () -> Unit,
    onNavigateToConnections: () -> Unit,
    onNavigateToActivity: () -> Unit,
    onLogout: () -> Unit
) {
    var githubStats by remember { mutableStateOf<com.kiriplatform.app.data.remote.models.GitHubStatsResponse?>(null) }
    var isGithubLoading by remember { mutableStateOf(false) }
    var githubError by remember { mutableStateOf<String?>(null) }

    var employabilityScore by remember { mutableStateOf<com.kiriplatform.app.data.remote.models.EmployabilityScoreResponse?>(null) }
    var isScoreLoading by remember { mutableStateOf(false) }
    var scoreError by remember { mutableStateOf<String?>(null) }


    LaunchedEffect(user.githubUrl) {
        val gitUrl = user.githubUrl
        if (!gitUrl.isNullOrBlank()) {
            val username = gitUrl.removeSuffix("/").substringAfterLast("/")
                .trim()
            if (username.isNotEmpty() && username != "github.com") {
                isGithubLoading = true
                githubError = null
                try {
                    val githubUser = GitHubApiClient.service.getUser(username)
                    val githubRepos = GitHubApiClient.service.getRepos(username)
                    githubStats = com.kiriplatform.app.data.remote.models.GitHubStatsResponse(
                        login = githubUser.login,
                        name = githubUser.name,
                        followers = githubUser.followers,
                        following = githubUser.following,
                        public_repos = githubUser.public_repos,
                        bio = githubUser.bio,
                        avatar_url = githubUser.avatar_url,
                        repos = githubRepos.map { repo ->
                            com.kiriplatform.app.data.remote.models.GitHubRepoDto(
                                name = repo.name,
                                description = repo.description,
                                language = repo.language,
                                stars = repo.stargazers_count,
                                forks = repo.forks_count,
                                url = repo.html_url
                            )
                        }
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    try {
                        githubStats = ApiClient.service.getGitHubStats(username)
                    } catch (e2: Exception) {
                        e2.printStackTrace()
                        githubError = "GitHub integration is temporarily unavailable. Please try again later."
                    }
                } finally {
                    isGithubLoading = false
                }
            }
        }
    }

    LaunchedEffect(user) {
        isScoreLoading = true
        scoreError = null
        try {
            employabilityScore = ApiClient.service.getEmployabilityScore()
        } catch (e: Exception) {
            e.printStackTrace()
            scoreError = "Unable to calculate employability score at this time."
        } finally {
            isScoreLoading = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item { 
            ProfileHeroSection(
                user = user, 
                onNavigateToEdit = onNavigateToEdit
            ) 
        }

        // Bio / About Section
        if (!user.bio.isNullOrEmpty()) {
            item { SectionHeader(title = "About", actionText = "", onActionClick = {}) }
            item {
                Text(
                    text = user.bio,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Skills / Services Section
        if (!user.services.isNullOrEmpty()) {
            item { SectionHeader(title = "Expertise & Services", actionText = "", onActionClick = {}) }
            item {
                FlowRow(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    user.services.forEach { service ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = service.uppercase(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        if (user.githubUrl.isNullOrBlank()) {
            item { SectionHeader(title = "GitHub Integration", actionText = "", onActionClick = {}) }
            item {
                Surface(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Connect GitHub Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Link your account to import your repository metrics, star counts, and display your coding activity directly on your profile.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        var isConnecting by remember { mutableStateOf(false) }
                        val context = LocalContext.current
                        val scope = rememberCoroutineScope()
                        
                        Button(
                            onClick = {
                                if (isConnecting) return@Button
                                isConnecting = true
                                scope.launch {
                                    try {
                                        val response = ApiClient.service.getGitHubAuthorizeUrl()
                                        if (response.url.isNotEmpty()) {
                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(response.url))
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(context, "Failed to fetch authorization URL", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isConnecting = false
                                    }
                                }
                            },
                            enabled = !isConnecting,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (isConnecting) "Loading..." else "Connect GitHub Account")
                        }
                    }
                }
            }
        }

        if (isGithubLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        } else if (githubStats != null) {
            item { SectionHeader(title = "GitHub Statistics", actionText = "", onActionClick = {}) }
            item {
                val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    GitHubStatsCard(stats = githubStats!!) { url ->
                        try {
                            uriHandler.openUri(url)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else if (githubError != null && !user.githubUrl.isNullOrBlank()) {
            item { SectionHeader(title = "GitHub Statistics", actionText = "", onActionClick = {}) }
            item {
                Surface(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = githubError!!,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Resources & Social Section
        
        // Digital Persona (AI-Generated)
        if (!user.digitalPersona.isNullOrEmpty()) {
            item { SectionHeader(title = "AI Digital Persona", actionText = "", onActionClick = {}) }
            item {
                Surface(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "ANALYSIS",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = user.digitalPersona,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            lineHeight = 22.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                modifier = Modifier.size(6.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary
                            ) {}
                            Text(
                                "GENERATED BY KIRI INTELLIGENCE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }
        }

        if (isScoreLoading) {
            item {
                Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        } else if (employabilityScore != null) {
            item { SectionHeader(title = "AI Employability Index", actionText = "", onActionClick = {}) }
            item {
                EmployabilityScoreCard(scoreResponse = employabilityScore!!)
            }
        } else if (scoreError != null) {
            item { SectionHeader(title = "AI Employability Index", actionText = "", onActionClick = {}) }
            item {
                Surface(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = scoreError!!,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item { SectionHeader(title = "Kiri Dashboard", actionText = "", onActionClick = {}) }
        item { 
            DashboardMenu(
                onNavigateToConnections = onNavigateToConnections,
                onNavigateToActivity = onNavigateToActivity
            ) 
        }
        item {
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
                    .height(44.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.8f),
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text(
                    "SECURE LOGOUT", 
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelSmall,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}


@Composable
fun ResourceRow(label: String, url: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        ClickableUrlText(
            text = url,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            linkColor = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ProfileHeroSection(
    user: UserDto, 
    onNavigateToEdit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Avatar - Notion style: simple, clean
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        user.fullName.take(1).uppercase(),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    user.fullName,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    user.role.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                if (!user.department.isNullOrEmpty() || !user.college.isNullOrEmpty()) {
                    Text(
                        text = listOfNotNull(user.department, user.college).joinToString(" • ").uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 8.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            IconButton(
                onClick = onNavigateToEdit,
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
            ) {
                Icon(
                    Icons.Default.Edit, 
                    contentDescription = "Edit Profile", 
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        // Stats Row - Subtle Notion blocks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatBox(user.eventsCount.toString(), "Events")
            StatBox(user.connectionsCount.toString(), "Nodes")
            StatBox(user.points.toString(), "Score")
        }
    }
}

@Composable
fun RowScope.StatBox(value: String, label: String) {
    Surface(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                label.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun DashboardMenu(
    onNavigateToConnections: () -> Unit,
    onNavigateToActivity: () -> Unit
) {
    val menuItems = listOf(
        DashboardMenuItem(Icons.Default.Build, "My Startup Profile", "Coming Soon"),
        DashboardMenuItem(Icons.Default.AccountCircle, "Team Requests", null),
        DashboardMenuItem(Icons.Default.CheckCircle, "My Community Activity", null)
    )

    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (menuItems.isEmpty()) {
            Text("No dashboard items available", modifier = Modifier.padding(24.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            menuItems.forEach { item ->
                DashboardMenuCard(
                    item = item,
                    onClick = {
                        when (item.label) {
                            "Team Requests" -> onNavigateToConnections()
                            "My Community Activity" -> onNavigateToActivity()
                        }
                    }
                )
            }
        }
    }
}

data class DashboardMenuItem(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String, val badge: String? = null)

@Composable
fun DashboardMenuCard(item: DashboardMenuItem, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Text(
                item.label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )
            
            if (item.badge != null) {
                Surface(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        item.badge.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            } else {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
            }
        }
    }
}

private @Composable
fun GitHubStatsCard(stats: com.kiriplatform.app.data.remote.models.GitHubStatsResponse, onOpenUrl: (String) -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Git",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "GitHub Statistics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GitHubStatMetric("Repositories", stats.public_repos.toString())
                GitHubStatMetric("Followers", stats.followers.toString())
                GitHubStatMetric("Following", stats.following.toString())
            }
            
            if (stats.repos.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Recent Repositories",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))
                
                stats.repos.take(3).forEach { repo ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clickable { onOpenUrl(repo.url) },
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = repo.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = repo.stars.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            if (!repo.description.isNullOrEmpty()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = repo.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2
                                )
                            }
                            if (!repo.language.isNullOrEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = repo.language,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private @Composable
fun GitHubStatMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun EmployabilityScoreCard(
    scoreResponse: com.kiriplatform.app.data.remote.models.EmployabilityScoreResponse
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular overall score indicator
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${scoreResponse.overallScore}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Employability Index",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val statusText = when {
                        scoreResponse.overallScore >= 85 -> "Ready for hire"
                        scoreResponse.overallScore >= 70 -> "High potential"
                        scoreResponse.overallScore >= 50 -> "Developing skills"
                        else -> "Needs project building"
                    }
                    Text(
                        text = "Current Status: $statusText",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Score Breakdown",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            val breakdown = scoreResponse.breakdown
            BreakdownRow("Technical Skills Match", breakdown.technicalSkills.score, breakdown.technicalSkills.weight)
            BreakdownRow("Project Portfolio", breakdown.projects.score, breakdown.projects.weight)
            BreakdownRow("Resume Quality ATS Score", breakdown.resume.score, breakdown.resume.weight)
            BreakdownRow("Industry Certifications", breakdown.certifications.score, breakdown.certifications.weight)
            BreakdownRow("Mock Interviews", breakdown.mockInterview.score, breakdown.mockInterview.weight)
            BreakdownRow("Communication & Soft Skills", breakdown.softSkills.score, breakdown.softSkills.weight)
            BreakdownRow("GitHub Activity Metrics", breakdown.githubActivity.score, breakdown.githubActivity.weight)
        }
    }
}

@Composable
private fun BreakdownRow(label: String, score: Int, weight: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "$score/100 (Weight: $weight%)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { score / 100f },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = if (score >= 80) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

interface PublicGitHubApiService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): PublicGitHubUser

    @GET("users/{username}/repos")
    suspend fun getRepos(
        @Path("username") username: String,
        @Query("sort") sort: String = "updated",
        @Query("per_page") perPage: Int = 3
    ): List<PublicGitHubRepo>
}

data class PublicGitHubUser(
    val login: String,
    val name: String?,
    val followers: Int,
    val following: Int,
    val public_repos: Int,
    val bio: String?,
    val avatar_url: String?
)

data class PublicGitHubRepo(
    val name: String,
    val description: String?,
    val language: String?,
    val stargazers_count: Int,
    val forks_count: Int,
    val html_url: String
)

object GitHubApiClient {
    val service: PublicGitHubApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PublicGitHubApiService::class.java)
    }
}

