package com.kirigenplatform.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.kirigenplatform.R;
import com.kirigenplatform.ui.main.MainActivity;
import com.kirigenplatform.utils.SharedPreferencesManager;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferencesManager = new SharedPreferencesManager(this);

        ImageView imgLogo = findViewById(R.id.img_logo);

        // Load premium fade-in animation
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(1200);
        if (imgLogo != null) {
            imgLogo.startAnimation(fadeIn);
        }

        // Wait 2500ms then decide navigation routing
        new Handler().postDelayed(() -> {
            if (sharedPreferencesManager.isLoggedIn()) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            finish();
        }, 2500);
    }
}
