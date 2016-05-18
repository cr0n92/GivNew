package com.givmed.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Number extends AppCompatActivity {
    EditText mPhoneView;
    String phone;
    AlertDialog alert, numAlert;
    private PrefManager pref;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.number);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.number);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

       // pref = new PrefManager(this);

//        // Checking for user session
//        // if user is already logged in, take him to elleipseis
//        if (pref.isLoggedIn()) {
//            //          pref.clearSession();
//            Intent intent = new Intent(Number.this, Elleipseis.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
//
//            finish();
//        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.num_alert))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog2, int id) {

                    }
                });
        numAlert = builder.create();
        numAlert.show();

        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.conf_error))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alert = builder.create();

        mPhoneView = (EditText) findViewById(R.id.phone);

        TextView oroi = (TextView) findViewById(R.id.thirdMes);
        oroi.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent oroi = new Intent(getApplicationContext(), OroiXrhshs.class);
                oroi.putExtra("number", "number");
                startActivity(oroi);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {
            phone = mPhoneView.getText().toString().trim();

            if (phone.length() != 10 || !phone.matches("[0-9]+") || !phone.startsWith("69")) {
                alert.show();
            } else {
                Intent confIntent = new Intent(getApplicationContext(), ConfirmNumber.class);
                confIntent.putExtra("phone", phone);
                startActivity(confIntent);
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }



}