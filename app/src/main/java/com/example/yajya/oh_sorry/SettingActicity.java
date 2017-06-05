package com.example.yajya.oh_sorry;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckedTextView;

public class SettingActicity extends AppCompatActivity {
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    CheckedTextView useTheater;
    CheckedTextView useLibrary;
    CheckedTextView useAutoStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_acticity);

        initSetting();
    }
    public void initSetting(){
        setting = getSharedPreferences("setting",0);
        editor = setting.edit();
        useTheater = (CheckedTextView)findViewById(R.id.checkTheater);
        useLibrary = (CheckedTextView)findViewById(R.id.checkLibrary);
        useAutoStart = (CheckedTextView)findViewById(R.id.checkAutoStart);
        useTheater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(useTheater.isChecked()){
                    //useTheater.setChecked(false);
                    editor.putBoolean("useTheater",false);
                    editor.commit();
                }
                else{
                    //useTheater.setChecked(true);
                    editor.putBoolean("useTheater",true);
                    editor.commit();
                }
            }
        });
        useLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(useLibrary.isChecked()){
                    //useLibrary.setChecked(false);
                    editor.putBoolean("useLibrary",false);
                    editor.commit();
                }
                else{
                    //useLibrary.setChecked(true);
                    editor.putBoolean("useLibrary",true);
                    editor.commit();
                }
            }
        });
        useAutoStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(useAutoStart.isChecked()){
                    //useAutoStart.setChecked(false);
                    editor.putBoolean("useAutoStart",false);
                    editor.commit();
                }
                else{
                    //useAutoStart.setChecked(true);
                    editor.putBoolean("useAutoStart",true);
                    editor.commit();
                }
            }
        });
        useAutoStart.setChecked(setting.getBoolean("useAutoStart",true));
        useLibrary.setChecked(setting.getBoolean("useLibrary",true));
        useTheater.setChecked(setting.getBoolean("useTheater",true));

        if(useAutoStart.isChecked())
            useAutoStart.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
        else
            useAutoStart.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        if(useTheater.isChecked())
            useTheater.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
        else
            useTheater.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
        if(useLibrary.isChecked())
            useLibrary.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
        else
            useLibrary.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);

        setting.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch(key){
                    case "useAutoStart":
                        useAutoStart.setChecked(setting.getBoolean("useAutoStart",true));
                        if(useAutoStart.isChecked())
                            useAutoStart.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                        else
                            useAutoStart.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
                        break;
                    case "useTheater":
                        useTheater.setChecked(setting.getBoolean("useTheater",true));
                        if(useTheater.isChecked())
                            useTheater.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                        else
                            useTheater.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);
                        break;
                    case "useLibrary":
                        useLibrary.setChecked(setting.getBoolean("useLibrary",true));
                        if(useLibrary.isChecked())
                            useLibrary.setCheckMarkDrawable(android.R.drawable.checkbox_on_background);
                        else
                            useLibrary.setCheckMarkDrawable(android.R.drawable.checkbox_off_background);

                        break;
                }
            }
        });
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

    public void startSvc(View view) {
        Intent intent = new Intent(getApplicationContext(),myService.class);
        startService(intent);
    }

    public void endSvc(View view) {
        Intent intent = new Intent(getApplicationContext(), myService.class);
        stopService(intent);
    }
}
