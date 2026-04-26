package com.apex.asg.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Data structures for APEX AI Launchpad (AAL) and ASG Ecosystem.
 * Mapped from the asg_aal_datastructure.md documentation.
 */

// --- 1. Core Users & Roles ---

data class AalUserDto(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val phone: String?,
    @SerializedName("user_category") val userCategory: UserCategory,
    @SerializedName("digital_persona") val digitalPersona: String?, // JSON string
    @SerializedName("created_at") val createdAt: String
)

enum class UserCategory {
    STUDENT, NON_STUDENT, ALUMNI, FACULTY
}

data class StakeholderRoleDto(
    @SerializedName("role_mapping_id") val roleMappingId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("role_name") val roleName: StakeholderRole
)

enum class StakeholderRole {
    FOUNDER, SERVICE_PROVIDER, MENTOR, INVESTOR, INCUBATOR, GUEST
}

// --- 2. Institutions, Committees & Repositories (R1-R5) ---

data class InstitutionDto(
    @SerializedName("institution_id") val institutionId: Int,
    val name: String,
    @SerializedName("spoc_user_id") val spocUserId: Int
)

data class InstitutionalCommitteeDto(
    @SerializedName("committee_id") val committeeId: Int,
    @SerializedName("institution_id") val institutionId: Int,
    @SerializedName("user_id") val userId: Int,
    val department: String,
    @SerializedName("role_type") val roleType: CommitteeRoleType
)

enum class CommitteeRoleType {
    FACULTY_REP, STUDENT_REP
}

data class UserRepositoryDto(
    @SerializedName("repo_mapping_id") val repoMappingId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("institution_id") val institutionId: Int,
    @SerializedName("repo_category") val repoCategory: RepoCategory,
    @SerializedName("approval_status") val approvalStatus: ApprovalStatus,
    @SerializedName("approved_by") val approvedBy: Int?
)

enum class RepoCategory {
    R1, R2, R3, R4, R5
}

enum class ApprovalStatus {
    PENDING, APPROVED, REJECTED
}

// --- 3. Apex AI Launchpad (AAL) & LMS ---

data class AalOnboardingDto(
    @SerializedName("aal_id") val aalId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("mindset_score") val mindsetScore: String?, // JSON string
    @SerializedName("lms_status") val lmsStatus: LmsStatus,
    @SerializedName("certificate_url") val certificateUrl: String?,
    @SerializedName("interview_status") val interviewStatus: InterviewStatus
)

enum class LmsStatus {
    ENROLLED, COMPLETED
}

enum class InterviewStatus {
    PENDING, PASSED, FAILED
}

data class AalActivityDto(
    @SerializedName("activity_id") val activityId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("activity_number") val activityNumber: Int,
    @SerializedName("submission_url") val submissionUrl: String,
    val status: ActivityStatus
)

enum class ActivityStatus {
    SUBMITTED, VERIFIED
}

// --- 4. Events, Hackathons & Workflows ---

data class AalEventDto(
    @SerializedName("event_id") val eventId: Int,
    val title: String,
    val type: EventType,
    @SerializedName("host_institution_id") val hostInstitutionId: Int?,
    @SerializedName("start_time") val startTime: String,
    val location: String,
    @SerializedName("qr_base_url") val qrBaseUrl: String?
)

enum class EventType {
    MEETUP, HACKATHON, QUIZ, OPEN_DAY
}

data class EventRegistrationDto(
    @SerializedName("registration_id") val registrationId: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("user_id") val userId: Int,
    val status: RegistrationStatus,
    @SerializedName("form_data") val formData: String?, // JSON string
    @SerializedName("qr_scanned_at") val qrScannedAt: String?
)

enum class RegistrationStatus {
    REGISTERED, WAITLIST, ATTENDED
}

data class EventTeamDto(
    @SerializedName("team_id") val teamId: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("team_name") val teamName: String,
    @SerializedName("ai_generated") val aiGenerated: Boolean
)

data class EventSubmissionDto(
    @SerializedName("submission_id") val submissionId: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("team_id") val teamId: Int?,
    @SerializedName("document_url") val documentUrl: String,
    @SerializedName("geo_lat") val geoLat: Double?,
    @SerializedName("geo_long") val geoLong: Double?,
    @SerializedName("ai_authenticity_score") val aiAuthenticityScore: Float?
)

data class JudgingRecordDto(
    @SerializedName("judge_record_id") val judgeRecordId: Int,
    @SerializedName("event_id") val eventId: Int,
    @SerializedName("team_id") val teamId: Int,
    @SerializedName("judge_user_id") val judgeUserId: Int?,
    @SerializedName("is_ai_judge") val isAiJudge: Boolean,
    val score: Float,
    val feedback: String?
)

// --- 5. The AI Core (Live Inputs & Mapping) ---

data class LiveInputDto(
    @SerializedName("input_id") val inputId: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("format_type") val formatType: InputFormat,
    @SerializedName("content_url") val contentUrl: String,
    val context: InputContext,
    @SerializedName("ai_processed") val aiProcessed: Boolean
)

enum class InputFormat {
    TEXT, VOICE, VIDEO, IMAGE
}

enum class InputContext {
    DAY_TO_DAY, ACHIEVEMENT, FAILURE, STUCK, ASK_FOR_HELP
}

data class AiResourceMatchDto(
    @SerializedName("match_id") val matchId: Int,
    @SerializedName("source_user_id") val sourceUserId: Int,
    @SerializedName("target_user_id") val targetUserId: Int,
    @SerializedName("match_reason") val matchReason: String,
    val status: MatchStatus
)

enum class MatchStatus {
    SUGGESTED, ACCEPTED, REJECTED
}

// --- 6. Ecosystem Dashboard (News, Jobs, Asks) ---

data class EcosystemBoardDto(
    @SerializedName("board_id") val boardId: Int,
    @SerializedName("author_user_id") val authorUserId: Int,
    @SerializedName("post_type") val postType: PostType,
    val title: String,
    val description: String,
    @SerializedName("media_url") val mediaUrl: String?,
    @SerializedName("created_at") val createdAt: String
)

enum class PostType {
    NEWS, WALL_OF_FAME, ASK, PRODUCT, ANNOUNCEMENT
}

data class JobProjectDto(
    @SerializedName("listing_id") val listingId: Int,
    @SerializedName("posted_by") val postedBy: Int,
    val type: JobType,
    val title: String,
    val description: String,
    val status: JobStatus
)

enum class JobType {
    JOB, INTERNSHIP, RESEARCH, CASE_STUDY
}

enum class JobStatus {
    OPEN, CLOSED
}
