package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

data class UserMinimalDto(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("avatarUrl") val avatarUrl: String? = null
)

data class ProjectReviewDto(
    @SerializedName("id") val id: String,
    @SerializedName("projectId") val projectId: String,
    @SerializedName("reviewerId") val reviewerId: String,
    @SerializedName("reviewer") val reviewer: UserMinimalDto,
    @SerializedName("codeQuality") val codeQuality: Int,
    @SerializedName("documentation") val documentation: Int,
    @SerializedName("architecture") val architecture: Int,
    @SerializedName("innovation") val innovation: Int,
    @SerializedName("comment") val comment: String,
    @SerializedName("createdAt") val createdAt: String
)

data class ProjectShowcaseDto(
    @SerializedName("id") val id: String,
    @SerializedName("studentId") val studentId: String,
    @SerializedName("student") val student: UserMinimalDto,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("githubUrl") val githubUrl: String,
    @SerializedName("techStack") val techStack: List<String>,
    @SerializedName("isFeatured") val isFeatured: Boolean,
    @SerializedName("reviews") val reviews: List<ProjectReviewDto>,
    @SerializedName("createdAt") val createdAt: String
)

data class ProjectListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("projects") val projects: List<ProjectShowcaseDto>
)

data class ProjectSubmitRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("githubUrl") val githubUrl: String,
    @SerializedName("techStack") val techStack: List<String>
)

data class ProjectSubmitResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("project") val project: ProjectShowcaseDto?
)

data class ProjectReviewRequest(
    @SerializedName("codeQuality") val codeQuality: Int,
    @SerializedName("documentation") val documentation: Int,
    @SerializedName("architecture") val architecture: Int,
    @SerializedName("innovation") val innovation: Int,
    @SerializedName("comment") val comment: String
)

data class ProjectReviewResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("review") val review: ProjectReviewDto?
)
