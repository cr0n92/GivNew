package com.givmed.android;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class Faq extends HelperActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.faq, R.string.faq, false);

        int num_match = 16;

        LinearLayout myLayout = (LinearLayout) findViewById(R.id.linear);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 10, 0);

        String[] header_strings = getResources().getStringArray(R.array.headers);
        String[] text_strings = getResources().getStringArray(R.array.texts);

        TextView[] headers = new TextView[num_match];
        TextView[] texts = new TextView[num_match];

        for(int i = 0; i < num_match; i++) {
            headers[i] = new TextView(this);
            headers[i].setTextSize(20);
            headers[i].setTextColor(Color.BLACK);
            headers[i].setTypeface(null, Typeface.BOLD);
            headers[i].setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
            headers[i].setPadding(5, 5, 5, 5);
            headers[i].setLayoutParams(lp);
            headers[i].setId(i);
            String txt = "" + (i + 1) + ". " + header_strings[i];
            headers[i].setText(txt);
            myLayout.addView(headers[i]);

            texts[i] = new TextView(this);
            texts[i].setTextSize(18);
            texts[i].setTextColor(Color.BLACK);
            texts[i].setGravity(Gravity.LEFT | Gravity.CENTER_HORIZONTAL);
            texts[i].setPadding(5, 5, 5, 5);
            texts[i].setLayoutParams(lp);
            texts[i].setId(i + 1);
            texts[i].setText(text_strings[i]);
            myLayout.addView(texts[i]);
        }
    }
}