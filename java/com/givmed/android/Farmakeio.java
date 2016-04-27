package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.givmed.android.R;

public class Farmakeio extends HelperActivity implements AdapterView.OnItemClickListener {

    public static MedNameAdapter mAdapter;
    public static MedName newMedName;
    private static TextView msgView;
    public static int count;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.pharmacy_names, R.string.farmakeio, true);

        db = new DBHandler(getApplicationContext());
        msgView = (TextView) findViewById(R.id.secondMes);
        msgView.setText(getResources().getString(R.string.pharm_left_half_msg) + " (0) " + getResources().getString(R.string.pharm_right_half_msg_plural));

        final FloatingActionButton floatingBut = (FloatingActionButton) findViewById(R.id.fab);
        floatingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Farmakeio.this, TwoButtons.class));
            }
        });

        mAdapter = new MedNameAdapter(getApplicationContext());
        ListView list = (ListView)findViewById(R.id.list);
        list.setOnItemClickListener(this);
        list.setFooterDividersEnabled(true);
        list.setAdapter(mAdapter);

        count = db.getAllNamesToAdapter(mAdapter);
        int right_msg = (count == 1) ? R.string.pharm_right_half_msg_single : R.string.pharm_right_half_msg_plural;
        msgView.setText(getResources().getString(R.string.pharm_left_half_msg) + " (" + count + ") " + getResources().getString(right_msg));
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        MedName name = (MedName) mAdapter.getItem(position);

        // an einai mono ena to farmako tote na deixnoume kateu8eian tis plhrofories
        // alliws na deixnoume thn lista me ola ta farmaka
        if (name.getCount().equals("1")) {
            Intent intent = new Intent(getApplicationContext(), AfterFarmakeio.class);
            intent.putExtra("name", name.getName());
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), AfterFarmakeio.class);
            intent.putExtra("name", name.getName());
            startActivity(intent);
        }
    }
}
