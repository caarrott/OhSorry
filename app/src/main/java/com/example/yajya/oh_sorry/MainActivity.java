package com.example.yajya.oh_sorry;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    MyDBHandler dbHandler;
    Cursor cursor;
    MyListAdapter adapter;
    ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar bar = getSupportActionBar();
        if(bar != null){
            bar.hide();
        }

        try {
            init();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        checkFirstRun();
    }

    public void init() throws ParseException {
        listView = (ListView)findViewById(R.id.showList);
        dbHandler = new MyDBHandler(getApplicationContext(), null, null, 4);

//        SQLiteDatabase db = dbHandler.getWritableDatabase();
//        db.delete("places", null, null);

        cursor = dbHandler.getQueryResult("select * from places");
        cursor.moveToFirst();
        places = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            Place place5 = new Place(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3), cursor.getInt(4), cursor.getInt(5));

            places.add(place5);
            cursor.moveToNext();
        }
        adapter = new MyListAdapter(this, R.layout.row, places);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View toolbar = view.findViewById(R.id.toolbar);

                // Creating the expand animation for the item
                ExpandAnimation expandAni = new ExpandAnimation(toolbar, 100);

                // Start the animation on the toolbar
                toolbar.startAnimation(expandAni);
            }
        });


    }

    public void btnMap(View view) {
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
        finish();
    }

    public void btnSetting(View view) {
        Intent intent = new Intent(this, SettingActicity.class);
        startActivity(intent);
        finish();
    }
    private void checkFirstRun(){

        final String PREFS_NAME = getPackageName();
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;

        int currentVersionCode = BuildConfig.VERSION_CODE;

        SharedPreferences pref = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = pref.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        if(currentVersionCode == savedVersionCode){
            //normal run
            return;
        }
        else if(savedVersionCode == DOESNT_EXIST){
            //first run
            updateTheater();
            updateLibrary();
        }
        else if(currentVersionCode > savedVersionCode){
            //upgrade
            updateTheater();
            updateLibrary();
        }
        pref.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }
    public void updateTheater(){

        Ion.with(this)
                .load("http://openapi.seoul.go.kr:8088/4a796644636a6f6e37324f617a4558/json/SearchCulturalFacilitiesDetailService/1/1000")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            JSONObject facilities = json.getJSONObject("SearchCulturalFacilitiesDetailService");
                            JSONArray array = facilities.getJSONArray("row");
                            SQLiteDatabase db = dbHandler.getWritableDatabase();
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
                                //String query = "INSERT INTO theater(name,lat,lng) VALUES("+name+","+lat+","+lng+");";
                                //db.execSQL(query);
                            }


                        } catch (JSONException JSONe) {
                            JSONe.printStackTrace();
                        };
                    }
                });
        //SQLiteDatabase db = dbHandler.getReadableDatabase();
        //Cursor cursor = db.rawQuery("SELECT * FROM theater",null);
        //int kkk= cursor.getCount();
    }
    public void updateLibrary(){
        Ion.with(this)
                .load("http://openapi.seoul.go.kr:8088/4a796644636a6f6e37324f617a4558/json/SeoulPublicLibrary/1/1000")
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        try {
                            JSONObject json = new JSONObject(result);
                            JSONObject facilities = json.getJSONObject("SeoulPublicLibrary");
                            JSONArray array = facilities.getJSONArray("row");
                            SQLiteDatabase db = dbHandler.getWritableDatabase();
                            db.delete("library", null, null);
                            for(int i = 0 ; i < array.length(); i++){
                                String name = array.getJSONObject(i).getString("LBRRY_NAME");
                                String lat = array.getJSONObject(i).getString("XCNTS");
                                String lng = array.getJSONObject(i).getString("YDNTS");
                                ContentValues value = new ContentValues();
                                value.put("name",name);
                                value.put("lat",Double.parseDouble(lat));
                                value.put("lng",Double.parseDouble(lng));
                                db.insert("library",null,value);
                                //String query = "INSERT INTO library (name,lat,lng) VALUES("+name+","+Double.parseDouble()+","+lng+");";
                                //db.execSQL(query);
                            }


                        } catch (JSONException JSONe) {
                            JSONe.printStackTrace();
                        }
                    }
                });
    }

//    public void btnAdd(View view) {
//        Intent intent = new Intent(this, Map.class);
//        intent.putExtra("add", 1);
//        startActivity(intent);
//        finish();
//    }
}
