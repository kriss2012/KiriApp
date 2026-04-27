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
    @PrimaryKey val userId: Int,
    val fullName: String,
    val email: String,
    val phone: String?,
    val userCategory: String,
    val digitalPersona: String?,
    val lastSyncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "institutions_mdm")
data class InstitutionEntity(
    @PrimaryKey val institutionId: Int,
    val name: String,
    val spocUserId: Int
)

@Entity(tableName = "aal_activities_history")
data class AalActivityEntity(
    @PrimaryKey val activityId: Int,
    val userId: Int,
    val activityNumber: Int,
    val submissionUrl: String,
    val status: String,
    val syncedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ecosystem_board_cache")
data class EcosystemBoardEntity(
    @PrimaryKey val boardId: Int,
    val authorUserId: Int,
    val postType: String,
    val title: String,
    val description: String,
    val mediaUrl: String?,
    val createdAt: String
)

@Entity(tableName = "ai_matches_vault")
data class AiResourceMatchEntity(
    @PrimaryKey val matchId: Int,
    val sourceUserId: Int,
    val targetUserId: Int,
    val matchReason: String,
    val status: String
)

@Entity(tableName = "jobs_projects_mdm")
data class JobProjectEntity(
    @PrimaryKey val listingId: Int,
    val postedBy: Int,
    val type: String,
    val title: String,
    val description: String,
    val status: String
)
