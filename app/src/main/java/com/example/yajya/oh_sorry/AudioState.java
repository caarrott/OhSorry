package com.example.yajya.oh_sorry;

/**
 * Created by JSK on 2017-05-29.
 */

public class AudioState {
    int ring;
    int alarm;
    int music;
    int ringerMode;

    public int getRingerMode() {
        return ringerMode;
    }

    public void setRingerMode(int ringerMode) {
        this.ringerMode = ringerMode;
    }

    public AudioState(int ring, int alarm, int music, int ringerMode) {
        this.ring = ring;
        this.alarm = alarm;
        this.music = music;
        this.ringerMode = ringerMode;

    }

    public int getRing() {
        return ring;
    }

    public void setRing(int ring) {
        this.ring = ring;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public int getMusic() {
        return music;
    }

    public void setMusic(int music) {
        this.music = music;
    }
}
