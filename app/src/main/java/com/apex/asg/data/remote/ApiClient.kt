package com.apex.asg.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

const val BASE_URL = "https://asgapp.onrender.com/api/"

interface ASGApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

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
    suspend fun updateProfile(@Path("userId") userId: String, @Body profileData: Map<String, Any?>): UserDto

    @PUT("users/toggle-access/{userId}")
    suspend fun toggleAccess(@Path("userId") userId: String, @Body data: Map<String, Boolean>): Map<String, String>

    @GET("users/verified")
    suspend fun getVerifiedUsers(): List<UserDto>

    // Notifications
    @GET("notifications/{userId}")
    suspend fun getNotifications(@Path("userId") userId: String): List<NotificationDto>

    @retrofit2.http.PATCH("notifications/{id}/read")
    suspend fun markNotificationAsRead(@Path("id") id: String): NotificationDto

    // Connections
    @POST("connections/send")
    suspend fun sendConnectionRequest(@Body request: Map<String, String>): Map<String, Any>

    @POST("connections/accept")
    suspend fun acceptConnectionRequest(@Body request: Map<String, String>): Map<String, Any>

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

    @POST("chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): MessageDto

    // Kiri AI
    @GET("ai/history")
    suspend fun getAiHistory(): List<AiMessageResponse>

    @POST("ai/chat")
    suspend fun sendAiMessage(@Body request: AiMessageRequest): AiMessageResponse

    @PUT("ai/specialization")
    suspend fun updateSpecialization(@Body request: Map<String, String>): Map<String, Any>

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
    suspend fun backPitch(@Body request: Map<String, String>): Map<String, Any>

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

    @GET("investor/heatmap")
    suspend fun getInnovationHeatmap(): List<HeatMapDto>

    @POST("invite/generate")
    suspend fun generateInvite(@Body request: GenerateInviteRequest): InviteCodeDto

    @GET("invite/list")
    suspend fun getInvites(): List<InviteCodeDto>
}

data class GenerateInviteRequest(
    val targetRole: String,
    val expiryDays: Int = 7
)

data class InviteCodeDto(
    val id: String,
    val code: String,
    val targetRole: String,
    val isUsed: Boolean,
    val expiresAt: String
)

data class MentorSessionDto(
    val id: String,
    val topic: String,
    val status: String,
    val scheduledAt: String?,
    val mentor: UserDto,
    val founder: UserDto,
    val createdAt: String
)

data class MentorSessionRequest(
    val mentorId: String,
    val topic: String,
    val scheduledAt: String? = null
)

data class HeatMapDto(
    val department: String,
    val _count: HeatMapCountDto
)

data class HeatMapCountDto(val activities: Int, val pitches: Int)

data class ProjectArtifactDto(
    val id: String,
    val title: String,
    val description: String?,
    val url: String?,
    val summary: String?,
    val createdAt: String
)

data class CreateArtifactRequest(
    val title: String,
    val description: String?,
    val url: String?,
    val userId: String
)

data class InvestorPitchDto(
    val id: String,
    val title: String,
    val description: String,
    val fundingGoal: Float,
    val category: String,
    val healthScore: Int,
    val founder: UserDto,
    val _count: PitchCountDto
)

data class MarketTrendDto(
    val category: String,
    val _count: Map<String, Int>,
    val _avg: Map<String, Float>
)

data class MatchSuggestionDto(
    val id: String,
    val fullName: String,
    val role: String,
    val avatarUrl: String?,
    val skills: List<String>,
    val college: String?,
    val intent: String?,
    val matchScore: Int
)

data class PitchDto(
    val id: String,
    val title: String,
    val description: String,
    val problem: String,
    val solution: String,
    val impact: String,
    val fundingGoal: Float,
    val status: String,
    val category: String,
    val founderId: String,
    val founder: UserDto,
    val _count: PitchCountDto,
    val createdAt: String
)

data class PitchCountDto(val backers: Int)

data class CreatePitchRequest(
    val title: String,
    val description: String,
    val problem: String,
    val solution: String,
    val impact: String,
    val fundingGoal: String,
    val category: String
)

data class ActivityDto(
    val id: String,
    val type: String,
    val title: String,
    val content: String?,
    val points: Int,
    val createdAt: String
)

data class UserStatsDto(
    val points: Int,
    val _count: UserCountDto
)

data class UserCountDto(
    val activities: Int,
    val connections: Int
)

data class AiMessageRequest(
    val content: String,
    val fileData: String? = null,
    val mimeType: String? = null
)
data class AiMessageResponse(val id: String, val content: String, val role: String, val createdAt: String)

data class UserResponse(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val bio: String? = null,
    val college: String? = null,
    val skills: List<String> = emptyList()
)

data class CreateJobRequest(
    val title: String,
    val description: String,
    val location: String?,
    val type: String,
    val posterId: String
)

data class SendMessageRequest(
    val senderId: String,
    val receiverId: String,
    val content: String
)

data class JobDto(
    val id: String,
    val title: String,
    val description: String,
    val location: String?,
    val type: String,
    val posterId: String,
    val createdAt: String,
    val poster: UserDto? = null
)

data class MessageDto(
    val id: String,
    val content: String,
    val senderId: String,
    val receiverId: String,
    val createdAt: String,
    val isRead: Boolean
)

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val ownerId: String,
    val type: String = "GENERAL"
)

object ApiClient {
    private var token: String? = null

    fun setToken(newToken: String?) {
        token = newToken
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val builder = chain.request().newBuilder()
            token?.let {
                builder.addHeader("Authorization", "Bearer $it")
            }
            chain.proceed(builder.build())
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: ASGApiService by lazy {
        retrofit.create(ASGApiService::class.java)
    }
}

data class EventDto(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val type: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val role: String,
    val studentLevel: String? = null,
    val department: String? = null,
    val college: String? = null,
    val year: String? = null,
    val section: String? = null
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class UserDto(
    val id: String,
    val email: String,
    val fullName: String,
    val role: String,
    val bio: String? = null,
    val department: String? = null,
    val college: String? = null,
    val year: String? = null,
    val section: String? = null,
    val avatarUrl: String? = null,
    val canCreateEvents: Boolean = false
)

data class NotificationDto(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)

data class ConnectionDto(
    val id: String,
    val status: String,
    val senderId: String,
    val receiverId: String,
    val sender: UserDto,
    val receiver: UserDto
)
