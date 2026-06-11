package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Data structures for APEX Kiri Organization (AAL) and ASG Ecosystem.
 * Mapped from the asg_aal_datastructure.md documentation.
 * Refactored with Any? for deserialization resiliency (Permanent fix for BEGIN_OBJECT errors).
 */

// --- 1. Core Users & Roles ---

data class AalUserDto(
    @SerializedName("user_id") val userId: Any? = null,
    @SerializedName("full_name") val fullName: Any? = null,
    val email: Any? = null,
    val phone: Any? = null,
    @SerializedName("user_category") val userCategory: UserCategory? = UserCategory.STUDENT,
    @SerializedName("digital_persona") val digitalPersona: Any? = null,
    @SerializedName("created_at") val createdAt: Any? = null
)

enum class UserCategory {
    STUDENT, NON_STUDENT, ALUMNI, FACULTY
}

data class StakeholderRoleDto(
    @SerializedName("role_mapping_id") val roleMappingId: Any? = null,
    @SerializedName("user_id") val userId: Any? = null,
    @SerializedName("role_name") val roleName: StakeholderRole? = StakeholderRole.GUEST
)

enum class StakeholderRole {
    FOUNDER, SERVICE_PROVIDER, MENTOR, INVESTOR, INCUBATOR, GUEST
}

// --- 2. Institutions, Committees & Repositories (R1-R5) ---

data class InstitutionDto(
    @SerializedName("institution_id") val institutionId: Any? = null,
    val name: Any? = null,
    @SerializedName("spoc_user_id") val spocUserId: Any? = null
)

data class InstitutionalCommitteeDto(
    @SerializedName("committee_id") val committeeId: Any? = null,
    @SerializedName("institution_id") val institutionId: Any? = null,
    @SerializedName("user_id") val userId: Any? = null,
    val department: Any? = null,
    @SerializedName("role_type") val roleType: CommitteeRoleType? = CommitteeRoleType.STUDENT_REP
)

enum class CommitteeRoleType {
    FACULTY_REP, STUDENT_REP
}

data class UserRepositoryDto(
    @SerializedName("repo_mapping_id") val repoMappingId: Any? = null,
    @SerializedName("user_id") val userId: Any? = null,
    @SerializedName("institution_id") val institutionId: Any? = null,
    @SerializedName("repo_category") val repoCategory: RepoCategory? = RepoCategory.R1,
    @SerializedName("approval_status") val approvalStatus: ApprovalStatus? = ApprovalStatus.PENDING,
    @SerializedName("approved_by") val approvedBy: Any? = null
)

enum class RepoCategory {
    R1, R2, R3, R4, R5
}

enum class ApprovalStatus {
    PENDING, APPROVED, REJECTED
}

// --- 3. Apex Kiri Organization (AAL) & LMS ---

data class AalOnboardingDto(
    @SerializedName("aal_id") val aalId: Any? = null,
    @SerializedName("user_id") val userId: Any? = null,
    @SerializedName("mindset_score") val mindsetScore: Any? = null,
    @SerializedName("lms_status") val lmsStatus: LmsStatus? = LmsStatus.ENROLLED,
    @SerializedName("certificate_url") val certificateUrl: Any? = null,
    @SerializedName("interview_status") val interviewStatus: InterviewStatus? = InterviewStatus.PENDING
)

enum class LmsStatus {
    ENROLLED, COMPLETED
}

enum class InterviewStatus {
    PENDING, PASSED, FAILED
}

data class AalActivityDto(
    @SerializedName("activity_id") val activityId: Any? = null,
    @SerializedName("user_id") val userId: Any? = null,
    @SerializedName("activity_number") val activityNumber: Int? = 0,
    @SerializedName("submission_url") val submissionUrl: Any? = null,
    @SerializedName("status") val status: ActivityStatus? = ActivityStatus.SUBMITTED
)

enum class ActivityStatus {
    SUBMITTED, VERIFIED
}

// --- 4. Events, Hackathons & Workflows ---

data class AalEventDto(
    @SerializedName("event_id") val eventId: Any? = null,
    val title: Any? = null,
    @SerializedName("type") val eventType: EventType? = EventType.MEETUP,
    @SerializedName("host_institution_id") val hostInstitutionId: Any? = null,
    @SerializedName("start_time") val startTime: Any? = null,
    val location: Any? = null,
    @SerializedName("qr_base_url") val qrBaseUrl: Any? = null
)

