package com.kiriplatform.app.`data`.local

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AalDao_Impl(
  __db: RoomDatabase,
) : AalDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAalUserEntity: EntityInsertAdapter<AalUserEntity>

  private val __insertAdapterOfInstitutionEntity: EntityInsertAdapter<InstitutionEntity>

  private val __insertAdapterOfAalActivityEntity: EntityInsertAdapter<AalActivityEntity>

  private val __insertAdapterOfEcosystemBoardEntity: EntityInsertAdapter<EcosystemBoardEntity>

  private val __insertAdapterOfAiResourceMatchEntity: EntityInsertAdapter<AiResourceMatchEntity>

  private val __insertAdapterOfJobProjectEntity: EntityInsertAdapter<JobProjectEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAalUserEntity = object : EntityInsertAdapter<AalUserEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `users_vault` (`userId`,`fullName`,`email`,`phone`,`userCategory`,`digitalPersona`,`avatarUrl`,`lastSyncedAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AalUserEntity) {
        statement.bindText(1, entity.userId)
        val _tmpFullName: String? = entity.fullName
        if (_tmpFullName == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpFullName)
        }
        val _tmpEmail: String? = entity.email
        if (_tmpEmail == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpEmail)
        }
        val _tmpPhone: String? = entity.phone
        if (_tmpPhone == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpPhone)
        }
        val _tmpUserCategory: String? = entity.userCategory
        if (_tmpUserCategory == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpUserCategory)
        }
        val _tmpDigitalPersona: String? = entity.digitalPersona
        if (_tmpDigitalPersona == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDigitalPersona)
        }
        val _tmpAvatarUrl: String? = entity.avatarUrl
        if (_tmpAvatarUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpAvatarUrl)
        }
        statement.bindLong(8, entity.lastSyncedAt)
      }
    }
    this.__insertAdapterOfInstitutionEntity = object : EntityInsertAdapter<InstitutionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `institutions_mdm` (`institutionId`,`name`,`spocUserId`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: InstitutionEntity) {
        statement.bindText(1, entity.institutionId)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.spocUserId)
      }
    }
    this.__insertAdapterOfAalActivityEntity = object : EntityInsertAdapter<AalActivityEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `aal_activities_history` (`activityId`,`userId`,`activityNumber`,`submissionUrl`,`status`,`syncedAt`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AalActivityEntity) {
        statement.bindText(1, entity.activityId)
        statement.bindText(2, entity.userId)
        statement.bindLong(3, entity.activityNumber.toLong())
        val _tmpSubmissionUrl: String? = entity.submissionUrl
        if (_tmpSubmissionUrl == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpSubmissionUrl)
        }
        statement.bindText(5, entity.status)
        statement.bindLong(6, entity.syncedAt)
      }
    }
    this.__insertAdapterOfEcosystemBoardEntity = object :
        EntityInsertAdapter<EcosystemBoardEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `ecosystem_board_cache` (`boardId`,`authorUserId`,`postType`,`title`,`description`,`mediaUrl`,`createdAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EcosystemBoardEntity) {
        statement.bindText(1, entity.boardId)
        statement.bindText(2, entity.authorUserId)
        statement.bindText(3, entity.postType)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        val _tmpMediaUrl: String? = entity.mediaUrl
        if (_tmpMediaUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpMediaUrl)
        }
        statement.bindText(7, entity.createdAt)
      }
    }
    this.__insertAdapterOfAiResourceMatchEntity = object :
        EntityInsertAdapter<AiResourceMatchEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `ai_matches_vault` (`matchId`,`sourceUserId`,`targetUserId`,`matchReason`,`status`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AiResourceMatchEntity) {
        statement.bindText(1, entity.matchId)
        statement.bindText(2, entity.sourceUserId)
        statement.bindText(3, entity.targetUserId)
        statement.bindText(4, entity.matchReason)
        statement.bindText(5, entity.status)
      }
    }
    this.__insertAdapterOfJobProjectEntity = object : EntityInsertAdapter<JobProjectEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `jobs_projects_mdm` (`listingId`,`postedBy`,`type`,`title`,`description`,`status`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: JobProjectEntity) {
        statement.bindText(1, entity.listingId)
        statement.bindText(2, entity.postedBy)
        statement.bindText(3, entity.type)
        statement.bindText(4, entity.title)
        statement.bindText(5, entity.description)
        statement.bindText(6, entity.status)
      }
    }
  }

  public override suspend fun insertUser(user: AalUserEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfAalUserEntity.insert(_connection, user)
  }

  public override suspend fun insertInstitutions(institutions: List<InstitutionEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfInstitutionEntity.insert(_connection, institutions)
  }

  public override suspend fun insertActivities(activities: List<AalActivityEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAalActivityEntity.insert(_connection, activities)
  }

  public override suspend fun insertBoardItems(items: List<EcosystemBoardEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEcosystemBoardEntity.insert(_connection, items)
  }

  public override suspend fun insertMatches(matches: List<AiResourceMatchEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAiResourceMatchEntity.insert(_connection, matches)
  }

  public override suspend fun insertJobs(jobs: List<JobProjectEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfJobProjectEntity.insert(_connection, jobs)
  }

  public override suspend fun getUser(userId: String): AalUserEntity? {
    val _sql: String = "SELECT * FROM users_vault WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _columnIndexOfUserCategory: Int = getColumnIndexOrThrow(_stmt, "userCategory")
        val _columnIndexOfDigitalPersona: Int = getColumnIndexOrThrow(_stmt, "digitalPersona")
        val _columnIndexOfAvatarUrl: Int = getColumnIndexOrThrow(_stmt, "avatarUrl")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "lastSyncedAt")
        val _result: AalUserEntity?
        if (_stmt.step()) {
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpPhone: String?
          if (_stmt.isNull(_columnIndexOfPhone)) {
            _tmpPhone = null
          } else {
            _tmpPhone = _stmt.getText(_columnIndexOfPhone)
          }
          val _tmpUserCategory: String?
          if (_stmt.isNull(_columnIndexOfUserCategory)) {
            _tmpUserCategory = null
          } else {
            _tmpUserCategory = _stmt.getText(_columnIndexOfUserCategory)
          }
          val _tmpDigitalPersona: String?
          if (_stmt.isNull(_columnIndexOfDigitalPersona)) {
            _tmpDigitalPersona = null
          } else {
            _tmpDigitalPersona = _stmt.getText(_columnIndexOfDigitalPersona)
          }
          val _tmpAvatarUrl: String?
          if (_stmt.isNull(_columnIndexOfAvatarUrl)) {
            _tmpAvatarUrl = null
          } else {
            _tmpAvatarUrl = _stmt.getText(_columnIndexOfAvatarUrl)
          }
          val _tmpLastSyncedAt: Long
          _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          _result =
              AalUserEntity(_tmpUserId,_tmpFullName,_tmpEmail,_tmpPhone,_tmpUserCategory,_tmpDigitalPersona,_tmpAvatarUrl,_tmpLastSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllInstitutions(): List<InstitutionEntity> {
    val _sql: String = "SELECT * FROM institutions_mdm"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfInstitutionId: Int = getColumnIndexOrThrow(_stmt, "institutionId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfSpocUserId: Int = getColumnIndexOrThrow(_stmt, "spocUserId")
        val _result: MutableList<InstitutionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: InstitutionEntity
          val _tmpInstitutionId: String
          _tmpInstitutionId = _stmt.getText(_columnIndexOfInstitutionId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpSpocUserId: String
          _tmpSpocUserId = _stmt.getText(_columnIndexOfSpocUserId)
          _item = InstitutionEntity(_tmpInstitutionId,_tmpName,_tmpSpocUserId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getActivities(userId: String): List<AalActivityEntity> {
    val _sql: String = "SELECT * FROM aal_activities_history WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfActivityId: Int = getColumnIndexOrThrow(_stmt, "activityId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfActivityNumber: Int = getColumnIndexOrThrow(_stmt, "activityNumber")
        val _columnIndexOfSubmissionUrl: Int = getColumnIndexOrThrow(_stmt, "submissionUrl")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<AalActivityEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AalActivityEntity
          val _tmpActivityId: String
          _tmpActivityId = _stmt.getText(_columnIndexOfActivityId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpActivityNumber: Int
          _tmpActivityNumber = _stmt.getLong(_columnIndexOfActivityNumber).toInt()
          val _tmpSubmissionUrl: String?
          if (_stmt.isNull(_columnIndexOfSubmissionUrl)) {
            _tmpSubmissionUrl = null
          } else {
            _tmpSubmissionUrl = _stmt.getText(_columnIndexOfSubmissionUrl)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpSyncedAt: Long
          _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          _item =
              AalActivityEntity(_tmpActivityId,_tmpUserId,_tmpActivityNumber,_tmpSubmissionUrl,_tmpStatus,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBoardItems(): List<EcosystemBoardEntity> {
    val _sql: String = "SELECT * FROM ecosystem_board_cache ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBoardId: Int = getColumnIndexOrThrow(_stmt, "boardId")
        val _columnIndexOfAuthorUserId: Int = getColumnIndexOrThrow(_stmt, "authorUserId")
        val _columnIndexOfPostType: Int = getColumnIndexOrThrow(_stmt, "postType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfMediaUrl: Int = getColumnIndexOrThrow(_stmt, "mediaUrl")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<EcosystemBoardEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EcosystemBoardEntity
          val _tmpBoardId: String
          _tmpBoardId = _stmt.getText(_columnIndexOfBoardId)
          val _tmpAuthorUserId: String
          _tmpAuthorUserId = _stmt.getText(_columnIndexOfAuthorUserId)
          val _tmpPostType: String
          _tmpPostType = _stmt.getText(_columnIndexOfPostType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpMediaUrl: String?
          if (_stmt.isNull(_columnIndexOfMediaUrl)) {
            _tmpMediaUrl = null
          } else {
            _tmpMediaUrl = _stmt.getText(_columnIndexOfMediaUrl)
          }
          val _tmpCreatedAt: String
          _tmpCreatedAt = _stmt.getText(_columnIndexOfCreatedAt)
          _item =
              EcosystemBoardEntity(_tmpBoardId,_tmpAuthorUserId,_tmpPostType,_tmpTitle,_tmpDescription,_tmpMediaUrl,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getMatches(userId: String): List<AiResourceMatchEntity> {
    val _sql: String = "SELECT * FROM ai_matches_vault WHERE sourceUserId = ? OR targetUserId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfMatchId: Int = getColumnIndexOrThrow(_stmt, "matchId")
        val _columnIndexOfSourceUserId: Int = getColumnIndexOrThrow(_stmt, "sourceUserId")
        val _columnIndexOfTargetUserId: Int = getColumnIndexOrThrow(_stmt, "targetUserId")
        val _columnIndexOfMatchReason: Int = getColumnIndexOrThrow(_stmt, "matchReason")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _result: MutableList<AiResourceMatchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AiResourceMatchEntity
          val _tmpMatchId: String
          _tmpMatchId = _stmt.getText(_columnIndexOfMatchId)
          val _tmpSourceUserId: String
          _tmpSourceUserId = _stmt.getText(_columnIndexOfSourceUserId)
          val _tmpTargetUserId: String
          _tmpTargetUserId = _stmt.getText(_columnIndexOfTargetUserId)
          val _tmpMatchReason: String
          _tmpMatchReason = _stmt.getText(_columnIndexOfMatchReason)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          _item =
              AiResourceMatchEntity(_tmpMatchId,_tmpSourceUserId,_tmpTargetUserId,_tmpMatchReason,_tmpStatus)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOpenJobs(): List<JobProjectEntity> {
    val _sql: String = "SELECT * FROM jobs_projects_mdm WHERE status = 'OPEN'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfListingId: Int = getColumnIndexOrThrow(_stmt, "listingId")
        val _columnIndexOfPostedBy: Int = getColumnIndexOrThrow(_stmt, "postedBy")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _result: MutableList<JobProjectEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: JobProjectEntity
          val _tmpListingId: String
          _tmpListingId = _stmt.getText(_columnIndexOfListingId)
          val _tmpPostedBy: String
          _tmpPostedBy = _stmt.getText(_columnIndexOfPostedBy)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          _item =
              JobProjectEntity(_tmpListingId,_tmpPostedBy,_tmpType,_tmpTitle,_tmpDescription,_tmpStatus)
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
