package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class Farmakeio extends HelperActivity implements AdapterView.OnItemClickListener {

    public static MedNameAdapter mAdapter;
    private static TextView msgView;
    public static int count;
    private static String right_plu, right_sin, left;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.pharmacy_names, R.string.farmakeio, true);

        db = new DBHandler(getApplicationContext());

        right_plu = getResources().getString(R.string.pharm_right_half_msg_plural);
        right_sin = getResources().getString(R.string.pharm_right_half_msg_single);
        left = getResources().getString(R.string.pharm_left_half_msg);
        msgView = (TextView) findViewById(R.id.secondMes);
        msgView.setText(left + " (0) " + right_plu);

        mAdapter = new MedNameAdapter(getApplicationContext());
        ListView list = (ListView)findViewById(R.id.list);
        list.setOnItemClickListener(this);
        list.setFooterDividersEnabled(true);
        list.setAdapter(mAdapter);

        putMedsToList();
    }

    private void putMedsToList() {
        mAdapter.clear();
        count = db.getAllNamesToAdapter(mAdapter);
        String right_msg = (count == 1) ? right_sin : right_plu;
        msgView.setText(left + " (" + count + ") " + right_msg);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        putMedsToList();
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        MedName name = (MedName) mAdapter.getItem(position);

        // an einai mono ena to farmako tote na deixnoume kateu8eian tis plhrofories
        // alliws na deixnoume thn lista me ola ta farmaka
        if (name.getCount().equals("1")) {
            Intent intent = new Intent(getApplicationContext(), DisplayMed.class);
            String barcode = db.getMedBarcodeByName(name.getName());
            intent.putExtra("barcode", barcode);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), AfterFarmakeio.class);
            intent.putExtra("name", name.getName());
            startActivity(intent);
        }
    }
}
