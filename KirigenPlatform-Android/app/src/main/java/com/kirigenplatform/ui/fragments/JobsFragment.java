package com.kirigenplatform.ui.fragments;

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
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobsFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private JobAdapter jobAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoJobs;
    private ApiService apiService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        
        apiService = ApiClient.getApiService();
        
        initViews(view);
        setupRecyclerView();
        loadJobs();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_jobs);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        tvNoJobs = view.findViewById(R.id.tv_no_jobs);
        
        swipeRefreshLayout.setOnRefreshListener(this::loadJobs);
    }
    
    private void setupRecyclerView() {
        jobAdapter = new JobAdapter(new JobAdapter.OnJobClickListener() {
            @Override
            public void onJobClick(Job job) {
                // Navigate to job detail
            }
            
            @Override
            public void onApplyClick(Job job) {
                // Handle apply action
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(jobAdapter);
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
