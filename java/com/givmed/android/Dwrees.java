package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Dwrees extends HelperActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "Dwrees";
    private static boolean inDone = false;
    public static DonationAdapter mAdapter;
    private static TextView msgView;
    private static Button progButton, doneButton;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.dwrees, R.string.dwrees, true);

        String left = getResources().getString(R.string.pharm_left_half_msg);
        String right = getResources().getString(R.string.donation_euros);
        String mid_plu = getResources().getString(R.string.donation_meds);
        String mid_sin = getResources().getString(R.string.donation_med);

        msgView = (TextView) findViewById(R.id.firstMes);
        progButton = (Button) findViewById(R.id.progButton);
        doneButton = (Button) findViewById(R.id.doneButton);
        msgView.setText(left + " 0 " + mid_plu + " 0 " + right);

        progButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(progButton, doneButton, R.drawable.button_pressed_left, R.drawable.button_unpressed_right);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(doneButton, progButton, R.drawable.button_pressed_right, R.drawable.button_unpressed_left);
            }
        });

        mAdapter = new DonationAdapter(getApplicationContext());

        ListView list = (ListView) findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        registerForContextMenu(list);
        list.setAdapter(mAdapter);

        db = new DBHandler(getApplicationContext());

        int count = db.getAllDonations(mAdapter);
        double price = 0.0;
        String mid_msg = (count == 1) ? mid_sin : mid_plu;
        msgView.setText(left + " " + count + " " + mid_msg + " " + price + " " + right);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Donation progDonation = (Donation) mAdapter.getItem(position);

        if (progDonation.getVolunteer().equals("V")) {
            Intent intent = new Intent(getApplicationContext(), DwreaVolunteer.class);
            startActivity(intent);
        }
        else if (progDonation.getVolunteer().equals("U")) {
            Intent intent = new Intent(getApplicationContext(), DwreaUser.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(getApplicationContext(), AfterDwrees.class);
            intent.putExtra("pharmName", progDonation.getPharNameGen());
            startActivity(intent);
        }
    }
}