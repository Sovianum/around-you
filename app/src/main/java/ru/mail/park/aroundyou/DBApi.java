package ru.mail.park.aroundyou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ru.mail.park.aroundyou.model.User;

/**
 * Created by sergey on 12.11.17.
 */

public class DBApi {
    @SuppressLint("StaticFieldLeak")
    private static final DBApi INSTANCE = new DBApi();
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final int VERSION = 1;
    private static final String DB_NAME = "AroundYouDB.db";
    private static final String TABLE_NAME = "NEIGHBOURS";
    private static final String COLUMN_NEIGHBOUR_ID = "neighbour_id";
    private static final String COLUMN_LOGIN = "login";
    private static final String COLUMN_SEX = "sex";
    private static final String COLUMN_ABOUT = "about";
    private static final String COLUMN_AGE = "age";




    private DBApi() {

    }

    public static DBApi
    getInstance(Context context) {
        INSTANCE.context = context.getApplicationContext();
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
        db.execSQL("CREATE TABLE '" + TABLE_NAME + "' (ID INTEGER PRIMARY KEY, "
                + COLUMN_NEIGHBOUR_ID + " INTEGER UNIQUE NOT NULL, "
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
        checkInitialized();
        try {
            database.execSQL("INSERT INTO " + TABLE_NAME
                            + " (" + COLUMN_NEIGHBOUR_ID + ", " + COLUMN_LOGIN + ", " + COLUMN_ABOUT + ", "
                            + COLUMN_SEX + ", " + COLUMN_AGE +  ") VALUES (?, ?, ?, ?, ?)",
                    new Object[]{neighbourItem.getId(), neighbourItem.getLogin(),
                            neighbourItem.getAbout(), neighbourItem.getSex(), neighbourItem.getAge()});
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

                Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
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
                                    cursor.getInt(cursor.getColumnIndex(COLUMN_NEIGHBOUR_ID))
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

    public void clean() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                cleanDB();
            }
        });
    }

    public void cleanDB() {
        checkInitialized();

        database.execSQL("DELETE FROM " + TABLE_NAME);
    }

    public void dropTableNeighbours() {
        dropTable(TABLE_NAME);
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


    public interface OnDBDataGetListener<T> {
        void onSuccess(final T items);

        void onError(final Exception error);
    }
}
