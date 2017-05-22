package com.example.yajya.oh_sorry;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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

    public void btnAdd(View view) {
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
        finish();
    }
}
