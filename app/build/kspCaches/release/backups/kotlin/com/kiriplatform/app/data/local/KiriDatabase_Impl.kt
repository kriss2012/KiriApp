package com.kiriplatform.app.`data`.local

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class KiriDatabase_Impl : KiriDatabase() {
  private val _eventDao: Lazy<EventDao> = lazy {
    EventDao_Impl(this)
  }

  private val _aalDao: Lazy<AalDao> = lazy {
    AalDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2,
        "45f21ba8a97c0bd29806927b9b8413a6", "206cf96f651d61eabe41781bbdf8855f") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `cached_events` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `type` TEXT NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `users_vault` (`userId` TEXT NOT NULL, `fullName` TEXT, `email` TEXT, `phone` TEXT, `userCategory` TEXT, `digitalPersona` TEXT, `avatarUrl` TEXT, `lastSyncedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `institutions_mdm` (`institutionId` TEXT NOT NULL, `name` TEXT NOT NULL, `spocUserId` TEXT NOT NULL, PRIMARY KEY(`institutionId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `aal_activities_history` (`activityId` TEXT NOT NULL, `userId` TEXT NOT NULL, `activityNumber` INTEGER NOT NULL, `submissionUrl` TEXT, `status` TEXT NOT NULL, `syncedAt` INTEGER NOT NULL, PRIMARY KEY(`activityId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `ecosystem_board_cache` (`boardId` TEXT NOT NULL, `authorUserId` TEXT NOT NULL, `postType` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `mediaUrl` TEXT, `createdAt` TEXT NOT NULL, PRIMARY KEY(`boardId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `ai_matches_vault` (`matchId` TEXT NOT NULL, `sourceUserId` TEXT NOT NULL, `targetUserId` TEXT NOT NULL, `matchReason` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`matchId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `jobs_projects_mdm` (`listingId` TEXT NOT NULL, `postedBy` TEXT NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`listingId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '45f21ba8a97c0bd29806927b9b8413a6')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `cached_events`")
        connection.execSQL("DROP TABLE IF EXISTS `users_vault`")
        connection.execSQL("DROP TABLE IF EXISTS `institutions_mdm`")
        connection.execSQL("DROP TABLE IF EXISTS `aal_activities_history`")
        connection.execSQL("DROP TABLE IF EXISTS `ecosystem_board_cache`")
        connection.execSQL("DROP TABLE IF EXISTS `ai_matches_vault`")
        connection.execSQL("DROP TABLE IF EXISTS `jobs_projects_mdm`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection):
          RoomOpenDelegate.ValidationResult {
        val _columnsCachedEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsCachedEvents.put("id", TableInfo.Column("id", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedEvents.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsCachedEvents.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysCachedEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesCachedEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoCachedEvents: TableInfo = TableInfo("cached_events", _columnsCachedEvents,
            _foreignKeysCachedEvents, _indicesCachedEvents)
        val _existingCachedEvents: TableInfo = read(connection, "cached_events")
        if (!_infoCachedEvents.equals(_existingCachedEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |cached_events(com.kiriplatform.app.data.local.EventEntity).
              | Expected:
              |""".trimMargin() + _infoCachedEvents + """
              |
              | Found:
              |""".trimMargin() + _existingCachedEvents)
        }
        val _columnsUsersVault: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsUsersVault.put("userId", TableInfo.Column("userId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsersVault.put("fullName", TableInfo.Column("fullName", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsersVault.put("email", TableInfo.Column("email", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsersVault.put("phone", TableInfo.Column("phone", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsersVault.put("userCategory", TableInfo.Column("userCategory", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsersVault.put("digitalPersona", TableInfo.Column("digitalPersona", "TEXT", false,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsUsersVault.put("avatarUrl", TableInfo.Column("avatarUrl", "TEXT", false, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsUsersVault.put("lastSyncedAt", TableInfo.Column("lastSyncedAt", "INTEGER", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysUsersVault: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesUsersVault: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoUsersVault: TableInfo = TableInfo("users_vault", _columnsUsersVault,
            _foreignKeysUsersVault, _indicesUsersVault)
        val _existingUsersVault: TableInfo = read(connection, "users_vault")
        if (!_infoUsersVault.equals(_existingUsersVault)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |users_vault(com.kiriplatform.app.data.local.AalUserEntity).
              | Expected:
              |""".trimMargin() + _infoUsersVault + """
              |
              | Found:
              |""".trimMargin() + _existingUsersVault)
        }
        val _columnsInstitutionsMdm: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsInstitutionsMdm.put("institutionId", TableInfo.Column("institutionId", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsInstitutionsMdm.put("name", TableInfo.Column("name", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsInstitutionsMdm.put("spocUserId", TableInfo.Column("spocUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysInstitutionsMdm: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesInstitutionsMdm: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoInstitutionsMdm: TableInfo = TableInfo("institutions_mdm", _columnsInstitutionsMdm,
            _foreignKeysInstitutionsMdm, _indicesInstitutionsMdm)
        val _existingInstitutionsMdm: TableInfo = read(connection, "institutions_mdm")
        if (!_infoInstitutionsMdm.equals(_existingInstitutionsMdm)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |institutions_mdm(com.kiriplatform.app.data.local.InstitutionEntity).
              | Expected:
              |""".trimMargin() + _infoInstitutionsMdm + """
              |
              | Found:
              |""".trimMargin() + _existingInstitutionsMdm)
        }
        val _columnsAalActivitiesHistory: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAalActivitiesHistory.put("activityId", TableInfo.Column("activityId", "TEXT", true,
            1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAalActivitiesHistory.put("userId", TableInfo.Column("userId", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAalActivitiesHistory.put("activityNumber", TableInfo.Column("activityNumber",
            "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAalActivitiesHistory.put("submissionUrl", TableInfo.Column("submissionUrl", "TEXT",
            false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAalActivitiesHistory.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAalActivitiesHistory.put("syncedAt", TableInfo.Column("syncedAt", "INTEGER", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAalActivitiesHistory: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAalActivitiesHistory: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAalActivitiesHistory: TableInfo = TableInfo("aal_activities_history",
            _columnsAalActivitiesHistory, _foreignKeysAalActivitiesHistory,
            _indicesAalActivitiesHistory)
        val _existingAalActivitiesHistory: TableInfo = read(connection, "aal_activities_history")
        if (!_infoAalActivitiesHistory.equals(_existingAalActivitiesHistory)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |aal_activities_history(com.kiriplatform.app.data.local.AalActivityEntity).
              | Expected:
              |""".trimMargin() + _infoAalActivitiesHistory + """
              |
              | Found:
              |""".trimMargin() + _existingAalActivitiesHistory)
        }
        val _columnsEcosystemBoardCache: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsEcosystemBoardCache.put("boardId", TableInfo.Column("boardId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEcosystemBoardCache.put("authorUserId", TableInfo.Column("authorUserId", "TEXT",
            true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEcosystemBoardCache.put("postType", TableInfo.Column("postType", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEcosystemBoardCache.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsEcosystemBoardCache.put("description", TableInfo.Column("description", "TEXT", true,
            0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEcosystemBoardCache.put("mediaUrl", TableInfo.Column("mediaUrl", "TEXT", false, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsEcosystemBoardCache.put("createdAt", TableInfo.Column("createdAt", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysEcosystemBoardCache: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesEcosystemBoardCache: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoEcosystemBoardCache: TableInfo = TableInfo("ecosystem_board_cache",
            _columnsEcosystemBoardCache, _foreignKeysEcosystemBoardCache,
            _indicesEcosystemBoardCache)
        val _existingEcosystemBoardCache: TableInfo = read(connection, "ecosystem_board_cache")
        if (!_infoEcosystemBoardCache.equals(_existingEcosystemBoardCache)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |ecosystem_board_cache(com.kiriplatform.app.data.local.EcosystemBoardEntity).
              | Expected:
              |""".trimMargin() + _infoEcosystemBoardCache + """
              |
              | Found:
              |""".trimMargin() + _existingEcosystemBoardCache)
        }
        val _columnsAiMatchesVault: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsAiMatchesVault.put("matchId", TableInfo.Column("matchId", "TEXT", true, 1, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsAiMatchesVault.put("sourceUserId", TableInfo.Column("sourceUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAiMatchesVault.put("targetUserId", TableInfo.Column("targetUserId", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAiMatchesVault.put("matchReason", TableInfo.Column("matchReason", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsAiMatchesVault.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysAiMatchesVault: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesAiMatchesVault: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoAiMatchesVault: TableInfo = TableInfo("ai_matches_vault", _columnsAiMatchesVault,
            _foreignKeysAiMatchesVault, _indicesAiMatchesVault)
        val _existingAiMatchesVault: TableInfo = read(connection, "ai_matches_vault")
        if (!_infoAiMatchesVault.equals(_existingAiMatchesVault)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |ai_matches_vault(com.kiriplatform.app.data.local.AiResourceMatchEntity).
              | Expected:
              |""".trimMargin() + _infoAiMatchesVault + """
              |
              | Found:
              |""".trimMargin() + _existingAiMatchesVault)
        }
        val _columnsJobsProjectsMdm: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsJobsProjectsMdm.put("listingId", TableInfo.Column("listingId", "TEXT", true, 1,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJobsProjectsMdm.put("postedBy", TableInfo.Column("postedBy", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsJobsProjectsMdm.put("type", TableInfo.Column("type", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsJobsProjectsMdm.put("title", TableInfo.Column("title", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        _columnsJobsProjectsMdm.put("description", TableInfo.Column("description", "TEXT", true, 0,
            null, TableInfo.CREATED_FROM_ENTITY))
        _columnsJobsProjectsMdm.put("status", TableInfo.Column("status", "TEXT", true, 0, null,
            TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysJobsProjectsMdm: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesJobsProjectsMdm: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoJobsProjectsMdm: TableInfo = TableInfo("jobs_projects_mdm",
            _columnsJobsProjectsMdm, _foreignKeysJobsProjectsMdm, _indicesJobsProjectsMdm)
        val _existingJobsProjectsMdm: TableInfo = read(connection, "jobs_projects_mdm")
        if (!_infoJobsProjectsMdm.equals(_existingJobsProjectsMdm)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |jobs_projects_mdm(com.kiriplatform.app.data.local.JobProjectEntity).
              | Expected:
              |""".trimMargin() + _infoJobsProjectsMdm + """
              |
              | Found:
              |""".trimMargin() + _existingJobsProjectsMdm)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "cached_events", "users_vault",
        "institutions_mdm", "aal_activities_history", "ecosystem_board_cache", "ai_matches_vault",
        "jobs_projects_mdm")
  }

  public override fun clearAllTables() {
    super.performClear(false, "cached_events", "users_vault", "institutions_mdm",
        "aal_activities_history", "ecosystem_board_cache", "ai_matches_vault", "jobs_projects_mdm")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(EventDao::class, EventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(AalDao::class, AalDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override
      fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>):
      List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun eventDao(): EventDao = _eventDao.value

  public override fun aalDao(): AalDao = _aalDao.value
}
