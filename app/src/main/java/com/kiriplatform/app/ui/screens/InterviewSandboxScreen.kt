package com.kiriplatform.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
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
import com.google.gson.Gson
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.InterviewEvaluation
import com.kiriplatform.app.data.remote.models.MockInterviewMessage
import com.kiriplatform.app.data.remote.models.MockInterviewRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSandboxScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    var role by remember { mutableStateOf("Android Developer") }
    var hasStarted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val messages = remember { mutableStateListOf<MockInterviewMessage>() }
    var inputText by remember { mutableStateOf("") }
    
    var showEvaluationDialog by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<InterviewEvaluation?>(null) }
    var isEvaluating by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()

    fun startInterview() {
        if (role.isBlank()) {
            Toast.makeText(context, "Please enter a target job role", Toast.LENGTH_SHORT).show()
            return
        }
        messages.clear()
        hasStarted = true
        scope.launch {
            isLoading = true
            try {
                val response = ApiClient.service.mockInterview(
                    MockInterviewRequest(role = role, messages = emptyList(), feedbackRequested = false)
                )
                if (response.success) {
                    messages.add(MockInterviewMessage(role = "assistant", content = response.response))
                } else {
                    Toast.makeText(context, "Failed to start interview", Toast.LENGTH_SHORT).show()
                    hasStarted = false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error starting interview: ${e.message}", Toast.LENGTH_SHORT).show()
                hasStarted = false
            } finally {
                isLoading = false
            }
        }
    }

    fun sendMessage() {
        if (inputText.isBlank()) return
        val userMsg = MockInterviewMessage(role = "user", content = inputText)
        messages.add(userMsg)
        val currentInput = inputText
        inputText = ""
        
        scope.launch {
            isLoading = true
            try {
                // Scroll to bottom
                listState.animateScrollToItem(messages.size - 1)
                
                val response = ApiClient.service.mockInterview(
                    MockInterviewRequest(
                        role = role,
                        messages = messages.toList(),
                        feedbackRequested = false
                    )
                )
                if (response.success) {
                    messages.add(MockInterviewMessage(role = "assistant", content = response.response))
                    listState.animateScrollToItem(messages.size - 1)
                } else {
                    Toast.makeText(context, "Server error responding", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to send message: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    fun requestEvaluation() {
        scope.launch {
            isEvaluating = true
            try {
                val response = ApiClient.service.mockInterview(
                    MockInterviewRequest(
                        role = role,
                        messages = messages.toList(),
                        feedbackRequested = true
                    )
                )
                if (response.success) {
                    // Parse JSON response into InterviewEvaluation
                    val cleanJson = response.response
                    val eval = Gson().fromJson(cleanJson, InterviewEvaluation::class.java)
                    evaluationResult = eval
                    showEvaluationDialog = true
                } else {
                    Toast.makeText(context, "Evaluation request failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Error evaluating interview: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isEvaluating = false
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Interview Prep Sandbox", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        if (hasStarted) {
                            Text("Interviewing for: $role", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
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
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (!hasStarted) {
                // Role Selection Setup UI
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Practice Mock Interviews with AI",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Specify your target role. The AI interviewer will ask realistic domain-specific questions one-by-one to evaluate your core competencies.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = role,
                        onValueChange = { role = it },
                        label = { Text("Target Job Role") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = { startInterview() },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start Practice Session", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Active Interview Chat Interface
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat messages list
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                    ) {
                        items(messages) { message ->
                            val isAssistant = message.role == "assistant"
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isAssistant) Arrangement.Start else Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .widthIn(max = 280.dp)
                                        .clip(
                                            RoundedCornerShape(
                                                topStart = 12.dp,
                                                topEnd = 12.dp,
                                                bottomStart = if (isAssistant) 0.dp else 12.dp,
                                                bottomEnd = if (isAssistant) 12.dp else 0.dp
                                            )
                                        )
                                        .background(
                                            if (isAssistant) MaterialTheme.colorScheme.surfaceVariant
                                            else MaterialTheme.colorScheme.primaryContainer
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = message.content,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isAssistant) MaterialTheme.colorScheme.onSurfaceVariant
                                        else MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        if (isLoading) {
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .padding(12.dp)
                                    ) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    }
                                }
                            }
                        }
                    }

                    // Top/Bottom action bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { requestEvaluation() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            enabled = !isEvaluating && messages.size >= 2,
                            modifier = Modifier.height(48.dp)
                        ) {
                            if (isEvaluating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onSecondary)
                            } else {
                                Text("Evaluate", fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text("Answer here...") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = { sendMessage() },
                            enabled = inputText.isNotBlank() && !isLoading,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = "Send",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }

            // Evaluation Report Dialog Overlay
            if (showEvaluationDialog && evaluationResult != null) {
                val eval = evaluationResult!!
                AlertDialog(
                    onDismissRequest = { showEvaluationDialog = false },
                    confirmButton = {
                        Button(onClick = { 
                            showEvaluationDialog = false 
                            hasStarted = false
                            messages.clear()
                        }) {
                            Text("Done")
                        }
                    },
                    title = {
                        Text("Mock Interview Feedback", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    },
                    text = {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Circular Scores
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Overall", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(
                                                progress = { eval.overallScore / 100f },
                                                modifier = Modifier.size(60.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                strokeWidth = 6.dp
                                            )
                                            Text("${eval.overallScore}%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("Confidence", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Box(contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(
                                                progress = { eval.confidenceScore / 100f },
                                                modifier = Modifier.size(60.dp),
                                                color = MaterialTheme.colorScheme.secondary,
                                                strokeWidth = 6.dp
                                            )
                                            Text("${eval.confidenceScore}%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }

                            // Technical Feedback
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Technical Competency", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(eval.feedback.technical, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }

                            // Communication Feedback
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Communication & Clarity", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(eval.feedback.communication, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }

                            // Problem Solving
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text("Problem Solving", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(eval.feedback.problemSolving, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }

                            // Suggestions List
                            item {
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("Actionable Recommendations", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                    eval.suggestions.forEach { suggestion ->
                                        Text("• $suggestion", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}
