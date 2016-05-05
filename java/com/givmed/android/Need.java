package com.givmed.android;

import android.database.Cursor;

public class Need {
    private String mPhone;
    private String mNeedName;
    private String mRegion;

    public Need() { }

    // constructor
    public Need(Cursor cursor) {
        mPhone = cursor.getString(0);
        mNeedName = cursor.getString(1);
        mRegion = cursor.getString(2);
    }

    // setters
    public void setPhone(String phone){
        mPhone = phone;
    }

    public void setNeedName(String needName) {
        mNeedName = needName;
    }

    public void setRegion(String region) { mRegion = region; }

    // getters
    public String getPhone(){
        return mPhone;
    }

    public String getNeedName() {
        return mNeedName;
    }

    public String getRegion() { return mRegion; }

}