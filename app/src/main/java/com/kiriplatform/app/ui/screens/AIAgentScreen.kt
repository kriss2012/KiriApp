package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Close
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
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.ui.viewmodels.KiriAIViewModel
import com.kiriplatform.app.utils.glassmorphism
import com.kiriplatform.app.utils.shimmer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.BorderStroke

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Decorative glow
        Box(
            modifier = Modifier
                .size(400.dp)
                .offset(x = 100.dp, y = (-100).dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f), RoundedCornerShape(200.dp))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            // ── Top bar ───────────────────────────────────────────────────
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "KIRI INTELLIGENCE",
                        style = MaterialTheme.typography.labelLarge,
                        color = NotionInk,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 2.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = NotionInk)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = NotionCanvas
                )
            )

            // ── Specialization chips ──────────────────────────────────────
            SpecializationSelector(
                selected = vm.currentSpecialization.collectAsState().value,
                onSelected = { vm.setSpecialization(it) }
            )

            // ── Messages 
            Box(modifier = Modifier.weight(1f)) {
                if (messages.isEmpty()) {
                    KiriEmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        state = listState,
                        contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(messages) { msg ->
                            KiriMessageBubble(msg)
                        }
                    }
                }
            }

            // ── Input bar ──────
            KiriInputBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .navigationBarsPadding(),
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
    }
}

@Composable
fun KiriInputBar(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttachClick: () -> Unit,
    selectedFileName: String?,
    onCancelAttachment: () -> Unit
) {
    Column(
        modifier = modifier
            .glassmorphism(cornerRadius = 32.dp, alpha = 0.08f)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Attachment preview
        if (selectedFileName != null) {
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp, top = 4.dp, start = 8.dp, end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Description, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Text(selectedFileName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1, modifier = Modifier.weight(1f))
                    IconButton(onClick = onCancelAttachment, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAttachClick) {
                Icon(Icons.Default.Add, contentDescription = "Attach", tint = MaterialTheme.colorScheme.primary)
            }

            Box(
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (text.isEmpty()) {
                    Text("Analyze with Kiri...", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    maxLines = 5
                )
            }

            FloatingActionButton(
                onClick = onSend,
                modifier = Modifier.size(44.dp),
                shape = RoundedCornerShape(22.dp),
                containerColor = if (text.isNotBlank() || selectedFileName != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer,
                elevation = FloatingActionButtonDefaults.elevation(0.dp)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (text.isNotBlank() || selectedFileName != null) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
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
        Box(
            modifier = Modifier
                .size(100.dp)
                .glassmorphism(cornerRadius = 30.dp, alpha = 0.1f)
                .shimmer()
        )
        Spacer(Modifier.height(48.dp))
        Text("KIRI AI", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground, fontSize = 64.sp, letterSpacing = (-2).sp)
        Spacer(Modifier.height(16.dp))
        Text(
            "MULTIMODAL INTELLIGENCE ACTIVE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 4.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(32.dp))
        Text(
            "Upload documents or describe your vision to begin high-fidelity processing.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 56.dp),
            lineHeight = 26.sp
        )
    }
}

@Composable
fun KiriMessageBubble(msg: com.kiriplatform.app.ui.viewmodels.KiriMessage) {
    val isUser = msg.role == "user"
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
            Surface(
                color = if (isUser) NotionPrimary else NotionSurface,
                shape = MaterialTheme.shapes.medium, // 8dp
                border = if (isUser) null else BorderStroke(1.dp, NotionHairline),
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(
                    text = msg.content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isUser) NotionOnPrimary else NotionInk,
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
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(options) { option ->
            val isSelected = selected == option
            Surface(
                onClick = { onSelected(option) },
                shape = RoundedCornerShape(20.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.height(40.dp)
            ) {
                Box(modifier = Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
                    Text(
                        option, 
                        style = MaterialTheme.typography.labelMedium, 
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
