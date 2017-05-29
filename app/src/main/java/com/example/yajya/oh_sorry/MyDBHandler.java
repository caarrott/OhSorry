package com.example.yajya.oh_sorry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yajya on 2017-05-27.
 */

public class MyDBHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "customDB.db";
    public static final String DATABASE_TABLE = "places";

    public static final String COLUMN_MAKERTAG = "markertag";
    public static final String COLUMN_LAT = "lat";
    public static final String COLUMN_LNG = "lng";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";
    public static final String COLUMN_NAME = "name";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS "+DATABASE_TABLE+"("
                        +COLUMN_MAKERTAG+" INTEGER PRIMARY KEY,"
                        +COLUMN_NAME+" TEXT,"
                        +COLUMN_LAT+" DOUBLE,"
                        +COLUMN_LNG+" DOUBLE,"
                        +COLUMN_START+" TEXT,"
                        +COLUMN_END+" TEXT );";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS"+DATABASE_TABLE);
        onCreate(db);
    }

    public void addPlace(Place place){
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAKERTAG, place.getTag());
        values.put(COLUMN_NAME, place.getName());
        values.put(COLUMN_LAT, place.getLat());
        values.put(COLUMN_LNG, place.getLng());
        values.put(COLUMN_START, " ");
        values.put(COLUMN_END, " ");

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    public Cursor getQueryResult(String str) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(str, null);
    }
}
