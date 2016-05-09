package com.givmed.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.givmed.android.R;

import java.util.ArrayList;
import java.util.List;


public class MedicineAdapter extends BaseAdapter {

    public final List<String> mItems = new ArrayList<String>();
    private final Context mContext;

    public MedicineAdapter(Context context) {
        mContext = context;
    }

    public void add(String item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void remove(Object item) {
        mItems.remove(item);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //called after setAdapter,notifyDataSetChanged
        final String barcode = (String) getItem(position);

        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.medicine, parent, false);

        final TextView barcodeView = (TextView) itemLayout.findViewById(R.id.barcodeView);
        barcodeView.setText("Barcode: " + barcode);

        return itemLayout;
    }
}
