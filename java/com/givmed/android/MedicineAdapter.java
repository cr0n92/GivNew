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

/**
 * Created by agroikos on 29/11/2015.
 */
public class MedicineAdapter extends BaseAdapter {

    public final List<Medicine> mItems = new ArrayList<Medicine>();
    private final Context mContext;

    public MedicineAdapter(Context context) {
        mContext = context;
    }

    public void add(Medicine item) {
        mItems.add(item);
        notifyDataSetChanged();
    }

    public void remove(Object item) {
        mItems.remove(item);
        notifyDataSetChanged();
    }

    // Clears the list adapter of all items.
    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    // Returns the number of Medicine
    @Override
    public int getCount() {
        return mItems.size();
    }

    // Retrieve the number of the Medicine
    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    // Get the ID for the Medicine
    // In this case it's just the position
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //called after setAdapter,notifyDataSetChanged
        final Medicine med = (Medicine) getItem(position);

        // from medicine.xml
        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.medicine, parent, false);

        // Fill in specific Medicine data
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        titleView.setText(med.getName());

        final TextView quantity = (TextView) itemLayout.findViewById(R.id.quantityView);
        quantity.setText(med.getPrice());

        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        dateView.setText(med.getDate());

        final TextView barcodeView = (TextView) itemLayout.findViewById(R.id.barcodeView);
        barcodeView.setText(med.getBarcode());

        // Return the View you just created
        return itemLayout;
    }
}
