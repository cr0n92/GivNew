package com.givmed.android;

public class Donation {
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static String NAME = "name";
    public final static String REGION = "region";
    public final static String DATE = "date";

    private String mName = new String();
    private String mRegion = new String();
    private String mDate = new String();

    Donation() { }

    Donation(String name, String region, String date) {
        this.mName = name;
        this.mRegion = region;
        this.mDate = date;
    }

    // Create a new Donation from data packaged in an Intent
//    Donation(Intent intent) {
//        mName = intent.getStringExtra(MedName.NAME);
//        mCount = intent.getStringExtra(MedName.COUNT);
//        mDate = intent.getStringExtra(MedName.DATE);
//    }

    //setters
    public void setName(String name) { mName = name; }

    public void setDate(String date) {
        mDate = date;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    //getters
    public String getName() { return mName; }

    public String getDate() {
        return mDate;
    }

    public String getRegion() {
        return mRegion;
    }

    // Take a set of String data values and
    // package them for transport in an Intent
//    public static void packageIntent(Intent intent, String name, String count, String date) {
//        intent.putExtra(Medicine.NAME, name);
//        intent.putExtra(Medicine.PRICE, count);
//        intent.putExtra(Medicine.DATE, date);
//    }

    public String toString() {
        return mName + ITEM_SEP + mRegion + ITEM_SEP + mDate;
    }
}
