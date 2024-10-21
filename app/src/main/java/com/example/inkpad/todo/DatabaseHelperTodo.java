package com.example.inkpad.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelperTodo extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "ToDo.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_TODO = "todo";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TASK = "task";
    public static final String COLUMN_IS_COMPLETED = "is_completed";


    public DatabaseHelperTodo(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelperTodo(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TASK + " TEXT,"
                + COLUMN_IS_COMPLETED + " INTEGER" + ")";
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public Cursor getAllToDoItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TODO;
        return db.rawQuery(query, null);
    }

    public boolean doesTaskExistById(long taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_TODO + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(taskId)});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public void updateToDoItem(long taskId, String task, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_TODO, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    public void deleteToDoItemById(long taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TODO, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
    }

    public long addToDoItem(String task, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK, task);
        values.put(COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);
        long newRowId = db.insert(TABLE_TODO, null, values);
        db.close();
        return newRowId;
    }

}