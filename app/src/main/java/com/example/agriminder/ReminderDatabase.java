package com.example.agriminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.Time;

public class ReminderDatabase extends SQLiteOpenHelper {

    // Database name and version
    private static final String DATABASE_NAME = "reminders.db";
    private static final int DATABASE_VERSION = 2;

    // Table and columns
    public static final String TABLE_NAME = "reminders";
    public static final String COL_ID = "_id"; // Changed column name to _id
    public static final String COL_REMINDER_TITLE = "REMINDER_TITLE";
    public static final String COL_REMINDER_TIME = "REMINDER_TIME";
    public static final String COL_REMINDER_DAY = "REMINDER_DAY";
    public static final String COL_REMINDER_LOCATION = "REMINDER_LOCATION";

    public ReminderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the products table
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_REMINDER_TITLE + " TEXT, " +
                COL_REMINDER_TIME + " TEXT, " +
                COL_REMINDER_DAY + " TEXT," +
                COL_REMINDER_LOCATION + " TEXT )";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table if it exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a new product into the database
    public boolean insertData(String reminderTitle, String reminderTime, String reminderDay, String reminderLocation) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_REMINDER_TITLE, reminderTitle);
        contentValues.put(COL_REMINDER_TIME, reminderTime);
        contentValues.put(COL_REMINDER_DAY, reminderDay);
        contentValues.put(COL_REMINDER_LOCATION, reminderLocation);

        // Insert the new row
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // If result is -1, insertion failed
    }

    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT _id, REMINDER_TITLE, REMINDER_TIME, REMINDER_DAY, REMINDER_LOCATION FROM reminders", null);
    }

    public boolean deleteData(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("reminders", "_id = ?", new String[]{String.valueOf(id)}) > 0;
    }
}
