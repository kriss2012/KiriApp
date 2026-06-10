package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Objects (DTOs) for the Kiri Community API.
 * Refactored for MAXIMUM null safety to prevent runtime crashes.
 */

data class GenerateInviteRequest(
    val targetRole: String,
    val expiryDays: Int = 7
)

data class InviteCodeDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("code") val _code: String? = null,
    @SerializedName("targetRole") val _targetRole: String? = null,
    @SerializedName("isUsed") val _isUsed: Boolean? = null,
    @SerializedName("expiresAt") val _expiresAt: String? = null
) {
    val id: String get() = _id ?: ""
    val code: String get() = _code ?: ""
    val targetRole: String get() = _targetRole ?: ""
    val isUsed: Boolean get() = _isUsed ?: false
    val expiresAt: String get() = _expiresAt ?: ""
}

data class MentorSessionDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("topic") val _topic: String? = null,
    @SerializedName("status") val _status: String? = null,
    val scheduledAt: String? = null,
    val mentor: UserDto? = null,
    val founder: UserDto? = null,
    @SerializedName("createdAt") val _createdAt: String? = null
) {
    val id: String get() = _id ?: ""
    val topic: String get() = _topic ?: ""
    val status: String get() = _status ?: "PENDING"
    val createdAt: String get() = _createdAt ?: ""
}

data class MentorSessionRequest(
    val mentorId: String,
    val topic: String,
    val scheduledAt: String? = null
)

data class HeatMapDto(
    val department: String? = null,
    val _count: HeatMapCountDto? = null
) {
    val dept: String get() = department ?: "General"
}

data class HeatMapCountDto(
    val activities: Int? = 0, 
    val pitches: Int? = 0
)

data class ProjectArtifactDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("title") val _title: String? = null,
    val description: String? = null,
    val url: String? = null,
    val summary: String? = null,
    @SerializedName("createdAt") val _createdAt: String? = null
) {
    val id: String get() = _id ?: ""
    val title: String get() = _title ?: "Untitled Project"
    val createdAt: String get() = _createdAt ?: ""
}

data class CreateArtifactRequest(
    val title: String,
    val description: String?,
    val url: String?,
    val userId: String
)

data class InvestorPitchDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("title") val _title: String? = null,
    @SerializedName("description") val _description: String? = null,
    @SerializedName("fundingGoal") val _fundingGoal: Float? = null,
    @SerializedName("category") val _category: String? = null,
    @SerializedName("healthScore") val _healthScore: Int? = null,
    val founder: UserDto? = null,
    val _count: PitchCountDto? = null
) {
    val id: String get() = _id ?: ""
    val title: String get() = _title ?: ""
    val description: String get() = _description ?: ""
    val fundingGoal: Float get() = _fundingGoal ?: 0f
    val category: String get() = _category ?: "General"
    val healthScore: Int get() = _healthScore ?: 0
}

data class MarketTrendDto(
    val category: String? = null,
    val _count: Map<String, Int>? = null,
    val _avg: Map<String, Float>? = null
)

data class MatchSuggestionDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("fullName") val _fullName: String? = null,
    @SerializedName("role") val _role: String? = null,
    val userCategory: String? = null,
    val avatarUrl: String? = null,
    val skills: List<String>? = emptyList(),
    val college: String? = null,
    val intent: String? = null,
    @SerializedName("matchScore") val _matchScore: Int? = null
) {
    val id: String get() = _id ?: ""
    val fullName: String get() = _fullName ?: "Kiri User"
    val role: String get() = _role ?: userCategory ?: "STUDENT"
    val matchScore: Int get() = _matchScore ?: 0
}

data class PitchDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("title") val _title: String? = null,
    @SerializedName("description") val _description: String? = null,
    @SerializedName("problem") val _problem: String? = null,
    @SerializedName("solution") val _solution: String? = null,
    @SerializedName("impact") val _impact: String? = null,
    @SerializedName("fundingGoal") val _fundingGoal: Float? = null,
    @SerializedName("status") val _status: String? = null,
    @SerializedName("category") val _category: String? = null,
    val founderId: String? = null,
    val founder: UserDto? = null,
    val _count: PitchCountDto? = null,
    @SerializedName("createdAt") val _createdAt: String? = null
) {
    val id: String get() = _id ?: ""
    val title: String get() = _title ?: ""
    val description: String get() = _description ?: ""
    val problem: String get() = _problem ?: ""
    val solution: String get() = _solution ?: ""
    val impact: String get() = _impact ?: ""
    val fundingGoal: Float get() = _fundingGoal ?: 0f
    val status: String get() = _status ?: "DRAFT"
    val category: String get() = _category ?: "General"
    val createdAt: String get() = _createdAt ?: ""
}

data class UserResponse(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("fullName") val _fullName: String? = null,
    @SerializedName("role") val _role: String? = null,
    val userCategory: String? = null,
    val avatarUrl: String? = null,
    val college: String? = null,
    val bio: String? = null
) {
    val id: String get() = _id ?: ""
    val fullName: String get() = _fullName ?: "Kiri User"
    val role: String get() = _role ?: userCategory ?: "STUDENT"
}

data class PitchCountDto(val backers: Int? = 0)

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
    @SerializedName("id") val _id: String? = null,
    @SerializedName("type") val _type: String? = null,
    @SerializedName("title") val _title: String? = null,
    val content: String? = null,
    @SerializedName("points") val _points: Int? = null,
    @SerializedName("createdAt") val _createdAt: String? = null
) {
    val id: String get() = _id ?: ""
    val type: String get() = _type ?: "GENERAL"
    val title: String get() = _title ?: ""
    val points: Int get() = _points ?: 0
    val createdAt: String get() = _createdAt ?: ""
}

