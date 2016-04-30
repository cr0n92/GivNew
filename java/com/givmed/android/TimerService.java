package com.givmed.android;


import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

/**
 * Created by agroikos on 29/4/2016.
 */
public class TimerService  extends Service {
    private static final String TAG = "BroadcastService";
    public static final String BROADCAST_ACTION = "Timer";
    Intent intent;


@Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BROADCAST_ACTION);

        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                    intent.putExtra("countdown", millisUntilFinished);
                    sendBroadcast(intent);
                    }

            @Override
            public void onFinish() {
            }
        }.start();
    }


}
