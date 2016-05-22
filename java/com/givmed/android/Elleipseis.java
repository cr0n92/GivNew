package com.givmed.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

public class Elleipseis extends HelperActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "Ellepseis";
    public static NeedAdapter nameAdapter, pharAdapter;
    private static TextView msgView;
    private static Button nameButton, regionButton;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.eleipseis, R.string.elleipseis, false);

        db = new DBHandler(getApplicationContext());

        String left_sin = getResources().getString(R.string.need_left_half_msg);
        String right_plu = getResources().getString(R.string.need_right_half_msg_plural);
        String right_sin = getResources().getString(R.string.need_right_half_msg_single);

        msgView = (TextView) findViewById(R.id.secondMes);
        nameButton = (Button) findViewById(R.id.nameButton);
        regionButton = (Button) findViewById(R.id.regionButton);
        msgView.setText(left_sin + " 0 " + right_plu);

        nameAdapter = new NeedAdapter(getApplicationContext());
        pharAdapter = new NeedAdapter(getApplicationContext());

        final ListView list = (ListView) findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        list.setOnItemClickListener(this);
        list.setAdapter(nameAdapter);

        nameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(nameButton, regionButton, R.drawable.button_pressed_left, R.drawable.button_unpressed_right, Color.WHITE, Color.BLACK);
                list.setAdapter(nameAdapter);
            }
        });

        regionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(regionButton, nameButton, R.drawable.button_pressed_right, R.drawable.button_unpressed_left, Color.WHITE, Color.BLACK);
                list.setAdapter(pharAdapter);
            }
        });

        int count = db.getAllNeeds(nameAdapter, "needName");
        db.getAllNeeds(pharAdapter, "pharName");
        String need_msg = (count == 1) ? left_sin + " " + count + " " + right_sin : left_sin + " " + count + " " + right_plu;
        msgView.setText(need_msg);
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        startActivity(new Intent(getApplicationContext(), TwoButtons.class));
    }
}