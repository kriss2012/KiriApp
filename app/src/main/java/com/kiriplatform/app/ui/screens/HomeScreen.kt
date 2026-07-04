package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.kiriplatform.app.utils.clickableDebounced
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.components.AIStatusChip
import com.kiriplatform.app.ui.components.KiriIconBadge
import com.kiriplatform.app.ui.components.SectionHeader
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.utils.shimmer
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.models.*
import com.kiriplatform.app.ui.viewmodels.HomeState
import com.kiriplatform.app.ui.viewmodels.HomeViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel(),
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToChats: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToRepository: () -> Unit = {},
    onNavigateToEvents: () -> Unit = {},
    onNavigateToEventDetail: (String) -> Unit = {},
    onNavigateToAddEvent: () -> Unit = {},
    onNavigateToAal: () -> Unit = {},
    onNavigateToResumeBuilder: () -> Unit = {},
    onNavigateToBadges: () -> Unit = {},
    onNavigateToProjectShowcase: () -> Unit = {},
    onNavigateToLeaderboard: () -> Unit = {},
    onNavigateToInterviewSandbox: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    val userId = remember { SessionManager.getInstance(context).getUserId() ?: "" }
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadHomeData(context, userId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = uiState) {
                is HomeState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is HomeState.Success -> {
                    HomeContent(
                        user = state.user, 
                        events = state.upcomingEvents,
                        aalOnboarding = state.aalOnboarding,
                        aalActivities = state.aalActivities,
                        onNavigateToNotifications = onNavigateToNotifications,
                        onNavigateToChats = onNavigateToChats,
                        onNavigateToSearch = onNavigateToSearch,
                        onNavigateToRepository = onNavigateToRepository,
                        onNavigateToEvents = onNavigateToEvents,
                        onNavigateToEventDetail = onNavigateToEventDetail,
                        onNavigateToAddEvent = onNavigateToAddEvent,
                        onNavigateToAal = onNavigateToAal,
                        onNavigateToResumeBuilder = onNavigateToResumeBuilder,
                        onNavigateToBadges = onNavigateToBadges,
                        onNavigateToProjectShowcase = onNavigateToProjectShowcase,
                        onNavigateToLeaderboard = onNavigateToLeaderboard,
                        onNavigateToInterviewSandbox = onNavigateToInterviewSandbox,
                        onNavigateToProfile = onNavigateToProfile
                    )
                }
                is HomeState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        Button(onClick = { viewModel.loadHomeData(context, userId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomeContent(
    user: UserDto, 
    events: List<EventDto>,
    aalOnboarding: AalOnboardingDto?,
    aalActivities: List<AalActivityDto>,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToRepository: () -> Unit,
    onNavigateToEvents: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToAddEvent: () -> Unit,
    onNavigateToAal: () -> Unit,
    onNavigateToResumeBuilder: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToProjectShowcase: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToInterviewSandbox: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val uriHandler = androidx.compose.ui.platform.LocalUriHandler.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        item { 
            HomeTopBar(
                userName = user.fullName, 
                onNavigateToNotifications = onNavigateToNotifications,
                onNavigateToChats = onNavigateToChats,
                onNavigateToProfile = onNavigateToProfile
            ) 
        }
        item { PersistentSearchBar(onClick = onNavigateToSearch) }
        item { GreetingSection(userName = user.fullName) }
        
        if (aalOnboarding != null) {
            item { AalInternshipCard(aalOnboarding, aalActivities, onClick = onNavigateToAal) }
        }
 
        item { InnovationProgressCard(points = user.pointsCount) }
        item { DiscoverCommunityCard(onNavigateToSearch = onNavigateToSearch) }
        item { ResumeBuilderCard(onClick = onNavigateToResumeBuilder) }
        item { MicroCredentialsCard(onClick = onNavigateToBadges) }
        item { ProjectShowcaseCard(onClick = onNavigateToProjectShowcase) }
        item { CampusAmbassadorCard(onClick = onNavigateToLeaderboard) }
        item { InterviewSandboxCard(onClick = onNavigateToInterviewSandbox) }
        item { 
            InnovationHubCard(
                onNavigateToHub = {
                    try {
                        uriHandler.openUri(com.kiriplatform.app.utils.AppConfig.WEBSITE_URL)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            )
        }
        item { 
            UpcomingEventsSection(
                events = events, 
                userRole = user.role,
                onNavigateToEvents = onNavigateToEvents,
                onNavigateToEventDetail = onNavigateToEventDetail,
                onNavigateToAddEvent = onNavigateToAddEvent
            ) 
        }
    }
}

@Composable
fun ResumeBuilderCard(onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickableDebounced { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) NotionTintSkyDark else NotionTintSky
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("AI CAREER TOOLS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Text("ATS Resume Builder", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text("Generate target-role optimized resume with Google X-Y-Z formula.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            KiriIconBadge(imageVector = Icons.Default.Description, backgroundColor = NotionCanvas)
        }
    }
}

@Composable
fun MicroCredentialsCard(onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickableDebounced { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) NotionTintYellowDark else NotionTintYellow
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("VERIFIABLE CREDENTIALS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Text("Badges & Certifications", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text("Showcase skill badges, achievements, and roadmap milestones.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            KiriIconBadge(imageVector = Icons.Default.WorkspacePremium, backgroundColor = NotionCanvas)
        }
    }
}

@Composable
fun ProjectShowcaseCard(onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickableDebounced { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) NotionTintMintDark else NotionTintMint
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("PEER CODE REVIEW", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Text("Project Showcase", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text("Submit repositories, get peer feedback, and feature your projects.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            KiriIconBadge(imageVector = Icons.Default.Code, backgroundColor = NotionCanvas)
        }
    }
}

@Composable
fun CampusAmbassadorCard(onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickableDebounced { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) NotionTintLavenderDark else NotionTintLavender
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("CAMPUS AMBASSADOR", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Text("Leaderboard & Referrals", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text("Invite friends, earn points, and climb the campus rank list.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            KiriIconBadge(imageVector = Icons.Default.Campaign, backgroundColor = NotionCanvas)
        }
    }
}

@Composable
fun InterviewSandboxCard(onClick: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickableDebounced { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) NotionTintRoseDark else NotionTintRose
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("AI CAREER SANDBOX", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Text("Interview Practice Sandbox", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text("Practice domain-specific interviews and get a confidence evaluation report.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            KiriIconBadge(imageVector = Icons.Default.Mic, backgroundColor = NotionCanvas)
        }
    }
}

@Composable
fun AalInternshipCard(onboarding: AalOnboardingDto, activities: List<AalActivityDto>, onClick: () -> Unit) {
    val completedCount = activities.count { it.status == ActivityStatus.VERIFIED }
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickableDebounced { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) NotionTintLavenderDark else NotionTintLavender
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("KIRI AI INTERNSHIP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
                    Text("Intelligence Progress", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                }
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        onboarding.lmsStatus?.name ?: "ENROLLED",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "$completedCount / 7",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { completedCount / 7f },
                    modifier = Modifier.weight(1f).height(8.dp).clip(CircleShape),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
            
            Spacer(Modifier.height(12.dp))
            Text(
                "Continue your journey to unlock Kiri Certification.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun InnovationHubCard(onNavigateToHub: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clickableDebounced { onNavigateToHub() },
        shape = RoundedCornerShape(8.dp),
        color = if (isDark) NotionTintPeachDark else NotionTintPeach
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("KIRI ECOSYSTEM", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
                Text("Hub Dashboard", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text("Marketplace & AI Services.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            }
            KiriIconBadge(imageVector = Icons.Default.Bolt, backgroundColor = NotionCanvas)
        }
    }
}

@Composable
fun InnovationProgressCard(points: Int) {
    val isDark = isSystemInDarkTheme()
    val rankInfo = remember(points, isDark) {
        when {
            points >= 10000 -> Triple("Neural Tier 5 (Apex)", 1.0f, if (isDark) NotionLinkBlue else NotionPrimary)
            points >= 5000 -> Triple("Neural Tier 4 (Elite)", (points - 5000) / 5000f, NotionBrandPink)
            points >= 2500 -> Triple("Neural Tier 3 (Advanced)", (points - 2500) / 2500f, NotionBrandOrange)
            points >= 1000 -> Triple("Neural Tier 2 (Growth)", (points - 1000) / 1500f, NotionBrandTeal)
            else -> Triple("Neural Tier 1 (Initiate)", (points / 1000f), if (isDark) NotionLinkBlue else NotionPrimary)
        }
    }

    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Box(modifier = Modifier.background(
            Brush.horizontalGradient(
                colors = listOf(
                    rankInfo.third.copy(alpha = 0.05f),
                    Color.Transparent
                )
            )
        )) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(rankInfo.third.copy(alpha = if (isDark) 0.25f else 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.TrendingUp,
                        contentDescription = null,
                        tint = rankInfo.third,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "INTELLIGENCE RANK", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), 
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        rankInfo.first, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Black, 
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(Modifier.height(8.dp))
                    
                    LinearProgressIndicator(
                        progress = { rankInfo.second.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape),
                        color = rankInfo.third,
                        trackColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        points.toString(), 
                        style = MaterialTheme.typography.headlineSmall, 
                        fontWeight = FontWeight.Black, 
                        color = rankInfo.third
                    )
                    Text(
                        "UNITS", 
                        style = MaterialTheme.typography.labelSmall, 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), 
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTopBar(
    userName: String,
    onNavigateToNotifications: () -> Unit,
    onNavigateToChats: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().statusBarsPadding(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val isLightTheme = MaterialTheme.colorScheme.surface == NaukriSurface
            Text(
                "KIRI",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (isLightTheme) MaterialTheme.colorScheme.primary else Color.White,
                letterSpacing = 1.sp
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = onNavigateToNotifications,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onNavigateToChats,
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chats",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Surface(
                    modifier = Modifier.size(36.dp).clickable { onNavigateToProfile() },
                    shape = CircleShape,
                    color = if (isLightTheme) MaterialTheme.colorScheme.primaryContainer else Color(0xFF1E3A8A)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userName.take(1).uppercase(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isLightTheme) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersistentSearchBar(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Search jobs, skills, or resources...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun GreetingSection(userName: String?) {
    val safeName = userName ?: "Innovator"
    val displayName = safeName.split(" ").firstOrNull() ?: safeName
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val greeting = when (hour) {
            in 0..11 -> "Good Morning,"
            in 12..16 -> "Good Afternoon,"
            else -> "Good Evening,"
        }
        Text(
            greeting,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Medium
        )
        Text(
            displayName,
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(16.dp))
        AIStatusChip()
    }
}

@Composable
fun DiscoverCommunityCard(onNavigateToSearch: () -> Unit) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .fillMaxWidth()
            .clickableDebounced { onNavigateToSearch() },
        shape = RoundedCornerShape(8.dp),
        color = NotionPrimary
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "INTELLIGENT NETWORK",
                    style = MaterialTheme.typography.labelSmall,
                    color = NotionOnPrimary.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                )
                Text(
                    "Connect with builders and creators.",
                    style = MaterialTheme.typography.titleMedium,
                    color = NotionOnPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    "DISCOVER NODES →",
                    style = MaterialTheme.typography.labelSmall,
                    color = NotionOnPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}



@Composable
fun UpcomingEventsSection(
    events: List<EventDto>, 
    userRole: String,
    onNavigateToEvents: () -> Unit,
    onNavigateToEventDetail: (String) -> Unit,
    onNavigateToAddEvent: () -> Unit
) {
    Column {
        SectionHeader(
            title = "Live Events", 
            actionText = "View all", 
            onActionClick = onNavigateToEvents,
            secondaryActionText = if (userRole == "ADMIN" || userRole == "SPOC") "Add" else null,
            onSecondaryActionClick = onNavigateToAddEvent
        )
        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (events.isEmpty()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ) {
                    Text(
                        "No events scheduled currently.", 
                        modifier = Modifier.padding(24.dp).fillMaxWidth(), 
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall, 
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            } else {
                events.forEach { event ->
                    EventItemCard(
                        event = event,
                        onClick = {
                            val json = com.google.gson.Gson().toJson(event)
                            onNavigateToEventDetail(json)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EventItemCard(event: EventDto, onClick: () -> Unit) {
    val dateParts = event.date.split("T").first().split("-")
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
        else -> "May"
    }
    val day = dateParts.lastOrNull() ?: "26"

    Surface(
        modifier = Modifier.fillMaxWidth().clickableDebounced { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(day, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    Text(month.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(event.title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(event.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}
