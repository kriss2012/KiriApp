package com.apex.asg.data.local

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

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class ASGDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
}
