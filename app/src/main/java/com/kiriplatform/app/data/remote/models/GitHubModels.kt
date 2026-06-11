package com.kiriplatform.app.data.remote.models

data class GitHubStatsResponse(
    val login: String,
    val name: String?,
    val followers: Int,
    val following: Int,
    val public_repos: Int,
    val bio: String?,
    val avatar_url: String?,
    val repos: List<GitHubRepoDto>
)

data class GitHubRepoDto(
    val name: String,
    val description: String?,
    val language: String?,
    val stars: Int,
    val forks: Int,
    val url: String
)

data class GitHubAuthorizeUrlResponse(
    val url: String
)

data class ScoreBreakdownItem(
    val score: Int,
    val weight: Int,
    val starsBonus: Int? = null
)

data class EmployabilityBreakdown(
    val technicalSkills: ScoreBreakdownItem,
    val projects: ScoreBreakdownItem,
    val resume: ScoreBreakdownItem,
    val certifications: ScoreBreakdownItem,
    val mockInterview: ScoreBreakdownItem,
    val softSkills: ScoreBreakdownItem,
    val githubActivity: ScoreBreakdownItem
)

data class EmployabilityScoreResponse(
    val success: Boolean,
    val overallScore: Int,
    val breakdown: EmployabilityBreakdown
)
