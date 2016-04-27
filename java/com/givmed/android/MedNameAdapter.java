package com.givmed.android;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MedNameAdapter extends BaseAdapter {

    public final List<MedName> mItems = new ArrayList<MedName>();
    private final Context mContext;

    public MedNameAdapter(Context context) {
        mContext = context;
    }

    public void add(MedName item) {
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

    // Returns the number of Medicine Name
    @Override
    public int getCount() {
        Log.e("count",""+mItems.size());

        return mItems.size();
    }

    // Retrieve the number of the Medicine Name
    @Override
    public Object getItem(int pos) {
        return mItems.get(pos);
    }

    // Get the ID for the Medicine Name
    // In this case it's just the position
    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) { //called after setAdapter,notifyDataSetChanged
        final MedName name = (MedName) getItem(position);
        String num = "";

        // from medName.xml
        LinearLayout itemLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.med_name, parent, false);

        // Fill in specific MedName data
        // Remember that the data that goes in this View
        // corresponds to the user interface elements defined
        // in the layout file

        final TextView titleView = (TextView) itemLayout.findViewById(R.id.titleView);
        if (!name.getCount().equals("1")) num = " (" + name.getCount() + ")";
        titleView.setText(name.getName() + num);

        final TextView dateView = (TextView) itemLayout.findViewById(R.id.dateView);
        dateView.setText(name.getDate());

        // Return the View you just created
        return itemLayout;
    }
}
