package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

/**
 * Created by agroikos on 2/5/2016.
 */
public class SplashActivity extends AppCompatActivity {
    private String needDate, pharDate;
    private PrefManager pref;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        db = new DBHandler(getApplicationContext());

        pref = new PrefManager(this);
        HelperActivity help = new HelperActivity();
        Object array[] = new Object[2];
        array[0] = db;
        array[1] = pref;
        try {
            help.new HttpGetNeedsPharms().execute(array).get();
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