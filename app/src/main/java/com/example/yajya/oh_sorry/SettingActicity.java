package com.example.yajya.oh_sorry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SettingActicity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_acticity);
    }

    public void btnMap(View view) {
        Intent intent = new Intent(this, Map.class);
        startActivity(intent);
        finish();
    }

    public void btnList(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
