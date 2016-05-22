package com.givmed.android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class OroiXrhshs extends HelperActivity {
    private boolean fromNumber = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("number")) {
            super.onCreate(savedInstanceState);
            super.setMenu(R.menu.menu_main_simple);

            fromNumber = true;
            setContentView(R.layout.faq_no_drawer);
            Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
            mToolBar.setTitle(R.string.oroi_xrhshs);
            mToolBar.setNavigationIcon(R.drawable.ic_arrows);
            setSupportActionBar(mToolBar);
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        else {
            super.onCreate(savedInstanceState);
            super.setMenu(R.menu.menu_main_simple);
            super.helperOnCreate(R.layout.faq, R.string.oroi_xrhshs, false);
        }
        Fabric.with(this, new Crashlytics());

        int num_match = 10;

        LinearLayout myLayout = (LinearLayout) findViewById(R.id.linear);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 10, 0);

        String[] header_strings = getResources().getStringArray(R.array.ox_headers);
        String[] text_strings = getResources().getStringArray(R.array.ox_texts);

        TextView[] headers = new TextView[num_match];
        TextView[] texts = new TextView[num_match + 1];

        for(int i = 0; i < num_match; i++) {

            headers[i] = new TextView(this);
            headers[i].setTextSize(20);
            headers[i].setTextColor(Color.BLACK);
            headers[i].setTypeface(null, Typeface.BOLD);
            headers[i].setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
            headers[i].setPadding(5, 5, 5, 5);
            headers[i].setLayoutParams(lp);
            headers[i].setId(i);
            headers[i].setText(header_strings[i]);
            myLayout.addView(headers[i]);

            texts[i] = new TextView(this);
            texts[i].setTextSize(18);
            texts[i].setTextColor(Color.BLACK);
            texts[i].setGravity(Gravity.START | Gravity.CENTER_HORIZONTAL);
            texts[i].setPadding(5, 5, 5, 5);
            texts[i].setLayoutParams(lp);
            texts[i].setId(i);
            texts[i].setText(text_strings[i]);
            myLayout.addView(texts[i]);
        }
    }

    @Override
    public void onBackPressed() {
        if (fromNumber) finish();
        else super.onBackPressed();
    }
}