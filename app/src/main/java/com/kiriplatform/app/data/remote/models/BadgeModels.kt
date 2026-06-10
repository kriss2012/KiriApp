package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

data class BadgeDto(
    @SerializedName("id") val id: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("badgeType") val badgeType: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("earnedAt") val earnedAt: String
)

data class BadgeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("badges") val badges: List<BadgeDto>
)

data class AwardBadgeRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("badgeType") val badgeType: String,
    @SerializedName("icon") val icon: String
)

data class AwardBadgeResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("badge") val badge: BadgeDto?
)
