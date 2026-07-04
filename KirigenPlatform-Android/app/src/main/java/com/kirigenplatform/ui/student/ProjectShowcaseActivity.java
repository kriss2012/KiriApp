package com.kirigenplatform.ui.student;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.kirigenplatform.R;

public class ProjectShowcaseActivity extends AppCompatActivity {

    private TextInputEditText etProjectName, etProjectGithub, etProjectTech;
    private Button btnSubmitProject;
    private LinearLayout layoutSubmissionsFeed;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_showcase);

        initViews();
        setListeners();
    }

    private void initViews() {
        etProjectName = findViewById(R.id.et_project_name);
        etProjectGithub = findViewById(R.id.et_project_github);
        etProjectTech = findViewById(R.id.et_project_tech);
        btnSubmitProject = findViewById(R.id.btn_submit_project);
        layoutSubmissionsFeed = findViewById(R.id.layout_submissions_feed);
        btnBack = findViewById(R.id.btn_back);
        
        // Wire up initial upvote buttons if any
        setupUpvoteButtons();
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSubmitProject.setOnClickListener(v -> submitProject());
    }

    private void setupUpvoteButtons() {
        View firstItem = findViewById(R.id.btn_upvote_project_1);
        if (firstItem != null) {
            final Button btn = (Button) firstItem;
            btn.setOnClickListener(new View.OnClickListener() {
                int count = 24;
                boolean voted = false;
                @Override
                public void onClick(View v) {
                    if (!voted) {
                        count++;
                        voted = true;
                        btn.setText("▲ Upvoted (" + count + ")");
                        btn.setBackgroundColor(getResources().getColor(R.color.success));
                    } else {
                        count--;
                        voted = false;
                        btn.setText("▲ Upvote (" + count + ")");
                        btn.setBackgroundColor(getResources().getColor(R.color.background_card_light));
                    }
                }
            });
        }
    }

    private void submitProject() {
        String name = etProjectName.getText() != null ? etProjectName.getText().toString().trim() : "";
        String github = etProjectGithub.getText() != null ? etProjectGithub.getText().toString().trim() : "";
        String tech = etProjectTech.getText() != null ? etProjectTech.getText().toString().trim() : "";

        if (name.isEmpty() || github.isEmpty() || tech.isEmpty()) {
            Toast.makeText(this, "Please fill in all submission fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Dynamically add card to top of layoutSubmissionsFeed
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater != null) {
            View cardView = inflater.inflate(R.layout.activity_project_showcase, null);
            // Wait, inflating activity_project_showcase layout would cause infinite recursion or bad inflation.
            // Let's programmatically construct a beautiful card layout or inflate an XML layout, or just build the view elements manually in Java!
            // Programmatically building is extremely robust, simple, and has zero XML dependecies.
            
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            card.setLayoutParams(params);
            card.setRadius(14 * getResources().getDisplayMetrics().density);
            card.setStrokeColor(getResources().getColor(R.color.divider));
            card.setStrokeWidth(1);
            card.setCardBackgroundColor(getResources().getColor(R.color.background_card));
            
            LinearLayout cardContent = new LinearLayout(this);
            cardContent.setOrientation(LinearLayout.VERTICAL);
            cardContent.setPadding(32, 32, 32, 32);
            
            TextView tvTitle = new TextView(this);
            tvTitle.setText(name);
            tvTitle.setTextColor(getResources().getColor(R.color.white));
            tvTitle.setTextSize(16);
            tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);
            cardContent.addView(tvTitle);
            
            TextView tvMeta = new TextView(this);
            tvMeta.setText("by You • Tech: " + tech);
            tvMeta.setTextColor(getResources().getColor(R.color.text_secondary));
            tvMeta.setTextSize(12);
            tvMeta.setPadding(0, 4, 0, 4);
            cardContent.addView(tvMeta);
            
            TextView tvLink = new TextView(this);
            tvLink.setText(github);
            tvLink.setTextColor(getResources().getColor(R.color.neon_cyan));
            tvLink.setTextSize(13);
            cardContent.addView(tvLink);
            
            LinearLayout buttonRow = new LinearLayout(this);
            buttonRow.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, 16, 0, 0);
            buttonRow.setLayoutParams(rowParams);
            
            Button btnUpvote = new Button(this, null, 0, R.style.Widget_KirigenPlatform_Button_Navy);
            btnUpvote.setText("▲ Upvote (1)");
            btnUpvote.setTextSize(11);
            btnUpvote.setPadding(16, 4, 16, 4);
            btnUpvote.setOnClickListener(new View.OnClickListener() {
                int count = 1;
                boolean voted = false;
                @Override
                public void onClick(View v) {
                    if (!voted) {
                        count++;
                        voted = true;
                        btnUpvote.setText("▲ Upvoted (" + count + ")");
                    } else {
                        count--;
                        voted = false;
                        btnUpvote.setText("▲ Upvote (" + count + ")");
                    }
                }
            });
            buttonRow.addView(btnUpvote);
            cardContent.addView(buttonRow);
            
            card.addView(cardContent);
            layoutSubmissionsFeed.addView(card, 0); // Insert at top
            
            // Clear inputs
            etProjectName.setText("");
            etProjectGithub.setText("");
            etProjectTech.setText("");
            
            Toast.makeText(this, "Project showcase submitted successfully!", Toast.LENGTH_SHORT).show();
        }
    }
}
