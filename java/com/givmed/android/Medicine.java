package com.givmed.android;

import android.database.Cursor;

public class Medicine {
    private String mBarcode;
    private String mEofcode;
    private String mName;
    private String mDate;
    private String mPrice;
    private String mNotes;
    private String mState;
    private String mSubstance;
    private String mCategory;
    private String mStatus;

    Medicine() { }

    Medicine(String barcode, String eofcode, String name, String date, String price, String notes,
             String state, String substance, String category, String status) {
        mBarcode = barcode;
        mEofcode = eofcode;
        mName = name;
        mDate = date;
        mPrice = price;
        mNotes = notes;
        mState = state;
        mSubstance = substance;
        mCategory = category;
        mStatus = status;
    }

//    Medicine(Cursor cursor) {
//        mBarcode = cursor.getString(0);
//        mEofcode = cursor.getString(0);
//        mName = cursor.getString(1);
//        mDate = cursor.getString(2);
//        mPrice = cursor.getString(3);
//        mNotes = cursor.getString(4);
//        mState = cursor.getString(5);
//        mSubstance = cursor.getString(6);
//        mCategory = cursor.getString(7);
//        mStatus = cursor.getString(9);
//    }

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

    public void setEofcode(String eofcode) {
        mEofcode = eofcode;
    }

    public void setNotes(String notes) { mNotes = notes; }

    public void setState(String state) {
        mState = state;
    }

    public void setSubstance(String substance) {
        mSubstance = substance;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public void setStatus(String status) { mStatus = status; }

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

    public String getEofcode() {
        return mEofcode;
    }

    public String getNotes() { return mNotes; }

    public String getState() {
        return mState;
    }

    public String getSubstance() {
        return mSubstance;
    }

    public String getCategory() {
        return mCategory;
    }

    public String getStatus() { return mStatus; }

}
