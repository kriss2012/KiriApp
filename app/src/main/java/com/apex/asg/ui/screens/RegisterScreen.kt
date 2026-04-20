package com.apex.asg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.apex.asg.ui.components.ASGPrimaryButton
import com.apex.asg.ui.theme.*
import com.apex.asg.data.SessionManager
import com.apex.asg.data.remote.ApiClient
import com.apex.asg.data.remote.models.RegisterRequest
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("STUDENT") }
    var studentLevel by remember { mutableStateOf<String?>(null) }
    var department by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var servicesStr by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val roles = listOf(
        "STUDENT" to "🎓 Student",
        "FOUNDER" to "🚀 Founder",
        "MENTOR" to "👨‍🏫 Mentor",
        "INVESTOR" to "💼 Investor",
        "SERVICE_PROVIDER" to "🛠 Service Provider",
        "SPOC" to "🏢 College Committee (SPOC)"
    )

    Scaffold(containerColor = BgCream) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Join ASG Community",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Create an account to start your journey",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches(),
                supportingText = {
                    if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                      Text("Please enter a valid email", color = Color.Red)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            var passwordVisible by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password *") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) androidx.compose.material.icons.Icons.Default.Visibility else androidx.compose.material.icons.Icons.Default.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(image, contentDescription = null)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Select Your Role *", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            
            Column {
                roles.forEach { (roleKey, roleLabel) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = roleKey }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedRole == roleKey, onClick = { selectedRole = roleKey })
                        Text(roleLabel, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
04:20:15:46:58:123
            // Invitation Code for protected roles
            val protectedRolesSet = setOf("ADMIN", "SPOC", "MENTOR", "INVESTOR")
            if (protectedRolesSet.contains(selectedRole)) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it },
                    label = { Text("Invitation Code (Required) *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = OrangePrimary,
                        unfocusedBorderColor = OrangePrimary.copy(alpha = 0.5f)
                    ),
                    placeholder = { Text("Enter the code provided by ASG Admin") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Professional Details (Optional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = website,
                onValueChange = { website = it },
                label = { Text("Website / Portfolio") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = servicesStr,
                onValueChange = { servicesStr = it },
                label = { Text("Services (comma separated)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (selectedRole == "STUDENT") {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Student Level", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("R1", "R2", "R3", "R4", "R5").forEach { level ->
                        FilterChip(
                            selected = studentLevel == level,
                            onClick = { studentLevel = level },
                            label = { Text(level) }
                        )
                    }
                }
            }

            if (selectedRole == "STUDENT" || selectedRole == "SPOC") {
                Spacer(modifier = Modifier.height(16.dp))
                
                var expanded by remember { mutableStateOf(false) }
                val jalgaonColleges = listOf(
                    "GH Raisoni Institute of Engineering & Business Management, Jalgaon",
                    "Government College of Engineering, Jalgaon (GCOEJ)",
                    "SSBT's College of Engineering & Technology, Bambhori",
                    "KBC North Maharashtra University (KBCNMU)",
                    "Moolji Jaitha College (MJ College)",
                    "KCES's College of Engineering and Management (COEM)",
                    "Godavari College of Engineering",
                    "Shri Gulabrao Deokar College of Engineering (SGDCOE)",
                    "Pratibha College of Education",
                    "DNCVP's College of Social Work",
                    "Nuton Maratha College",
                    "Other / Outsider"
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = college,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("College Name") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        jalgaonColleges.forEach { collegeName ->
                            DropdownMenuItem(
                                text = { Text(collegeName) },
                                onClick = {
                                    college = collegeName
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = year,
                        onValueChange = { year = it },
                        label = { Text("Year (1-4)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = section,
                        onValueChange = { section = it },
                        label = { Text("Section (S1/S2)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(8.dp))
            }

            ASGPrimaryButton(
                text = if (isLoading) "Creating Account..." else "Register",
                onClick = {
                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val request = RegisterRequest(
                                email = email.trim(),
                                password = password.trim(),
                                fullName = fullName.trim(),
                                role = selectedRole,
                                studentLevel = studentLevel,
                                department = department.takeIf { it.isNotBlank() },
                                college = college.takeIf { it.isNotBlank() },
                                year = year.takeIf { it.isNotBlank() },
                                section = section.takeIf { it.isNotBlank() },
                                phoneNumber = phoneNumber.takeIf { it.isNotBlank() },
                                website = website.takeIf { it.isNotBlank() },
                                services = servicesStr.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                                inviteCode = inviteCode.takeIf { it.isNotBlank() }
                            )
                            val response = ApiClient.service.register(request)
                            
                            sessionManager.saveToken(response.token)
                            sessionManager.saveUserId(response.user.id)
                            sessionManager.saveUserRole(response.user.role)
                            ApiClient.setToken(response.token)

                            isLoading = false
                            onRegisterSuccess()
                        } catch (e: Exception) {
                            isLoading = false
                            errorMessage = e.message ?: "Registration failed"
                        }
                    }
                },
                enabled = !isLoading && 
                          email.isNotEmpty() && 
                          android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
                          password.isNotEmpty() && 
                          fullName.isNotEmpty() && 
                          selectedRole.isNotEmpty() && 
                          department.isNotEmpty() &&
                          (!setOf("ADMIN", "SPOC", "MENTOR", "INVESTOR").contains(selectedRole) || inviteCode.isNotBlank())
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Already have an account? ", color = TextSecondary)
                Text(
                    "Sign In",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
