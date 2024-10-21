package com.example.inkpad.memories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MemoriesDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "memories.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_MEMORIES = "memories";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_MEDIA_URI = "media_uri";
    private static final String COLUMN_MEDIA_TYPE = "media_type";
    private static final String COLUMN_IMAGE_BYTES = "image_bytes";

    public MemoriesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_MEMORIES = "CREATE TABLE " + TABLE_MEMORIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_MEDIA_URI + " TEXT, " +
                COLUMN_MEDIA_TYPE + " TEXT, " +
                COLUMN_IMAGE_BYTES + " BLOB)";
        db.execSQL(CREATE_TABLE_MEMORIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMORIES);
        onCreate(db);
    }

    public boolean saveMemory(Memory memory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DESCRIPTION, memory.getDescription());
        values.put(COLUMN_MEDIA_URI, memory.getMediaUri());
        values.put(COLUMN_MEDIA_TYPE, memory.getMediaType());
        values.put(COLUMN_IMAGE_BYTES, memory.getImageBytes());

        long result = db.insert(TABLE_MEMORIES, null, values);
        return result != -1;
    }

    public Memory getMemory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEMORIES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Memory memory = new Memory(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDIA_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MEDIA_TYPE)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_BYTES)));
            cursor.close();
            return memory;
        }
        cursor.close();
        return null;
    }

    public void deleteMemories() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEMORIES, null, null);
    }
}
