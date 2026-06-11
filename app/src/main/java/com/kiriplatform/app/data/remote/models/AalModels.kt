package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * Data structures for APEX Kiri Organization (AAL) and ASG Ecosystem.
 * Mapped from the asg_aal_datastructure.md documentation.
 * Refactored for MAXIMUM null safety.
 */

// --- 1. Core Users & Roles ---

data class AalUserDto(
    @SerializedName("user_id") val _userId: Any? = null,
    @SerializedName("full_name") val _fullName: Any? = null,
    val email: Any? = null,
    val phone: Any? = null,
    @SerializedName("user_category") val _userCategory: UserCategory? = UserCategory.STUDENT,
    @SerializedName("digital_persona") val _digitalPersona: Any? = null,
    @SerializedName("created_at") val _createdAt: Any? = null
) {
    val userId: String get() = _userId?.toString() ?: ""
    val fullName: String get() = _fullName?.toString() ?: "Kiri Member"
    val userCategory: UserCategory get() = _userCategory ?: UserCategory.STUDENT
    val createdAt: String get() = _createdAt?.toString() ?: ""
    val digitalPersona: String get() = _digitalPersona?.toString() ?: ""
}

enum class UserCategory {
    STUDENT, NON_STUDENT, ALUMNI, FACULTY
}

data class StakeholderRoleDto(
    @SerializedName("role_mapping_id") val _roleMappingId: Any? = null,
    @SerializedName("user_id") val _userId: Any? = null,
    @SerializedName("role_name") val _roleName: StakeholderRole? = StakeholderRole.GUEST
) {
    val roleMappingId: String get() = _roleMappingId?.toString() ?: ""
    val userId: String get() = _userId?.toString() ?: ""
    val roleName: StakeholderRole get() = _roleName ?: StakeholderRole.GUEST
}

enum class StakeholderRole {
    FOUNDER, SERVICE_PROVIDER, MENTOR, INVESTOR, INCUBATOR, GUEST
}

// --- 2. Institutions, Committees & Repositories (R1-R5) ---

data class InstitutionDto(
    @SerializedName("institution_id") val _institutionId: Any? = null,
    @SerializedName("name") val _name: Any? = null,
    @SerializedName("spoc_user_id") val _spocUserId: Any? = null
) {
    val institutionId: String get() = _institutionId?.toString() ?: ""
    val institutionName: String get() = _name?.toString() ?: "Kiri Institution"
    val spocUserId: String get() = _spocUserId?.toString() ?: ""
}

data class InstitutionalCommitteeDto(
    @SerializedName("committee_id") val _committeeId: Any? = null,
    @SerializedName("institution_id") val _institutionId: Any? = null,
    @SerializedName("user_id") val _userId: Any? = null,
    val department: Any? = null,
    @SerializedName("role_type") val _roleType: CommitteeRoleType? = CommitteeRoleType.STUDENT_REP
) {
    val committeeId: String get() = _committeeId?.toString() ?: ""
    val institutionId: String get() = _institutionId?.toString() ?: ""
    val userId: String get() = _userId?.toString() ?: ""
    val roleType: CommitteeRoleType get() = _roleType ?: CommitteeRoleType.STUDENT_REP
}

enum class CommitteeRoleType {
    FACULTY_REP, STUDENT_REP
}

data class UserRepositoryDto(
    @SerializedName("repo_mapping_id") val _repoMappingId: Any? = null,
    @SerializedName("user_id") val _userId: Any? = null,
    @SerializedName("institution_id") val _institutionId: Any? = null,
    @SerializedName("repo_category") val _repoCategory: RepoCategory? = RepoCategory.R1,
    @SerializedName("approval_status") val _approvalStatus: ApprovalStatus? = ApprovalStatus.PENDING,
    @SerializedName("approved_by") val _approvedBy: Any? = null
) {
    val repoMappingId: String get() = _repoMappingId?.toString() ?: ""
    val userId: String get() = _userId?.toString() ?: ""
    val institutionId: String get() = _institutionId?.toString() ?: ""
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
    @SerializedName("aal_id") val _aalId: Any? = null,
    @SerializedName("user_id") val _userId: Any? = null,
    @SerializedName("mindset_score") val _mindsetScore: Any? = null,
    @SerializedName("lms_status") val _lmsStatus: LmsStatus? = LmsStatus.ENROLLED,
    @SerializedName("certificate_url") val _certificateUrl: Any? = null,
    @SerializedName("interview_status") val _interviewStatus: InterviewStatus? = InterviewStatus.PENDING
) {
    val aalId: String get() = _aalId?.toString() ?: ""
    val userId: String get() = _userId?.toString() ?: ""
    val mindsetScore: String get() = _mindsetScore?.toString() ?: ""
    val lmsStatus: LmsStatus get() = _lmsStatus ?: LmsStatus.ENROLLED
    val interviewStatus: InterviewStatus get() = _interviewStatus ?: InterviewStatus.PENDING
}

