package com.vn.btl.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MusicApp.db";
    private static final int DATABASE_VERSION = 1;

    // Table for search history
    private static final String TABLE_SEARCH_HISTORY = "search_history";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_QUERY = "query";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE " + TABLE_SEARCH_HISTORY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_QUERY + " TEXT,"
                + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ")";
        db.execSQL(CREATE_SEARCH_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEARCH_HISTORY);
        onCreate(db);
    }

    // Add search query to history
    public void addSearchQuery(String query) {
        // First remove existing same query to avoid duplicates
        deleteSearchQuery(query);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUERY, query);
        db.insert(TABLE_SEARCH_HISTORY, null, values);
        db.close();
    }

    // Get all search history - METHOD NÀY ĐANG THIẾU
    public List<String> getAllSearchHistory() {
        List<String> historyList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_SEARCH_HISTORY + " ORDER BY " + COLUMN_TIMESTAMP + " DESC LIMIT 10";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                historyList.add(cursor.getString(1)); // COLUMN_QUERY ở index 1
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return historyList;
    }

    // Delete specific search query
    public void deleteSearchQuery(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEARCH_HISTORY, COLUMN_QUERY + " = ?", new String[]{query});
        db.close();
    }

    // Clear all search history
    public void clearSearchHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SEARCH_HISTORY, null, null);
        db.close();
    }
}