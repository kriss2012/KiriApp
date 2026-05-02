package com.kiriplatform.app.ui.screens

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
import com.kiriplatform.app.ui.components.KiriPrimaryButton
import com.kiriplatform.app.ui.theme.*
import com.kiriplatform.app.data.SessionManager
import com.kiriplatform.app.data.remote.ApiClient
import com.kiriplatform.app.data.remote.models.LoginRequest
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager.getInstance(context) }
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
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sign in to continue to Kiri Community",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                    focusedLabelColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (errorMessage != null) {
                Text(text = errorMessage ?: "", color = Color.Red, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            KiriPrimaryButton(
                text = if (isLoading) "Signing in..." else "Sign In",
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Please enter both email and password"
                        return@KiriPrimaryButton
                    }
                    if (isLoading) return@KiriPrimaryButton

                    isLoading = true
                    errorMessage = null
                    coroutineScope.launch {
                        try {
                            val response = ApiClient.service.login(LoginRequest(email.trim(), password.trim()))
                            
                            val user = response.user
                            val token = response.token
                            
                            // Validate token and user data
                            if (user == null) {
                                isLoading = false
                                errorMessage = "Invalid user data received"
                                return@launch
                            }
                            
                            if (token.isBlank()) {
                                isLoading = false
                                errorMessage = "Invalid authentication token received"
                                return@launch
                            }
                            
                            if (user.id.isBlank()) {
                                isLoading = false
                                errorMessage = "Invalid user ID received"
                                return@launch
                            }
                            
                            // Save to session
                            sessionManager.saveToken(token)
                            sessionManager.saveUserId(user.id)
                            sessionManager.saveUserName(user.fullName)
                            sessionManager.saveUserRole(user.role)
                            sessionManager.setCanCreateEvents(user.canCreateEvents)
                            
                            // Set token for future API calls
                            ApiClient.setToken(token)
                            
                            isLoading = false
                            onLoginSuccess()
                        } catch (e: Throwable) {
                            isLoading = false
                            errorMessage = when (e) {
                                is java.net.UnknownHostException -> "No internet connection. Please check your network."
                                is java.net.SocketTimeoutException -> "Server connection timed out."
                                is retrofit2.HttpException -> {
                                    if (e.code() == 401) "Invalid email or password."
                                    else "Server error: ${e.code()}"
                                }
                                else -> e.localizedMessage ?: "Login failed. Please try again."
                            }
                        }
                    }
                },
                enabled = !isLoading && email.isNotEmpty() && password.isNotEmpty()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text("Don't have an account? ", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "Sign Up",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}
