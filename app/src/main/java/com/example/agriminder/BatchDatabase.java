package com.example.agriminder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BatchDatabase extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "Agriminder.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name and Columns
    private static final String TABLE_BATCH = "Batch";
    private static final String COLUMN_BATCH_ID = "Batch_ID";
    private static final String COLUMN_BATCH_NAME = "Batch_Name";
    private static final String COLUMN_BATCH_TYPE = "Batch_Type";
    private static final String COLUMN_BATCH_DOB = "Batch_DateofBirth";

    public BatchDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BATCH_TABLE = "CREATE TABLE " + TABLE_BATCH + " (" +
                COLUMN_BATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_BATCH_NAME + " TEXT, " +
                COLUMN_BATCH_TYPE + " TEXT, " +
                COLUMN_BATCH_DOB + " TEXT)";
        db.execSQL(CREATE_BATCH_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATCH);
        onCreate(db);
    }

    // Add a new batch
    public void addBatch(String batchName, String batchType, String batchDOB) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BATCH_NAME, batchName);
        values.put(COLUMN_BATCH_TYPE, batchType);
        values.put(COLUMN_BATCH_DOB, batchDOB);

        db.insert(TABLE_BATCH, null, values);
        db.close();
    }

    // Fetch all batches
    public Cursor getAllBatches() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_BATCH, null);
    }
}
