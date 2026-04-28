package com.kiriplatform.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.RegisterRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    val scope = rememberCoroutineScope()
    
    var currentStep by remember { mutableIntStateOf(1) }
    
    // Form Data
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("STUDENT") }
    var rollNumber by remember { mutableStateOf("") }
    var studentLevel by remember { mutableStateOf("R1") }
    var department by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var autoEnrollAal by remember { mutableStateOf(true) }
    var skills by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var expertise by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = BgCream,
        topBar = {
            TopAppBar(
                title = { Text("Student Onboarding", fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (currentStep > 1) currentStep-- else onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Step Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StepCircle(1, currentStep)
                HorizontalDivider(modifier = Modifier.weight(1f), color = if (currentStep > 1) OrangePrimary else Color.LightGray)
                StepCircle(2, currentStep)
                HorizontalDivider(modifier = Modifier.weight(1f), color = if (currentStep > 2) OrangePrimary else Color.LightGray)
                StepCircle(3, currentStep)
                HorizontalDivider(modifier = Modifier.weight(1f), color = if (currentStep > 3) OrangePrimary else Color.LightGray)
                StepCircle(4, currentStep)
            }
            
            Spacer(Modifier.height(32.dp))

            AnimatedContent(targetState = currentStep, label = "stepTransition") { step ->
                when (step) {
                    1 -> AccountStep(
                        fullName = fullName, onFullNameChange = { fullName = it },
                        email = email, onEmailChange = { email = it },
                        password = password, onPasswordChange = { password = it }
                    )
                    2 -> RoleStep(
                        selectedRole = selectedRole,
                        onRoleChange = { selectedRole = it },
                        inviteCode = inviteCode,
                        onInviteCodeChange = { inviteCode = it }
                    )
                    3 -> DetailStep(
                        role = selectedRole,
                        rollNumber = rollNumber, onRollChange = { rollNumber = it },
                        college = college, onCollegeChange = { college = it },
                        department = department, onDeptChange = { department = it },
                        year = year, onYearChange = { year = it },
                        section = section, onSectionChange = { section = it },
                        phoneNumber = phoneNumber, onPhoneChange = { phoneNumber = it },
                        autoEnroll = autoEnrollAal, onAutoEnrollChange = { autoEnrollAal = it }
                    )
                    4 -> PersonaStep(
                        role = selectedRole,
                        bio = bio, onBioChange = { bio = it },
                        skills = skills, onSkillsChange = { skills = it },
                        expertise = expertise, onExpertiseChange = { expertise = it }
                    )
                }
            }
            
            Spacer(Modifier.height(24.dp))

            if (errorMessage != null) {
                Text(errorMessage!!, color = Color.Red, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    if (currentStep < 4) {
                        // Validation before moving to next step
                        when (currentStep) {
                            1 -> {
                                if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
                                    errorMessage = "Please fill all required fields (*)"
                                    return@Button
                                }
                                if (!email.contains("@")) {
                                    errorMessage = "Please enter a valid email"
                                    return@Button
                                }
                            }
                            2 -> {
                                if (selectedRole.isBlank()) {
                                    errorMessage = "Please select your role"
                                    return@Button
                                }
                            }
                            3 -> {
                                if (department.isBlank() || college.isBlank()) {
                                    errorMessage = "Department and Institution are required"
                                    return@Button
                                }
                            }
                        }
                        errorMessage = null
                        currentStep++
                    } else {
                        if (isLoading) return@Button
                        isLoading = true
                        errorMessage = null
                        scope.launch {
                            try {
                                if (department.isBlank()) {
                                    errorMessage = "Department is required."
                                    isLoading = false
                                    return@launch
                                }
                                val request = RegisterRequest(
                                    email = email.trim(),
                                    password = password.trim(),
                                    fullName = fullName.trim(),
                                    role = selectedRole,
                                    studentLevel = studentLevel,
                                    rollNumber = rollNumber.takeIf { it.isNotBlank() },
                                    department = department.takeIf { it.isNotBlank() },
                                    college = college.takeIf { it.isNotBlank() },
                                    year = year.takeIf { it.isNotBlank() },
                                    section = section.takeIf { it.isNotBlank() },
                                    phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                                    inviteCode = inviteCode.takeIf { it.isNotBlank() },
                                    aalAutoEnroll = autoEnrollAal,
                                    services = skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                )
                                // We'll update the bio/expertise in a separate profile call or extend the request
                                val response = ApiClient.service.register(request)
                                val user = response.user
                                val token = response.token
                                
                                // Validate response data
                                if (user == null) {
                                    errorMessage = "Registration succeeded but user data is missing."
                                    isLoading = false
                                    return@launch
                                }
                                
                                if (token.isBlank()) {
                                    errorMessage = "Registration succeeded but authentication token is missing."
                                    isLoading = false
                                    return@launch
                                }
                                
                                if (user.id.isBlank()) {
                                    errorMessage = "Registration succeeded but user ID is missing."
                                    isLoading = false
                                    return@launch
                                }
                                
                                sessionManager.saveToken(token)
                                sessionManager.saveUserId(user.id)
                                sessionManager.saveUserName(user.fullName)
                                sessionManager.saveUserRole(user.role)
                                ApiClient.setToken(token)

                                onRegisterSuccess()
                            } catch (e: Throwable) {
                                errorMessage = when (e) {
                                    is java.net.UnknownHostException -> "No internet connection. Please check your network."
                                    is java.net.SocketTimeoutException -> "Server connection timed out."
                                    is retrofit2.HttpException -> {
                                        if (e.code() == 409) "An account with this email already exists."
                                        else "Server error: ${e.code()}"
                                    }
                                    else -> e.localizedMessage ?: "Registration failed. Please try again."
                                }
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (currentStep == 4) "Finalize Account" else "Next Step →", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StepCircle(step: Int, currentStep: Int) {
    val isCompleted = currentStep > step
    val isActive = currentStep == step
    
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (isCompleted || isActive) OrangePrimary else Color.LightGray),
        contentAlignment = Alignment.Center
    ) {
        if (isCompleted) {
            Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
        } else {
            Text(step.toString(), color = if (isActive) Color.White else Color.DarkGray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AccountStep(
    fullName: String, onFullNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Account Basics", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        OutlinedTextField(fullName, onFullNameChange, label = { Text("Full Name *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(email, onEmailChange, label = { Text("Email Address *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        var passVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            password, onPasswordChange, 
            label = { Text("Password *") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passVisible = !passVisible }) {
                    Icon(if (passVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, null)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleStep(
    selectedRole: String, onRoleChange: (String) -> Unit,
    inviteCode: String, onInviteCodeChange: (String) -> Unit
) {
    val roles = listOf("STUDENT" to "🎓 Student", "FOUNDER" to "🚀 Founder", "MENTOR" to "👨‍🏫 Mentor", "SPOC" to "🏢 SPOC")
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Identify Your Role", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        roles.forEach { (key, label) ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onRoleChange(key) },
                colors = CardDefaults.cardColors(containerColor = if (selectedRole == key) OrangePrimary.copy(alpha = 0.1f) else Color.White),
                border = androidx.compose.foundation.BorderStroke(2.dp, if (selectedRole == key) OrangePrimary else Color.Transparent)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selectedRole == key, onClick = { onRoleChange(key) })
                    Text(label, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
        if (selectedRole != "STUDENT") {
            OutlinedTextField(inviteCode, onInviteCodeChange, label = { Text("Invite Code") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailStep(
    role: String,
    rollNumber: String, onRollChange: (String) -> Unit,
    college: String, onCollegeChange: (String) -> Unit,
    department: String, onDeptChange: (String) -> Unit,
    year: String, onYearChange: (String) -> Unit,
    section: String, onSectionChange: (String) -> Unit,
    phoneNumber: String, onPhoneChange: (String) -> Unit,
    autoEnroll: Boolean, onAutoEnrollChange: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Professional Identity", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        
        if (role == "STUDENT") {
            OutlinedTextField(rollNumber, onRollChange, label = { Text("Roll Number / PRN") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
                OutlinedTextField(college, {}, readOnly = true, label = { Text("Institution") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp))
                ExposedDropdownMenu(expanded, { expanded = false }) {
                    listOf("GH Raisoni Jalgaon", "GCOE Jalgaon", "SSBT Bambhori", "KBCNMU", "Other").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { onCollegeChange(it); expanded = false })
                    }
                }
            }
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(year, onYearChange, label = { Text("Year") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(section, onSectionChange, label = { Text("Section") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(autoEnroll, onAutoEnrollChange, colors = CheckboxDefaults.colors(checkedColor = OrangePrimary))
                Text("Enroll in Kiri Organization Internship", style = MaterialTheme.typography.bodyMedium)
            }
        }
        
        OutlinedTextField(department, onDeptChange, label = { Text("Department *") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(phoneNumber, onPhoneChange, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
    }
}

@Composable
fun PersonaStep(
    role: String,
    bio: String, onBioChange: (String) -> Unit,
    skills: String, onSkillsChange: (String) -> Unit,
    expertise: String, onExpertiseChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Digital Persona", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text("Help the AI Agent map your skills to regional opportunities.", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        
        OutlinedTextField(
            value = bio,
            onValueChange = onBioChange,
            label = { Text("Short Bio / Intent") },
            placeholder = { Text("e.g. Building an EdTech startup for rural students...") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = skills,
            onValueChange = onSkillsChange,
            label = { Text(if (role == "STUDENT") "Skills (React, Python, etc.)" else "Services Provided") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (role != "STUDENT") {
            OutlinedTextField(
                value = expertise,
                onValueChange = onExpertiseChange,
                label = { Text("Primary Expertise (Fintech, AI, etc.)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}
