package com.kiriplatform.app.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AalDao_Impl implements AalDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AalUserEntity> __insertionAdapterOfAalUserEntity;

  private final EntityInsertionAdapter<InstitutionEntity> __insertionAdapterOfInstitutionEntity;

  private final EntityInsertionAdapter<AalActivityEntity> __insertionAdapterOfAalActivityEntity;

  private final EntityInsertionAdapter<EcosystemBoardEntity> __insertionAdapterOfEcosystemBoardEntity;

  private final EntityInsertionAdapter<AiResourceMatchEntity> __insertionAdapterOfAiResourceMatchEntity;

  private final EntityInsertionAdapter<JobProjectEntity> __insertionAdapterOfJobProjectEntity;

  public AalDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAalUserEntity = new EntityInsertionAdapter<AalUserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users_vault` (`userId`,`fullName`,`email`,`phone`,`userCategory`,`digitalPersona`,`lastSyncedAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AalUserEntity entity) {
        statement.bindLong(1, entity.getUserId());
        if (entity.getFullName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getFullName());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getEmail());
        }
        if (entity.getPhone() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getPhone());
        }
        if (entity.getUserCategory() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getUserCategory());
        }
        if (entity.getDigitalPersona() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDigitalPersona());
        }
        statement.bindLong(7, entity.getLastSyncedAt());
      }
    };
    this.__insertionAdapterOfInstitutionEntity = new EntityInsertionAdapter<InstitutionEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `institutions_mdm` (`institutionId`,`name`,`spocUserId`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final InstitutionEntity entity) {
        statement.bindLong(1, entity.getInstitutionId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getSpocUserId());
      }
    };
    this.__insertionAdapterOfAalActivityEntity = new EntityInsertionAdapter<AalActivityEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `aal_activities_history` (`activityId`,`userId`,`activityNumber`,`submissionUrl`,`status`,`syncedAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AalActivityEntity entity) {
        statement.bindLong(1, entity.getActivityId());
        statement.bindLong(2, entity.getUserId());
        statement.bindLong(3, entity.getActivityNumber());
        if (entity.getSubmissionUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSubmissionUrl());
        }
        statement.bindString(5, entity.getStatus());
        statement.bindLong(6, entity.getSyncedAt());
      }
    };
    this.__insertionAdapterOfEcosystemBoardEntity = new EntityInsertionAdapter<EcosystemBoardEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ecosystem_board_cache` (`boardId`,`authorUserId`,`postType`,`title`,`description`,`mediaUrl`,`createdAt`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EcosystemBoardEntity entity) {
        statement.bindLong(1, entity.getBoardId());
        statement.bindLong(2, entity.getAuthorUserId());
        statement.bindString(3, entity.getPostType());
        statement.bindString(4, entity.getTitle());
        statement.bindString(5, entity.getDescription());
        if (entity.getMediaUrl() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getMediaUrl());
        }
        statement.bindString(7, entity.getCreatedAt());
      }
    };
    this.__insertionAdapterOfAiResourceMatchEntity = new EntityInsertionAdapter<AiResourceMatchEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ai_matches_vault` (`matchId`,`sourceUserId`,`targetUserId`,`matchReason`,`status`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AiResourceMatchEntity entity) {
        statement.bindLong(1, entity.getMatchId());
        statement.bindLong(2, entity.getSourceUserId());
        statement.bindLong(3, entity.getTargetUserId());
        statement.bindString(4, entity.getMatchReason());
        statement.bindString(5, entity.getStatus());
      }
    };
    this.__insertionAdapterOfJobProjectEntity = new EntityInsertionAdapter<JobProjectEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `jobs_projects_mdm` (`listingId`,`postedBy`,`type`,`title`,`description`,`status`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final JobProjectEntity entity) {
        statement.bindLong(1, entity.getListingId());
        statement.bindLong(2, entity.getPostedBy());
        statement.bindString(3, entity.getType());
        statement.bindString(4, entity.getTitle());
        statement.bindString(5, entity.getDescription());
        statement.bindString(6, entity.getStatus());
      }
    };
  }

  @Override
  public Object insertUser(final AalUserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAalUserEntity.insert(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertInstitutions(final List<InstitutionEntity> institutions,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfInstitutionEntity.insert(institutions);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertActivities(final List<AalActivityEntity> activities,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAalActivityEntity.insert(activities);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertBoardItems(final List<EcosystemBoardEntity> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfEcosystemBoardEntity.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMatches(final List<AiResourceMatchEntity> matches,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfAiResourceMatchEntity.insert(matches);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertJobs(final List<JobProjectEntity> jobs,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfJobProjectEntity.insert(jobs);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getUser(final int userId, final Continuation<? super AalUserEntity> $completion) {
    final String _sql = "SELECT * FROM users_vault WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AalUserEntity>() {
      @Override
      @Nullable
      public AalUserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfUserCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "userCategory");
          final int _cursorIndexOfDigitalPersona = CursorUtil.getColumnIndexOrThrow(_cursor, "digitalPersona");
          final int _cursorIndexOfLastSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncedAt");
          final AalUserEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final String _tmpFullName;
            if (_cursor.isNull(_cursorIndexOfFullName)) {
              _tmpFullName = null;
            } else {
              _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            }
            final String _tmpEmail;
            if (_cursor.isNull(_cursorIndexOfEmail)) {
              _tmpEmail = null;
            } else {
              _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            }
            final String _tmpPhone;
            if (_cursor.isNull(_cursorIndexOfPhone)) {
              _tmpPhone = null;
            } else {
              _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            }
            final String _tmpUserCategory;
            if (_cursor.isNull(_cursorIndexOfUserCategory)) {
              _tmpUserCategory = null;
            } else {
              _tmpUserCategory = _cursor.getString(_cursorIndexOfUserCategory);
            }
            final String _tmpDigitalPersona;
            if (_cursor.isNull(_cursorIndexOfDigitalPersona)) {
              _tmpDigitalPersona = null;
            } else {
              _tmpDigitalPersona = _cursor.getString(_cursorIndexOfDigitalPersona);
            }
            final long _tmpLastSyncedAt;
            _tmpLastSyncedAt = _cursor.getLong(_cursorIndexOfLastSyncedAt);
            _result = new AalUserEntity(_tmpUserId,_tmpFullName,_tmpEmail,_tmpPhone,_tmpUserCategory,_tmpDigitalPersona,_tmpLastSyncedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAllInstitutions(
      final Continuation<? super List<InstitutionEntity>> $completion) {
    final String _sql = "SELECT * FROM institutions_mdm";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<InstitutionEntity>>() {
      @Override
      @NonNull
      public List<InstitutionEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfInstitutionId = CursorUtil.getColumnIndexOrThrow(_cursor, "institutionId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfSpocUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "spocUserId");
          final List<InstitutionEntity> _result = new ArrayList<InstitutionEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final InstitutionEntity _item;
            final int _tmpInstitutionId;
            _tmpInstitutionId = _cursor.getInt(_cursorIndexOfInstitutionId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpSpocUserId;
            _tmpSpocUserId = _cursor.getInt(_cursorIndexOfSpocUserId);
            _item = new InstitutionEntity(_tmpInstitutionId,_tmpName,_tmpSpocUserId);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActivities(final int userId,
      final Continuation<? super List<AalActivityEntity>> $completion) {
    final String _sql = "SELECT * FROM aal_activities_history WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AalActivityEntity>>() {
      @Override
      @NonNull
      public List<AalActivityEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfActivityId = CursorUtil.getColumnIndexOrThrow(_cursor, "activityId");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfActivityNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "activityNumber");
          final int _cursorIndexOfSubmissionUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "submissionUrl");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final int _cursorIndexOfSyncedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "syncedAt");
          final List<AalActivityEntity> _result = new ArrayList<AalActivityEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AalActivityEntity _item;
            final int _tmpActivityId;
            _tmpActivityId = _cursor.getInt(_cursorIndexOfActivityId);
            final int _tmpUserId;
            _tmpUserId = _cursor.getInt(_cursorIndexOfUserId);
            final int _tmpActivityNumber;
            _tmpActivityNumber = _cursor.getInt(_cursorIndexOfActivityNumber);
            final String _tmpSubmissionUrl;
            if (_cursor.isNull(_cursorIndexOfSubmissionUrl)) {
              _tmpSubmissionUrl = null;
            } else {
              _tmpSubmissionUrl = _cursor.getString(_cursorIndexOfSubmissionUrl);
            }
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            final long _tmpSyncedAt;
            _tmpSyncedAt = _cursor.getLong(_cursorIndexOfSyncedAt);
            _item = new AalActivityEntity(_tmpActivityId,_tmpUserId,_tmpActivityNumber,_tmpSubmissionUrl,_tmpStatus,_tmpSyncedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getBoardItems(final Continuation<? super List<EcosystemBoardEntity>> $completion) {
    final String _sql = "SELECT * FROM ecosystem_board_cache ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EcosystemBoardEntity>>() {
      @Override
      @NonNull
      public List<EcosystemBoardEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfBoardId = CursorUtil.getColumnIndexOrThrow(_cursor, "boardId");
          final int _cursorIndexOfAuthorUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "authorUserId");
          final int _cursorIndexOfPostType = CursorUtil.getColumnIndexOrThrow(_cursor, "postType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfMediaUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "mediaUrl");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<EcosystemBoardEntity> _result = new ArrayList<EcosystemBoardEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EcosystemBoardEntity _item;
            final int _tmpBoardId;
            _tmpBoardId = _cursor.getInt(_cursorIndexOfBoardId);
            final int _tmpAuthorUserId;
            _tmpAuthorUserId = _cursor.getInt(_cursorIndexOfAuthorUserId);
            final String _tmpPostType;
            _tmpPostType = _cursor.getString(_cursorIndexOfPostType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpMediaUrl;
            if (_cursor.isNull(_cursorIndexOfMediaUrl)) {
              _tmpMediaUrl = null;
            } else {
              _tmpMediaUrl = _cursor.getString(_cursorIndexOfMediaUrl);
            }
            final String _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getString(_cursorIndexOfCreatedAt);
            _item = new EcosystemBoardEntity(_tmpBoardId,_tmpAuthorUserId,_tmpPostType,_tmpTitle,_tmpDescription,_tmpMediaUrl,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getMatches(final int userId,
      final Continuation<? super List<AiResourceMatchEntity>> $completion) {
    final String _sql = "SELECT * FROM ai_matches_vault WHERE sourceUserId = ? OR targetUserId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, userId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AiResourceMatchEntity>>() {
      @Override
      @NonNull
      public List<AiResourceMatchEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfMatchId = CursorUtil.getColumnIndexOrThrow(_cursor, "matchId");
          final int _cursorIndexOfSourceUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUserId");
          final int _cursorIndexOfTargetUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "targetUserId");
          final int _cursorIndexOfMatchReason = CursorUtil.getColumnIndexOrThrow(_cursor, "matchReason");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<AiResourceMatchEntity> _result = new ArrayList<AiResourceMatchEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AiResourceMatchEntity _item;
            final int _tmpMatchId;
            _tmpMatchId = _cursor.getInt(_cursorIndexOfMatchId);
            final int _tmpSourceUserId;
            _tmpSourceUserId = _cursor.getInt(_cursorIndexOfSourceUserId);
            final int _tmpTargetUserId;
            _tmpTargetUserId = _cursor.getInt(_cursorIndexOfTargetUserId);
            final String _tmpMatchReason;
            _tmpMatchReason = _cursor.getString(_cursorIndexOfMatchReason);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new AiResourceMatchEntity(_tmpMatchId,_tmpSourceUserId,_tmpTargetUserId,_tmpMatchReason,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getOpenJobs(final Continuation<? super List<JobProjectEntity>> $completion) {
    final String _sql = "SELECT * FROM jobs_projects_mdm WHERE status = 'OPEN'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<JobProjectEntity>>() {
      @Override
      @NonNull
      public List<JobProjectEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfListingId = CursorUtil.getColumnIndexOrThrow(_cursor, "listingId");
          final int _cursorIndexOfPostedBy = CursorUtil.getColumnIndexOrThrow(_cursor, "postedBy");
          final int _cursorIndexOfType = CursorUtil.getColumnIndexOrThrow(_cursor, "type");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<JobProjectEntity> _result = new ArrayList<JobProjectEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final JobProjectEntity _item;
            final int _tmpListingId;
            _tmpListingId = _cursor.getInt(_cursorIndexOfListingId);
            final int _tmpPostedBy;
            _tmpPostedBy = _cursor.getInt(_cursorIndexOfPostedBy);
            final String _tmpType;
            _tmpType = _cursor.getString(_cursorIndexOfType);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new JobProjectEntity(_tmpListingId,_tmpPostedBy,_tmpType,_tmpTitle,_tmpDescription,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
