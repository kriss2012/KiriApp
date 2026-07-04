package com.kirigenplatform.ui.student;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.kirigenplatform.R;

public class BadgesActivity extends AppCompatActivity {

    private View badgeJava, badgeAndroid, badgeGit, badgeCloud;
    private MaterialCardView cardVerificationDetails;
    private TextView tvBadgeDetailsTitle, tvBadgeDetailsDate, tvBadgeDetailsHash;
    private Button btnShareCredentials;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badges);

        initViews();
        setListeners();
    }

    private void initViews() {
        badgeJava = findViewById(R.id.badge_java);
        badgeAndroid = findViewById(R.id.badge_android);
        badgeGit = findViewById(R.id.badge_git);
        badgeCloud = findViewById(R.id.badge_cloud);
        
        cardVerificationDetails = findViewById(R.id.card_verification_details);
        tvBadgeDetailsTitle = findViewById(R.id.tv_badge_details_title);
        tvBadgeDetailsDate = findViewById(R.id.tv_badge_details_date);
        tvBadgeDetailsHash = findViewById(R.id.tv_badge_details_hash);
        
        btnShareCredentials = findViewById(R.id.btn_share_credentials);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        badgeJava.setOnClickListener(v -> showBadgeDetails("Java Foundations", "Issued: October 12, 2025", "kg_tx_8a92fbc0412e87d3910c"));
        badgeAndroid.setOnClickListener(v -> showBadgeDetails("Android Architecture", "Issued: November 01, 2025", "kg_tx_092bc3e7a0e1c2d93e8a"));
        badgeGit.setOnClickListener(v -> showBadgeDetails("Git & Collaboration", "Issued: November 15, 2025", "kg_tx_d389a9f2010c2830f81d"));
        badgeCloud.setOnClickListener(v -> showBadgeDetails("Cloud & DevOps", "Issued: Pending Verification", "Verification in progress. Estimated: 2 days"));
        
        btnShareCredentials.setOnClickListener(v -> {
            String text = "Verify my KiriGen Credential for " + tvBadgeDetailsTitle.getText() + 
                    " here: https://kirigentech.in/verify/" + tvBadgeDetailsHash.getText();
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Badge Credential", text);
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Verification link copied to clipboard!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBadgeDetails(String title, String date, String hash) {
        tvBadgeDetailsTitle.setText(title);
        tvBadgeDetailsDate.setText(date);
        tvBadgeDetailsHash.setText(hash);
        cardVerificationDetails.setVisibility(View.VISIBLE);
    }
}
