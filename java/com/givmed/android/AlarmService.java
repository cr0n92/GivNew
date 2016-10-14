package com.givmed.android;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by agroikos on 3/5/2016.
 */
public class AlarmService extends Service {
    private PrefManager pref;
    private DBHandler db;
    //compat to support older devices
    @Override
    public void onStart(Intent intent, int startId) {
        onStartCommand(intent, 0, startId);
    }

//H Oncreate kaleitai mono thn prwth fora pou arxizei to service.Kathe fora pou ksanarxizei kaleitai h onStartCommand.
// H OnDestroy kaleitai mono an stamathsoume to service me stopService

//Otan h o8onh tou kinhtou einai kleisth to service trexei kanonika
    @Override
    public int onStartCommand (Intent intent, int flags, int startId){
        //your method to check if an alarm must be fired today
        Calendar calendar = Calendar.getInstance();
        int cur_month= calendar.get(Calendar.MONTH);
        pref = new PrefManager(this);
        //ta updates ginontai eite an o trexwn mhnas einai megaluteros apo ton apo8hkeumeno eite an o palios mhnas einai 11 kai
        //o trexwn 0
        if (cur_month > pref.getOldMonth() || pref.getOldMonth() - cur_month ==11) {
            int exp_month = (cur_month + 3) % 12 + 1;
            int exp_year = (exp_month > 3)? calendar.get(Calendar.YEAR):calendar.get(Calendar.YEAR) + 1;
            String three_months_later =  "" +exp_month+ "/"+exp_year;
            Log.e("Three Months Later",""+three_months_later);
            db = new DBHandler(getApplicationContext());
            ArrayList<String> topics = db.updateMedStatus(three_months_later);
            ArrayList<String> topics1 = new ArrayList<String>();
            //tsekaroume an einai ta prwta me Y/SY.isws einai xeirotero apo to na kanoume apla subscribe peritta.tha deiksei
            //apo thn ektenh data flow analush pou tha pragmatopoih8ei
            for (String x: topics) {
                if (db.checkMedSubscribe(x,true))
                    topics1.add(x);
            }
            //TODO: na tsekarw an ginetai to subscribe 3 mhnes prin lhksoun kai genika an ginontai amesh dwrea
            if (!topics1.isEmpty()) {
                Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
                serviceIntent.putExtra("subscribe", true);
                serviceIntent.putStringArrayListExtra("topic", topics1);
                startService(serviceIntent);
            }
            //enhmerwnoume ton Pref oti o eginan ta updates stis dwrees
            pref.setOldMonth(cur_month);


        }

        Log.e("Date", "" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        Log.e("Alarm", "re");

        //reschedule me to check again tomorrow
        Intent serviceIntent = new Intent(AlarmService.this,AlarmService.class);
        PendingIntent restartServiceIntent = PendingIntent.getService(AlarmService.this, 0, serviceIntent,0);
        AlarmManager alarms = (AlarmManager)getSystemService(ALARM_SERVICE);
        // cancel previous alarm
        alarms.cancel(restartServiceIntent);
        // schedule alarm for today + 1 day
        calendar.add(Calendar.DATE, 1);
       // Log.e("Epomenh mera se:",""+calendar.getTimeInMillis());
        // schedule the alarm
        alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), restartServiceIntent);
        //ama to service termatisei apo to susthma dn to ksanarxizoume
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }





}
