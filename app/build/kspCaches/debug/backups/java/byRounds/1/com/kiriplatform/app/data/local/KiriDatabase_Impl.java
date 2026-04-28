package com.kiriplatform.app.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class KiriDatabase_Impl extends KiriDatabase {
  private volatile EventDao _eventDao;

  private volatile AalDao _aalDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cached_events` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `type` TEXT NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `users_vault` (`userId` INTEGER NOT NULL, `fullName` TEXT, `email` TEXT, `phone` TEXT, `userCategory` TEXT, `digitalPersona` TEXT, `lastSyncedAt` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `institutions_mdm` (`institutionId` INTEGER NOT NULL, `name` TEXT NOT NULL, `spocUserId` INTEGER NOT NULL, PRIMARY KEY(`institutionId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `aal_activities_history` (`activityId` INTEGER NOT NULL, `userId` INTEGER NOT NULL, `activityNumber` INTEGER NOT NULL, `submissionUrl` TEXT, `status` TEXT NOT NULL, `syncedAt` INTEGER NOT NULL, PRIMARY KEY(`activityId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ecosystem_board_cache` (`boardId` INTEGER NOT NULL, `authorUserId` INTEGER NOT NULL, `postType` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `mediaUrl` TEXT, `createdAt` TEXT NOT NULL, PRIMARY KEY(`boardId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ai_matches_vault` (`matchId` INTEGER NOT NULL, `sourceUserId` INTEGER NOT NULL, `targetUserId` INTEGER NOT NULL, `matchReason` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`matchId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `jobs_projects_mdm` (`listingId` INTEGER NOT NULL, `postedBy` INTEGER NOT NULL, `type` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `status` TEXT NOT NULL, PRIMARY KEY(`listingId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'd998f308ae293fc9c62476a1e2ea1870')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cached_events`");
        db.execSQL("DROP TABLE IF EXISTS `users_vault`");
        db.execSQL("DROP TABLE IF EXISTS `institutions_mdm`");
        db.execSQL("DROP TABLE IF EXISTS `aal_activities_history`");
        db.execSQL("DROP TABLE IF EXISTS `ecosystem_board_cache`");
        db.execSQL("DROP TABLE IF EXISTS `ai_matches_vault`");
        db.execSQL("DROP TABLE IF EXISTS `jobs_projects_mdm`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCachedEvents = new HashMap<String, TableInfo.Column>(3);
        _columnsCachedEvents.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedEvents.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCachedEvents.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCachedEvents = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCachedEvents = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCachedEvents = new TableInfo("cached_events", _columnsCachedEvents, _foreignKeysCachedEvents, _indicesCachedEvents);
        final TableInfo _existingCachedEvents = TableInfo.read(db, "cached_events");
        if (!_infoCachedEvents.equals(_existingCachedEvents)) {
          return new RoomOpenHelper.ValidationResult(false, "cached_events(com.kiriplatform.app.data.local.EventEntity).\n"
                  + " Expected:\n" + _infoCachedEvents + "\n"
                  + " Found:\n" + _existingCachedEvents);
        }
        final HashMap<String, TableInfo.Column> _columnsUsersVault = new HashMap<String, TableInfo.Column>(7);
        _columnsUsersVault.put("userId", new TableInfo.Column("userId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsersVault.put("fullName", new TableInfo.Column("fullName", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsersVault.put("email", new TableInfo.Column("email", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsersVault.put("phone", new TableInfo.Column("phone", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsersVault.put("userCategory", new TableInfo.Column("userCategory", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsersVault.put("digitalPersona", new TableInfo.Column("digitalPersona", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsersVault.put("lastSyncedAt", new TableInfo.Column("lastSyncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsersVault = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsersVault = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsersVault = new TableInfo("users_vault", _columnsUsersVault, _foreignKeysUsersVault, _indicesUsersVault);
        final TableInfo _existingUsersVault = TableInfo.read(db, "users_vault");
        if (!_infoUsersVault.equals(_existingUsersVault)) {
          return new RoomOpenHelper.ValidationResult(false, "users_vault(com.kiriplatform.app.data.local.AalUserEntity).\n"
                  + " Expected:\n" + _infoUsersVault + "\n"
                  + " Found:\n" + _existingUsersVault);
        }
        final HashMap<String, TableInfo.Column> _columnsInstitutionsMdm = new HashMap<String, TableInfo.Column>(3);
        _columnsInstitutionsMdm.put("institutionId", new TableInfo.Column("institutionId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstitutionsMdm.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsInstitutionsMdm.put("spocUserId", new TableInfo.Column("spocUserId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysInstitutionsMdm = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesInstitutionsMdm = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoInstitutionsMdm = new TableInfo("institutions_mdm", _columnsInstitutionsMdm, _foreignKeysInstitutionsMdm, _indicesInstitutionsMdm);
        final TableInfo _existingInstitutionsMdm = TableInfo.read(db, "institutions_mdm");
        if (!_infoInstitutionsMdm.equals(_existingInstitutionsMdm)) {
          return new RoomOpenHelper.ValidationResult(false, "institutions_mdm(com.kiriplatform.app.data.local.InstitutionEntity).\n"
                  + " Expected:\n" + _infoInstitutionsMdm + "\n"
                  + " Found:\n" + _existingInstitutionsMdm);
        }
        final HashMap<String, TableInfo.Column> _columnsAalActivitiesHistory = new HashMap<String, TableInfo.Column>(6);
        _columnsAalActivitiesHistory.put("activityId", new TableInfo.Column("activityId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAalActivitiesHistory.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAalActivitiesHistory.put("activityNumber", new TableInfo.Column("activityNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAalActivitiesHistory.put("submissionUrl", new TableInfo.Column("submissionUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAalActivitiesHistory.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAalActivitiesHistory.put("syncedAt", new TableInfo.Column("syncedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAalActivitiesHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAalActivitiesHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAalActivitiesHistory = new TableInfo("aal_activities_history", _columnsAalActivitiesHistory, _foreignKeysAalActivitiesHistory, _indicesAalActivitiesHistory);
        final TableInfo _existingAalActivitiesHistory = TableInfo.read(db, "aal_activities_history");
        if (!_infoAalActivitiesHistory.equals(_existingAalActivitiesHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "aal_activities_history(com.kiriplatform.app.data.local.AalActivityEntity).\n"
                  + " Expected:\n" + _infoAalActivitiesHistory + "\n"
                  + " Found:\n" + _existingAalActivitiesHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsEcosystemBoardCache = new HashMap<String, TableInfo.Column>(7);
        _columnsEcosystemBoardCache.put("boardId", new TableInfo.Column("boardId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcosystemBoardCache.put("authorUserId", new TableInfo.Column("authorUserId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcosystemBoardCache.put("postType", new TableInfo.Column("postType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcosystemBoardCache.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcosystemBoardCache.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcosystemBoardCache.put("mediaUrl", new TableInfo.Column("mediaUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEcosystemBoardCache.put("createdAt", new TableInfo.Column("createdAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEcosystemBoardCache = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEcosystemBoardCache = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEcosystemBoardCache = new TableInfo("ecosystem_board_cache", _columnsEcosystemBoardCache, _foreignKeysEcosystemBoardCache, _indicesEcosystemBoardCache);
        final TableInfo _existingEcosystemBoardCache = TableInfo.read(db, "ecosystem_board_cache");
        if (!_infoEcosystemBoardCache.equals(_existingEcosystemBoardCache)) {
          return new RoomOpenHelper.ValidationResult(false, "ecosystem_board_cache(com.kiriplatform.app.data.local.EcosystemBoardEntity).\n"
                  + " Expected:\n" + _infoEcosystemBoardCache + "\n"
                  + " Found:\n" + _existingEcosystemBoardCache);
        }
        final HashMap<String, TableInfo.Column> _columnsAiMatchesVault = new HashMap<String, TableInfo.Column>(5);
        _columnsAiMatchesVault.put("matchId", new TableInfo.Column("matchId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiMatchesVault.put("sourceUserId", new TableInfo.Column("sourceUserId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiMatchesVault.put("targetUserId", new TableInfo.Column("targetUserId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiMatchesVault.put("matchReason", new TableInfo.Column("matchReason", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAiMatchesVault.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAiMatchesVault = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAiMatchesVault = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAiMatchesVault = new TableInfo("ai_matches_vault", _columnsAiMatchesVault, _foreignKeysAiMatchesVault, _indicesAiMatchesVault);
        final TableInfo _existingAiMatchesVault = TableInfo.read(db, "ai_matches_vault");
        if (!_infoAiMatchesVault.equals(_existingAiMatchesVault)) {
          return new RoomOpenHelper.ValidationResult(false, "ai_matches_vault(com.kiriplatform.app.data.local.AiResourceMatchEntity).\n"
                  + " Expected:\n" + _infoAiMatchesVault + "\n"
                  + " Found:\n" + _existingAiMatchesVault);
        }
        final HashMap<String, TableInfo.Column> _columnsJobsProjectsMdm = new HashMap<String, TableInfo.Column>(6);
        _columnsJobsProjectsMdm.put("listingId", new TableInfo.Column("listingId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobsProjectsMdm.put("postedBy", new TableInfo.Column("postedBy", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobsProjectsMdm.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobsProjectsMdm.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobsProjectsMdm.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsJobsProjectsMdm.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysJobsProjectsMdm = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesJobsProjectsMdm = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoJobsProjectsMdm = new TableInfo("jobs_projects_mdm", _columnsJobsProjectsMdm, _foreignKeysJobsProjectsMdm, _indicesJobsProjectsMdm);
        final TableInfo _existingJobsProjectsMdm = TableInfo.read(db, "jobs_projects_mdm");
        if (!_infoJobsProjectsMdm.equals(_existingJobsProjectsMdm)) {
          return new RoomOpenHelper.ValidationResult(false, "jobs_projects_mdm(com.kiriplatform.app.data.local.JobProjectEntity).\n"
                  + " Expected:\n" + _infoJobsProjectsMdm + "\n"
                  + " Found:\n" + _existingJobsProjectsMdm);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "d998f308ae293fc9c62476a1e2ea1870", "1bea0f357bc34f6dfe78959fcc24680d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cached_events","users_vault","institutions_mdm","aal_activities_history","ecosystem_board_cache","ai_matches_vault","jobs_projects_mdm");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `cached_events`");
      _db.execSQL("DELETE FROM `users_vault`");
      _db.execSQL("DELETE FROM `institutions_mdm`");
      _db.execSQL("DELETE FROM `aal_activities_history`");
      _db.execSQL("DELETE FROM `ecosystem_board_cache`");
      _db.execSQL("DELETE FROM `ai_matches_vault`");
      _db.execSQL("DELETE FROM `jobs_projects_mdm`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(EventDao.class, EventDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AalDao.class, AalDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public EventDao eventDao() {
    if (_eventDao != null) {
      return _eventDao;
    } else {
      synchronized(this) {
        if(_eventDao == null) {
          _eventDao = new EventDao_Impl(this);
        }
        return _eventDao;
      }
    }
  }

  @Override
  public AalDao aalDao() {
    if (_aalDao != null) {
      return _aalDao;
    } else {
      synchronized(this) {
        if(_aalDao == null) {
          _aalDao = new AalDao_Impl(this);
        }
        return _aalDao;
      }
    }
  }
}
