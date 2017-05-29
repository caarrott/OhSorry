package com.example.yajya.oh_sorry;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yajya on 2017-05-26.
 */

public class Place {
    int mTag;
    String mName;
    double mLat, mLng;
    SimpleDateFormat mStart, mEnd;

    public Place(int tag, String name, double lat, double lng, SimpleDateFormat simpleDateFormat1, SimpleDateFormat simpleDateFormat2) {
        mTag = tag;
        mName = name;
        mLat = lat;
        mLng = lng;
        mStart = simpleDateFormat1;
        mEnd = simpleDateFormat2;
    }

    public Place(int tag, String name, double lat, double lng){
        mTag = tag;
        mName = name;
        mLat = lat;
        mLng = lng;
        mStart = null;
        mEnd = null;
    }

    public int getTag() { return mTag; }

    public String getName() {
        return mName;
    }

    public double getLat() {
        return mLat;
    }

    public double getLng() {
        return mLng;
    }

    public String getStart() {
        Date from = new Date();
        String _mStart = mStart.format(from);
        return _mStart;
    }

    public String getEnd() {
        Date from = new Date();
        String _mEnd = mEnd.format(from);
        return _mEnd;
    }
}
