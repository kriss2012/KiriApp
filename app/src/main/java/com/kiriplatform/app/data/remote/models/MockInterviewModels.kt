package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

data class MockInterviewMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class MockInterviewRequest(
    @SerializedName("role") val role: String,
    @SerializedName("messages") val messages: List<MockInterviewMessage>,
    @SerializedName("feedbackRequested") val feedbackRequested: Boolean
)

data class MockInterviewResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("response") val response: String
)

data class InterviewFeedback(
    @SerializedName("technical") val technical: String,
    @SerializedName("communication") val communication: String,
    @SerializedName("problemSolving") val problemSolving: String
)

data class InterviewEvaluation(
    @SerializedName("overallScore") val overallScore: Int,
    @SerializedName("confidenceScore") val confidenceScore: Int,
    @SerializedName("feedback") val feedback: InterviewFeedback,
    @SerializedName("suggestions") val suggestions: List<String>
)
