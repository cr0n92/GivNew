package com.givmed.android;

public class MedName {
    private String mName = new String();
    private String mCount = new String();
    private String mDate = new String();

    MedName() { }

    MedName(String name, String count, String date) {
        this.mName = name;
        this.mCount = count;
        this.mDate = date;
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