enum class LmsStatus {
    ENROLLED, COMPLETED
}

enum class InterviewStatus {
    PENDING, PASSED, FAILED
}

class AalActivityDto(
    @SerializedName("activity_id") val _activityId: Any? = null,
    @SerializedName("user_id") val _userId: Any? = null,
    @SerializedName("activity_number") val activityNumber: Int? = 0,
    @SerializedName("submission_url") val _submissionUrl: Any? = null,
    @SerializedName("status") val _status: ActivityStatus? = ActivityStatus.SUBMITTED
) {
    val activityId: String get() = _activityId?.toString() ?: ""
    val userId: String get() = _userId?.toString() ?: ""
    val submissionUrl: String? get() = _submissionUrl?.toString()
    val status: ActivityStatus get() = _status ?: ActivityStatus.SUBMITTED

    fun copy(
        activityId: Any? = _activityId,
        userId: Any? = _userId,
        activityNumber: Int? = this.activityNumber,
        submissionUrl: Any? = _submissionUrl,
        status: ActivityStatus? = _status
    ) = AalActivityDto(
        _activityId = activityId,
        _userId = userId,
        activityNumber = activityNumber,
        _submissionUrl = submissionUrl,
        _status = status
    )
}

enum class ActivityStatus {
    SUBMITTED, VERIFIED
}

// --- 4. Events, Hackathons & Workflows ---

data class AalEventDto(
    @SerializedName("event_id") val _eventId: Any? = null,
    @SerializedName("title") val _title: Any? = null,
    @SerializedName("type") val _type: EventType? = EventType.MEETUP,
    @SerializedName("host_institution_id") val _hostInstitutionId: Any? = null,
    @SerializedName("start_time") val _startTime: Any? = null,
    @SerializedName("location") val _location: Any? = null,
    @SerializedName("qr_base_url") val _qrBaseUrl: Any? = null
) {
    val eventId: String get() = _eventId?.toString() ?: ""
    val title: String get() = _title?.toString() ?: ""
    val eventType: EventType get() = _type ?: EventType.MEETUP
}

enum class EventType {
    MEETUP, HACKATHON, QUIZ, OPEN_DAY
}

data class EventRegistrationDto(
    @SerializedName("registration_id") val _registrationId: Any? = null,
    @SerializedName("event_id") val _eventId: Any? = null,
    @SerializedName("user_id") val _userId: Any? = null,
    @SerializedName("status") val _status: RegistrationStatus? = RegistrationStatus.REGISTERED,
    @SerializedName("form_data") val _formData: Any? = null,
    @SerializedName("qr_scanned_at") val _qrScannedAt: Any? = null
) {
    val registrationId: String get() = _registrationId?.toString() ?: ""
    val eventId: String get() = _eventId?.toString() ?: ""
    val userId: String get() = _userId?.toString() ?: ""
    val status: RegistrationStatus get() = _status ?: RegistrationStatus.REGISTERED
}

enum class RegistrationStatus {
    REGISTERED, WAITLIST, ATTENDED
}

data class EventTeamDto(
    @SerializedName("team_id") val _teamId: Any? = null,
    @SerializedName("event_id") val _eventId: Any? = null,
    @SerializedName("team_name") val _teamName: Any? = null,
    @SerializedName("ai_generated") val aiGenerated: Boolean? = false
) {
    val teamId: String get() = _teamId?.toString() ?: ""
    val eventId: String get() = _eventId?.toString() ?: ""
    val teamName: String get() = _teamName?.toString() ?: ""
}

data class EventSubmissionDto(
    @SerializedName("submission_id") val _submissionId: Any? = null,
    @SerializedName("event_id") val _eventId: Any? = null,
    @SerializedName("team_id") val _teamId: Any? = null,
    @SerializedName("document_url") val _documentUrl: Any? = null,
    @SerializedName("geo_lat") val geoLat: Double? = null,
    @SerializedName("geo_long") val geoLong: Double? = null,
    @SerializedName("ai_authenticity_score") val aiAuthenticityScore: Float? = null
) {
    val submissionId: String get() = _submissionId?.toString() ?: ""
    val eventId: String get() = _eventId?.toString() ?: ""
    val teamId: String get() = _teamId?.toString() ?: ""
    val documentUrl: String get() = _documentUrl?.toString() ?: ""
}

data class JudgingRecordDto(
    @SerializedName("judge_record_id") val _judgeRecordId: Any? = null,
    @SerializedName("event_id") val _eventId: Any? = null,
    @SerializedName("team_id") val _teamId: Any? = null,
    @SerializedName("judge_user_id") val _judgeUserId: Any? = null,
    @SerializedName("is_ai_judge") val isAiJudge: Boolean? = false,
    val score: Float? = 0f,
    val feedback: Any? = null
) {
    val judgeRecordId: String get() = _judgeRecordId?.toString() ?: ""
    val eventId: String get() = _eventId?.toString() ?: ""
    val teamId: String get() = _teamId?.toString() ?: ""
    val judgeUserId: String get() = _judgeUserId?.toString() ?: ""
    val feedbackText: String get() = feedback?.toString() ?: ""
}

