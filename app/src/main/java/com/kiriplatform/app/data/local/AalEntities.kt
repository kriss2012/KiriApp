package com.kiriplatform.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kiriplatform.app.data.remote.models.*

/**
 * Room Entities mapping to the ASG/AAL Architecture.
 * Reflects the "Data Vault / History (Immutable)" and "MDM" layers from the architecture diagram.
 */

@Entity(tableName = "users_vault")
data class AalUserEntity(
    @PrimaryKey val userId: String,
    val fullName: String? = "Kiri Member",
    val email: String? = "",
    val phone: String?,
    val userCategory: String? = "STUDENT",
    val digitalPersona: String?,
    val avatarUrl: String? = null,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "institutions_mdm")
data class InstitutionEntity(
    @PrimaryKey val institutionId: String,
    val name: String,
    val spocUserId: String
)

@Entity(tableName = "aal_activities_history")
data class AalActivityEntity(
    @PrimaryKey val activityId: String,
    val userId: String,
    val activityNumber: Int,
    val submissionUrl: String?,
    val status: String,
    val syncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ecosystem_board_cache")
data class EcosystemBoardEntity(
    @PrimaryKey val boardId: String,
    val authorUserId: String,
    val postType: String,
    val title: String,
    val description: String,
    val mediaUrl: String?,
    val createdAt: String
)

@Entity(tableName = "ai_matches_vault")
data class AiResourceMatchEntity(
    @PrimaryKey val matchId: String,
    val sourceUserId: String,
    val targetUserId: String,
    val matchReason: String,
    val status: String
)

@Entity(tableName = "jobs_projects_mdm")
data class JobProjectEntity(
    @PrimaryKey val listingId: String,
    val postedBy: String,
    val type: String,
    val title: String,
    val description: String,
    val status: String
)
