package com.apex.asg.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Current Local IP for testing (change to your local PC IP for emulator access)
// Android emulator uses 10.0.2.2 to access localhost of the host machine
const val BASE_URL = "http://10.0.2.2:5000/api/"

interface ASGApiService {
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

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
}

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val ownerId: String,
    val type: String = "GENERAL"
)

object ApiClient {
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
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
