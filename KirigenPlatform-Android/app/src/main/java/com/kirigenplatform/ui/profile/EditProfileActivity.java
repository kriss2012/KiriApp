package com.kirigenplatform.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.kirigenplatform.R;
import com.kirigenplatform.data.api.ApiClient;
import com.kirigenplatform.data.api.ApiService;
import com.kirigenplatform.data.model.User;
import com.kirigenplatform.utils.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private TextInputEditText etName, etRole, etBio, etDepartment, etCollege, etLinkedIn, etGithub;
    private Button btnSave;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferencesManager sharedPreferencesManager;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiService = ApiClient.getApiService();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        userId = sharedPreferencesManager.getUserId();

        initViews();
        loadCurrentProfile();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        etName = findViewById(R.id.et_edit_name);
        etRole = findViewById(R.id.et_edit_role);
        etBio = findViewById(R.id.et_edit_bio);
        etDepartment = findViewById(R.id.et_edit_department);
        etCollege = findViewById(R.id.et_edit_college);
        etLinkedIn = findViewById(R.id.et_edit_linkedin);
        etGithub = findViewById(R.id.et_edit_github);
        btnSave = findViewById(R.id.btn_save_profile);
        progressBar = findViewById(R.id.progress_bar);

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadCurrentProfile() {
        if (userId == null) return;

        showLoading(true);
        apiService.getProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    populateFields(response.body());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(User user) {
        etName.setText(user.getFullName());
        etRole.setText(user.getRole());
        etBio.setText(user.getBio());
        etDepartment.setText(user.getDepartment());
        etCollege.setText(user.getCollege());
        etLinkedIn.setText(user.getLinkedInUrl());
        etGithub.setText(user.getGithubUrl());
    }

    private void saveProfile() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }

        User userUpdate = new User();
        userUpdate.setFullName(name);
        userUpdate.setRole(etRole.getText().toString().trim());
        userUpdate.setBio(etBio.getText().toString().trim());
        userUpdate.setDepartment(etDepartment.getText().toString().trim());
        userUpdate.setCollege(etCollege.getText().toString().trim());
        userUpdate.setLinkedInUrl(etLinkedIn.getText().toString().trim());
        userUpdate.setGithubUrl(etGithub.getText().toString().trim());

        showLoading(true);
        apiService.updateProfile(userId, userUpdate).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
                Toast.makeText(EditProfileActivity.this, "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!show);
    }
}
