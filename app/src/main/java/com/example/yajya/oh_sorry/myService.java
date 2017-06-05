package com.example.yajya.oh_sorry;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by JSK on 2017-05-22.
 */

public class myService extends Service {
    AudioState audioState;
    boolean isMuted;
    boolean isRunning;
    LocationManager lm;
    LocationListener LocaListen;
    String gpsX;
    String gpsY;

    Notification notifier;
    RemoteViews contentView;
    NotificationCompat.Builder mCompatBuilder;

    BroadcastReceiver broadRcvr;

    MyDBHandler dbHandler;

    Location formerLocation;

    static final int NOTIFICATION_ID = 931222;

    SharedPreferences settings;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getSound() {
        AudioManager mgr = (AudioManager) getSystemService(AUDIO_SERVICE);
        int ringerMode = mgr.getRingerMode();
        int ringVolume = mgr.getStreamVolume(AudioManager.STREAM_RING);
        int musicVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
        int alarmVolume = mgr.getStreamVolume(AudioManager.STREAM_ALARM);

        if (!isMuted) {
            audioState = new AudioState(ringVolume, alarmVolume, musicVolume, ringerMode);
        }
    }

    public void setSound() {
        if (audioState != null) {
            AudioManager mgr = (AudioManager) getSystemService(AUDIO_SERVICE);
            mgr.setRingerMode(audioState.getRingerMode());
            mgr.setStreamVolume(AudioManager.STREAM_ALARM, audioState.getAlarm(), 0);
            mgr.setStreamVolume(AudioManager.STREAM_RING, audioState.getRing(), 0);
            mgr.setStreamVolume(AudioManager.STREAM_MUSIC, audioState.getMusic(), 0);
            isMuted = false;
        }
    }

    public void setMute() {
        getSound();
        isMuted = true;
        AudioManager mgr = (AudioManager) getSystemService(AUDIO_SERVICE);
        mgr.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    public void reSound() {
        getSound();
        isMuted = false;
        AudioManager mgr = (AudioManager) getSystemService(AUDIO_SERVICE);
        mgr.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //서비스가 호출될 떄마다 실행
        try {
            Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                gpsX = String.valueOf(location.getLongitude());
                gpsY = String.valueOf(location.getLatitude());
            }
            formerLocation = location;

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, LocaListen);
        } catch (SecurityException e) {
            e.printStackTrace();
            //접근권한 미 설정시 실행될 상황
            Log.v("test6767", "failed");
        }


