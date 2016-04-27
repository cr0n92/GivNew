package com.givmed.android;

import android.database.Cursor;

public class Need {
    private String mPhone = new String();
    private String mName = new String();
    private String mAddr = new String();
    private String mNeedName = new String();
    private String mRegion = new String();

    public Need() { }

    // constructor
    public Need(Cursor cursor){
        this.mPhone= cursor.getString(0);
        this.mName = cursor.getString(1);
        this.mAddr = cursor.getString(2);
        this.mNeedName = cursor.getString(3);
        this.mRegion = cursor.getString(4);
    }

    // setters
    public void setPhone(String phone){
        mPhone = phone;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setAddress(String addr) {
        mAddr = addr;
    }

    public void setNeedName(String needName) {
        mNeedName = needName;
    }

    public void setRegion(String region) { mRegion = region; }

    // getters
    public String getPhone(){
        return mPhone;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddr;
    }

    public String getNeedName() {
        return mNeedName;
    }

    public String getRegion() { return mRegion; }

}