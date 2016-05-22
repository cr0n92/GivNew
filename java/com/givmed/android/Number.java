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
    AlertDialog numAlert;
    AlertDialog.Builder builder;

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

        builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.num_alert))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog2, int id) {

                    }
                });
        numAlert = builder.create();
        numAlert.show();

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
            if (!HelperActivity.isOnline(getApplicationContext())) {
                HelperActivity.httpErrorToast(getApplicationContext(), 1);
            }
            else {
                phone = mPhoneView.getText().toString().trim();

            	if (phone.length() != 10 || !phone.matches("[0-9]+") || !phone.startsWith("69")) {
                    builder.setMessage(getString(R.string.conf_error))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            });
                    AlertDialog alert = builder.create();
                	alert.show();
            	} else {
                    builder.setMessage(getString(R.string.num_sure_left) + "\n\n(+30) " + phone + "\n\n" + getString(R.string.num_sure_right))
                            .setCancelable(false)
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog2, int id) {
                                    Intent confIntent = new Intent(getApplicationContext(), ConfirmNumber.class);
                                    confIntent.putExtra("phone", phone);
                                    startActivity(confIntent);
                                    finish();
                                }
                            })
                            .setPositiveButton("Αλλαγή", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog2, int id) {

                                }
                            });
                    AlertDialog youSure = builder.create();
                    youSure.show();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }



}