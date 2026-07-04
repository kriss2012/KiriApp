package com.kirigenplatform.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kirigenplatform.R;
import com.kirigenplatform.data.api.ApiClient;
import com.kirigenplatform.data.api.ApiService;
import com.kirigenplatform.data.model.User;
import com.kirigenplatform.ui.auth.LoginActivity;
import com.kirigenplatform.utils.SharedPreferencesManager;
import com.kirigenplatform.ui.profile.EditProfileActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    
    private TextView tvName, tvEmail, tvRole, tvDepartment, tvCollege, tvBio;
    private Button btnLogout, btnEditProfile;
    private ProgressBar progressBar;
    private SharedPreferencesManager sharedPreferencesManager;
    private ApiService apiService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        apiService = ApiClient.getApiService();
        
        initViews(view);
        loadUserProfile();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile(); // Refresh profile data when returning from EditProfileActivity
    }
    
    private void initViews(View view) {
        tvName = view.findViewById(R.id.tv_profile_name);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvRole = view.findViewById(R.id.tv_profile_role);
        tvDepartment = view.findViewById(R.id.tv_profile_department);
        tvCollege = view.findViewById(R.id.tv_profile_college);
        tvBio = view.findViewById(R.id.tv_profile_bio);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnEditProfile = view.findViewById(R.id.btn_edit_profile);
        progressBar = view.findViewById(R.id.progress_bar);
        
        btnLogout.setOnClickListener(v -> logout());
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), EditProfileActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadUserProfile() {
        String userId = sharedPreferencesManager.getUserId();
        if (userId == null) {
            logout();
            return;
        }
        
        showLoading(true);
        
        apiService.getProfile(userId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    displayUserInfo(user);
                }
            }
            
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                showLoading(false);
            }
        });
    }
    
    private void displayUserInfo(User user) {
        tvName.setText(user.getFullName() != null ? user.getFullName() : "User Name");
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "email@example.com");
        tvRole.setText(user.getRole() != null ? user.getRole() : user.getUserCategory());
        tvDepartment.setText(user.getDepartment() != null ? user.getDepartment() : "Not specified");
        tvCollege.setText(user.getCollege() != null ? user.getCollege() : "Not specified");
        tvBio.setText(user.getBio() != null ? user.getBio() : "No bio available");
    }
    
    private void logout() {
        sharedPreferencesManager.logout();
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
    
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }
}
