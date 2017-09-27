package com.example.junhaozeng.testdesign.Utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by junhaozeng on 2017/9/10.
 */

public class DbManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_steps";
    private static final String TABLE_STEPS = "tb_steps";
    private static final String COL_DATE = "date";
    private static final String COL_STEPS = "steps";
    private static final String SQL_CREATE_STEPS_TABLE
            = "CREATE TABLE " + TABLE_STEPS + " ("
            + COL_DATE + " TEXT PRIMARY KEY,"
            + COL_STEPS + " INTEGER" + " )";

    public DbManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_STEPS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_STEPS);
        onCreate(sqLiteDatabase);
    }

    public void insertRecord(String date, int steps) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_STEPS, steps);
        sqLiteDatabase.insert(TABLE_STEPS, null, contentValues);
        sqLiteDatabase.close();
    }

    public int readRecord(String date) {
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        int steps = -1;
        Cursor cursor = sqLiteDatabase.query(TABLE_STEPS,
                new String[]{COL_DATE, COL_STEPS},
                COL_DATE + " = ?",
                new String[]{date},
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            steps = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STEPS));
        }
        sqLiteDatabase.close();
        return steps;
    }

    public int updateRecord(String date, int steps) {
        int flag = 0;
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_STEPS, steps);
        flag =  sqLiteDatabase.update(TABLE_STEPS, contentValues,
                COL_DATE + "=?", new String[]{date});
        sqLiteDatabase.close();
        return flag;
    }

    public List<DateStepsPair> readAllRecords() {
        List<DateStepsPair> records = new ArrayList<>();
        String selectQuery = "SELECT * FROM "
                + TABLE_STEPS
                + " ORDER BY "
                + COL_DATE;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DateStepsPair record = new DateStepsPair();
                record.setDate(cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE)));
                record.setSteps(cursor.getInt(cursor.getColumnIndexOrThrow(COL_STEPS)));
                records.add(record);
            } while (cursor.moveToNext());
        }
        sqLiteDatabase.close();
        return records;
    }

    public void deleteRecord(String date) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE_STEPS, COL_DATE + " = ?",
                new String[]{date});
        sqLiteDatabase.close();
    }
}
