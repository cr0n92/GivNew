package com.givmed.android;

public class Donation {
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static String NAME = "name";
    public final static String COUNT = "count";
    public final static String DATE = "date";

    private String mName = new String();
    private String mCount = new String();
    private String mDate = new String();

    Donation() { }

    Donation(String name, String count, String date) {
        this.mName = name;
        this.mCount = count;
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

    // Take a set of String data values and
    // package them for transport in an Intent
//    public static void packageIntent(Intent intent, String name, String count, String date) {
//        intent.putExtra(Medicine.NAME, name);
//        intent.putExtra(Medicine.PRICE, count);
//        intent.putExtra(Medicine.DATE, date);
//    }

    public String toString() {
        return mName + ITEM_SEP + mCount + ITEM_SEP + mDate;
    }
}
