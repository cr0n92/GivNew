package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

/**
 * Created by agroikos on 2/5/2016.
 */
public class SplashActivity extends AppCompatActivity {
    private DBHandler db;
    public static DonationAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar calendar = Calendar.getInstance();
//        Log.e("Day",""+calendar.get(Calendar.DAY_OF_MONTH));
//        Log.e("Month",""+calendar.get(Calendar.MONTH));
//        Log.e("Day of Year",""+calendar.get(Calendar.DAY_OF_YEAR));


//        db = new DBHandler(getApplicationContext());
//        db.printAllMeds();




        startService(new Intent(this, AlarmService.class));
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        finish();
    }
}