        startForeground(NOTIFICATION_ID, notifier);
        return super.onStartCommand(intent, flags, startId);
    }

    public void initDB() {
        dbHandler = new MyDBHandler(getApplicationContext(), null, null, 4);
    }

    public void init() {
        settings = getSharedPreferences("setting",0);
        isRunning = true;
        isMuted = false;
        gpsX = "gpsX";
        gpsY = "gpsY";
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        contentView = new RemoteViews(getPackageName(), R.layout.notifier);
        contentView.setTextViewText(R.id.svcTitle, "아이고 나네");
        contentView.setTextViewText(R.id.svcStatus, "서비스가 실행중입니다");
        contentView.setImageViewResource(R.id.logoImg, R.drawable.ic_launcher);
        contentView.setImageViewResource(R.id.statusBtn, R.drawable.onoff);

        LocaListen = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                gpsX = "Altitude : " + String.valueOf(location.getLongitude());
                gpsY = "Latitude : " + String.valueOf(location.getLatitude());

                if (formerLocation.distanceTo(location) >= 0) {
                    formerLocation.setLongitude(location.getLongitude());
                    formerLocation.setLatitude(location.getLatitude());
                    SQLiteDatabase db = dbHandler.getReadableDatabase();
                    if(settings.getBoolean("useTheater",true)) {
                        String findTheater = "SELECT * FROM theater;";
                        Cursor cursor = db.rawQuery(findTheater, null);
                        cursor.moveToFirst();
                        int colCount = cursor.getColumnCount();
                        int rowCount = cursor.getCount();

                        while (!cursor.isAfterLast()) {
                            double latitude = cursor.getDouble(2);
                            double longitude = cursor.getDouble(3);
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            if (formerLocation.distanceTo(location) < 10) {
                                setMute();
                                return;
                            }
                            cursor.moveToNext();
                        }
                    }

                    if(settings.getBoolean("useLibrary",true)) {
                        String findLibrary = "SELECT * FROM library;";
                        Cursor cursor = db.rawQuery(findLibrary, null);
                        cursor.moveToFirst();
                        while (!cursor.isAfterLast()) {
                            double latitude = cursor.getDouble(2);
                            double longitude = cursor.getDouble(3);
                            location.setLatitude(latitude);
                            location.setLongitude(longitude);
                            if (formerLocation.distanceTo(location) < 10) {
                                setMute();
                                return;
                            }
                            cursor.moveToNext();
                        }
                    }
                    String findCustomPlace = "SELECT * FROM places;";
                    Cursor cursor=db.rawQuery(findCustomPlace, null);
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()){
                        double latitude = cursor.getDouble(2);
                        double longitude = cursor.getDouble(3);
                        int startTime = cursor.getInt(4);
                        int endTime = cursor.getInt(5);
                        location.setLatitude(latitude);
                        location.setLongitude(longitude);
                        if (formerLocation.distanceTo(location) < 100) {
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy:MM:dd-hh:mm:ss");
                            String datetime1 = sdf1.format(cal.getTime());

                            int hour = cal.get(Calendar.HOUR);
                            int minute = cal.get(Calendar.MINUTE);

                            int curTime = hour * 100 + minute;

                            if(startTime != 0 && endTime != 0) {
                                if (curTime >= startTime && curTime <= endTime)
                                    setMute();
                                else
                                    reSound();
                            } else
                                setMute();
                            return;
                        }
                        cursor.moveToNext();
                    }
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    public void initNotification() {
        Intent _intent = new Intent("com.example.jsk.muter.myService");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.statusBtn, pendingIntent);


        mCompatBuilder = new NotificationCompat.Builder(this);
        mCompatBuilder.setSmallIcon(R.drawable.ic_launcher);
        // mCompatBuilder.setTicker("NotificationCompat.Builder");
        //mCompatBuilder.setWhen(System.currentTimeMillis());
        //mCompatBuilder.setNumber(10);
        mCompatBuilder.setContent(contentView);
        //Intent _intent = new Intent("com.example.jsk.muter.MainActivity");
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, _intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mCompatBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        //mCompatBuilder.setContentIntent(pendingIntent);
        // mCompatBuilder.setAutoCancel(true);

        notifier = mCompatBuilder.build();
        broadRcvr = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isRunning) {
                    contentView.setTextViewText(R.id.svcStatus, "서비스가 중단되었습니다.");
                    isRunning = false;
                    mCompatBuilder.setContent(contentView);
                    notifier = mCompatBuilder.build();
                    startForeground(NOTIFICATION_ID, notifier);
                    lm.removeUpdates(LocaListen);
                } else {
                    contentView.setTextViewText(R.id.svcStatus, "서비스가 실행중입니다.");
                    isRunning = true;
                    mCompatBuilder.setContent(contentView);
                    notifier = mCompatBuilder.build();
                    startForeground(NOTIFICATION_ID, notifier);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, LocaListen);
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.jsk.muter.myService");
        registerReceiver(broadRcvr, intentFilter);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //서비스에서 가장 먼저 호출됨

        init();
        initNotification();
        initDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try{
            lm.removeUpdates(LocaListen);
        }catch (SecurityException e) {
            e.printStackTrace();
        }
        unregisterReceiver(broadRcvr);
        reSound();
    }
}
