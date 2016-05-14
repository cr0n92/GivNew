package com.givmed.android;

public class Donation {
    private String mName;
    private String mBarcode;
    private String mpharmName;
    private String mpharmNameGen;
    private String mDate1;
    private String mVolunteer;

    Donation(String name, String date1, String volunteer, String barcode, String pharmName, String pharmNameGen) {
        this.mName = name;
        this.mBarcode = barcode;
        this.mpharmName = pharmName;
        this.mpharmNameGen = pharmNameGen;
        this.mDate1 = date1;
        this.mVolunteer = volunteer;
    }

    //setters
    public void setName(String name) { mName = name; }

    public void setDate(String date) {
        mDate1 = date;
    }

    //getters
    public String getName() { return mName; }

    public String getBarcode() { return mBarcode; }

    public String getPharName() { return mpharmName; }

    public String getPharNameGen() { return mpharmNameGen; }

    public String getDate1() {
        return mDate1;
    }

    public String getVolunteer() {
        return mVolunteer;
    }

}
