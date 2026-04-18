package com.apex.asg.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.apex.asg.ui.components.ASGPrimaryButton
import com.apex.asg.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(containerColor = BgCream) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "Sign in to continue to ASG community",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = BorderColor
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = BorderColor
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Text(text = errorMessage!!, color = Color.Red, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            ASGPrimaryButton(
                text = if (isLoading) "Signing in..." else "Sign In",
                onClick = {
                    isLoading = true
                    errorMessage = null
                    // For now, bypass real API until backend is deployed
                    // Actual implementation would be:
                    // coroutineScope.launch { 
                    //    val response = ApiClient.service.login(LoginRequest(email, password))
                    //    onLoginSuccess()
                    // }
                    coroutineScope.launch {
                        kotlinx.coroutines.delay(1000)
                        isLoading = false
                        onLoginSuccess()
                    }
                },
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Don't have an account? ", color = TextSecondary)
                Text(
                    "Sign Up",
                    color = OrangePrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}
