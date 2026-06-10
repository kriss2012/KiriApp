package com.kiriplatform.app.data.remote.models

import com.google.gson.annotations.SerializedName

data class MarketSkillTrend(
    @SerializedName("id") val id: String,
    @SerializedName("skillName") val skillName: String,
    @SerializedName("category") val category: String,
    @SerializedName("demand") val demand: Float,
    @SerializedName("changePct") val changePct: Float,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class MarketSalaryTrend(
    @SerializedName("id") val id: String,
    @SerializedName("roleName") val roleName: String,
    @SerializedName("city") val city: String,
    @SerializedName("minSalary") val minSalary: Float,
    @SerializedName("maxSalary") val maxSalary: Float,
    @SerializedName("avgSalary") val avgSalary: Float,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class TopHiringCompany(
    @SerializedName("id") val id: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("openingsCount") val openingsCount: Int,
    @SerializedName("logoUrl") val logoUrl: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class MarketTrendsResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("fallback") val fallback: Boolean? = false,
    @SerializedName("skillTrends") val skillTrends: List<MarketSkillTrend> = emptyList(),
    @SerializedName("salaryTrends") val salaryTrends: List<MarketSalaryTrend> = emptyList(),
    @SerializedName("topCompanies") val topCompanies: List<TopHiringCompany> = emptyList()
)
