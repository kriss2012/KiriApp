package com.kiriplatform.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.components.KiriPrimaryButton
import com.kiriplatform.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@Composable
fun SplashScreen(
    onJoinCommunity: () -> Unit,
    onOrganization: () -> Unit,
    onSignIn: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    val buttonOffset = remember { Animatable(50f) }

    LaunchedEffect(true) {
        // Reduced delays and removed logo animations for seamless transition
        delay(100) 
        launch {
            alpha.animateTo(1f, animationSpec = tween(500))
        }
        launch {
            buttonOffset.animateTo(0f, animationSpec = tween(500))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // ASG Premium Logo Component (Static to avoid double animation)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
        ) {
            Image(
                painter = painterResource(id = com.kiriplatform.app.R.drawable.kiri_logo_new),
                contentDescription = "Kiri Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "KIRI",
                style = MaterialTheme.typography.displaySmall,
                color = NotionInk,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
            Text(
                "INNOVATION HUB OF BHARAT",
                style = MaterialTheme.typography.labelSmall,
                color = NotionSteel,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pulsing Badge
        Surface(
            color = NotionTintLavender,
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, NotionPrimary.copy(alpha = 0.1f)),
            modifier = Modifier.alpha(alpha.value)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(NotionPrimary, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Startup Community of Bharat",
                    style = MaterialTheme.typography.labelSmall,
                    color = NotionBrandPurple800,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = buildAnnotatedString {
                append("Where ")
                withStyle(style = SpanStyle(color = NotionPrimary)) { append("Founders, Investors") }
                append("\n& Mentors Connect")
            },
            style = MaterialTheme.typography.headlineMedium,
            color = NotionInk,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(alpha.value),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "We bring together the most ambitious entrepreneurs to learn, collaborate and grow.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(alpha.value)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Buttons
        Column(
            modifier = Modifier
                .offset(y = buttonOffset.value.dp)
                .alpha(if (buttonOffset.value < 50f) 1f else 0f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KiriPrimaryButton(
                text = "Join the Community →",
                onClick = onJoinCommunity
            )
            
            OutlinedButton(
                onClick = onOrganization,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, NotionHairline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NotionInk)
            ) {
                Text(
                    "KIRI AI Launchpad →",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TextButton(
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Already have an account? Sign In",
                    style = MaterialTheme.typography.labelMedium,
                    color = NotionSteel,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
