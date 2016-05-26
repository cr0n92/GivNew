package com.givmed.android;


import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by agroikos on 29/4/2016.
 */
public class TimerService  extends Service {
    public static final String BROADCAST_ACTION = "Timer";
    private String data;
    Intent TimerIntent;


@Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        data = intent.getStringExtra("attempt");
        Log.e("data",""+data);
        TimerIntent = new Intent(BROADCAST_ACTION);
        long duration = (data.equals("first")) ? 40000 : 60000;

        new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                TimerIntent.putExtra("countdown", millisUntilFinished);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(TimerIntent);
            }

            @Override
            public void onFinish() {
            }
        }.start();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }



}
