package com.givmed.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
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
    private String progDonationMsg, doneDonationMsg;
    private String left, right, mid_plu, mid_sin, progRight_sin, progRight_plu;
    public static DonationAdapter progAdapter,doneAdapter;
    private TextView msgView, msgView2;
    private static Button progButton, doneButton;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.dwrees, R.string.dwrees, false);

        db = new DBHandler(getApplicationContext());

        left = getResources().getString(R.string.donation_med_left);
        right = getResources().getString(R.string.donation_euros);
        mid_plu = getResources().getString(R.string.donation_values);
        mid_sin = getResources().getString(R.string.donation_value);

        progRight_sin = getResources().getString(R.string.donation_done_right);
        progRight_plu = getResources().getString(R.string.donation_done_rights);

        msgView = (TextView) findViewById(R.id.firstMes);
        msgView2 = (TextView) findViewById(R.id.secondMes);
        progButton = (Button) findViewById(R.id.progButton);
        doneButton = (Button) findViewById(R.id.doneButton);

        progAdapter = new DonationAdapter(getApplicationContext(),true);
        doneAdapter = new DonationAdapter(getApplicationContext(),false);

        final ListView list = (ListView) findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        list.setOnItemClickListener(this);
        list.setAdapter(progAdapter);

        progButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inDone = false;
                changeButtonsLayout(progButton, doneButton, R.drawable.button_pressed_left, R.drawable.button_unpressed_right, Color.WHITE, Color.BLACK);
                msgView.setText(progDonationMsg);
                msgView2.setVisibility(View.VISIBLE);
                list.setAdapter(progAdapter);
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inDone = true;
                changeButtonsLayout(doneButton, progButton, R.drawable.button_pressed_right, R.drawable.button_unpressed_left, Color.WHITE, Color.BLACK);
                msgView.setText(doneDonationMsg);
                msgView2.setVisibility(View.GONE);
                list.setAdapter(doneAdapter);
            }
        });
    }

    public void getProgrammed() {
        progAdapter.clear();
        int count = db.getAllDonations(progAdapter);
        progDonationMsg = (count == 1) ? count + " " + progRight_sin : count + " " + progRight_plu;
        msgView.setText(progDonationMsg);
    }

    public void getDone() {
        doneAdapter.clear();
        int[] info = db.getAllDoneDonations(doneAdapter);
        int count = info[0];
        int price = info[1];

        if (count == 0) {
            doneDonationMsg = getString(R.string.donation_done_zero);
            msgView.setText(doneDonationMsg);
        }
        else {
            String mid_msg = (count == 1) ? mid_sin : mid_plu;
            doneDonationMsg = left + " " + count + " " + mid_msg + " " + price + " " + right;
            msgView.setText(doneDonationMsg);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        getDone();
        getProgrammed();
    }


    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        if (inDone) return;

        Donation progDonation = (Donation) progAdapter.getItem(position);
        Intent intent;

        if (progDonation.getVolunteer().equals("V")) {
            intent = new Intent(getApplicationContext(), DwreaVolunteer.class);
            intent.putExtra("secondTime", "true");
        }
        else if (progDonation.getVolunteer().equals("U")) {
            intent = new Intent(getApplicationContext(), DwreaUser.class);
            intent.putExtra("secondTime", "true");
        }
        else
            intent = new Intent(getApplicationContext(), AfterDwrees.class);

        intent.putExtra("pharName", progDonation.getPharNameGen());
        intent.putExtra("barcode", progDonation.getBarcode());
        intent.putExtra("medName", progDonation.getName());
        startActivity(intent);
    }
}