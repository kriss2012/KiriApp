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
import com.kirigenplatform.adapter.NotificationAdapter;
import com.kirigenplatform.data.api.ApiClient;
import com.kirigenplatform.data.api.ApiService;
import com.kirigenplatform.data.model.Notification;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private NotificationAdapter notificationAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoNotifications;
    private ApiService apiService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        
        apiService = ApiClient.getApiService();
        
        initViews(view);
        setupRecyclerView();
        loadNotifications();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_notifications);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        tvNoNotifications = view.findViewById(R.id.tv_no_notifications);
        
        swipeRefreshLayout.setOnRefreshListener(this::loadNotifications);
    }
    
    private void setupRecyclerView() {
        notificationAdapter = new NotificationAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(notificationAdapter);
    }
    
    private void loadNotifications() {
        showLoading(true);
        
        apiService.getNotifications().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();
                    if (notifications.isEmpty()) {
                        tvNoNotifications.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoNotifications.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        notificationAdapter.setNotifications(notifications);
                    }
                } else {
                    tvNoNotifications.setVisibility(View.VISIBLE);
                    tvNoNotifications.setText("Failed to load notifications");
                }
            }
            
            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                tvNoNotifications.setVisibility(View.VISIBLE);
                tvNoNotifications.setText("Network error: " + t.getMessage());
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
