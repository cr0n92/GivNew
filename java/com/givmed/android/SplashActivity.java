package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        DBHandler db = new DBHandler(getApplicationContext());
        PrefManager pref = new PrefManager(this);

        HelperActivity.HttpGetNeedsPharms service = new HelperActivity.HttpGetNeedsPharms(this, db, pref) {
            @Override
            public void onResponseReceived(Object result) {
            }
        };

        try {
            service.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Intent intent;
        startService(new Intent(this, AlarmService.class));
        switch (pref.getNextSplash()) {
            case "Tutorial":
                intent = new Intent(this, Tutorial.class);
                break;
            case "Register":
                intent = new Intent(this, Register.class);
                break;
            default:
                intent = new Intent(this, TwoButtons.class);
        }
        startActivity(intent);
        finish();
    }
}