package ru.mail.park.aroundyou.datasource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.mail.park.aroundyou.common.ListenerHandler;
import ru.mail.park.aroundyou.model.User;

public class DBApi {
    @SuppressLint("StaticFieldLeak")
    private static final DBApi INSTANCE = new DBApi();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final int VERSION = 1;
    private static final String DB_NAME = "AroundYouDB.db";

    private static final String TABLE_NAME_NEIGHBOURS = "NEIGHBOURS";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_SEX = "sex";
    private static final String COLUMN_ABOUT = "about";
    private static final String COLUMN_AGE = "age";

    private static final String TABLE_NAME_USERS= "USERS";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private DBApi() {

    }

    public static DBApi
    getInstance(Context context) {
        if (INSTANCE.context == null) {
            INSTANCE.context = context.getApplicationContext();
        }
        return INSTANCE;
    }

    private Context context;

    private SQLiteDatabase database;

    private void checkInitialized() {
        if (database != null) {
            return;
        }

        SQLiteOpenHelper helper = new SQLiteOpenHelper(context, DB_NAME, null, VERSION) {

            @Override
            public void onCreate(SQLiteDatabase db) {
                createDatabase(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            }
        };

        database = helper.getWritableDatabase();
    }

    private void createDatabase(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE '" + TABLE_NAME_NEIGHBOURS + "' (ID INTEGER PRIMARY KEY, "
                + COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, "
                + COLUMN_LOGIN + " TEXT NOT NULL, "
                + COLUMN_ABOUT + " TEXT NOT NULL, "
                + COLUMN_AGE + " INTEGER NOT NULL, "
                + COLUMN_SEX + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE '" + TABLE_NAME_USERS + "' (ID INTEGER PRIMARY KEY, "
                + COLUMN_USER_ID + " INTEGER UNIQUE NOT NULL, "
                + COLUMN_LOGIN + " TEXT NOT NULL, "
                + COLUMN_ABOUT + " TEXT NOT NULL, "
                + COLUMN_AGE + " INTEGER NOT NULL, "
                + COLUMN_SEX + " TEXT NOT NULL)");
    }

    public void insertNeighbours(final List<User> neighbourItems) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (User neighbourItem : neighbourItems) {
                    insertNeighbourDB(neighbourItem);
                }
            }
        });
    }

    private void insertNeighbourDB(User neighbourItem) {
        insertNeighbourOrUserDB(neighbourItem, TABLE_NAME_NEIGHBOURS);
    }

    public void insertUsers(final List<User> users) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                for (User user : users) {
                    insertUserDB(user);
                }
            }
        });
    }

    public void insertUser(final User user) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                insertUserDB(user);
            }
        });
    }

    private void insertUserDB(User neighbourItem) {
        insertNeighbourOrUserDB(neighbourItem, TABLE_NAME_USERS);
    }

    private void insertNeighbourOrUserDB(User user, String neighbourOrUser) {
        checkInitialized();
        try {
            database.execSQL("INSERT INTO " + neighbourOrUser
                            + " (" + COLUMN_USER_ID + ", " + COLUMN_LOGIN + ", " + COLUMN_ABOUT + ", "
                            + COLUMN_SEX + ", " + COLUMN_AGE +  ") VALUES (?, ?, ?, ?, ?)",
                    new Object[]{user.getId(), user.getLogin(),
                            user.getAbout(), user.getSex(), user.getAge()});
        } catch (SQLException ignored) {

        }
    }

    public ListenerHandler<OnDBDataGetListener<List<User>>>
    getNeighbours(final OnDBDataGetListener<List<User>> listener) {
        final ListenerHandler<OnDBDataGetListener<List<User>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                checkInitialized();

                Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME_NEIGHBOURS + ";", null);
                if (cursor == null) {
                    invokeError(handler, new IOException("DB exception"));
                } else {
                    final List<User> result = new ArrayList<>();
                    try {
                        while (cursor.moveToNext()) {
                            result.add(new User(
                                    cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_SEX)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_ABOUT)),
                                    cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)),
                                    cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
                            ));
                        }
                    } finally {
                        cursor.close();
                    }
                    invokeSuccess(handler, result);
                }
            }
        });
        return handler;
    }


    public ListenerHandler<OnDBDataGetListener<List<User>>>
    getUsers(final OnDBDataGetListener<List<User>> listener) {
        final ListenerHandler<OnDBDataGetListener<List<User>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                checkInitialized();

                Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME_USERS, null);
                if (cursor == null) {
                    invokeError(handler, new IOException("DB exception"));
                } else {
                    final List<User> result = new ArrayList<>();
                    try {
                        while (cursor.moveToNext()) {
                            result.add(new User(
                                    cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_SEX)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_ABOUT)),
                                    cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)),
                                    cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
                            ));
                        }
                    } finally {
                        cursor.close();
                    }
                    invokeSuccess(handler, result);
                }
            }
        });
        return handler;
    }

    public ListenerHandler<OnDBDataGetListener<User>>
    getUser(final OnDBDataGetListener<User> listener, final int userId) {
        final ListenerHandler<OnDBDataGetListener<User>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                checkInitialized();

                Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME_USERS + " WHERE " + COLUMN_USER_ID + " = ?",
                        new String[] {String.valueOf(userId)});
                if (cursor == null) {
                    invokeError(handler, new IOException("DB exception"));
                } else {
                    User user = null;
                    try {
                        if (cursor.moveToFirst()) {
                            user = new User(cursor.getString(cursor.getColumnIndex(COLUMN_LOGIN)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_SEX)),
                                    cursor.getString(cursor.getColumnIndex(COLUMN_ABOUT)),
                                    cursor.getInt(cursor.getColumnIndex(COLUMN_AGE)),
                                    cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
                            );
                        }
                    } finally {
                        cursor.close();
                    }
                    if (user != null) {
                        invokeSuccess(handler, user);
                    } else {
                        invokeError(handler, new IOException("There is no user in DB"));
                    }
                }
            }
        });


        return handler;
    }

    public void cleanDB() {
        cleanNeighbours();
        cleanUsers();
    }

    public void cleanNeighbours() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                cleanTable(TABLE_NAME_NEIGHBOURS);
            }
        });
    }

    public void cleanUsers() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                cleanTable(TABLE_NAME_USERS);
            }
        });
    }

    public void cleanTable(String tableName) {
        checkInitialized();
        database.execSQL("DELETE FROM " + tableName);
    }

    private void dropTable(final String tableName) {
        checkInitialized();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.execSQL("DROP TABLE IF EXISTS " + tableName);
            }
        });
    }

    private <T> void
    invokeSuccess(final ListenerHandler<OnDBDataGetListener<T>> handler, final T payload) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnDBDataGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeSucces");
                    listener.onSuccess(payload);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    private <T> void
    invokeError(final ListenerHandler<OnDBDataGetListener<T>> handler, final Exception error) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                OnDBDataGetListener<T> listener = handler.getListener();
                if (listener != null) {
                    Log.d("API", "listener NOT null in invokeError");
                    listener.onError(error);
                } else {
                    Log.d("API", "listener is null");
                }
            }
        });
    }

    public interface OnDBDataGetListener<T> {
        void onSuccess(final T items);

        void onError(final Exception error);
    }
}
