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
import com.google.android.material.textfield.TextInputEditText;
import com.kirigenplatform.R;

public class ResumeBuilderActivity extends AppCompatActivity {

    private TextInputEditText etName, etRole, etSummary, etProject, etSkills;
    private Button btnGenerate, btnCopy;
    private MaterialCardView cardResult;
    private TextView tvResumePreview;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resume_builder);

        initViews();
        setListeners();
    }

    private void initViews() {
        etName = findViewById(R.id.et_name);
        etRole = findViewById(R.id.et_role);
        etSummary = findViewById(R.id.et_summary);
        etProject = findViewById(R.id.et_project);
        etSkills = findViewById(R.id.et_skills);
        btnGenerate = findViewById(R.id.btn_generate);
        btnCopy = findViewById(R.id.btn_copy);
        cardResult = findViewById(R.id.card_result);
        tvResumePreview = findViewById(R.id.tv_resume_preview);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnGenerate.setOnClickListener(v -> generateResume());
        btnCopy.setOnClickListener(v -> copyToClipboard());
    }

    private void generateResume() {
        String name = etName.getText() != null ? etName.getText().toString().trim() : "";
        String role = etRole.getText() != null ? etRole.getText().toString().trim() : "";
        String summary = etSummary.getText() != null ? etSummary.getText().toString().trim() : "";
        String project = etProject.getText() != null ? etProject.getText().toString().trim() : "";
        String skills = etSkills.getText() != null ? etSkills.getText().toString().trim() : "";

        if (name.isEmpty() || role.isEmpty() || summary.isEmpty() || project.isEmpty() || skills.isEmpty()) {
            Toast.makeText(this, "Please fill out all fields to generate your resume", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format according to Google X-Y-Z formula
        String formattedResume = "========================================\n" +
                name.toUpperCase() + "\n" +
                role.toUpperCase() + "\n" +
                "========================================\n\n" +
                "PROFESSIONAL SUMMARY\n" +
                summary + "\n\n" +
                "CORE TECHNICAL SKILLS\n" +
                skills + "\n\n" +
                "KEY INITIATIVE & PROJECTS\n" +
                "• " + project + "\n" +
                "  [Google X-Y-Z: Accomplished X as measured by Y, by doing Z]\n\n" +
                "EDUCATION & TRAINING\n" +
                "• B.Tech in Computer Science & Engineering\n" +
                "  KiriGen Innovation Academy Verified\n" +
                "========================================";

        tvResumePreview.setText(formattedResume);
        cardResult.setVisibility(View.VISIBLE);
        Toast.makeText(this, "ATS Resume Generated Successfully!", Toast.LENGTH_SHORT).show();
    }

    private void copyToClipboard() {
        String text = tvResumePreview.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ATS Resume", text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Resume copied to clipboard!", Toast.LENGTH_SHORT).show();
        }
    }
}
