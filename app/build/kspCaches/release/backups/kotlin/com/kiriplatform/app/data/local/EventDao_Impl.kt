package com.kiriplatform.app.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class EventDao_Impl(
  __db: RoomDatabase,
) : EventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEventEntity: EntityInsertAdapter<EventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfEventEntity = object : EntityInsertAdapter<EventEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `cached_events` (`id`,`title`,`type`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EventEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.type)
      }
    }
  }

  public override suspend fun insertEvents(events: List<EventEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEventEntity.insert(_connection, events)
  }

  public override suspend fun getAllEvents(): List<EventEntity> {
    val _sql: String = "SELECT * FROM cached_events"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _result: MutableList<EventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EventEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          _item = EventEntity(_tmpId,_tmpTitle,_tmpType)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
