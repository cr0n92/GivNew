package com.givmed.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class AfterDwrees extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static String mid_msg = " ";
    public static String selectedPharm = " ";
    public static TextView msgView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dwrrea_chooser);

        // Pername apo tis programmatismenes dwrees to onoma tou farmakeiou an einai mono ena
        // sto opoio yparxei h elleipsh, alliws den vazoume tipota, deixnoume katalhlo mhnyma
        // se ka8e periptwsh kai an yparxei se perisottera tote deixnoume to spinner wste na
        // epile3ei o xrhsths to farmakeio pou 8elei epishs ena mhnyma panw apo to spinner.
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("pharName"))
            mid_msg = " " + getString(R.string.choo_mid_first_msg) + " " + intent.getStringExtra("pharName") + " ";

        msgView = (TextView) findViewById(R.id.firstMes);
        String msg = getString(R.string.choo_left_first_msg) + mid_msg + getString(R.string.choo_right_first_msg);
        msgView.setText(msg);

        TextView chooseMsg = (TextView) findViewById(R.id.chooseMsg);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        if (mid_msg.equals(" ")) {
            chooseMsg.setText(getString(R.string.choo_pharm_msg));

            // TODO: pairnoume apo thn vash kai ta gyrname se ena list me strings

            List<String> list = new ArrayList<String>();
            list.add("ellhniko");
            list.add("alvaniko");
            list.add("scanouri");

            spinner.setOnItemSelectedListener(this);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
        else {
            chooseMsg.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
        }

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
                intent.putExtra("selected", selectedPharm);
                startActivity(intent);
            }
        });

        final Button voluButton = (Button) findViewById(R.id.button2);
        voluButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DwreaVolunteer.class);
                intent.putExtra("selected", selectedPharm);
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
