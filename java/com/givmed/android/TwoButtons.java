package com.givmed.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import io.fabric.sdk.android.Fabric;

public class TwoButtons extends HelperActivity
{

    //!!!!!!!!!!!PUSH!!!!!!!!!!
    private final String TAG = "TwoButtons";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isReceiverRegistered;

    //!!!!!!!!!!!!!!!!!!!!!!PUSH!!!!!!!!!!!!!!!!!!!!!!!

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.mainee, R.string.register_med, false);

        //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!PUSH!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e(TAG, "Kati phra");

                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(PrefManager.SENT_TOKEN_TO_SERVER, false);
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

        final Button scanButton = (Button) findViewById(R.id.button1);
        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GivmedApplication.getInstance().trackEvent("TwoButtons", "Push", "Scan");
                PackageManager pm = getApplicationContext().getPackageManager();

                // koitame an to kinito exei kamera kai an den exei deixnoume toast
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    Toast.makeText(getApplicationContext(), "Device has no camera!", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getApplicationContext(), BarcodeScanner.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        final Button userButton = (Button) findViewById(R.id.button2);
        userButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GivmedApplication.getInstance().trackEvent("TwoButtons", "Push", "Hand");

                Intent myIntent = new Intent(getApplicationContext(), Inputter.class);
                startActivity(myIntent);
            }
        });

    }

    private void registerReceiver(){

        if(!isReceiverRegistered) {

            Log.e(TAG, "Ton kanw register");

            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(PrefManager.REGISTRATION_COMPLETE));
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

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        GivmedApplication.getInstance().trackScreenView("TwoButtons");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            String barcode = intent.getStringExtra("BARCODE");

            if (barcode.equals("NULL"))
                ;//Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
            else {
                Intent showItemIntent = new Intent(getApplicationContext(), Inputter.class);
                showItemIntent.putExtra("barcode", barcode);
                startActivity(showItemIntent);
            }
        }
    }
}