package com.apex.asg.data.remote

import com.apex.asg.data.remote.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository for handling APEX AI Launchpad (AAL) and ASG Ecosystem data operations.
 * Implements the "workings" for the data structures defined in documentation.
 */
class AalRepository(private val apiService: ASGApiService = ApiClient.service) {

    /**
     * Fetches the AAL onboarding status for a user.
     */
    fun getOnboarding(userId: Int): Flow<AalOnboardingDto> = flow {
        emit(apiService.getAalOnboarding(userId))
    }

    /**
     * Fetches activities for a specific intern.
     */
    fun getActivities(userId: Int): Flow<List<AalActivityDto>> = flow {
        emit(apiService.getAalActivities(userId))
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
    fun getAiMatches(userId: Int): Flow<List<AiResourceMatchDto>> = flow {
        emit(apiService.getAiMatches(userId))
    }

    /**
     * Fetches the Ecosystem Board (News, Wall of Fame, Asks).
     */
    fun getBoard(): Flow<List<EcosystemBoardDto>> = flow {
        emit(apiService.getEcosystemBoard())
    }

    /**
     * Fetches open Jobs and Research Projects.
     */
    fun getJobsAndProjects(): Flow<List<JobProjectDto>> = flow {
        emit(apiService.getJobsProjects())
    }
}
