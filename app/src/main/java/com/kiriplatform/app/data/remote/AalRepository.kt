package com.kiriplatform.app.data.remote

import com.kiriplatform.app.data.remote.models.*
import com.kiriplatform.app.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for handling APEX Kiri Organization (AAL) and ASG Ecosystem data operations.
 * Implements the "workings" for the data structures defined in documentation.
 * Now enhanced with Room caching for offline-first architecture (Data Vault).
 */
class AalRepository(
    private val apiService: ASGApiService = ApiClient.service,
    private val aalDao: AalDao
) {

    /**
     * Fetches the AAL onboarding status for a user.
     */
    fun getOnboarding(userId: String): Flow<AalOnboardingDto> = flow {
        emit(apiService.getAalOnboarding(userId))
    }

    /**
     * Fetches activities for a specific intern with local caching.
     */
    fun getActivities(userId: String): Flow<List<AalActivityDto>> = flow {
        // 1. Emit from cache first
        val cached = aalDao.getActivities(userId).map { it.toDto() }
        if (cached.isNotEmpty()) emit(cached)

        // 2. Fetch from network
        try {
            val remote = apiService.getAalActivities(userId)
            // 3. Save to cache (Data Vault)
            aalDao.insertActivities(remote.map { it.toEntity() })
            // 4. Emit fresh data
            emit(remote)
        } catch (e: Exception) {
            // Log or handle error, cache is already emitted
        }
    }

    /**
     * Submits an AAL activity (1-7).
     */
    suspend fun submitActivity(activity: AalActivityDto): AalActivityDto {
        return apiService.submitAalActivity(activity)
    }

    /**
     * Fetches institutional data.
     */
    fun getInstitutions(): Flow<List<InstitutionDto>> = flow {
        emit(apiService.getInstitutions())
    }

    /**
     * Fetches AAL specific events and hackathons.
     */
    fun getEvents(): Flow<List<AalEventDto>> = flow {
        emit(apiService.getAalEvents())
    }

    /**
     * Registers a user for an event with dynamic form data.
     */
    suspend fun registerForEvent(registration: EventRegistrationDto): EventRegistrationDto {
        return apiService.registerForEvent(registration)
    }

    /**
     * Submits a live input (voice, text, video) for AI processing.
     * This triggers the background AI matching engine.
     */
    suspend fun submitLiveInput(input: LiveInputDto): LiveInputDto {
        return apiService.submitLiveInput(input)
    }

    /**
     * Retrieves AI-generated resource matches for a user.
     */
    fun getAiMatches(userId: String): Flow<List<AiResourceMatchDto>> = flow {
        emit(apiService.getAiMatches(userId))
    }

    /**
     * Fetches the Ecosystem Board (News, Wall of Fame, Asks).
     */
    fun getBoard(): Flow<List<EcosystemBoardDto>> = flow {
        val cached = aalDao.getBoardItems().map { it.toDto() }
        if (cached.isNotEmpty()) emit(cached)

        try {
            val remote = apiService.getEcosystemBoard()
            aalDao.insertBoardItems(remote.map { it.toEntity() })
            emit(remote)
        } catch (e: Exception) {}
    }

    /**
     * Fetches open Jobs and Research Projects.
     */
    fun getJobsAndProjects(): Flow<List<JobProjectDto>> = flow {
        val cached = aalDao.getOpenJobs().map { it.toDto() }
        if (cached.isNotEmpty()) emit(cached)

        try {
            val remote = apiService.getJobsProjects()
            aalDao.insertJobs(remote.map { it.toEntity() })
            emit(remote)
        } catch (e: Exception) {}
    }

    // --- Mappers for Data Transformation Layer ---

    private fun AalActivityDto.toEntity() = AalActivityEntity(
        activityId = activityId?.toString() ?: "",
        userId = userId?.toString() ?: "",
        activityNumber = activityNumber ?: 0,
        submissionUrl = submissionUrl?.toString(),
        status = status?.name ?: "SUBMITTED"
    )

    private fun AalActivityEntity.toDto() = AalActivityDto(
        activityId = activityId,
        userId = userId,
        activityNumber = activityNumber,
        submissionUrl = submissionUrl,
        status = safeValueOf<ActivityStatus>(status, ActivityStatus.SUBMITTED)
    )

    private fun EcosystemBoardDto.toEntity() = EcosystemBoardEntity(
        boardId = boardId?.toString() ?: "",
        authorUserId = authorUserId?.toString() ?: "",
        postType = postType?.name ?: "NEWS",
        title = title?.toString() ?: "",
        description = description?.toString() ?: "",
        mediaUrl = mediaUrl?.toString(),
        createdAt = createdAt?.toString() ?: ""
    )

    private fun EcosystemBoardEntity.toDto() = EcosystemBoardDto(
        boardId = boardId,
        authorUserId = authorUserId,
        postType = safeValueOf<PostType>(postType, PostType.NEWS),
        title = title,
        description = description,
        mediaUrl = mediaUrl,
        createdAt = createdAt
    )

    private fun JobProjectDto.toEntity() = JobProjectEntity(
        listingId = listingId?.toString() ?: "",
        postedBy = postedBy?.toString() ?: "",
        type = type?.name ?: "JOB",
        title = title?.toString() ?: "",
        description = description?.toString() ?: "",
        status = status?.name ?: "OPEN"
    )

    private fun JobProjectEntity.toDto() = JobProjectDto(
        listingId = listingId,
        postedBy = postedBy,
        type = safeValueOf<JobType>(type, JobType.JOB),
        title = title,
        description = description,
        status = safeValueOf<JobStatus>(status, JobStatus.OPEN)
    )

    private inline fun <reified T : Enum<T>> safeValueOf(value: String, default: T): T {
        return try {
            java.lang.Enum.valueOf(T::class.java, value.uppercase())
        } catch (e: Exception) {
            default
        }
    }
}
