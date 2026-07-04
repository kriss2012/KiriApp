package com.kirigenplatform.data.api;

import com.kirigenplatform.data.model.AuthResponse;
import com.kirigenplatform.data.model.Job;
import com.kirigenplatform.data.model.User;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    
    // Auth endpoints
    @POST("auth/register")
    Call<AuthResponse> register(@Body com.kirigenplatform.data.model.RegisterRequest request);
    
    @POST("auth/login")
    Call<AuthResponse> login(@Body com.kirigenplatform.data.model.LoginRequest request);
    
    @POST("auth/refresh")
    Call<AuthResponse> refresh(@Body com.kirigenplatform.data.model.RefreshRequest request);
    
    // User endpoints
    @GET("users/profile/{userId}")
    Call<User> getProfile(@Path("userId") String userId);
    
    @POST("users/profile/{userId}")
    Call<User> updateProfile(@Path("userId") String userId, @Body User user);
    
    @GET("users/")
    Call<List<User>> searchUsers(@Query("q") String query);
    
    @GET("users/verified")
    Call<List<User>> getAllVerifiedUsers();
    
    @GET("users/activities/{userId}")
    Call<List<com.kirigenplatform.data.model.Activity>> getActivities(@Path("userId") String userId);
    
    @GET("users/stats/{userId}")
    Call<com.kirigenplatform.data.model.UserStats> getUserStats(@Path("userId") String userId);
    
    // Job endpoints
    @GET("jobs/")
    Call<List<Job>> getAllJobs();
    
    @POST("jobs/")
    Call<Job> createJob(@Body com.kirigenplatform.data.model.JobRequest request);
    
    // Connections endpoints
    @GET("connections/")
    Call<List<User>> getConnections();
    
    // Notifications endpoints
    @GET("notifications/")
    Call<List<com.kirigenplatform.data.model.Notification>> getNotifications();
    
    // Projects endpoints
    @GET("projects/")
    Call<List<com.kirigenplatform.data.model.Project>> getProjects();
}
