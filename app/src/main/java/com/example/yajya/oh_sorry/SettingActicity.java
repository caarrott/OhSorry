package com.example.yajya.oh_sorry;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ServiceInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckedTextView;

import java.util.List;

public class SettingActicity extends AppCompatActivity {
    SharedPreferences setting;
    SharedPreferences.Editor editor;

    CheckedTextView useTheater;
    CheckedTextView useLibrary;
    CheckedTextView useAutoStart;

    Button startSvcBtn;
    Button endSvcBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.activity_setting_acticity,null);
        getFragmentManager().beginTransaction().replace(R.id.settingLayout, new myPreferenceFragment()).commit();
        addContentView(v, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        startSvcBtn = (Button)findViewById(R.id.startBtn);
        endSvcBtn = (Button)findViewById(R.id.endBtn);

        if(isServiceRunning(this, myService.class)) {
            startSvcBtn.setEnabled(false);
            endSvcBtn.setEnabled(true);
        }
        else{
            startSvcBtn.setEnabled(true);
            endSvcBtn.setEnabled(false);
        }
    }
    public static class myPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);
        }
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
        startSvcBtn.setEnabled(false);
        endSvcBtn.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(),myService.class);
        startService(intent);
    }

    public void endSvc(View view) {
        startSvcBtn.setEnabled(true);
        endSvcBtn.setEnabled(false);
        Intent intent = new Intent(getApplicationContext(), myService.class);
        stopService(intent);
    }
    public static Boolean isServiceRunning(Context context, Class<?> serviceClass){
        final ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for(ActivityManager.RunningServiceInfo runningServiceInfo : services){
            if(runningServiceInfo.service.getClassName().equals(serviceClass.getName())){
                return true;
            }
        }
        return false;
    }
}
