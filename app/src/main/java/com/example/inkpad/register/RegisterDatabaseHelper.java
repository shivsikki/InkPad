package com.example.inkpad.register;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RegisterDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";


    public RegisterDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_EMAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT" + ")";
        db.execSQL(CREATE_USER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void saveUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public boolean validateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, // Selecting Table
                new String[]{COLUMN_ID}, // Selecting Column
                COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?", // Where clause
                new String[]{email, password}, // Where clause arguments
                null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }

    public String getPassword(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_PASSWORD},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
            cursor.close();
            return password;
        }
        return null; // No password found
    }

    public Cursor getUserData(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_USERS,
                new String[]{COLUMN_USERNAME, COLUMN_EMAIL},
                COLUMN_EMAIL + "=?",
                new String[]{email},
                null, null, null);
    }
}
