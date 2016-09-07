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

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class Dwrees extends HelperActivity {
    private final String TAG = "Dwrees";
    private String progDonationMsg, doneDonationMsg;
    private String left, right, mid_plu, mid_sin, progRight_sin, progRight_plu;
    public static DonationAdapter progAdapter,doneAdapter;
    private TextView msgView, msgView2;
    private static Button progButton, doneButton;
    AdapterView.OnItemClickListener listener;
    private ListView list;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.dwrees, R.string.dwrees, false);

        db = new DBHandler(getApplicationContext());

        Intent pushIntent = getIntent();
        if (pushIntent.hasExtra("fromPush")) {
            ArrayList<String> topics = new ArrayList<String>();
            topics.add(pushIntent.getStringExtra("medName"));
            Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
            serviceIntent.putExtra("subscribe", false);
            serviceIntent.putStringArrayListExtra("topic", topics);
            startService(serviceIntent);
            db.addDonationFromPush(pushIntent.getStringExtra("medName"),pushIntent.getStringExtra("pharPhone"));
        }

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

        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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

                intent.putExtra("pharName", progDonation.getPharName());
                intent.putExtra("barcode", progDonation.getBarcode());
                intent.putExtra("medName", progDonation.getName());
                startActivity(intent);
            }
        };

        list = (ListView) findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        list.setOnItemClickListener(listener);
        list.setAdapter(progAdapter);

        progButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showProg();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDone();
            }
        });
    }

    public void showProg() {
        changeButtonsLayout(progButton, doneButton, R.drawable.button_pressed_left, R.drawable.button_unpressed_right, Color.WHITE, Color.BLACK);
        msgView.setText(progDonationMsg);
        msgView2.setVisibility(View.VISIBLE);
        list.setOnItemClickListener(listener);
        list.setAdapter(progAdapter);
    }

    public void getProgrammed() {
        progAdapter.clear();
        int count = db.getAllDonations(progAdapter);

        if (count == 0) {
            progDonationMsg = getString(R.string.donation_prog_zero);
            msgView.setText(progDonationMsg);
        }
        else {
            progDonationMsg = (count == 1) ? count + " " + progRight_sin : count + " " + progRight_plu;
            msgView.setText(progDonationMsg);
        }
    }

    public void showDone() {
        changeButtonsLayout(doneButton, progButton, R.drawable.button_pressed_right, R.drawable.button_unpressed_left, Color.WHITE, Color.BLACK);
        msgView.setText(doneDonationMsg);
        msgView2.setVisibility(View.GONE);
        list.setOnItemClickListener(null);
        list.setAdapter(doneAdapter);
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
    protected void onNewIntent (Intent intent){
        if (intent.hasExtra("done")) {
            setIntent(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        getDone();
        getProgrammed();

        //edw pairnoume to intent ka8e fora, epeidh kaleite panta h onResume
        //alla an einai hdh anoixto to Farmakeio.class tote prepei na valoume
        //thn onNewIntent giati den evlepe to intent mesa sthn onResume
        Intent intent = getIntent();
        if (intent.hasExtra("done") && !intent.hasExtra("consumed")) {
            showDone();

            //otan paroume kai xrhsimopoihsoume to intent tote vazoume ena epipleon pedio
            //gia na 3eroume oti den to xreiazomaste
            intent.putExtra("consumed", true);
        }

    }
}