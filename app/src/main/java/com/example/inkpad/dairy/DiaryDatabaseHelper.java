package com.example.inkpad.dairy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 2; // Incremented due to schema change

    // Table name
    public static final String TABLE_NAME = "diary_entries";

    // Columns
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_DIARY_TEXT = "diary_text";
    public static final String COLUMN_DIARY_TOPIC = "diary_topic"; // New column for topic
    public static final String COLUMN_DATE = "date";

    // Create table SQL query
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_DIARY_TEXT + " TEXT, " +
                    COLUMN_DIARY_TOPIC + " TEXT, " + // New column for diary topic
                    COLUMN_DATE + " TEXT UNIQUE" + // Date should be unique for each entry
                    ");";

    public DiaryDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE); // Create table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // If upgrading from version 1, add the new column for diary_topic
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_DIARY_TOPIC + " TEXT");
        }
    }

    // Insert new diary entry
    public boolean insertDiaryEntry(String diaryText, String diaryTopic, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DIARY_TEXT, diaryText);
        values.put(COLUMN_DIARY_TOPIC, diaryTopic); // Insert the topic
        values.put(COLUMN_DATE, date);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1; // Return true if insert is successful
    }

    // Update existing diary entry
    public boolean updateDiaryEntry(String diaryText, String diaryTopic, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DIARY_TEXT, diaryText);
        values.put(COLUMN_DIARY_TOPIC, diaryTopic); // Update the topic

        long result = db.update(TABLE_NAME, values, COLUMN_DATE + " = ?", new String[]{date});
        return result > 0; // Return true if update is successful
    }

    // Get diary entry by date
    public Cursor getDiaryEntryByDate(String date) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_NAME,
                null,
                COLUMN_DATE + " = ?",
                new String[]{date},
                null,
                null,
                null);
    }

    // Delete diary entry by date
    public boolean deleteDiaryEntry(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_DATE + " = ?", new String[]{date});
        return result > 0; // Return true if delete is successful
    }
}
