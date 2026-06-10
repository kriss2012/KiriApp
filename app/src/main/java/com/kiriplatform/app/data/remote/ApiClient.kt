package com.kiriplatform.app.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import com.kiriplatform.app.data.remote.models.*
import com.kiriplatform.app.utils.AppConfig
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.util.concurrent.TimeUnit

interface ASGApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: Map<String, String>): AuthResponse

    @GET("users")
    suspend fun getUsers(
        @retrofit2.http.Query("name") name: String? = null,
        @retrofit2.http.Query("role") role: String? = null
    ): List<UserResponse>

    @GET("events")
    suspend fun getEvents(): List<EventDto>

    @POST("events")
    suspend fun createEvent(@Body request: CreateEventRequest): EventDto

    @GET("users/profile/{userId}")
    suspend fun getProfile(@Path("userId") userId: String): UserDto

    @PUT("users/profile/{userId}")
    suspend fun updateProfile(@Path("userId") userId: String, @Body request: UpdateProfileRequest): UserDto

    @PUT("users/toggle-access/{userId}")
    suspend fun toggleAccess(@Path("userId") userId: String, @Body data: Map<String, Boolean>): Map<String, String>

    @GET("users/verified")
    suspend fun getVerifiedUsers(): List<UserDto>

    // Notifications
    @GET("notifications/{userId}")
    suspend fun getNotifications(@Path("userId") userId: String): List<NotificationDto>

    @retrofit2.http.PATCH("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: String): NotificationDto

    @retrofit2.http.PATCH("notifications/mark-all-read/{userId}")
    suspend fun markAllNotificationsAsRead(@Path("userId") userId: String): Map<String, String>

    // Connections
    @POST("connections/send")
    suspend fun sendConnectionRequest(@Body request: Map<String, String>): Map<String, String>

    @POST("connections/accept")
    suspend fun acceptConnectionRequest(@Body request: Map<String, String>): Map<String, String>

    @GET("connections/{userId}")
    suspend fun getUserConnections(@Path("userId") userId: String): List<ConnectionDto>

    // Jobs
    @GET("jobs")
    suspend fun getJobs(): List<JobDto>

    @POST("jobs")
    suspend fun createJob(@Body request: CreateJobRequest): JobDto

    // Chat
    @GET("chat/history/{user1}/{user2}")
    suspend fun getChatHistory(@Path("user1") user1: String, @Path("user2") user2: String): List<MessageDto>

    @GET("chat/list")
    suspend fun getConversations(): List<ConversationResponse>

    @GET("chat/search")
    suspend fun searchUsers(@retrofit2.http.Query("query") query: String): List<UserDto>

    @POST("chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): MessageDto

    // Kiri AI
    @GET("ai/history")
    suspend fun getAiHistory(): List<AiMessageResponse>

    @POST("ai/chat")
    suspend fun sendAiMessage(@Body request: AiMessageRequest): AiMessageResponse

    @PUT("ai/specialization")
    suspend fun updateSpecialization(@Body request: Map<String, String>): Map<String, String>

    @GET("users/activities/{userId}")
    suspend fun getActivities(@Path("userId") userId: String): List<ActivityDto>

    @GET("users/stats/{userId}")
    suspend fun getUserStats(@Path("userId") userId: String): UserStatsDto

    // Pitches / Marketplace
    @GET("pitches")
    suspend fun getPitches(): List<PitchDto>

    @POST("pitches")
    suspend fun createPitch(@Body request: CreatePitchRequest): PitchDto

    @POST("pitches/back")
    suspend fun backPitch(@Body request: Map<String, String>): Map<String, String>

    @GET("match/suggestions")
    suspend fun getMatchSuggestions(): List<MatchSuggestionDto>

    @GET("investor/high-potential")
    suspend fun getHighPotentialPitches(): List<InvestorPitchDto>

    @GET("investor/trends")
    suspend fun getMarketTrends(): List<MarketTrendDto>

    @GET("projects/artifacts/{userId}")
    suspend fun getProjectArtifacts(@Path("userId") userId: String): List<ProjectArtifactDto>

    @POST("projects/artifacts")
    suspend fun createProjectArtifact(@Body request: CreateArtifactRequest): ProjectArtifactDto

    // Mentorship
    @POST("mentor/request")
    suspend fun requestMentorSession(@Body request: MentorSessionRequest): MentorSessionDto

    @GET("mentor/history")
    suspend fun getMentorHistory(): List<MentorSessionDto>

    @retrofit2.http.PATCH("mentor/{sessionId}")
    suspend fun updateMentorSessionStatus(
        @Path("sessionId") sessionId: String,
        @Body request: Map<String, String>
    ): MentorSessionDto

    @GET("investor/heatmap")
    suspend fun getInnovationHeatmap(): List<HeatMapDto>

    @POST("invite/generate")
    suspend fun generateInvite(@Body request: GenerateInviteRequest): InviteCodeDto

    @GET("invite/list")
    suspend fun getInvites(): List<InviteCodeDto>

    // --- AAL & Ecosystem New Endpoints ---

    @GET("aal/onboarding/{userId}")
    suspend fun getAalOnboarding(@Path("userId") userId: String): AalOnboardingDto

    @GET("aal/activities/{userId}")
    suspend fun getAalActivities(@Path("userId") userId: String): List<AalActivityDto>

    @POST("aal/activities/submit")
    suspend fun submitAalActivity(@Body request: AalActivityDto): AalActivityDto

    @GET("aal/institutions")
    suspend fun getInstitutions(): List<InstitutionDto>

    @GET("aal/events/aal")
    suspend fun getAalEvents(): List<AalEventDto>

    @POST("aal/events/register")
    suspend fun registerForEvent(@Body request: EventRegistrationDto): EventRegistrationDto

    @POST("aal/live-inputs")
    suspend fun submitLiveInput(@Body request: LiveInputDto): LiveInputDto

    @GET("aal/matches/{userId}")
    suspend fun getAiMatches(@Path("userId") userId: String): List<AiResourceMatchDto>

    @GET("aal/board")
    suspend fun getEcosystemBoard(): List<EcosystemBoardDto>

    @GET("aal/jobs-projects")
    suspend fun getJobsProjects(): List<JobProjectDto>

    @GET("users/github-stats/{username}")
    suspend fun getGitHubStats(@Path("username") username: String): GitHubStatsResponse
}

object ApiClient {
    private var token: String? = null
    private var refreshToken: String? = null
    private var sessionManager: com.kiriplatform.app.data.SessionManager? = null

    fun init(manager: com.kiriplatform.app.data.SessionManager) {
        sessionManager = manager
        token = manager.getToken()
        refreshToken = manager.getRefreshToken()
    }

    fun setToken(newToken: String?, newRefreshToken: String? = null) {
        token = newToken
        sessionManager?.saveToken(newToken)
        newRefreshToken?.let {
            refreshToken = it
            sessionManager?.saveRefreshToken(it)
        }
    }

    private fun responseCount(response: okhttp3.Response): Int {
        var result = 1
        var parent = response.priorResponse
        while (parent != null) {
            result++
            parent = parent.priorResponse
        }
        return result
    }

    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(AppConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(AppConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(AppConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request()
                val builder = request.newBuilder()
                token?.let {
                    builder.addHeader("Authorization", "Bearer $it")
                }
                val method = request.method
                if (method == "POST" || method == "PATCH" || method == "DELETE") {
                    val idempotencyKey = java.util.UUID.randomUUID().toString()
                    builder.addHeader("X-Idempotency-Key", idempotencyKey)
                }
                chain.proceed(builder.build())
            }
            .authenticator { _, response ->
                if (responseCount(response) >= 3) {
                    return@authenticator null
                }
                synchronized(this) {
                    val currentToken = token
                    if (response.request.header("Authorization") != "Bearer $currentToken") {
                        return@authenticator response.request.newBuilder()
                            .header("Authorization", "Bearer $currentToken")
                            .build()
                    }
                    val currentRefreshToken = refreshToken
                    if (currentRefreshToken != null) {
                        try {
                            val refreshRequest = okhttp3.Request.Builder()
                                .url("${AppConfig.BASE_URL}auth/refresh")
                                .post(okhttp3.RequestBody.create(
                                    "application/json".toMediaTypeOrNull(),
                                    "{\"refreshToken\":\"$currentRefreshToken\"}"
                                ))
                                .build()
                            val clientForRefresh = OkHttpClient.Builder()
                                .connectTimeout(AppConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                                .readTimeout(AppConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                                .writeTimeout(AppConfig.NETWORK_TIMEOUT, TimeUnit.SECONDS)
                                .build()
                            val refreshResponse = clientForRefresh.newCall(refreshRequest).execute()
                            if (refreshResponse.isSuccessful) {
                                val bodyString = refreshResponse.body?.string()
                                if (bodyString != null) {
                                    val gson = com.google.gson.Gson()
                                    val authResponse = gson.fromJson(bodyString, AuthResponse::class.java)
                                    val newToken = authResponse.token
                                    val newRefreshToken = authResponse.refreshToken
                                    if (newToken.isNotEmpty()) {
                                        setToken(newToken, newRefreshToken)
                                        return@authenticator response.request.newBuilder()
                                            .header("Authorization", "Bearer $newToken")
                                            .build()
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                null
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.HEADERS
            })
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(AppConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ASGApiService by lazy {
        retrofit.create(ASGApiService::class.java)
    }
}
