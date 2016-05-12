package com.givmed.android;

public class Donation {
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static String NAME = "name";
    public final static String BARCODE = "barcode";
    public final static String PHARNAME = "pharmName";
    public final static String PHARNAMEGEN = "pharmNameGen";

    public final static String DATE1 = "date1";
    public final static String DATE2 = "date2";
    public final static String DATE3 = "date3";
    public final static String VOLUNTEER = "volunteer";



    private String mName ;
    private String mBarcode ;

    private String mpharmName ;

    private String mpharmNameGen ;


    private String mDate1 ;
    private String mDate2 ;
    private String mDate3 ;
    private String mVolunteer ;



    Donation(String name, String date1,String date2,String date3,String volunteer,String barcode,String pharmName,String pharmNameGen) {
        this.mName = name;
        this.mBarcode = barcode;

        this.mpharmName = pharmName;

        this.mpharmNameGen = pharmNameGen;

        this.mDate1 = date1;
        this.mDate1 = date2;

        this.mDate1 = date3;

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
    public String getDate2() {
        return mDate2;
    }

    public String getDate3() {
        return mDate3;
    }

    public String getVolunteer() {
        return mVolunteer;
    }




}
