package com.kiriplatform.app.data.remote.models

/**
 * Data Transfer Objects (DTOs) for the Kiri Community API.
 * These models represent the structured responses and requests used across the platform.
 */

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

data class ConversationResponse(
    val otherUser: UserDto,
    val lastMessage: MessageDto,
    val unreadCount: Int
)

data class CreateEventRequest(
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val ownerId: String,
    val coordinatorName: String,
    val coordinatorPhone: String? = null,
    val imageUrl: String? = null,
    val type: String = "GENERAL",
    val registrationLink: String? = null,
    val prize: String? = null
)

// Typed request to avoid Retrofit wildcard serialization errors
data class UpdateProfileRequest(
    val fullName: String,
    val role: String,
    val bio: String? = null,
    val department: String? = null,
    val college: String? = null,
    val year: String? = null,
    val phoneNumber: String? = null,
    val website: String? = null,
    val githubUrl: String? = null,
    val linkedInUrl: String? = null,
    val services: List<String> = emptyList()
)

data class EventDto(
    val id: String,
    val title: String,
    val description: String,
    val date: String,
    val location: String,
    val imageUrl: String? = null,
    val coordinatorName: String? = null,
    val coordinatorPhone: String? = null,
    val type: String? = null,
    val registrationLink: String? = null,
    val prize: String? = null,
    val ownerId: String? = null
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
    val rollNumber: String? = null,
    val department: String? = null,
    val college: String? = null,
    val year: String? = null,
    val section: String? = null,
    val phoneNumber: String? = null,
    val website: String? = null,
    val githubUrl: String? = null,
    val linkedInUrl: String? = null,
    val services: List<String>? = null,
    val inviteCode: String? = null,
    val aalAutoEnroll: Boolean = true
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
    val canCreateEvents: Boolean = false,
    val points: Int = 100,
    val phoneNumber: String? = null,
    val website: String? = null,
    val githubUrl: String? = null,
    val linkedInUrl: String? = null,
    val services: List<String> = emptyList(),
    val eventsCount: Int = 0,
    val connectionsCount: Int = 0,
    val digitalPersona: String? = null,
    val userCategory: String? = null
)

data class NotificationDto(
    val id: String,
    val userId: String,
    val title: String,
    val content: String,
    val type: String,
    val relatedId: String? = null,
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
