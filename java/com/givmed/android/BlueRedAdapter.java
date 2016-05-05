package com.givmed.android;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BlueRedAdapter extends BaseAdapter {

    public static ArrayList<BlueRedItem> mItems = new ArrayList<BlueRedItem>();
    private final Context mContext;

    public BlueRedAdapter(Context context) {
        mContext = context;
    }

    public void add(BlueRedItem item) {
        mItems.add(item);
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

    static class ViewHolder {
        TextView name;
        ImageView tick;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BlueRedItem medItem = (BlueRedItem) getItem(position);
        View vi = convertView;
        final ViewHolder holder;

        if (convertView == null) {
            vi = LayoutInflater.from(mContext).inflate(R.layout.blue_red_item, parent, false);

            holder = new ViewHolder();
            holder.name  = (TextView) vi.findViewById(R.id.medView);
            holder.tick  = (ImageView) vi.findViewById(R.id.statusView);

            vi.setTag(holder);
        } else
            holder = (ViewHolder) vi.getTag();

        holder.name.setText(medItem.getName());
        holder.tick.setImageResource(medItem.getStatus());

        holder.tick.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                holder.tick.setImageResource(medItem.nextStatus());
                notifyDataSetChanged();
            }
        });

        return vi;
    }
}
