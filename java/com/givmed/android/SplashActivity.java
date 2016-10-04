package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import java.util.concurrent.ExecutionException;

import io.fabric.sdk.android.Fabric;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.splash_screen);

        final PrefManager pref = new PrefManager(this);
        final DBHandler db = new DBHandler(getApplicationContext());

        final HelperActivity.HttpGetNeedsPharms service = new HelperActivity.HttpGetNeedsPharms(this, db, pref) {
            @Override
            public void onResponseReceived(Object result) {
            }
        };

        Thread timer = new Thread() {
            public void run() {
                try {
                    service.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }finally {

                    Intent intent;
                    startService(new Intent(SplashActivity.this, AlarmService.class));
                    switch (pref.getNextSplash()) {
                        case "Tutorial":
                            intent = new Intent(SplashActivity.this, Tutorial.class);
                            break;
                        case "Register":
                            intent = new Intent(SplashActivity.this, Register.class);
                            break;
                        default:
                            intent = new Intent(SplashActivity.this, TwoButtons.class);
                    }
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        };

        timer.start();
    }
}