data class UserStatsDto(
    val points: Int? = 0,
    val _count: UserCountDto? = null
)

data class UserCountDto(
    val activities: Int? = 0,
    val connections: Int? = 0
)

data class AiMessageRequest(
    val content: String,
    val fileData: String? = null,
    val mimeType: String? = null,
    val language: String? = null
)

data class AiMessageResponse(
    @SerializedName("id") val _id: String? = null, 
    @SerializedName("content") val _content: String? = null, 
    @SerializedName("role") val _role: String? = null, 
    @SerializedName("createdAt") val _createdAt: String? = null
) {
    val id: String get() = _id ?: ""
    val content: String get() = _content ?: ""
    val role: String get() = _role ?: "assistant"
    val createdAt: String get() = _createdAt ?: ""
}

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
    @SerializedName("id") val _id: String? = null,
    @SerializedName("title") val _title: String? = null,
    @SerializedName("description") val _description: String? = null,
    val location: String? = null,
    @SerializedName("type") val _type: String? = null,
    val posterId: String? = null,
    @SerializedName("createdAt") val _createdAt: String? = null,
    val poster: UserDto? = null
) {
    val id: String get() = _id ?: ""
    val title: String get() = _title ?: ""
    val description: String get() = _description ?: ""
    val type: String get() = _type ?: "JOB"
    val createdAt: String get() = _createdAt ?: ""
}

data class MessageDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("content") val _content: String? = null,
    @SerializedName("senderId") val _senderId: String? = null,
    @SerializedName("receiverId") val _receiverId: String? = null,
    @SerializedName("createdAt") val _createdAt: String? = null,
    @SerializedName("isRead") val _isRead: Boolean? = null
) {
    val id: String get() = _id ?: ""
    val content: String get() = _content ?: ""
    val senderId: String get() = _senderId ?: ""
    val receiverId: String get() = _receiverId ?: ""
    val createdAt: String get() = _createdAt ?: ""
    val isRead: Boolean get() = _isRead ?: false
}

data class ConversationResponse(
    val otherUser: UserDto? = null,
    val lastMessage: MessageDto? = null,
    val unreadCount: Int? = 0
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
    @SerializedName("id") val _id: String? = null,
    @SerializedName("title") val _title: String? = null,
    @SerializedName("description") val _description: String? = null,
    @SerializedName("date") val _date: String? = null,
    @SerializedName("location") val _location: String? = null,
    val imageUrl: String? = null,
    val coordinatorName: String? = null,
    val coordinatorPhone: String? = null,
    val type: String? = null,
    val registrationLink: String? = null,
    val prize: String? = null
) {
    val id: String get() = _id ?: ""
    val title: String get() = _title ?: ""
    val description: String get() = _description ?: ""
    val date: String get() = _date ?: "2024-01-01"
    val location: String get() = _location ?: "TBD"
}

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
    val bio: String? = null,
    val website: String? = null,
    val githubUrl: String? = null,
    val linkedInUrl: String? = null,
    val services: List<String>? = null,
    val inviteCode: String? = null,
    val aalAutoEnroll: Boolean = true
)

data class AuthResponse(
    @SerializedName("token") val _token: String? = null,
    @SerializedName("refreshToken") val _refreshToken: String? = null,
    val user: UserDto? = null
) {
    val token: String get() = _token ?: ""
    val refreshToken: String get() = _refreshToken ?: ""
}

data class UserDto(
    @SerializedName(value = "id", alternate = ["_id"]) val _id: String? = null,
    @SerializedName(value = "email") val _email: String? = null,
    @SerializedName(value = "fullName", alternate = ["name"]) val _fullName: String? = null,
    @SerializedName(value = "role") val _role: String? = null,
    val bio: String? = null,
    val department: String? = null,
    val college: String? = null,
    val year: String? = null,
    val section: String? = null,
    val avatarUrl: String? = null,
    val _canCreateEvents: Boolean? = false,
    val points: Int? = 100,
    val phoneNumber: String? = null,
    val website: String? = null,
    val githubUrl: String? = null,
    val linkedInUrl: String? = null,
    val services: List<String>? = emptyList(),
    val eventsCount: Int? = 0,
    val connectionsCount: Int? = 0,
    val digitalPersona: String? = null,
    val userCategory: String? = null
) {
    val id: String get() = _id ?: ""
    val email: String get() = _email ?: ""
    val fullName: String get() = _fullName ?: "Kiri Member"
    val role: String get() = _role ?: userCategory ?: "STUDENT"
    val canCreateEvents: Boolean get() = _canCreateEvents ?: false
    val pointsCount: Int get() = points ?: 0
}

data class NotificationDto(
    @SerializedName("id") val _id: String? = null,
    val userId: String? = null,
    @SerializedName("title") val _title: String? = null,
    @SerializedName("content") val _content: String? = null,
    @SerializedName("type") val _type: String? = null,
    val relatedId: String? = null,
    @SerializedName("isRead") val _isRead: Boolean? = null,
    @SerializedName("createdAt") val _createdAt: String? = null
) {
    val id: String get() = _id ?: ""
    val title: String get() = _title ?: "Notification"
    val content: String get() = _content ?: ""
    val type: String get() = _type ?: "ALERT"
    val isRead: Boolean get() = _isRead ?: false
    val createdAt: String get() = _createdAt ?: ""
}

data class ConnectionDto(
    @SerializedName("id") val _id: String? = null,
    @SerializedName("status") val _status: String? = null,
    val senderId: String? = null,
    val receiverId: String? = null,
    val sender: UserDto? = null,
    val receiver: UserDto? = null
) {
    val id: String get() = _id ?: ""
    val status: String get() = _status ?: "PENDING"
}
