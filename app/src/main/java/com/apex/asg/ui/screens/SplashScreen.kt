package com.apex.asg.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.apex.asg.ui.components.ASGPrimaryButton
import com.apex.asg.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource

@Composable
fun SplashScreen(
    onJoinCommunity: () -> Unit,
    onLaunchpad: () -> Unit,
    onSignIn: () -> Unit
) {
    val scale = remember { Animatable(0.8f) }
    val alpha = remember { Animatable(0f) }
    val buttonOffset = remember { Animatable(50f) }

    LaunchedEffect(true) {
        // Reduced delays and removed logo animations for seamless transition
        delay(100) 
        launch {
            buttonOffset.animateTo(0f, animationSpec = tween(500))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgCream)
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
                painter = painterResource(id = com.apex.asg.R.drawable.asg_logo_new),
                contentDescription = "ASG Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "ASG PLATFORM",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp
            )
            Text(
                "INNOVATION HUB OF BHARAT",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Pulsing Badge
        Surface(
            color = OrangeLight,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, OrangePrimary),
            modifier = Modifier.alpha(alpha.value)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(1.dp, OrangePrimary, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(OrangePrimary, RoundedCornerShape(50))
                )
                Spacer(Modifier.width(5.dp))
                Text(
                    "Building the Startup Community of Bharat",
                    style = MaterialTheme.typography.labelSmall,
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = buildAnnotatedString {
                append("Where ")
                withStyle(style = SpanStyle(color = OrangePrimary)) { append("Founders, Investors") }
                append(" & Mentors Connect")
            },
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(alpha.value)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            "We bring together the most ambitious entrepreneurs to learn, collaborate and grow.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.alpha(alpha.value)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Bottom Buttons
        Column(
            modifier = Modifier
                .offset(y = buttonOffset.value.dp)
                .alpha(if (buttonOffset.value < 50f) 1f else 0f)
        ) {
            ASGPrimaryButton(
                text = "Join the Community →",
                onClick = onJoinCommunity
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onLaunchpad,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
            ) {
                Text(
                    "APEX AI Launchpad →",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onSignIn,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, BorderColor) // Using BorderStroke
            ) {
                Text(
                    "Sign In",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

