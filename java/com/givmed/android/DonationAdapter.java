package com.givmed.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agroikos on 29/11/2015.
 */
public class DonationAdapter extends BaseAdapter {

    public final List<Donation> mItems = new ArrayList<Donation>();
    private final Context mContext;

    public DonationAdapter(Context context) {
        mContext = context;
    }

    public void add(Donation item) {
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

    // Returns the number of Donation
    @Override
    public int getCount() {
        return mItems.size();
    }

    // Retrieve the number of the Donation
    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    // Get the ID for the Donation
    // In this case it's just the position
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //called after setAdapter,notifyDataSetChanged
        final Medicine med = (Medicine) getItem(position);

        // from donation.xml
        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.donation, parent, false);

        // Fill in specific Donation data
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.medView);
        titleView.setText(med.getName());

        final TextView quantity = (TextView) itemLayout.findViewById(R.id.pharView);
        quantity.setText(med.getPrice());

        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        dateView.setText(med.getDate());

        // Return the View you just created
        return itemLayout;
    }
}
