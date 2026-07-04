package com.kirigenplatform.ui.student;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.kirigenplatform.R;

public class InterviewSandboxActivity extends AppCompatActivity {

    private View layoutTrackSelection, layoutInterviewPanel, layoutScorecard;
    private Button btnTrackAndroid, btnTrackJava, btnTrackFrontend;
    private Button btnSubmitAnswer, btnNextQuestion, btnFinish;
    private ImageButton btnBack;
    
    private TextView tvTrackTitle, tvQuestionNumber, tvQuestionText, tvScore, tvFeedbackTitle, tvFeedbackTips, tvFinalScore;
    private TextInputEditText etAnswer;
    private ProgressBar evaluationProgress;
    private MaterialCardView cardFeedback;

    private String[] questions;
    private String currentTrack = "";
    private int currentQuestionIndex = 0;
    private int cumulativeScore = 0;

    private final String[] androidQuestions = {
            "Explain the Android Activity Lifecycle and how you handle state restoration during configuration changes.",
            "What are the key benefits of Jetpack Compose over XML-based layouts, and how does it handle recomposition?",
            "What is the role of Dependency Injection (e.g., Hilt/Dagger) in Android, and how does it improve testability?"
    };

    private final String[] javaQuestions = {
            "Explain the concept of Dependency Injection in Spring Framework, and how @Autowired works under the hood.",
            "Compare SQL vs NoSQL databases in terms of horizontal vs vertical scalability, schema design, and ACID transactions.",
            "How do you implement secure authorization in a REST API using JSON Web Tokens (JWT) and Spring Security?"
    };

    private final String[] frontendQuestions = {
            "Explain the virtual DOM in React and how the reconciliation process works.",
            "What is the difference between state and props, and how do you handle state lifting in a React application?",
            "How does CSS Flexbox differ from CSS Grid, and in what scenarios would you choose one over the other?"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_sandbox);

        initViews();
        setListeners();
    }

    private void initViews() {
        layoutTrackSelection = findViewById(R.id.layout_track_selection);
        layoutInterviewPanel = findViewById(R.id.layout_interview_panel);
        layoutScorecard = findViewById(R.id.layout_scorecard);

        btnTrackAndroid = findViewById(R.id.btn_track_android);
        btnTrackJava = findViewById(R.id.btn_track_java);
        btnTrackFrontend = findViewById(R.id.btn_track_frontend);
        btnSubmitAnswer = findViewById(R.id.btn_submit_answer);
        btnNextQuestion = findViewById(R.id.btn_next_question);
        btnFinish = findViewById(R.id.btn_finish);
        btnBack = findViewById(R.id.btn_back);

        tvTrackTitle = findViewById(R.id.tv_track_title);
        tvQuestionNumber = findViewById(R.id.tv_question_number);
        tvQuestionText = findViewById(R.id.tv_question_text);
        tvScore = findViewById(R.id.tv_score);
        tvFeedbackTitle = findViewById(R.id.tv_feedback_title);
        tvFeedbackTips = findViewById(R.id.tv_feedback_tips);
        tvFinalScore = findViewById(R.id.tv_final_score);
        etAnswer = findViewById(R.id.et_answer);
        evaluationProgress = findViewById(R.id.evaluation_progress);
        cardFeedback = findViewById(R.id.card_feedback);
    }

    private void setListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnTrackAndroid.setOnClickListener(v -> startInterview("Android Development", androidQuestions));
        btnTrackJava.setOnClickListener(v -> startInterview("Java Backend Engineer", javaQuestions));
        btnTrackFrontend.setOnClickListener(v -> startInterview("Frontend Development", frontendQuestions));

        btnSubmitAnswer.setOnClickListener(v -> evaluateAnswer());
        btnNextQuestion.setOnClickListener(v -> loadNextQuestion());
        btnFinish.setOnClickListener(v -> finish());
    }

    private void startInterview(String trackName, String[] trackQuestions) {
        currentTrack = trackName;
        questions = trackQuestions;
        currentQuestionIndex = 0;
        cumulativeScore = 0;

        layoutTrackSelection.setVisibility(View.GONE);
        layoutInterviewPanel.setVisibility(View.VISIBLE);
        
        tvTrackTitle.setText(currentTrack.toUpperCase());
        loadQuestion();
    }

    private void loadQuestion() {
        etAnswer.setText("");
        cardFeedback.setVisibility(View.GONE);
        btnSubmitAnswer.setVisibility(View.VISIBLE);
        
        tvQuestionNumber.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.length);
        tvQuestionText.setText(questions[currentQuestionIndex]);
    }

    private void evaluateAnswer() {
        String answer = etAnswer.getText() != null ? etAnswer.getText().toString().trim() : "";
        if (answer.isEmpty()) {
            Toast.makeText(this, "Please write your answer first", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmitAnswer.setVisibility(View.GONE);
        evaluationProgress.setVisibility(View.VISIBLE);

        // Simulate AI feedback latency
        new Handler().postDelayed(() -> {
            evaluationProgress.setVisibility(View.GONE);
            
            // Random scoring simulator (75 - 98)
            int score = (int) (Math.random() * 24) + 75;
            cumulativeScore += score;
            
            tvScore.setText("Score: " + score + "%");
            
            if (score >= 90) {
                tvFeedbackTitle.setText("Exceptional Response!");
                tvFeedbackTips.setText("Tip: Your response is extremely comprehensive and covers production-level trade-offs beautifully.");
            } else if (score >= 80) {
                tvFeedbackTitle.setText("Strong Answer!");
                tvFeedbackTips.setText("Tip: Solid conceptual understanding. Add concrete examples to further impress the interviewer.");
            } else {
                tvFeedbackTitle.setText("Satisfactory Answer!");
                tvFeedbackTips.setText("Tip: Try to expand more on runtime implications and memory details next time.");
            }

            cardFeedback.setVisibility(View.VISIBLE);
        }, 1500);
    }

    private void loadNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questions.length) {
            loadQuestion();
        } else {
            showScorecard();
        }
    }

    private void showScorecard() {
        layoutInterviewPanel.setVisibility(View.GONE);
        layoutScorecard.setVisibility(View.VISIBLE);
        
        int finalAvg = cumulativeScore / questions.length;
        tvFinalScore.setText("Average Score: " + finalAvg + "%");
    }
}
