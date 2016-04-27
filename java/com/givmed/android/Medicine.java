package com.givmed.android;

import android.content.Intent;

/**
 * Created by agroikos on 29/11/2015.
 */
public class Medicine {
    public static final String ITEM_SEP = System.getProperty("line.separator");

    public final static String BARCODE = "barcode";
    public final static String NAME = "name";
    public final static String DATE = "date";
    public final static String PRICE = "price";

    //prepei na valoume kai to state kai na kovoume to onoma kai anti gia arxeio
    //na exoume mia vash me ta local farmaka giati mpales me autes tis mlkies

    private String mBarcode = new String();
    private String mName = new String();
    private String mDate = new String();
    private String mPrice = new String();

    Medicine() { }

    Medicine(String barcode, String name, String date, String price) {
        this.mBarcode = barcode;
        this.mName = name;
        this.mDate = date;
        this.mPrice = price;
    }

    // Create a new ToDoItem from data packaged in an Intent
    Medicine(Intent intent) {
        mBarcode = intent.getStringExtra(Medicine.BARCODE);
        mName = intent.getStringExtra(Medicine.NAME);
        mDate = intent.getStringExtra(Medicine.DATE);
        mPrice = intent.getStringExtra(Medicine.PRICE);
    }

    //setters
    public void setName(String name) { mName = name; }

    public void setPrice(String price) {
        mPrice = price;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    //getters
    public String getName() { return mName; }

    public String getPrice() {
        return mPrice;
    }

    public String getDate() {
        return mDate;
    }

    public String getBarcode() {
        return mBarcode;
    }

    // Take a set of String data values and
    // package them for transport in an Intent
    public static void packageIntent(Intent intent, String barcode, String name, String date, String price) {
        intent.putExtra("outputter", "new_med");
        intent.putExtra(Medicine.NAME, name);
        intent.putExtra(Medicine.PRICE, price);
        intent.putExtra(Medicine.DATE, date);
        intent.putExtra(Medicine.BARCODE, barcode);
    }

    public String toString() {
        return mName + ITEM_SEP + mPrice + ITEM_SEP + mDate + ITEM_SEP + mBarcode;
    }
}
