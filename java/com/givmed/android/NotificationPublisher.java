package com.givmed.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by agroikos on 24/4/2016.
 */
public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";
    private PrefManager pref;


    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        pref = new PrefManager(context);

        int id = intent.getIntExtra(NOTIFICATION_ID, 0);

        //Post a notification to be shown in the status bar.
        // If a notification with the same id has already been posted by your application and has not yet been canceled,
        // it will be replaced by the updated information.
        if (pref.getNotificiationPermission()) {
            notificationManager.notify(id, notification);
        }
    }


}