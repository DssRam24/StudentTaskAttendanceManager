package com.example.attendancemanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "StudentManager.db";
    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_STUDENTS = "students";
    public static final String COL_ROLL_NO = "roll_no";
    public static final String COL_NAME = "name";
    public static final String COL_ATTENDANCE = "attendance";
    public static final String COL_TASK = "task";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // roll number is the primary key, so it is always unique
        db.execSQL("CREATE TABLE " + TABLE_STUDENTS + " ("
                + COL_ROLL_NO + " TEXT PRIMARY KEY, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_ATTENDANCE + " REAL NOT NULL, "
                + COL_TASK + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // Add: returns false if the roll number already exists
    public boolean insertStudent(String rollNo, String name, double attendance, String task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ROLL_NO, rollNo);
        values.put(COL_NAME, name);
        values.put(COL_ATTENDANCE, attendance);
        values.put(COL_TASK, task);
        long result = db.insert(TABLE_STUDENTS, null, values);
        return result != -1;
    }

    // View / Search: an empty search text gives all the records
    public ArrayList<StudentModel> getStudents(String search) {
        ArrayList<StudentModel> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(TABLE_STUDENTS, null,
                COL_ROLL_NO + " LIKE ? OR " + COL_NAME + " LIKE ?",
                new String[]{"%" + search + "%", "%" + search + "%"},
                null, null, COL_ROLL_NO + " ASC");

        while (cursor.moveToNext()) {
            String roll = cursor.getString(cursor.getColumnIndexOrThrow(COL_ROLL_NO));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_NAME));
            double attendance = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ATTENDANCE));
            String task = cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK));
            list.add(new StudentModel(roll, name, attendance, task));
        }
        cursor.close(); // always close the cursor
        return list;
    }

    // Update: the roll number identifies the record
    public boolean updateStudent(String rollNo, String name, double attendance, String task) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_ATTENDANCE, attendance);
        values.put(COL_TASK, task);
        int rows = db.update(TABLE_STUDENTS, values, COL_ROLL_NO + "=?", new String[]{rollNo});
        return rows > 0;
    }

    // Delete
    public boolean deleteStudent(String rollNo) {
        SQLiteDatabase db = getWritableDatabase();
        int rows = db.delete(TABLE_STUDENTS, COL_ROLL_NO + "=?", new String[]{rollNo});
        return rows > 0;
    }
}
