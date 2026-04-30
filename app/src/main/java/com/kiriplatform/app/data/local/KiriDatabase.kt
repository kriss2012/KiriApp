package com.kiriplatform.app.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// Demonstrating Caching strategy mentioned in System Design images
@Entity(tableName = "cached_events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val type: String
)

@Dao
interface EventDao {
    @Query("SELECT * FROM cached_events")
    suspend fun getAllEvents(): List<EventEntity>

    @Insert
    suspend fun insertEvents(events: List<EventEntity>)
}

@Database(
    entities = [
        EventEntity::class,
        AalUserEntity::class,
        InstitutionEntity::class,
        AalActivityEntity::class,
        EcosystemBoardEntity::class,
        AiResourceMatchEntity::class,
        JobProjectEntity::class
    ],
    version = 2, // Incremented version for the new structure
    exportSchema = false
)
abstract class KiriDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun aalDao(): AalDao
}

@Dao
interface AalDao {
    @Query("SELECT * FROM users_vault WHERE userId = :userId")
    suspend fun getUser(userId: String): AalUserEntity?

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AalUserEntity)

    @Query("SELECT * FROM institutions_mdm")
    suspend fun getAllInstitutions(): List<InstitutionEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertInstitutions(institutions: List<InstitutionEntity>)

    @Query("SELECT * FROM aal_activities_history WHERE userId = :userId")
    suspend fun getActivities(userId: String): List<AalActivityEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<AalActivityEntity>)

    @Query("SELECT * FROM ecosystem_board_cache ORDER BY createdAt DESC")
    suspend fun getBoardItems(): List<EcosystemBoardEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertBoardItems(items: List<EcosystemBoardEntity>)

    @Query("SELECT * FROM ai_matches_vault WHERE sourceUserId = :userId OR targetUserId = :userId")
    suspend fun getMatches(userId: String): List<AiResourceMatchEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<AiResourceMatchEntity>)

    @Query("SELECT * FROM jobs_projects_mdm WHERE status = 'OPEN'")
    suspend fun getOpenJobs(): List<JobProjectEntity>

    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobProjectEntity>)
}
