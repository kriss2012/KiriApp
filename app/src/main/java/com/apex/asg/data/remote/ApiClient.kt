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
    suspend fun getUsers(): List<UserResponse>

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

    // Connections
    @POST("connections/request")
    suspend fun sendConnectionRequest(@Body request: Map<String, String>): Map<String, String>
}

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
    val date: String
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
    val role: String
)
