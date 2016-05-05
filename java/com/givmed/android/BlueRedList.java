package com.givmed.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.givmed.android.R;

public class BlueRedList extends HelperActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "BlueRedList";
    ProgressDialog dialog;
    public static BlueRedAdapter mAdapter;
    public static boolean inRed = false;
    private static TextView firstMes;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main);
        super.helperOnCreate(R.layout.blue_red_list, R.string.two_buttons, true);

        firstMes = (TextView) findViewById(R.id.firstMes);
        mAdapter = new BlueRedAdapter(getApplicationContext());
        ListView list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(this);
        list.setFooterDividersEnabled(true);
        list.setAdapter(mAdapter);

        if(savedInstanceState == null || !savedInstanceState.containsKey("blueRedList")) {
            inRed = false;
            firstMes.setText(getString(R.string.br_first_msg_blue));

            if (mAdapter.getCount() == 0) {
//                mAdapter.add(new BlueRedItem("DEPON", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("LEXOTANIL", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("ULTRA", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("MAKIS", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("CECLOR", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("DEPON", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("ULTRA", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("MEDROL", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("DEPON", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("CECLOR", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("ULTRA", R.drawable.ic_tick_in_circle_gray));
//                mAdapter.add(new BlueRedItem("MAKIS", R.drawable.ic_tick_in_circle_gray));
            }
        }
        else {
            inRed = savedInstanceState.getBoolean("inRed");
            if (inRed)
                firstMes.setText(getString(R.string.br_first_msg_red));
            else
                firstMes.setText(getString(R.string.br_first_msg_blue));

            mAdapter.mItems = savedInstanceState.getParcelableArrayList("blueRedList");
        }
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        BlueRedItem item = (BlueRedItem) mAdapter.getItem(position);
        final ImageView statusView = (ImageView) v.findViewById(R.id.statusView);
        statusView.setImageResource(item.nextStatus());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {
            if (inRed) {
                matching();
            } else {
                inRed = true;
                firstMes.setText(getString(R.string.br_first_msg_red));
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (inRed) {
                inRed = false;
                firstMes.setText(getString(R.string.br_first_msg_blue));
                mAdapter.notifyDataSetChanged();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("blueRedList", mAdapter.mItems);
        savedInstanceState.putBoolean("inRed", inRed);
    }

    public void matching() {
        int matchedMeds = 0;

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.loading_msg));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        for (BlueRedItem item : mAdapter.mItems) {
            //matchedMeds += db.updateMedAndMatch();
        }

        dialog.dismiss();
        dialog = null;

        Intent toDonations = new Intent(getApplicationContext(), Dwrees.class);
        toDonations.putExtra("matchedMeds", matchedMeds);
        startActivity(toDonations);
        finish();
    }
}