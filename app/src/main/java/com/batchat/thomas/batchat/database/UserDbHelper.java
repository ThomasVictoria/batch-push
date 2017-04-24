package com.batchat.thomas.batchat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by thomas on 03/04/2017.
 */

public class UserDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "batchat.db";

    public UserDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_USER);
        db.execSQL(SQL_CREATE_ENTRIES_CHAT);
        db.execSQL(SQL_CREATE_ENTRIES_MESSAGES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String COLUMN_NAME_PSEUDO = "pseudo";
    }

    public static class ChatEntry implements BaseColumns {
        public static final String TABLE_NAME = "chat";
        public static final String COLUMN_NAME_CHAT_ID = "chat_id";
    }

    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "message";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_CHAT = "chat";
        public static final String COLUMN_NAME_ATTRIBUTE = "attribute";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_PSEUDO = "pseudo";
    }

    private static final String SQL_CREATE_ENTRIES_USER =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY," +
                    UserEntry.COLUMN_NAME_PSEUDO + " TEXT)";

    private static final String SQL_CREATE_ENTRIES_MESSAGES =
            "CREATE TABLE " + MessageEntry.TABLE_NAME + " (" +
                    MessageEntry._ID + " INTEGER PRIMARY KEY," +
                    MessageEntry.COLUMN_NAME_CHAT  + " TEXT," +
                    MessageEntry.COLUMN_NAME_PSEUDO + " TEXT," +
                    MessageEntry.COLUMN_NAME_ATTRIBUTE + " TEXT," +
                    MessageEntry.COLUMN_NAME_TYPE + " TEXT," +
                    MessageEntry.COLUMN_NAME_MESSAGE + " TEXT)";

    private static final String SQL_CREATE_ENTRIES_CHAT =
            "CREATE TABLE "+ ChatEntry.TABLE_NAME + " (" +
                    ChatEntry._ID + " INTEGER PRIMARY KEY," +
                    ChatEntry.COLUMN_NAME_CHAT_ID + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME + ";" +
            "DROP TABLE IF EXISTS " + MessageEntry.TABLE_NAME + ";" +
            "DROP TABLE IF EXISTS " + ChatEntry.TABLE_NAME;

}
