package com.sync.auto;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME   = "simple_sync";
    public static final String table_name = "student";
    public static final String column_id   = "id";
    public static final String column_name = "name";
    public static final String column_status = "status";

    // db version
    private static final int DB_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String db = "CREATE TABLE " + table_name + "(" +
                column_id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                column_name + " VARCHAR, " +
                column_status + " TINYINT " +
                ")";
        sqLiteDatabase.execSQL(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql = "DROP TABLE IF EXISTS " + table_name;
        sqLiteDatabase.execSQL(sql);
        onCreate(sqLiteDatabase);
    }

    public boolean addName(String name, int status) {
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(column_name, name);
        contentValues.put(column_status, status);
        db.insert(table_name, null, contentValues);
        db.close();

        return true;
    }

    public boolean updateStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(column_status, status);
        db.update(table_name, contentValues, column_id + " = " +id, null );
        db.close();

        return true;
    }

    public Cursor getName() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + table_name + " ORDER BY " + column_id + " DESC";
        Cursor result = db.rawQuery(sql, null);

        return result;
    }

    public Cursor getUnsyncedName() {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "SELECT * FROM " + table_name + " WHERE " + column_status + "=0";
        Cursor result = db.rawQuery(sql, null);

        return result;
    }










}
