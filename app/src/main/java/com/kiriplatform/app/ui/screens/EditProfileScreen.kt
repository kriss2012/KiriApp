package com.kiriplatform.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.ui.viewmodels.ProfileState
import com.kiriplatform.app.ui.viewmodels.ProfileViewModel
import com.kiriplatform.app.ui.components.ErrorDialog
import com.kiriplatform.app.ui.components.SuccessDialog
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val userId = sessionManager.getUserId() ?: ""
    val uiState by viewModel.uiState.collectAsState()

    var fullName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var githubUrl by remember { mutableStateOf("") }
    var linkedInUrl by remember { mutableStateOf("") }
    var servicesStr by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchProfile(context, userId)
        }
    }

    // Single unified effect — Compose only runs one LaunchedEffect per key.
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is ProfileState.Success -> {
                if (isSaving) {
                    showSuccessDialog = true
                    isSaving = false
                } else {
                    // Initial fetch population
                    fullName = state.user.fullName
                    bio = state.user.bio ?: ""
                    role = state.user.role
                    department = state.user.department ?: ""
                    college = state.user.college ?: ""
                    year = state.user.year ?: ""
                    phoneNumber = state.user.phoneNumber ?: ""
                    website = state.user.website ?: ""
                    githubUrl = state.user.githubUrl ?: ""
                    linkedInUrl = state.user.linkedInUrl ?: ""
                    servicesStr = state.user.services?.joinToString(", ") ?: ""
                }
            }
            is ProfileState.Error -> {
                errorMessage = state.message
                showErrorDialog = true
                isSaving = false
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EditField(label = "Full Name", value = fullName, onValueChange = { fullName = it })
            EditField(label = "Bio", value = bio, onValueChange = { bio = it }, singleLine = false)
            EditField(label = "PhoneNumber", value = phoneNumber, onValueChange = { phoneNumber = it })
            EditField(label = "Website", value = website, onValueChange = { website = it })
            EditField(label = "GitHub URL", value = githubUrl, onValueChange = { githubUrl = it })
            EditField(label = "LinkedIn URL", value = linkedInUrl, onValueChange = { linkedInUrl = it })
            EditField(label = "Services (comma separated)", value = servicesStr, onValueChange = { servicesStr = it })
            
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), thickness = 1.dp)
            
            val roles = listOf("STUDENT", "FOUNDER", "MENTOR", "SPOC")
            var expanded by remember { mutableStateOf(false) }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Role", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        roles.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    role = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            EditField(label = "Department", value = department, onValueChange = { department = it })
            EditField(label = "College", value = college, onValueChange = { college = it })
            EditField(label = "Year / Level", value = year, onValueChange = { year = it })

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val cleanServices = servicesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    viewModel.updateProfile(
                        context = context,
                        userId = userId,
                        fullName = fullName,
                        role = role,
                        bio = bio,
                        department = department,
                        college = college,
                        year = year,
                        phoneNumber = phoneNumber,
                        website = website,
                        githubUrl = githubUrl,
                        linkedInUrl = linkedInUrl,
                        services = cleanServices
                    )
                    isSaving = true
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                if (isSaving && uiState is ProfileState.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (showErrorDialog) {
            ErrorDialog(
                title = "Profile Update Failed",
                message = errorMessage,
                onDismiss = { showErrorDialog = false }
            )
        }

        if (showSuccessDialog) {
            SuccessDialog(
                message = "Your profile changes have been saved successfully!",
                onDismiss = {
                    showSuccessDialog = false
                    onBack()
                }
            )
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    readOnly: Boolean = false
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            readOnly = readOnly,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
                disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
