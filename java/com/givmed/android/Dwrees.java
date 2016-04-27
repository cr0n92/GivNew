package com.givmed.android;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Dwrees extends HelperActivity {
    private final String TAG = "Dwrees";
    public static NeedAdapter mAdapter;
    private static TextView msgView;
    private static Button progButton, doneButton;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        mAdapter = new NeedAdapter(getApplicationContext());
        ListView list = (ListView) findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        //registerForContextMenu(getListView());
        list.setAdapter(mAdapter);

        db = new DBHandler(getApplicationContext());

        //int count = db.getAllNeeds(mAdapter, "region");
        int count = 0;
        double price = 0.0;
        String mid_msg = (count == 1) ? mid_sin : mid_plu;
        msgView.setText(left + " " + count + " " + mid_msg + " " + price + " " + right);
    }
}