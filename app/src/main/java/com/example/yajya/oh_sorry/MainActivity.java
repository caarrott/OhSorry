package com.example.yajya.oh_sorry;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    MyDBHandler dbHandler;
    Cursor cursor;
    ArrayAdapter<String> adapter;
    String place[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init(){
        listView = (ListView)findViewById(R.id.showList);
        dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
        cursor = dbHandler.getQueryResult("select * from places");
        cursor.moveToFirst();
        place = new String[cursor.getCount()];
        while(!cursor.isAfterLast()){
            place[cursor.getPosition()]=cursor.getString(1);
            cursor.moveToNext();
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, place);
        listView.setAdapter(adapter);
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

//    public void btnAdd(View view) {
//        Intent intent = new Intent(this, Map.class);
//        intent.putExtra("add", 1);
//        startActivity(intent);
//        finish();
//    }
}
