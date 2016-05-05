package com.givmed.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.givmed.android.R;

public class Settings extends HelperActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.settings, R.string.settings, true);

        final Button yesButton1 = (Button) findViewById(R.id.yesButton1);
        final Button noButton1 = (Button) findViewById(R.id.noButton1);
        final PrefManager pref = new PrefManager(getApplicationContext());

        if (pref.getNotificiationPermission())
          changeButtonsLayout(yesButton1, noButton1, R.drawable.button_pressed_left, R.drawable.button_unpressed_right);
        else
          changeButtonsLayout(noButton1, yesButton1, R.drawable.button_pressed_right, R.drawable.button_unpressed_left);


        yesButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(yesButton1, noButton1, R.drawable.button_pressed_left, R.drawable.button_unpressed_right);
                pref.setNotificationPermission(true);
            }
        });

        noButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(noButton1, yesButton1, R.drawable.button_pressed_right, R.drawable.button_unpressed_left);
                pref.setNotificationPermission(false);
            }
        });

    }
}