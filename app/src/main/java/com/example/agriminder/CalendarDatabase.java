package com.example.agriminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CalendarDatabase extends SQLiteOpenHelper {

    // Database Name and Version
    private static final String DATABASE_NAME = "AgriMinder.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name for Calendar
    private static final String TABLE_CALENDAR = "Calendar";

    // Calendar Table Columns
    private static final String COL_CALENDAR_ID = "Calendar_ID";
    private static final String COL_CALENDAR_TITLE = "Calendar_Title";
    private static final String COL_BATCH_ID = "Batch_ID";
    private static final String COL_CALENDAR_DATE = "Calendar_Date";
    private static final String COL_CALENDAR_NOTE = "Calendar_Note";

    // Batch Table (for foreign key reference)
    private static final String TABLE_BATCH = "Batch";
    private static final String COL_BATCH_NAME = "Batch_Name";

    public CalendarDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Calendar Table
        String CREATE_CALENDAR_TABLE = "CREATE TABLE " + TABLE_CALENDAR + "("
                + COL_CALENDAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CALENDAR_TITLE + " TEXT NOT NULL, "
                + COL_BATCH_ID + " INTEGER NOT NULL, "
                + COL_CALENDAR_DATE + " TEXT NOT NULL, "
                + COL_CALENDAR_NOTE + " TEXT, "
                + "FOREIGN KEY(" + COL_BATCH_ID + ") REFERENCES " + TABLE_BATCH + "(Batch_ID)"
                + ")";
        db.execSQL(CREATE_CALENDAR_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the table if it exists and create it again
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR);
        onCreate(db);
    }

    // Method to insert new calendar entry
    public boolean insertCalendar(String title, int batchID, String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CALENDAR_TITLE, title);
        values.put(COL_BATCH_ID, batchID);
        values.put(COL_CALENDAR_DATE, date);
        values.put(COL_CALENDAR_NOTE, note);

        long result = db.insert(TABLE_CALENDAR, null, values);
        return result != -1; // Return true if inserted successfully, false otherwise
    }

    // Method to get all calendar entries
    public Cursor getAllCalendars() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Calendar ORDER BY Calendar_Date ASC", null);
    }

    // Method to get calendar by batch
    public Cursor getCalendarsByBatch(int batchID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_CALENDAR + " WHERE " + COL_BATCH_ID + " = ?", new String[]{String.valueOf(batchID)});
    }

    // Method to update a calendar entry
    public boolean updateCalendar(int calendarID, String title, int batchID, String date, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_CALENDAR_TITLE, title);
        values.put(COL_BATCH_ID, batchID);
        values.put(COL_CALENDAR_DATE, date);
        values.put(COL_CALENDAR_NOTE, note);

        int result = db.update(TABLE_CALENDAR, values, COL_CALENDAR_ID + " = ?", new String[]{String.valueOf(calendarID)});
        return result > 0; // Return true if updated successfully, false otherwise
    }

    // Method to delete a calendar entry
    public boolean deleteCalendar(int calendarID) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CALENDAR, COL_CALENDAR_ID + " = ?", new String[]{String.valueOf(calendarID)});
        return result > 0; // Return true if deleted successfully, false otherwise
    }
}
