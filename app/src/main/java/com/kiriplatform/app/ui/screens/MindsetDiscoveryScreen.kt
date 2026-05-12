package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MindsetDiscoveryScreen(
    onBack: () -> Unit,
    onComplete: (Map<String, Int>) -> Unit
) {
    val questions = listOf(
        "I am comfortable with failure as a learning step." to "mindset_resilience",
        "I actively look for problems to solve in my local area." to "problem_identification",
        "I believe AI can solve complex social issues." to "ai_optimism",
        "I prefer building solutions over theoretical learning." to "builder_intent",
        "I am willing to collaborate with diverse teams." to "collaboration"
    )
    
    val scores = remember { mutableStateMapOf<String, Int>() }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mindset Discovery", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "Activity 1: The Entrepreneurial Audit",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            
            questions.forEach { (text, key) ->
                QuestionItem(text = text, onScoreChange = { scores[key] = it })
            }
            
            Button(
                onClick = { onComplete(scores.toMap()) },
                modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Lock Mindset Data", fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun QuestionItem(text: String, onScoreChange: (Int) -> Unit) {
    var score by remember { mutableIntStateOf(3) }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Disagree", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Slider(
                value = score.toFloat(),
                onValueChange = { 
                    score = it.toInt()
                    onScoreChange(score)
                },
                valueRange = 1f..5f,
                steps = 3,
                modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
            )
            Text("Agree", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
