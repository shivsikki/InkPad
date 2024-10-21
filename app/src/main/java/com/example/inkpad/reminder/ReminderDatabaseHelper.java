package com.example.inkpad.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class    ReminderDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "reminders";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";
    private static final String COL_MESSAGE = "message";
    private static final String COL_TIME = "time";
    private static final String COL_DATE = "date";
    private static final String COL_NOTIFICATION_ID = "notification_id"; // Notification ID

    public ReminderDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_MESSAGE + " TEXT, " +
                COL_TIME + " INTEGER, " +
                COL_DATE + " TEXT, " +
                COL_NOTIFICATION_ID + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new reminder into the database
    public boolean insertReminder(String name, String message, long timeInMillis, String date, int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_MESSAGE, message);
        contentValues.put(COL_TIME, timeInMillis);
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_NOTIFICATION_ID, notificationId); // Store notification ID

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    // Retrieve all reminders from the database
    public List<Reminder> getAllReminders() {
        List<Reminder> reminders = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(COL_NAME);
                int messageIndex = cursor.getColumnIndex(COL_MESSAGE);
                int timeIndex = cursor.getColumnIndex(COL_TIME);
                int dateIndex = cursor.getColumnIndex(COL_DATE);
                int notificationIdIndex = cursor.getColumnIndex(COL_NOTIFICATION_ID); // Notification ID

                do {
                    String name = cursor.getString(nameIndex);
                    String message = cursor.getString(messageIndex);
                    long time = cursor.getLong(timeIndex);
                    String date = cursor.getString(dateIndex);
                    int notificationId = cursor.getInt(notificationIdIndex); // Retrieve notification ID
                    reminders.add(new Reminder(name, message, time, date, notificationId)); // Create reminder object
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return reminders;
    }

    // Delete a reminder
    public boolean deleteReminder(int reminderId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, COL_NOTIFICATION_ID + " = ?", new String[]{String.valueOf(reminderId)});
        db.close();
        return rowsDeleted > 0; // Return true if at least one row was deleted
    }

}
