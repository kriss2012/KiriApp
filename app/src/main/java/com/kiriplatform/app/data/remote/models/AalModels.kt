package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Data structures for APEX Kiri Organization (AAL) and ASG Ecosystem.
 * Mapped from the asg_aal_datastructure.md documentation.
 * Refactored for MAXIMUM null safety.
 */

// --- 1. Core Users & Roles ---

data class AalUserDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("full_name") val _fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    @SerializedName("user_category") val _userCategory: UserCategory? = UserCategory.STUDENT,
    @SerializedName("digital_persona") val digitalPersona: String? = null, 
    @SerializedName("created_at") val _createdAt: String? = null
) {
    val fullName: String get() = _fullName ?: "Kiri Member"
    val userCategory: UserCategory get() = _userCategory ?: UserCategory.STUDENT
    val createdAt: String get() = _createdAt ?: ""
}

enum class UserCategory {
    STUDENT, NON_STUDENT, ALUMNI, FACULTY
}

data class StakeholderRoleDto(
    @SerializedName("role_mapping_id") val roleMappingId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("role_name") val _roleName: StakeholderRole? = StakeholderRole.GUEST
) {
    val roleName: StakeholderRole get() = _roleName ?: StakeholderRole.GUEST
}

enum class StakeholderRole {
    FOUNDER, SERVICE_PROVIDER, MENTOR, INVESTOR, INCUBATOR, GUEST
}

// --- 2. Institutions, Committees & Repositories (R1-R5) ---

data class InstitutionDto(
    @SerializedName("institution_id") val institutionId: String? = null,
    val name: String? = null,
    @SerializedName("spoc_user_id") val spocUserId: String? = null
) {
    val institutionName: String get() = name ?: "Kiri Institution"
}

data class InstitutionalCommitteeDto(
    @SerializedName("committee_id") val committeeId: String? = null,
    @SerializedName("institution_id") val institutionId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    val department: String? = null,
    @SerializedName("role_type") val _roleType: CommitteeRoleType? = CommitteeRoleType.STUDENT_REP
) {
    val roleType: CommitteeRoleType get() = _roleType ?: CommitteeRoleType.STUDENT_REP
}

enum class CommitteeRoleType {
    FACULTY_REP, STUDENT_REP
}

data class UserRepositoryDto(
    @SerializedName("repo_mapping_id") val repoMappingId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("institution_id") val institutionId: String? = null,
    @SerializedName("repo_category") val _repoCategory: RepoCategory? = RepoCategory.R1,
    @SerializedName("approval_status") val _approvalStatus: ApprovalStatus? = ApprovalStatus.PENDING,
    @SerializedName("approved_by") val approvedBy: String? = null
) {
    val repoCategory: RepoCategory get() = _repoCategory ?: RepoCategory.R1
    val approvalStatus: ApprovalStatus get() = _approvalStatus ?: ApprovalStatus.PENDING
}

enum class RepoCategory {
    R1, R2, R3, R4, R5
}

enum class ApprovalStatus {
    PENDING, APPROVED, REJECTED
}

// --- 3. Apex Kiri Organization (AAL) & LMS ---

data class AalOnboardingDto(
    @SerializedName("aal_id") val aalId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("mindset_score") val mindsetScore: String? = null,
    @SerializedName("lms_status") val _lmsStatus: LmsStatus? = LmsStatus.ENROLLED,
    @SerializedName("certificate_url") val certificateUrl: String? = null,
    @SerializedName("interview_status") val _interviewStatus: InterviewStatus? = InterviewStatus.PENDING
) {
    val lmsStatus: LmsStatus get() = _lmsStatus ?: LmsStatus.ENROLLED
    val interviewStatus: InterviewStatus get() = _interviewStatus ?: InterviewStatus.PENDING
}

enum class LmsStatus {
    ENROLLED, COMPLETED
}

enum class InterviewStatus {
    PENDING, PASSED, FAILED
}

data class AalActivityDto(
    @SerializedName("activity_id") val activityId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("activity_number") val activityNumber: Int? = 0,
    @SerializedName("submission_url") val submissionUrl: String? = null,
    @SerializedName("status") val _status: ActivityStatus? = ActivityStatus.SUBMITTED
) {
    val status: ActivityStatus get() = _status ?: ActivityStatus.SUBMITTED
}

enum class ActivityStatus {
    SUBMITTED, VERIFIED
}

// --- 4. Events, Hackathons & Workflows ---

data class AalEventDto(
    @SerializedName("event_id") val eventId: String? = null,
    val title: String? = null,
    val _type: EventType? = EventType.MEETUP,
    @SerializedName("host_institution_id") val hostInstitutionId: String? = null,
    @SerializedName("start_time") val startTime: String? = null,
    val location: String? = null,
    @SerializedName("qr_base_url") val qrBaseUrl: String? = null
) {
    val eventType: EventType get() = _type ?: EventType.MEETUP
}

