package com.niilopoutanen.rss_feed.database.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.niilopoutanen.rss_feed.rss.Source;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class SourceDao_Impl implements SourceDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Source> __insertionAdapterOfSource;

  private final EntityDeletionOrUpdateAdapter<Source> __deletionAdapterOfSource;

  private final EntityDeletionOrUpdateAdapter<Source> __updateAdapterOfSource;

  public SourceDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSource = new EntityInsertionAdapter<Source>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `source` (`id`,`title`,`description`,`url`,`home`,`image`,`language`,`visible`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Source entity) {
        statement.bindLong(1, entity.id);
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.title);
        }
        if (entity.description == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.description);
        }
        if (entity.url == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.url);
        }
        if (entity.home == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.home);
        }
        if (entity.image == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.image);
        }
        if (entity.language == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.language);
        }
        final int _tmp = entity.visible ? 1 : 0;
        statement.bindLong(8, _tmp);
      }
    };
    this.__deletionAdapterOfSource = new EntityDeletionOrUpdateAdapter<Source>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `source` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Source entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfSource = new EntityDeletionOrUpdateAdapter<Source>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `source` SET `id` = ?,`title` = ?,`description` = ?,`url` = ?,`home` = ?,`image` = ?,`language` = ?,`visible` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement, final Source entity) {
        statement.bindLong(1, entity.id);
        if (entity.title == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.title);
        }
        if (entity.description == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.description);
        }
        if (entity.url == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.url);
        }
        if (entity.home == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.home);
        }
        if (entity.image == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.image);
        }
        if (entity.language == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.language);
        }
        final int _tmp = entity.visible ? 1 : 0;
        statement.bindLong(8, _tmp);
        statement.bindLong(9, entity.id);
      }
    };
  }

  @Override
  public void insert(final Source source) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfSource.insert(source);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void delete(final Source source) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __deletionAdapterOfSource.handle(source);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public void update(final Source source) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __updateAdapterOfSource.handle(source);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<Source>> getAll() {
    final String _sql = "SELECT * FROM source";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"source"}, false, new Callable<List<Source>>() {
      @Override
      @Nullable
      public List<Source> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfHome = CursorUtil.getColumnIndexOrThrow(_cursor, "home");
          final int _cursorIndexOfImage = CursorUtil.getColumnIndexOrThrow(_cursor, "image");
          final int _cursorIndexOfLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "language");
          final int _cursorIndexOfVisible = CursorUtil.getColumnIndexOrThrow(_cursor, "visible");
          final List<Source> _result = new ArrayList<Source>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Source _item;
            _item = new Source();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _item.title = null;
            } else {
              _item.title = _cursor.getString(_cursorIndexOfTitle);
            }
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _item.description = null;
            } else {
              _item.description = _cursor.getString(_cursorIndexOfDescription);
            }
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _item.url = null;
            } else {
              _item.url = _cursor.getString(_cursorIndexOfUrl);
            }
            if (_cursor.isNull(_cursorIndexOfHome)) {
              _item.home = null;
            } else {
              _item.home = _cursor.getString(_cursorIndexOfHome);
            }
            if (_cursor.isNull(_cursorIndexOfImage)) {
              _item.image = null;
            } else {
              _item.image = _cursor.getString(_cursorIndexOfImage);
            }
            if (_cursor.isNull(_cursorIndexOfLanguage)) {
              _item.language = null;
            } else {
              _item.language = _cursor.getString(_cursorIndexOfLanguage);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfVisible);
            _item.visible = _tmp != 0;
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<Source> getById(final int id) {
    final String _sql = "SELECT * FROM source WHERE id=?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return __db.getInvalidationTracker().createLiveData(new String[] {"source"}, false, new Callable<Source>() {
      @Override
      @Nullable
      public Source call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfHome = CursorUtil.getColumnIndexOrThrow(_cursor, "home");
          final int _cursorIndexOfImage = CursorUtil.getColumnIndexOrThrow(_cursor, "image");
          final int _cursorIndexOfLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "language");
          final int _cursorIndexOfVisible = CursorUtil.getColumnIndexOrThrow(_cursor, "visible");
          final Source _result;
          if (_cursor.moveToFirst()) {
            _result = new Source();
            _result.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _result.title = null;
            } else {
              _result.title = _cursor.getString(_cursorIndexOfTitle);
            }
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _result.description = null;
            } else {
              _result.description = _cursor.getString(_cursorIndexOfDescription);
            }
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _result.url = null;
            } else {
              _result.url = _cursor.getString(_cursorIndexOfUrl);
            }
            if (_cursor.isNull(_cursorIndexOfHome)) {
              _result.home = null;
            } else {
              _result.home = _cursor.getString(_cursorIndexOfHome);
            }
            if (_cursor.isNull(_cursorIndexOfImage)) {
              _result.image = null;
            } else {
              _result.image = _cursor.getString(_cursorIndexOfImage);
            }
            if (_cursor.isNull(_cursorIndexOfLanguage)) {
              _result.language = null;
            } else {
              _result.language = _cursor.getString(_cursorIndexOfLanguage);
            }
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfVisible);
            _result.visible = _tmp != 0;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
