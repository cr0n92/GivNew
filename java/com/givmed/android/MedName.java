package com.givmed.android;

import android.database.Cursor;

public class MedName {
    private String mName;
    private String mCount;
    private String mDate;

    MedName() { }

    MedName(String name, String count, String date) {
        mName = name;
        mCount = count;
        mDate = date;
    }

    MedName(Cursor cursor) {
        mName = cursor.getString(0);
        mCount = cursor.getString(1);
        mDate = cursor.getString(2);
    }

    //setters
    public void setName(String name) { mName = name; }

    public void setDate(String date) {
        mDate = date;
    }

    public void setCount(String count) {
        mCount = count;
    }

    //getters
    public String getName() { return mName; }

    public String getDate() {
        return mDate;
    }

    public String getCount() {
        return mCount;
    }
}
