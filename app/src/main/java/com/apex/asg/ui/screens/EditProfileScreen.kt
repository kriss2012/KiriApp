package com.apex.asg.ui.screens

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.apex.asg.data.SessionManager
import com.apex.asg.ui.theme.*
import com.apex.asg.ui.viewmodels.ProfileState
import com.apex.asg.ui.viewmodels.ProfileViewModel

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

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.fetchProfile(context, userId)
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is ProfileState.Success) {
            val user = (uiState as ProfileState.Success).user
            fullName = user.fullName
            bio = user.bio ?: ""
            role = user.role
            department = user.department ?: ""
            college = user.college ?: ""
            year = user.year ?: ""
            phoneNumber = user.phoneNumber ?: ""
            website = user.website ?: ""
            githubUrl = user.githubUrl ?: ""
            linkedInUrl = user.linkedInUrl ?: ""
            servicesStr = user.services.joinToString(", ")
        }
    }

    LaunchedEffect(uiState) {
        if (isSaving && uiState is ProfileState.Success) {
            onBack()
            isSaving = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgCream)
            )
        },
        containerColor = BgCream
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
            
            Divider(color = BorderColor.copy(alpha = 0.5f), thickness = 1.dp)
            
            EditField(label = "Role", value = role, onValueChange = { role = it })
            EditField(label = "Department", value = department, onValueChange = { department = it })
            EditField(label = "College", value = college, onValueChange = { college = it })
            EditField(label = "Year / Level", value = year, onValueChange = { year = it })

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
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
                        services = servicesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                    )
                    isSaving = true
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary)
            ) {
                if (uiState is ProfileState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EditField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelMedium, color = TextSecondary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = OrangePrimary,
                unfocusedBorderColor = BorderColor
            )
        )
    }
}
