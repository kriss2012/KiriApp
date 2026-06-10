package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

data class LeaderboardUserDto(
    @SerializedName("id") val id: String,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("avatarUrl") val avatarUrl: String? = null,
    @SerializedName("points") val points: Int,
    @SerializedName("college") val college: String?,
    @SerializedName("userCategory") val userCategory: String?,
    @SerializedName("rank") val rank: Int,
    @SerializedName("isCampusLead") val isCampusLead: Boolean
)

data class LeaderboardResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("college") val college: String,
    @SerializedName("leaderboard") val leaderboard: List<LeaderboardUserDto>
)

data class ReferralRequest(
    @SerializedName("email") val email: String
)

data class ReferralResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("points") val points: Int
)

data class RedeemRequest(
    @SerializedName("rewardKey") val rewardKey: String
)

data class RedeemResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("points") val points: Int
)
