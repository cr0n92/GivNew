package com.givmed.android;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.ArrayList;

/**
 * Created by agroikos on 10/5/2016.
 */

//IntentService:Clients send requests through startService(Intent) calls; the service is started as needed,
// handles each Intent in turn using a worker thread, and stops itself when it runs out of work.
public class SubscribeService extends IntentService {

    private static final String TAG = "SubscribeService";
    private static ArrayList<String> topics;
    private static Boolean subscribe;




    public SubscribeService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {


        topics = intent.getStringArrayListExtra("topic");
        subscribe = intent.getBooleanExtra("subscribe",true);


        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            GcmPubSub pubSub = GcmPubSub.getInstance(this);

            if (subscribe) {
                for (String topic : topics) {
                    Log.e("Kanw subscribe sto",""+topic);
                    pubSub.subscribe(token, "/topics/" + topic, null);
                }
            }
            else {
                for (String topic : topics) {

                    pubSub.unsubscribe(token, "/topics/" + topic);
                }
            }





        } catch (Exception e) {

        }

    }



}
