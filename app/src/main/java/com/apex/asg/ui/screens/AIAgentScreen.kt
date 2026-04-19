package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*
import com.apex.asg.ui.viewmodels.KiriAIViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAgentScreen(vm: KiriAIViewModel = viewModel()) {
    var textState by remember { mutableStateOf("") }
    val messages = vm.messages
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = BgCream,
        topBar = {
            TopAppBar(
                title = { Text("KIRI AI", style = MaterialTheme.typography.labelLarge, color = TextPrimary, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center) },
                navigationIcon = { Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.padding(16.dp)) },
                actions = {
                    Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.padding(8.dp))
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.padding(end = 16.dp, start = 8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCream)
            )
        },
        bottomBar = {
            KiriInputBar(
                text = textState,
                onTextChange = { textState = it },
                onSend = {
                    vm.sendMessage(textState)
                    textState = ""
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (messages.isEmpty()) {
                KiriEmptyState()
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 24.dp)
                ) {
                    items(messages) { msg ->
                        KiriMessageBubble(msg)
                    }
                }
            }
        }
    }
}

@Composable
fun KiriEmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "SYSTEM_READY",
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            letterSpacing = 2.sp
        )
        Spacer(Modifier.height(32.dp))
        Text(
            "KIRI AI",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Black,
            color = TextPrimary,
            fontSize = 72.sp
        )
        Spacer(Modifier.height(32.dp))
        Text(
            "Multimodal intelligence layer active.\nSend a message to begin analysis.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp),
            lineHeight = 24.sp
        )
    }
}

@Composable
fun KiriMessageBubble(msg: com.apex.asg.ui.viewmodels.KiriMessage) {
    val isUser = msg.role == "user"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Text(
            text = if (isUser) "YOU" else "KIRI AI",
            style = MaterialTheme.typography.labelSmall,
            color = if (isUser) OrangePrimary else TextSecondary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Surface(
            color = if (isUser) OrangePrimary else Color.White,
            shape = RoundedCornerShape(12.dp),
            border = if (!isUser) BorderStroke(1.dp, BorderColor) else null,
            shadowElevation = if (isUser) 4.dp else 1.dp
        ) {
            Text(
                text = msg.content,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color.White else TextPrimary
            )
        }
    }
}

@Composable
fun KiriInputBar(text: String, onTextChange: (String) -> Unit, onSend: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 80.dp), // Adjust for bottom nav
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            placeholder = { Text("MESSAGE / LOG", style = MaterialTheme.typography.bodyMedium, color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Add, contentDescription = null) },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BorderColor.copy(alpha = 0.3f),
                unfocusedContainerColor = BorderColor.copy(alpha = 0.3f),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )
        Spacer(Modifier.width(12.dp))
        FloatingActionButton(
            onClick = onSend,
            containerColor = BorderColor.copy(alpha = 0.5f),
            contentColor = TextPrimary,
            shape = CircleShape,
            modifier = Modifier.size(56.dp),
            elevation = FloatingActionButtonDefaults.elevation(0.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
        }
    }
}

