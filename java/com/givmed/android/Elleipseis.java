package com.givmed.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
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

public class Elleipseis extends HelperActivity
{
    private final String TAG = "Ellepseis";
    private String right_plu, right_sin, left_sin;
    public static NeedAdapter mAdapter;
    private static TextView msgView;
    private static Button nameButton, regionButton;
    DBHandler db;



    //!!!!!!!!!!!PUSH!!!!!!!!!!
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private TextView mInformationTextView;
    private boolean isReceiverRegistered;
    private PrefManager pref;


    //!!!!!!!!!!!!!!!!!!!!!!PUSH!!!!!!!!!!!!!!!!!!!!!!1

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.eleipseis, R.string.elleipseis, false);

        db = new DBHandler(getApplicationContext());
        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!PUSH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        pref = new PrefManager(getApplicationContext());


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "Kati phra");

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Log.e(TAG, "Token received and sent to server");
                } else {
                    Log.e(TAG, "Error while fetching the InstanceID") ;
                }
            }
        };

        // Registering BroadcastReceiver
        registerReceiver();


        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Log.e("Koble","Einai");
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!PUSH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        left_sin = getResources().getString(R.string.need_left_half_msg);
        right_plu = getResources().getString(R.string.need_right_half_msg_plural);
        right_sin = getResources().getString(R.string.need_right_half_msg_single);

        msgView = (TextView) findViewById(R.id.secondMes);
        nameButton = (Button) findViewById(R.id.nameButton);
        regionButton = (Button) findViewById(R.id.regionButton);
        msgView.setText(left_sin + " (0) " + right_plu);

        nameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(nameButton, regionButton, R.drawable.button_pressed_left, R.drawable.button_unpressed_right);
                mAdapter.clear();
                db.getAllNeeds(mAdapter, "needName");
                //pref.setNotificationPermission(false);
            }
        });

        regionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(regionButton, nameButton, R.drawable.button_pressed_right, R.drawable.button_unpressed_left);
                mAdapter.clear();
                db.getAllNeeds(mAdapter, "pharName");
                //pref.setNotificationPermission(true);

            }
        });

        mAdapter = new NeedAdapter(getApplicationContext());
        ListView list = (ListView)findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        //registerForContextMenu(getListView());
        list.setAdapter(mAdapter);

        int count = db.getAllNeeds(mAdapter, "pharName");
        String right_msg = (count == 1) ? right_sin: right_plu;
        msgView.setText(left_sin + " " + count + " " + right_msg);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

        registerReceiver(); //PUSH
    }

    //!!!!!!!!!!!!!!!!!!!!!!!!!!PUSH!!!!!!!!!!!!!!!!!!!!!!!
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    private void registerReceiver(){

        if(!isReceiverRegistered) {

            Log.e(TAG, "Ton kanw register");

            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.e(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    //!!!!!!!!!!!!!!!!!!!!!!!!!!PUSH!!!!!!!!!!!!!!!!!!!!!!!
}