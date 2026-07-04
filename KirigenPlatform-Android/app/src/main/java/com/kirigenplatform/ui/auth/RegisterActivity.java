package com.kirigenplatform.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.kirigenplatform.R;
import com.kirigenplatform.data.api.ApiClient;
import com.kirigenplatform.data.api.ApiService;
import com.kirigenplatform.data.model.AuthResponse;
import com.kirigenplatform.data.model.RegisterRequest;
import com.kirigenplatform.ui.auth.LoginActivity;
import com.kirigenplatform.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    
    private TextInputEditText etEmail, etPassword, etFullName, etPhone;
    private Button btnRegister;
    private TextView tvLogin;
    private ProgressBar progressBar;
    private SharedPreferencesManager sharedPreferencesManager;
    private ApiService apiService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        sharedPreferencesManager = new SharedPreferencesManager(this);
        apiService = ApiClient.getApiService();
        
        initViews();
        setListeners();
    }
    
    private void initViews() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etFullName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setListeners() {
        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> navigateToLogin());
    }
    
    private void attemptRegister() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (fullName.isEmpty()) {
            Toast.makeText(this, "Please enter full name", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setFullName(fullName);
        registerRequest.setPhoneNumber(phone);
        registerRequest.setUserCategory("STUDENT");
        
        apiService.register(registerRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    
                    // Save tokens and user info
                    sharedPreferencesManager.saveAuthToken(authResponse.getToken());
                    sharedPreferencesManager.saveRefreshToken(authResponse.getRefreshToken());
                    sharedPreferencesManager.setLoggedIn(true);
                    
                    if (authResponse.getUser() != null) {
                        sharedPreferencesManager.saveUserId(authResponse.getUser().getId());
                        sharedPreferencesManager.saveUserEmail(authResponse.getUser().getEmail());
                        sharedPreferencesManager.saveUserName(authResponse.getUser().getFullName());
                    }
                    
                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                    navigateToLogin();
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration failed. Email may already exist.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(RegisterActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            btnRegister.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            btnRegister.setEnabled(true);
        }
    }
}
