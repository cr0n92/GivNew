package com.givmed.android;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class BlueRedItem implements Parcelable {

    static final int gray = R.drawable.ic_tick_in_circle_gray;
    static final int blue = R.drawable.ic_tick_in_circle_blue;
    static final int red = R.drawable.ic_tick_in_circle_red;

    private String mBarcode;
    private String mName;
    private boolean mSirup;
    private int mBlueStatus = gray;
    private int mRedStatus = gray;

    BlueRedItem(String barcode, String name, boolean sirup, int blueS) {
        mBarcode = barcode;
        mName = name;
        mSirup = sirup;
        mBlueStatus = mRedStatus = blueS;
    }

    private BlueRedItem(Parcel in) {
        mBarcode = in.readString();
        mName = in.readString();
        mSirup = in.readByte() != 0;
        mBlueStatus = in.readInt();
        mRedStatus = in.readInt();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(mBarcode);
        out.writeString(mName);
        out.writeByte((byte) (mSirup ? 1 : 0));
        out.writeInt(mBlueStatus);
        out.writeInt(mRedStatus);
    }

    public static final Parcelable.Creator<BlueRedItem> CREATOR = new Parcelable.Creator<BlueRedItem>() {
        public BlueRedItem createFromParcel(Parcel in) {
            return new BlueRedItem(in);
        }

        public BlueRedItem[] newArray(int size) {
            return new BlueRedItem[size];
        }
    };

    public boolean getSirup() {
        return mSirup;
    }

    public String getBarcode() {
        return mBarcode;
    }

    public void setBarcode(String barcode) {
        mBarcode = barcode;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getStatus() {
        if (BlueRedList.inRed)
            return mRedStatus;
        else {
            mRedStatus = mBlueStatus;
            return mBlueStatus;
        }
    }

    public int swapBlue() {
        if (mBlueStatus == gray)
            mBlueStatus = mRedStatus = blue;
        else
            mBlueStatus = mRedStatus = gray;

        return mBlueStatus;
    }

    public int swapRed() {
        if (mRedStatus == red)
            mRedStatus = mBlueStatus;
        else
            mRedStatus = red;

        return mRedStatus;
    }

    public int nextStatus() {
        if (BlueRedList.inRed)
            return swapRed();
        else
            return swapBlue();
    }
}

