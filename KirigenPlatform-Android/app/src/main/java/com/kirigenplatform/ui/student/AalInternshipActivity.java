package com.kirigenplatform.ui.student;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.kirigenplatform.R;

public class AalInternshipActivity extends AppCompatActivity {

    private ProgressBar progressOnboarding;
    private TextView tvTasksSummary;
    private Button btnAction;
    private ImageButton btnBack;
    
    private boolean isGitChallengeCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aal_internship);

        progressOnboarding = findViewById(R.id.progress_onboarding);
        tvTasksSummary = findViewById(R.id.tv_tasks_summary);
        btnAction = findViewById(R.id.btn_action);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
        
        btnAction.setOnClickListener(v -> {
            if (!isGitChallengeCompleted) {
                isGitChallengeCompleted = true;
                progressOnboarding.setProgress(86);
                tvTasksSummary.setText("6 of 7 Tasks Completed");
                btnAction.setText("Final Capstone Locked");
                btnAction.setEnabled(false);
                Toast.makeText(this, "Advanced Git Challenge completed! Verification points added.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
