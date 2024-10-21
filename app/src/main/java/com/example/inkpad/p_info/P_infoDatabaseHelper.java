package com.example.inkpad.p_info;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class P_infoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PersonalInfo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "personal_info";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_MIDDLE_NAME = "middle_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_DOB = "dob";
    private static final String COLUMN_DOC_IMAGE = "doc_image";
    private static final String COLUMN_CERT_IMAGE = "cert_image";

    public P_infoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_FIRST_NAME + " TEXT, " +
                COLUMN_MIDDLE_NAME + " TEXT, " +
                COLUMN_LAST_NAME + " TEXT, " +
                COLUMN_DOB + " TEXT, " +
                COLUMN_DOC_IMAGE + " BLOB, " +
                COLUMN_CERT_IMAGE + " BLOB)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean savePersonalInfo(P_info info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FIRST_NAME, info.getFirstName());
        values.put(COLUMN_MIDDLE_NAME, info.getMiddleName());
        values.put(COLUMN_LAST_NAME, info.getLastName());
        values.put(COLUMN_DOB, info.getDob());
        values.put(COLUMN_DOC_IMAGE, info.getDocImage());
        values.put(COLUMN_CERT_IMAGE, info.getCertImage());

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean deletePersonalInfo() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_NAME, null, null);
        return rowsDeleted > 0;
    }

    public P_info getPersonalInfo() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            P_info info = new P_info(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MIDDLE_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOB)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_DOC_IMAGE)),
                    cursor.getBlob(cursor.getColumnIndexOrThrow(COLUMN_CERT_IMAGE))
            );
            cursor.close();
            return info;
        }
        cursor.close();
        return null;
    }
}
