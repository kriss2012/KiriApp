package com.kirigenplatform.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kirigenplatform.R;
import com.kirigenplatform.ui.auth.LoginActivity;
import com.kirigenplatform.ui.fragments.HomeFragment;
import com.kirigenplatform.ui.fragments.JobsFragment;
import com.kirigenplatform.ui.fragments.NetworkFragment;
import com.kirigenplatform.ui.fragments.NotificationsFragment;
import com.kirigenplatform.ui.fragments.ProfileFragment;
import com.kirigenplatform.utils.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {
    
    private BottomNavigationView bottomNavigationView;
    private SharedPreferencesManager sharedPreferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        sharedPreferencesManager = new SharedPreferencesManager(this);
        
        // Check if user is logged in
        if (!sharedPreferencesManager.isLoggedIn()) {
            navigateToLogin();
            return;
        }
        
        initViews();
        setupBottomNavigation();
        
        // Load Home fragment by default
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
    }
    
    private void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }
    
    private void setupBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                int itemId = item.getItemId();
                
                if (itemId == R.id.navigation_home) {
                    fragment = new HomeFragment();
                } else if (itemId == R.id.navigation_jobs) {
                    fragment = new JobsFragment();
                } else if (itemId == R.id.navigation_network) {
                    fragment = new NetworkFragment();
                } else if (itemId == R.id.navigation_notifications) {
                    fragment = new NotificationsFragment();
                } else if (itemId == R.id.navigation_profile) {
                    fragment = new ProfileFragment();
                }
                
                if (fragment != null) {
                    loadFragment(fragment);
                    return true;
                }
                
                return false;
            }
        });
    }
    
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
