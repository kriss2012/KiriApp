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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("STUDENT") }
    var studentLevel by remember { mutableStateOf<String?>(null) }
    var department by remember { mutableStateOf("") }
    var college by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var section by remember { mutableStateOf("") }
    
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
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text("Select Your Role", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            
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
                OutlinedTextField(
                    value = college,
                    onValueChange = { college = it },
                    label = { Text("College Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = department,
                    onValueChange = { department = it },
                    label = { Text("Department") },
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
                        kotlinx.coroutines.delay(1500)
                        isLoading = false
                        onRegisterSuccess()
                    }
                },
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty() && fullName.isNotEmpty()
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