enum class EventType {
    MEETUP, HACKATHON, QUIZ, OPEN_DAY
}

data class EventRegistrationDto(
    @SerializedName("registration_id") val registrationId: Any? = null,
    @SerializedName("event_id") val eventId: Any? = null,
    @SerializedName("user_id") val userId: Any? = null,
    val status: RegistrationStatus? = RegistrationStatus.REGISTERED,
    @SerializedName("form_data") val formData: Any? = null,
    @SerializedName("qr_scanned_at") val qrScannedAt: Any? = null
)

enum class RegistrationStatus {
    REGISTERED, WAITLIST, ATTENDED
}

data class EventTeamDto(
    @SerializedName("team_id") val teamId: Any? = null,
    @SerializedName("event_id") val eventId: Any? = null,
    @SerializedName("team_name") val teamName: Any? = null,
    @SerializedName("ai_generated") val aiGenerated: Boolean? = false
)

data class EventSubmissionDto(
    @SerializedName("submission_id") val submissionId: Any? = null,
    @SerializedName("event_id") val eventId: Any? = null,
    @SerializedName("team_id") val teamId: Any? = null,
    @SerializedName("document_url") val documentUrl: Any? = null,
    @SerializedName("geo_lat") val geoLat: Double? = null,
    @SerializedName("geo_long") val geoLong: Double? = null,
    @SerializedName("ai_authenticity_score") val aiAuthenticityScore: Float? = null
)

data class JudgingRecordDto(
    @SerializedName("judge_record_id") val judgeRecordId: Any? = null,
    @SerializedName("event_id") val eventId: Any? = null,
    @SerializedName("team_id") val teamId: Any? = null,
    @SerializedName("judge_user_id") val judgeUserId: Any? = null,
    @SerializedName("is_ai_judge") val isAiJudge: Boolean? = false,
    val score: Float? = 0f,
    val feedback: Any? = null
)

// --- 5. The AI Core (Live Inputs & Mapping) ---

data class LiveInputDto(
    @SerializedName("input_id") val inputId: Any? = null,
    @SerializedName("user_id") val userId: Any? = null,
    @SerializedName("format_type") val formatType: InputFormat? = InputFormat.TEXT,
    @SerializedName("content_url") val contentUrl: Any? = null,
    val context: InputContext? = InputContext.DAY_TO_DAY,
    @SerializedName("ai_processed") val aiProcessed: Boolean? = false
)

enum class InputFormat {
    TEXT, VOICE, VIDEO, IMAGE
}

enum class InputContext {
    DAY_TO_DAY, ACHIEVEMENT, FAILURE, STUCK, ASK_FOR_HELP
}

data class AiResourceMatchDto(
    @SerializedName("match_id") val matchId: Any? = null,
    @SerializedName("source_user_id") val sourceUserId: Any? = null,
    @SerializedName("target_user_id") val targetUserId: Any? = null,
    @SerializedName("match_reason") val matchReason: Any? = null,
    val status: MatchStatus? = MatchStatus.SUGGESTED
)

enum class MatchStatus {
    SUGGESTED, ACCEPTED, REJECTED
}

// --- 6. Ecosystem Dashboard (News, Jobs, Asks) ---

data class EcosystemBoardDto(
    @SerializedName("board_id") val boardId: Any? = null,
    @SerializedName("author_user_id") val authorUserId: Any? = null,
    @SerializedName("post_type") val postType: PostType? = PostType.NEWS,
    val title: Any? = null,
    val description: Any? = null,
    @SerializedName("media_url") val mediaUrl: Any? = null,
    @SerializedName("created_at") val createdAt: Any? = null
)

enum class PostType {
    NEWS, WALL_OF_FAME, ASK, PRODUCT, ANNOUNCEMENT
}

data class JobProjectDto(
    @SerializedName("listing_id") val listingId: Any? = null,
    @SerializedName("posted_by") val postedBy: Any? = null,
    val type: JobType? = JobType.JOB,
    val title: Any? = null,
    val description: Any? = null,
    val status: JobStatus? = JobStatus.OPEN
)

enum class JobType {
    JOB, INTERNSHIP, RESEARCH, CASE_STUDY
}

enum class JobStatus {
    OPEN, CLOSED
}
