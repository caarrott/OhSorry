package com.example.yajya.oh_sorry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by JSK on 2017-06-05.
 */

public class BootReciever extends BroadcastReceiver {
    SharedPreferences settings;
    @Override
    public void onReceive(Context context, Intent intent) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        if(settings.getBoolean("useAutoStart",false)) {
            if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                Intent i = new Intent(context, myService.class);
                context.startService(i);
            }
        }
    }
}
