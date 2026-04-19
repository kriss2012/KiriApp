package com.apex.asg.data.remote;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\bf\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0007\u001a\u00020\b2\b\b\u0001\u0010\u0004\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ(\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\f2\b\b\u0001\u0010\u0011\u001a\u00020\u00122\b\b\u0001\u0010\u0013\u001a\u00020\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u0014J\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00030\fH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\b0\fH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u0017\u001a\u00020\u00182\b\b\u0001\u0010\u0019\u001a\u00020\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u001aJ\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u001c0\fH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00180\fH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0018\u0010\u001e\u001a\u00020\u001f2\b\b\u0001\u0010\u0004\u001a\u00020 H\u00a7@\u00a2\u0006\u0002\u0010!J\u0018\u0010\"\u001a\u00020\u001f2\b\b\u0001\u0010\u0004\u001a\u00020#H\u00a7@\u00a2\u0006\u0002\u0010$J\u0018\u0010%\u001a\u00020\r2\b\b\u0001\u0010\u0004\u001a\u00020&H\u00a7@\u00a2\u0006\u0002\u0010\'J0\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00120)2\u0014\b\u0001\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00120)H\u00a7@\u00a2\u0006\u0002\u0010*J\u0018\u0010+\u001a\u00020\u00102\b\b\u0001\u0010\u0004\u001a\u00020,H\u00a7@\u00a2\u0006\u0002\u0010-J:\u0010.\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u00020\u00120)2\b\b\u0001\u0010\u0019\u001a\u00020\u00122\u0014\b\u0001\u0010/\u001a\u000e\u0012\u0004\u0012\u00020\u0012\u0012\u0004\u0012\u0002000)H\u00a7@\u00a2\u0006\u0002\u00101J0\u00102\u001a\u00020\u00182\b\b\u0001\u0010\u0019\u001a\u00020\u00122\u0016\b\u0001\u00103\u001a\u0010\u0012\u0004\u0012\u00020\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u00010)H\u00a7@\u00a2\u0006\u0002\u00101\u00a8\u00064"}, d2 = {"Lcom/apex/asg/data/remote/ASGApiService;", "", "createEvent", "Lcom/apex/asg/data/remote/EventDto;", "request", "Lcom/apex/asg/data/remote/CreateEventRequest;", "(Lcom/apex/asg/data/remote/CreateEventRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "createJob", "Lcom/apex/asg/data/remote/JobDto;", "Lcom/apex/asg/data/remote/CreateJobRequest;", "(Lcom/apex/asg/data/remote/CreateJobRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAiHistory", "", "Lcom/apex/asg/data/remote/AiMessageResponse;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getChatHistory", "Lcom/apex/asg/data/remote/MessageDto;", "user1", "", "user2", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getEvents", "getJobs", "getProfile", "Lcom/apex/asg/data/remote/UserDto;", "userId", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUsers", "Lcom/apex/asg/data/remote/UserResponse;", "getVerifiedUsers", "login", "Lcom/apex/asg/data/remote/AuthResponse;", "Lcom/apex/asg/data/remote/LoginRequest;", "(Lcom/apex/asg/data/remote/LoginRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "register", "Lcom/apex/asg/data/remote/RegisterRequest;", "(Lcom/apex/asg/data/remote/RegisterRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendAiMessage", "Lcom/apex/asg/data/remote/AiMessageRequest;", "(Lcom/apex/asg/data/remote/AiMessageRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendConnectionRequest", "", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendMessage", "Lcom/apex/asg/data/remote/SendMessageRequest;", "(Lcom/apex/asg/data/remote/SendMessageRequest;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleAccess", "data", "", "(Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateProfile", "profileData", "app_debug"})
public abstract interface ASGApiService {
    
    @retrofit2.http.POST(value = "auth/register")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object register(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.RegisterRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.AuthResponse> $completion);
    
    @retrofit2.http.POST(value = "auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.LoginRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.AuthResponse> $completion);
    
    @retrofit2.http.GET(value = "users")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUsers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.apex.asg.data.remote.UserResponse>> $completion);
    
    @retrofit2.http.GET(value = "events")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getEvents(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.apex.asg.data.remote.EventDto>> $completion);
    
    @retrofit2.http.POST(value = "events")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createEvent(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.CreateEventRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.EventDto> $completion);
    
    @retrofit2.http.GET(value = "users/profile/{userId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getProfile(@retrofit2.http.Path(value = "userId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.UserDto> $completion);
    
    @retrofit2.http.PUT(value = "users/profile/{userId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateProfile(@retrofit2.http.Path(value = "userId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, ? extends java.lang.Object> profileData, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.UserDto> $completion);
    
    @retrofit2.http.PUT(value = "users/toggle-access/{userId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object toggleAccess(@retrofit2.http.Path(value = "userId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String userId, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Boolean> data, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.Map<java.lang.String, java.lang.String>> $completion);
    
    @retrofit2.http.GET(value = "users/verified")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getVerifiedUsers(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.apex.asg.data.remote.UserDto>> $completion);
    
    @retrofit2.http.GET(value = "jobs")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getJobs(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.apex.asg.data.remote.JobDto>> $completion);
    
    @retrofit2.http.POST(value = "jobs")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createJob(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.CreateJobRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.JobDto> $completion);
    
    @retrofit2.http.GET(value = "chat/history/{user1}/{user2}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getChatHistory(@retrofit2.http.Path(value = "user1")
    @org.jetbrains.annotations.NotNull()
    java.lang.String user1, @retrofit2.http.Path(value = "user2")
    @org.jetbrains.annotations.NotNull()
    java.lang.String user2, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.apex.asg.data.remote.MessageDto>> $completion);
    
    @retrofit2.http.POST(value = "chat/send")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendMessage(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.SendMessageRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.MessageDto> $completion);
    
    @retrofit2.http.GET(value = "ai/history")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAiHistory(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.apex.asg.data.remote.AiMessageResponse>> $completion);
    
    @retrofit2.http.POST(value = "ai/chat")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendAiMessage(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    com.apex.asg.data.remote.AiMessageRequest request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.apex.asg.data.remote.AiMessageResponse> $completion);
    
    @retrofit2.http.POST(value = "connections/request")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendConnectionRequest(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> request, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.Map<java.lang.String, java.lang.String>> $completion);
}