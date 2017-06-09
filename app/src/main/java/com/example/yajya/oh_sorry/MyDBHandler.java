package com.example.yajya.oh_sorry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    Context context;
    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS "+DATABASE_TABLE+"("
                        +COLUMN_MAKERTAG+" INTEGER PRIMARY KEY,"
                        +COLUMN_NAME+" TEXT,"
                        +COLUMN_LAT+" DOUBLE,"
                        +COLUMN_LNG+" DOUBLE,"
                        +COLUMN_START+" INTEGER,"
                        +COLUMN_END+" INTEGER );";
        db.execSQL(CREATE_TABLE);
        String CREATE_THEATER =
                "CREATE TABLE IF NOT EXISTS theater("
                        +"key INTEGER PRIMARY KEY AUTOINCREMENT,"
                        +"name TEXT,"
                        +"lat DOUBLE,"
                        +"lng DOUBLE,"
                        +"UNIQUE(name)"
                        +");";
        db.execSQL(CREATE_THEATER);
        String CREATE_LIBRARY =
                "CREATE TABLE IF NOT EXISTS library("
                        +"key INTEGER PRIMARY KEY AUTOINCREMENT,"
                        +"name TEXT,"
                        +"lat DOUBLE,"
                        +"lng DOUBLE,"
                        +"UNIQUE(name)"
                        +");";
        db.execSQL(CREATE_LIBRARY);
        updateTheater();
        updateLibrary();
    }
    public void updateTheater(){
        Ion.with(context)
                .load("http://openapi.seoul.go.kr:8088/4a796644636a6f6e37324f617a4558/json/SearchCulturalFacilitiesDetailService/1/1000")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            JSONObject facilities = json.getJSONObject("SearchCulturalFacilitiesDetailService");
                            JSONArray array = facilities.getJSONArray("row");
                            SQLiteDatabase db = getWritableDatabase();
                            db.delete("theater",null,null);
                            for(int i = 0 ; i < array.length(); i++){
                                String name = array.getJSONObject(i).getString("FAC_NAME");
                                String lat = array.getJSONObject(i).getString("X_COORD");
                                String lng = array.getJSONObject(i).getString("Y_COORD");
                                ContentValues value = new ContentValues();
                                value.put("name",name);
                                value.put("lat",Double.parseDouble(lat));
                                value.put("lng",Double.parseDouble(lng));
                                db.insert("theater",null,value);
                            }


                        } catch (JSONException JSONe) {
                            JSONe.printStackTrace();
                        };
                    }
                });
    }
    public void updateLibrary(){
        Ion.with(context)
                .load("http://openapi.seoul.go.kr:8088/4a796644636a6f6e37324f617a4558/json/SeoulPublicLibrary/1/1000")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            JSONObject facilities = json.getJSONObject("SeoulPublicLibrary");
                            JSONArray array = facilities.getJSONArray("row");
                            SQLiteDatabase db = getWritableDatabase();
                            db.delete("library",null,null);
                            for(int i = 0 ; i < array.length(); i++){
                                String name = array.getJSONObject(i).getString("LBRRY_NAME");
                                String lat = array.getJSONObject(i).getString("XCNTS");
                                String lng = array.getJSONObject(i).getString("YDNTS");
                                ContentValues value = new ContentValues();
                                value.put("name",name);
                                value.put("lat",Double.parseDouble(lat));
                                value.put("lng",Double.parseDouble(lng));
                                db.insert("library",null,value);
                            }
                        } catch (JSONException JSONe) {
                            JSONe.printStackTrace();
                        };
                    }
                });
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS theater");
        db.execSQL("DROP TABLE IF EXISTS library");
        onCreate(db);
    }

    public void addPlace(Place place){
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAKERTAG, place.getTag());
        values.put(COLUMN_NAME, place.getName());
        values.put(COLUMN_LAT, place.getLat());
        values.put(COLUMN_LNG, place.getLng());
        values.put(COLUMN_START, 0);
        values.put(COLUMN_END, 0);

        SQLiteDatabase db = this.getWritableDatabase();

        db.insert(DATABASE_TABLE, null, values);
        db.close();
    }

    public void updatePlace(Place place){
        ContentValues values = new ContentValues();
        values.put(COLUMN_MAKERTAG, place.getTag());
        values.put(COLUMN_NAME, place.getName());
        values.put(COLUMN_LAT, place.getLat());
        values.put(COLUMN_LNG, place.getLng());
        values.put(COLUMN_START, place.getStart());
        values.put(COLUMN_END, place.getEnd());

        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete("places", "name=?", new String[]{item.getName()});
//        db.insert("places", null, values);

        db.update(DATABASE_TABLE, values, "name=?", new String[]{place.getName()});
        db.close();
    }

    public void deletePlace(int tag) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DATABASE_TABLE, "markertag=?", new String[]{String.valueOf(tag)});
        db.close();
    }

    public Cursor getQueryResult(String str) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery(str, null);
    }
}