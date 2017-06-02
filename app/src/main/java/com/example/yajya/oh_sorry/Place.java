package com.example.yajya.oh_sorry;

/**
 * Created by yajya on 2017-05-26.
 */

public class Place {
    int mTag;
    String mName;
    double mLat, mLng;
    int mStart, mEnd;

    public Place(int tag, String name, double lat, double lng, int date1, int date2) {
        mTag = tag;
        mName = name;
        mLat = lat;
        mLng = lng;
        mStart = date1;
        mEnd = date2;
    }

    public Place(int tag, String name, double lat, double lng){
        mTag = tag;
        mName = name;
        mLat = lat;
        mLng = lng;
        mStart = 0;
        mEnd = 0;
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

    public int getStart() { return mStart; }

    public int getEnd() { return mEnd; }
}
