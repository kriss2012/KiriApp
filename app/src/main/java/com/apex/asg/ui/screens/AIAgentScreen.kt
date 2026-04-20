package com.apex.asg.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.theme.*
import com.apex.asg.ui.viewmodels.KiriAIViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIAgentScreen(
    onBack: () -> Unit = {},
    vm: KiriAIViewModel = viewModel()
) {
    var textState by remember { mutableStateOf("") }
    val messages = vm.messages
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val context = androidx.compose.ui.platform.LocalContext.current
    
    // File Picker
    val selectedFileUri by vm.selectedFileUri.collectAsState()
    val selectedFileName by vm.selectedFileName.collectAsState()
    
    val filePickerLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { vm.onFileSelected(context, it) }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text("KIRI AI", style = MaterialTheme.typography.labelLarge, color = TextPrimary, fontWeight = FontWeight.Black, letterSpacing = 2.sp) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.navigationBarsPadding().imePadding()) {
                KiriInputBar(
                    text = textState,
                    onTextChange = { textState = it },
                    selectedFileName = selectedFileName,
                    onAttachClick = { filePickerLauncher.launch("*/*") },
                    onCancelAttachment = { vm.clearFileSelection() },
                    onSend = {
                        vm.sendMessage(textState, context)
                        textState = ""
                    }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding())
        ) {
            SpecializationSelector(
                selected = vm.currentSpecialization.collectAsState().value,
                onSelected = { vm.setSpecialization(it) }
            )

            if (messages.isEmpty()) {
                KiriEmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    state = listState,
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
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
fun KiriInputBar(
    text: String, 
    onTextChange: (String) -> Unit, 
    onSend: () -> Unit,
    onAttachClick: () -> Unit,
    selectedFileName: String?,
    onCancelAttachment: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Color.White.copy(alpha = 0.98f),
                shape = RoundedCornerShape(28.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // Attachment Preview
        if (selectedFileName != null) {
            Surface(
                color = OrangePrimary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 8.dp, top = 8.dp, start = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(16.dp))
                    Text(selectedFileName, style = MaterialTheme.typography.labelSmall, color = OrangePrimary, maxLines = 1)
                    IconButton(onClick = onCancelAttachment, modifier = Modifier.size(16.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = OrangePrimary)
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttachClick) {
                Icon(Icons.Default.Add, contentDescription = "Attach", tint = TextPrimary)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text(
                        "MESSAGE / LOG",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary.copy(alpha = 0.6f)
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    cursorBrush = SolidColor(OrangePrimary)
                )
            }

            IconButton(
                onClick = onSend,
                enabled = text.isNotBlank() || selectedFileName != null
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank()) OrangePrimary else TextSecondary.copy(alpha = 0.3f),
                    modifier = Modifier.size(24.dp)
                )
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
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Text(
                text = if (isUser) "YOU" else "KIRI AI",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (isUser) OrangePrimary else TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp, start = if (isUser) 0.dp else 12.dp, end = if (isUser) 12.dp else 0.dp)
            )
            Surface(
                color = if (isUser) OrangePrimary else Color.White,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isUser) 20.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 20.dp
                ),
                shadowElevation = 2.dp,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                val bubbleModifier = if (isUser) {
                    Modifier.background(
                        Brush.linearGradient(
                            colors = listOf(OrangePrimary, Color(0xFFE04F0A))
                        )
                    )
                } else Modifier

                Text(
                    text = msg.content,
                    modifier = bubbleModifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) Color.White else TextPrimary,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun SpecializationSelector(selected: String, onSelected: (String) -> Unit) {
    val options = listOf("GENERAL", "TECH", "LEGAL", "GTM")
    androidx.compose.foundation.lazy.LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(options) { option ->
            val isSelected = selected == option
            ElevatedFilterChip(
                selected = isSelected,
                onClick = { onSelected(option) },
                label = { 
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = OrangePrimary,
                    selectedLabelColor = Color.White,
                    containerColor = Color.White,
                    labelColor = TextSecondary
                ),
                elevation = FilterChipDefaults.elevatedFilterChipElevation(elevation = 2.dp),
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

