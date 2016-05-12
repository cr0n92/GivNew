package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class ImFine extends HelperActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.oxi_eimai_entaxei, R.string.outputer, true);

        final Button yesButton = (Button) findViewById(R.id.button1);
        yesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), TwoButtons.class);
                startActivity(myIntent);
            }
        });

        final Button noButton = (Button) findViewById(R.id.button2);
        noButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), BlueRedList.class);
                startActivity(myIntent);
            }
        });

    }
}