enum class EventType {
    MEETUP, HACKATHON, QUIZ, OPEN_DAY
}

data class EventRegistrationDto(
    @SerializedName("registration_id") val registrationId: String? = null,
    @SerializedName("event_id") val eventId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    val _status: RegistrationStatus? = RegistrationStatus.REGISTERED,
    @SerializedName("form_data") val formData: String? = null,
    @SerializedName("qr_scanned_at") val qrScannedAt: String? = null
) {
    val status: RegistrationStatus get() = _status ?: RegistrationStatus.REGISTERED
}

enum class RegistrationStatus {
    REGISTERED, WAITLIST, ATTENDED
}

data class EventTeamDto(
    @SerializedName("team_id") val teamId: String? = null,
    @SerializedName("event_id") val eventId: String? = null,
    @SerializedName("team_name") val teamName: String? = null,
    @SerializedName("ai_generated") val aiGenerated: Boolean? = false
)

data class EventSubmissionDto(
    @SerializedName("submission_id") val submissionId: String? = null,
    @SerializedName("event_id") val eventId: String? = null,
    @SerializedName("team_id") val teamId: String? = null,
    @SerializedName("document_url") val documentUrl: String? = null,
    @SerializedName("geo_lat") val geoLat: Double? = null,
    @SerializedName("geo_long") val geoLong: Double? = null,
    @SerializedName("ai_authenticity_score") val aiAuthenticityScore: Float? = null
)

data class JudgingRecordDto(
    @SerializedName("judge_record_id") val judgeRecordId: String? = null,
    @SerializedName("event_id") val eventId: String? = null,
    @SerializedName("team_id") val teamId: String? = null,
    @SerializedName("judge_user_id") val judgeUserId: String? = null,
    @SerializedName("is_ai_judge") val isAiJudge: Boolean? = false,
    val score: Float? = 0f,
    val feedback: String? = null
)

// --- 5. The AI Core (Live Inputs & Mapping) ---

data class LiveInputDto(
    @SerializedName("input_id") val inputId: String? = null,
    @SerializedName("user_id") val userId: String? = null,
    @SerializedName("format_type") val _formatType: InputFormat? = InputFormat.TEXT,
    @SerializedName("content_url") val contentUrl: String? = null,
    val _context: InputContext? = InputContext.DAY_TO_DAY,
    @SerializedName("ai_processed") val aiProcessed: Boolean? = false
) {
    val formatType: InputFormat get() = _formatType ?: InputFormat.TEXT
    val context: InputContext get() = _context ?: InputContext.DAY_TO_DAY
}

enum class InputFormat {
    TEXT, VOICE, VIDEO, IMAGE
}

enum class InputContext {
    DAY_TO_DAY, ACHIEVEMENT, FAILURE, STUCK, ASK_FOR_HELP
}

data class AiResourceMatchDto(
    @SerializedName("match_id") val matchId: String? = null,
    @SerializedName("source_user_id") val sourceUserId: String? = null,
    @SerializedName("target_user_id") val targetUserId: String? = null,
    @SerializedName("match_reason") val matchReason: String? = null,
    val _status: MatchStatus? = MatchStatus.SUGGESTED
) {
    val status: MatchStatus get() = _status ?: MatchStatus.SUGGESTED
}

enum class MatchStatus {
    SUGGESTED, ACCEPTED, REJECTED
}

// --- 6. Ecosystem Dashboard (News, Jobs, Asks) ---

data class EcosystemBoardDto(
    @SerializedName("board_id") val boardId: String? = null,
    @SerializedName("author_user_id") val authorUserId: String? = null,
    @SerializedName("post_type") val _postType: PostType? = PostType.NEWS,
    val title: String? = null,
    val description: String? = null,
    @SerializedName("media_url") val mediaUrl: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
) {
    val postType: PostType get() = _postType ?: PostType.NEWS
}

enum class PostType {
    NEWS, WALL_OF_FAME, ASK, PRODUCT, ANNOUNCEMENT
}

data class JobProjectDto(
    @SerializedName("listing_id") val listingId: String? = null,
    @SerializedName("posted_by") val postedBy: String? = null,
    val _type: JobType? = JobType.JOB,
    val title: String? = null,
    val description: String? = null,
    val _status: JobStatus? = JobStatus.OPEN
) {
    val type: JobType get() = _type ?: JobType.JOB
    val status: JobStatus get() = _status ?: JobStatus.OPEN
}

enum class JobType {
    JOB, INTERNSHIP, RESEARCH, CASE_STUDY
}

enum class JobStatus {
    OPEN, CLOSED
}
