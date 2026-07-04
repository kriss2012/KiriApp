package com.kirigenplatform.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.kirigenplatform.R;
import com.kirigenplatform.adapter.JobAdapter;
import com.kirigenplatform.data.api.ApiClient;
import com.kirigenplatform.data.api.ApiService;
import com.kirigenplatform.data.model.Job;
import com.kirigenplatform.ui.student.AalInternshipActivity;
import com.kirigenplatform.ui.student.BadgesActivity;
import com.kirigenplatform.ui.student.InterviewSandboxActivity;
import com.kirigenplatform.ui.student.LeaderboardActivity;
import com.kirigenplatform.ui.student.ProjectShowcaseActivity;
import com.kirigenplatform.ui.student.ResumeBuilderActivity;
import com.kirigenplatform.utils.SharedPreferencesManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvWelcome, tvNoJobs, tvRankTier, tvRankPoints;
    private ProgressBar progressRank;
    private View cardResumeBuilder, cardInterviewSandbox, cardBadges, cardProjectShowcase, cardCampusAmbassador, cardAiInternship;
    private SharedPreferencesManager sharedPreferencesManager;
    private ApiService apiService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        sharedPreferencesManager = new SharedPreferencesManager(requireContext());
        apiService = ApiClient.getApiService();
        
        initViews(view);
        setupRecyclerView();
        setupLaunchers();
        loadJobs();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_jobs);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        tvWelcome = view.findViewById(R.id.tv_welcome);
        tvNoJobs = view.findViewById(R.id.tv_no_jobs);
        
        // Rank Views
        tvRankTier = view.findViewById(R.id.tv_rank_tier);
        tvRankPoints = view.findViewById(R.id.tv_rank_points);
        progressRank = view.findViewById(R.id.progress_rank);
        
        // Launcher Cards
        cardResumeBuilder = view.findViewById(R.id.card_resume_builder);
        cardInterviewSandbox = view.findViewById(R.id.card_interview_sandbox);
        cardBadges = view.findViewById(R.id.card_badges);
        cardProjectShowcase = view.findViewById(R.id.card_project_showcase);
        cardCampusAmbassador = view.findViewById(R.id.card_campus_ambassador);
        cardAiInternship = view.findViewById(R.id.card_ai_internship);
        
        String userName = sharedPreferencesManager.getUserName();
        tvWelcome.setText("Welcome back, " + (userName != null ? userName : "Innovator") + "!");
        
        // Set dynamic simulated rank data
        tvRankTier.setText("Neural Tier 3 (Advanced)");
        tvRankPoints.setText("2,840 pts");
        progressRank.setProgress(72);
        
        swipeRefreshLayout.setOnRefreshListener(this::loadJobs);
    }
    
    private void setupRecyclerView() {
        jobAdapter = new JobAdapter(new JobAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Job job) {
                // Navigate to job detail if implemented
            }
            
            @Override
            public void onApplyClick(Job job) {
                // Handle apply action
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(jobAdapter);
    }
    
    private void setupLaunchers() {
        cardResumeBuilder.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ResumeBuilderActivity.class);
            startActivity(intent);
        });
        
        cardInterviewSandbox.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), InterviewSandboxActivity.class);
            startActivity(intent);
        });
        
        cardBadges.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), BadgesActivity.class);
            startActivity(intent);
        });
        
        cardProjectShowcase.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ProjectShowcaseActivity.class);
            startActivity(intent);
        });
        
        cardCampusAmbassador.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), LeaderboardActivity.class);
            startActivity(intent);
        });
        
        cardAiInternship.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AalInternshipActivity.class);
            startActivity(intent);
        });
    }
    
    private void loadJobs() {
        showLoading(true);
        
        apiService.getAllJobs().enqueue(new Callback<List<Job>>() {
            @Override
            public void onResponse(Call<List<Job>> call, Response<List<Job>> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Job> jobs = response.body();
                    if (jobs.isEmpty()) {
                        tvNoJobs.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoJobs.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        jobAdapter.setJobs(jobs);
                    }
                } else {
                    tvNoJobs.setVisibility(View.VISIBLE);
                    tvNoJobs.setText("Failed to load jobs");
                }
            }
            
            @Override
            public void onFailure(Call<List<Job>> call, Throwable t) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                tvNoJobs.setVisibility(View.VISIBLE);
                tvNoJobs.setText("Network error: " + t.getMessage());
            }
        });
    }
    
    private void showLoading(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}
