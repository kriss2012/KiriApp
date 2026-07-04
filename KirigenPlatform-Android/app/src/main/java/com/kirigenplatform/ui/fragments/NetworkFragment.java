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
import com.kirigenplatform.adapter.UserAdapter;
import com.kirigenplatform.data.api.ApiClient;
import com.kirigenplatform.data.api.ApiService;
import com.kirigenplatform.data.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkFragment extends Fragment {
    
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoUsers;
    private ApiService apiService;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network, container, false);
        
        apiService = ApiClient.getApiService();
        
        initViews(view);
        setupRecyclerView();
        loadUsers();
        
        return view;
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recycler_users);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        tvNoUsers = view.findViewById(R.id.tv_no_users);
        
        swipeRefreshLayout.setOnRefreshListener(this::loadUsers);
    }
    
    private void setupRecyclerView() {
        userAdapter = new UserAdapter(new UserAdapter.OnUserClickListener() {
            @Override
            public void onUserClick(User user) {
                // Navigate to user profile
            }
            
            @Override
            public void onConnectClick(User user) {
                // Handle connect action
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(userAdapter);
    }
    
    private void loadUsers() {
        showLoading(true);
        
        apiService.getAllVerifiedUsers().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    List<User> users = response.body();
                    if (users.isEmpty()) {
                        tvNoUsers.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tvNoUsers.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        userAdapter.setUsers(users);
                    }
                } else {
                    tvNoUsers.setVisibility(View.VISIBLE);
                    tvNoUsers.setText("Failed to load users");
                }
            }
            
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                showLoading(false);
                swipeRefreshLayout.setRefreshing(false);
                tvNoUsers.setVisibility(View.VISIBLE);
                tvNoUsers.setText("Network error: " + t.getMessage());
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
