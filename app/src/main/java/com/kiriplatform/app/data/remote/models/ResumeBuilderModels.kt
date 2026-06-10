package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

data class ResumeEducation(
    @SerializedName("school") val school: String,
    @SerializedName("degree") val degree: String,
    @SerializedName("year") val year: String,
    @SerializedName("gpa") val gpa: String
)

data class ResumeExperience(
    @SerializedName("company") val company: String,
    @SerializedName("role") val role: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("description") val description: String
)

data class ResumeProject(
    @SerializedName("name") val name: String,
    @SerializedName("techStack") val techStack: String,
    @SerializedName("description") val description: String
)

data class OptimizedExperience(
    @SerializedName("company") val company: String,
    @SerializedName("role") val role: String,
    @SerializedName("duration") val duration: String,
    @SerializedName("bullets") val bullets: List<String>
)

data class OptimizedProject(
    @SerializedName("name") val name: String,
    @SerializedName("techStack") val techStack: String,
    @SerializedName("bullets") val bullets: List<String>
)

data class OptimizedResume(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("linkedInUrl") val linkedInUrl: String,
    @SerializedName("githubUrl") val githubUrl: String,
    @SerializedName("optimizedSummary") val optimizedSummary: String,
    @SerializedName("education") val education: List<ResumeEducation>,
    @SerializedName("experience") val experience: List<OptimizedExperience>,
    @SerializedName("projects") val projects: List<OptimizedProject>,
    @SerializedName("skills") val skills: List<String>,
    @SerializedName("atsScore") val atsScore: Int
)

data class ResumeGenerateRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("linkedInUrl") val linkedInUrl: String,
    @SerializedName("githubUrl") val githubUrl: String,
    @SerializedName("education") val education: List<ResumeEducation>,
    @SerializedName("experience") val experience: List<ResumeExperience>,
    @SerializedName("projects") val projects: List<ResumeProject>,
    @SerializedName("skills") val skills: List<String>,
    @SerializedName("targetRole") val targetRole: String
)

data class ResumeGenerateResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("resume") val resume: OptimizedResume
)
