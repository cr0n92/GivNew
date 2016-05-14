package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;


public class AfterDwrees extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static String mid_msg = " ";
    private static String selectedPharm = ";", barcode = "", medname = "";
    private static TextView msgView;
    DBHandler db;

    //TODO ama o xrhsths epileksei farmakeio kai pathsei koumpi dn exoume ti ginetai??

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dwrrea_chooser);

        db = new DBHandler(getApplicationContext());
        TextView chooseMsg = (TextView) findViewById(R.id.chooseMsg);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        // Pername apo tis programmatismenes dwrees to onoma tou farmakeiou an einai mono ena
        // sto opoio yparxei h elleipsh, alliws den vazoume tipota, deixnoume katalhlo mhnyma
        // se ka8e periptwsh kai an yparxei se perisottera tote deixnoume to spinner wste na
        // epile3ei o xrhsths to farmakeio pou 8elei epishs ena mhnyma panw apo to spinner.
        Intent intent = getIntent();

        if (intent != null && intent.hasExtra("pharName") && !(intent.getStringExtra("pharName").equals(";"))) {
            //to selectedpharm tha exei to pharNameGen = pharName
            selectedPharm = intent.getStringExtra("pharName");
            barcode = intent.getStringExtra("barcode");
            medname = intent.getStringExtra("medName");

            mid_msg = " " + getString(R.string.choo_mid_first_msg) + " " + selectedPharm + " ";

            chooseMsg.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        }
        else {
            barcode = intent.getStringExtra("barcode");
            medname = intent.getStringExtra("medName");

            chooseMsg.setText(getString(R.string.choo_pharm_msg));

            List<String> list;
            list = db.getPharmaciesForNeed(medname);

            spinner.setOnItemSelectedListener(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }

        msgView = (TextView) findViewById(R.id.firstMes);
        String msg = getString(R.string.choo_left_first_msg) + mid_msg + getString(R.string.choo_right_first_msg);
        msgView.setText(msg);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.tropos_paradoshs);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final Button userButton = (Button) findViewById(R.id.button1);
        userButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DwreaUser.class);
                intent.putExtra("barcode", barcode);
                intent.putExtra("medName", medname);

                //edw einai eite to onoma farmakeiou sthn genikh an eixame ena farmakeio eite to onoma tou farmakeiou
                // kanonika an to epileksame apo thn lista
                intent.putExtra("pharName", selectedPharm);

                String[] pharInfo = new String[5];
                db.getPharmacy(selectedPharm, pharInfo);
                db.updateProgDonation(barcode, pharInfo[0], ";", ";", ";", "U", ";");
                startActivity(intent);
            }
        });

        final Button voluButton = (Button) findViewById(R.id.button2);
        voluButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DwreaVolunteer.class);
                intent.putExtra("barcode", barcode);
                intent.putExtra("medName", medname);
                intent.putExtra("pharName", selectedPharm);

                String[] pharInfo = new String[5];
                db.getPharmacy(selectedPharm, pharInfo);
                db.updateProgDonation(barcode, pharInfo[0], ";", ";", ";", "V", ";");
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_simple, menu);
        return true;
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        selectedPharm = (String) parent.getItemAtPosition(pos);
        Log.e("kourampies", selectedPharm);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