// --- 5. The AI Core (Live Inputs & Mapping) ---

class LiveInputDto(
    @SerializedName("input_id") val _inputId: Any? = null,
    @SerializedName("user_id") val _userId: Any? = null,
    @SerializedName("format_type") val _formatType: InputFormat? = InputFormat.TEXT,
    @SerializedName("content_url") val _contentUrl: Any? = null,
    @SerializedName("context") val _context: InputContext? = InputContext.DAY_TO_DAY,
    @SerializedName("ai_processed") val aiProcessed: Boolean? = false
) {
    val inputId: String get() = _inputId?.toString() ?: ""
    val userId: String get() = _userId?.toString() ?: ""
    val formatType: InputFormat get() = _formatType ?: InputFormat.TEXT
    val context: InputContext get() = _context ?: InputContext.DAY_TO_DAY
    val contentUrl: String? get() = _contentUrl?.toString()

    fun copy(
        inputId: Any? = _inputId,
        userId: Any? = _userId,
        formatType: InputFormat? = _formatType,
        contentUrl: Any? = _contentUrl,
        context: InputContext? = _context,
        aiProcessed: Boolean? = this.aiProcessed
    ) = LiveInputDto(
        _inputId = inputId,
        _userId = userId,
        _formatType = formatType,
        _contentUrl = contentUrl,
        _context = context,
        aiProcessed = aiProcessed
    )
}

enum class InputFormat {
    TEXT, VOICE, VIDEO, IMAGE
}

enum class InputContext {
    DAY_TO_DAY, ACHIEVEMENT, FAILURE, STUCK, ASK_FOR_HELP
}

data class AiResourceMatchDto(
    @SerializedName("match_id") val _matchId: Any? = null,
    @SerializedName("source_user_id") val _sourceUserId: Any? = null,
    @SerializedName("target_user_id") val _targetUserId: Any? = null,
    @SerializedName("match_reason") val _matchReason: Any? = null,
    @SerializedName("status") val _status: MatchStatus? = MatchStatus.SUGGESTED
) {
    val matchId: String get() = _matchId?.toString() ?: ""
    val sourceUserId: String get() = _sourceUserId?.toString() ?: ""
    val targetUserId: String get() = _targetUserId?.toString() ?: ""
    val matchReason: String get() = _matchReason?.toString() ?: ""
    val status: MatchStatus get() = _status ?: MatchStatus.SUGGESTED
}

enum class MatchStatus {
    SUGGESTED, ACCEPTED, REJECTED
}

// --- 6. Ecosystem Dashboard (News, Jobs, Asks) ---

data class EcosystemBoardDto(
    @SerializedName("board_id") val _boardId: Any? = null,
    @SerializedName("author_user_id") val _authorUserId: Any? = null,
    @SerializedName("post_type") val _postType: PostType? = PostType.NEWS,
    @SerializedName("title") val _title: Any? = null,
    @SerializedName("description") val _description: Any? = null,
    @SerializedName("media_url") val _mediaUrl: Any? = null,
    @SerializedName("created_at") val _createdAt: Any? = null
) {
    val boardId: String get() = _boardId?.toString() ?: ""
    val authorUserId: String get() = _authorUserId?.toString() ?: ""
    val postType: PostType get() = _postType ?: PostType.NEWS
    val title: String get() = _title?.toString() ?: ""
    val description: String get() = _description?.toString() ?: ""
    val mediaUrl: String? get() = _mediaUrl?.toString()
    val createdAt: String get() = _createdAt?.toString() ?: ""
}

enum class PostType {
    NEWS, WALL_OF_FAME, ASK, PRODUCT, ANNOUNCEMENT
}

data class JobProjectDto(
    @SerializedName("listing_id") val _listingId: Any? = null,
    @SerializedName("posted_by") val _postedBy: Any? = null,
    @SerializedName("type") val _type: JobType? = JobType.JOB,
    @SerializedName("title") val _title: Any? = null,
    @SerializedName("description") val _description: Any? = null,
    @SerializedName("status") val _status: JobStatus? = JobStatus.OPEN
) {
    val listingId: String get() = _listingId?.toString() ?: ""
    val postedBy: String get() = _postedBy?.toString() ?: ""
    val type: JobType get() = _type ?: JobType.JOB
    val title: String get() = _title?.toString() ?: ""
    val description: String get() = _description?.toString() ?: ""
    val status: JobStatus get() = _status ?: JobStatus.OPEN
}

enum class JobType {
    JOB, INTERNSHIP, RESEARCH, CASE_STUDY
}

enum class JobStatus {
    OPEN, CLOSED
}
