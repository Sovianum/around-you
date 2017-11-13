package ru.mail.park.aroundyou.datasource;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

    /*private static final String TABLE_NAME_REQUESTS = "REQUESTS";
    private static final String COLUMN_REQUESTER_ID = "requester_id";
    private static final String COLUMN_REQUESTED_ID = "requested_id";
    private static final String COLUMN_REQUESTER_LOGIN = "requester_login";
    private static final String COLUMN_REQUESTED_LOGIN = "requested_login";
    private static final String COLUMN_REQUESTER_ABOUT= "requester_about";
    private static final String COLUMN_TIME= "time";
    private static final String COLUMN_STATUS= "status";*/

    private static final String TABLE_NAME_USERS= "USERS";

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
        /*db.execSQL("CREATE TABLE '" + TABLE_NAME_REQUESTS + "' (ID INTEGER PRIMARY KEY, "
                + COLUMN_REQUESTER_ID + " INTEGER UNIQUE NOT NULL, "
                + COLUMN_REQUESTED_ID+ " INTEGER UNIQUE NOT NULL, "
                + COLUMN_REQUESTER_LOGIN + " TEXT NOT NULL, "
                + COLUMN_REQUESTED_LOGIN + " TEXT NOT NULL, "
                + COLUMN_REQUESTER_ABOUT + " TEXT NOT NULL, "
                + COLUMN_TIME + " DATETIME NOT NULL, "
                + COLUMN_STATUS + " TEXT NOT NULL)");*/
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

    private void insertUserDB(User neighbourItem) {
        insertNeighbourOrUserDB(neighbourItem, TABLE_NAME_USERS);
    }

    private void insertNeighbourOrUserDB(User user, String neighbourOrUser) {
        checkInitialized();
        try {
            database.execSQL("INSERT INTO " + TABLE_NAME_NEIGHBOURS
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

                Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME_USERS, null);
                if (cursor == null) {
                    listener.onError(new IOException("DB exception"));
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
                    listener.onSuccess(result);
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
                    listener.onError(new IOException("DB exception"));
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
                    listener.onSuccess(result);
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
                    listener.onError(new IOException("DB exception"));
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
                        listener.onSuccess(user);
                    } else {
                        listener.onError(new IOException("There is no user in DB"));
                    }
                }
            }
        });


        return handler;
    }


    /*public ListenerHandler<OnDBDataGetListener<List<MeetRequest>>>
    getNeighbours(final OnDBDataGetListener<List<MeetRequest>> listener) {
        final ListenerHandler<OnDBDataGetListener<List<MeetRequest>>> handler = new ListenerHandler<>(listener);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                checkInitialized();

                Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME_NEIGHBOURS, null);
                if (cursor == null) {
                    listener.onError(new IOException("DB exception"));
                } else {
                    final List<MeetRequest> result = new ArrayList<>();
                    try {
                        while (cursor.moveToNext()) {
                            result.add(new MeetRequest()
                            ));
                        }
                    } finally {
                        cursor.close();
                    }
                    listener.onSuccess(result);
                }
            }
        });
        return handler;
    }*/

    public void cleanNeighbours() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                cleanDB(TABLE_NAME_NEIGHBOURS);
            }
        });
    }

    public void cleanDB(String tableName) {
        checkInitialized();

        database.execSQL("DELETE FROM " + tableName);
    }

    public void dropTableNeighbours() {
        dropTable(TABLE_NAME_NEIGHBOURS);
    }

    /*public void insertRequset(final MeetRequest request) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                insertRequestDB(request);
            }
        });
    }

    private void insertRequestDB(MeetRequest request) {
        checkInitialized();
        try {
            database.execSQL("INSERT INTO " + TABLE_NAME_REQUESTS
                            + " (" + COLUMN_REQUESTED_ID + ", " + COLUMN_REQUESTER_ID + ", "
                            + COLUMN_REQUESTED_LOGIN+ ", " + COLUMN_REQUESTER_LOGIN + ", "
                            + COLUMN_REQUESTER_ABOUT + ", " + COLUMN_TIME + ", "
                            + COLUMN_STATUS +  ") VALUES (?, ?, ?, ?, ?, ?, ?)",
                    new Object[]{request.getRequestedId(), request.getRequesterId(),
                            request.getRequestedLogin(), request.getRequesterLogin(),
                            request.getRequesterAbout(), request.getTime(),
                            request.getStatus()});
        } catch (SQLException ignored) {

        }
    }*/

    private void dropTable(final String tableName) {
        checkInitialized();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                database.execSQL("DROP TABLE IF EXISTS " + tableName);
            }
        });
    }

    public interface OnDBDataGetListener<T> {
        void onSuccess(final T items);

        void onError(final Exception error);
    }
}
