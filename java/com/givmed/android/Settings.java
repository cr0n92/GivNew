package com.givmed.android;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Settings extends HelperActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.settings, R.string.settings, false);

        final Button yesButton1 = (Button) findViewById(R.id.yesButton1);
        final Button noButton1 = (Button) findViewById(R.id.noButton1);

        final TextView version = (TextView) findViewById(R.id.version);
        String versione = getString(R.string.version) + " " + BuildConfig.VERSION_NAME;
        version.setText(versione);

        final PrefManager pref = new PrefManager(getApplicationContext());

        if (pref.getNotificiationPermission())
          changeButtonsLayout(yesButton1, noButton1, R.drawable.button_pressed_left, R.drawable.button_unpressed_right, Color.WHITE, Color.BLACK);
        else
          changeButtonsLayout(noButton1, yesButton1, R.drawable.button_pressed_right, R.drawable.button_unpressed_left, Color.WHITE, Color.BLACK);


        yesButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(yesButton1, noButton1, R.drawable.button_pressed_left, R.drawable.button_unpressed_right, Color.WHITE, Color.BLACK);
                pref.setNotificationPermission(true);
            }
        });

        noButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(noButton1, yesButton1, R.drawable.button_pressed_right, R.drawable.button_unpressed_left, Color.WHITE, Color.BLACK);
                pref.setNotificationPermission(false);
            }
        });

    }
}