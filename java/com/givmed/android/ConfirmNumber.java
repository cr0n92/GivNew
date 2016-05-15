package com.givmed.android;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import io.fabric.sdk.android.Fabric;

public class ConfirmNumber extends AppCompatActivity {
    private String phone;
    private String token;
    private int returnCode,regCount;
    private boolean second = false;
    private EditText pin0, pin1, pin2, pin3;
    private TextView sendAgain;
    private PrefManager pref;
    private Intent TimerIntent;
    ProgressDialog dialog;

    private BroadcastReceiver mTimerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long countdown=intent.getLongExtra("countdown", 1L);

            new CountDownTimer(countdown, 1000) {

                public void onTick(long millisUntilFinished) {
                    sendAgain.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    TimerIntent = new Intent(getApplicationContext(), TimerService.class);
                    stopService(TimerIntent);
                    if (pref.getCountdown().equals("firstRunning"))
                        pref.setCountdown("second");
                    else if (pref.getCountdown().equals("secondRunning"))
                        pref.setCountdown("Last");
                    Log.e("TZA","TZA");
                    sendAgain.setText(getString(R.string.conf_send_again));
                    sendAgain.setPaintFlags(sendAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    sendAgain.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (pref.getCountdown().equals("second")) {
                                pref.setCountdown("secondRunning");
                                TimerIntent.putExtra("attempt", "second");
                                startService(TimerIntent);
                                sendAgain.setPaintFlags(sendAgain.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                                second  = true;
                                HelperActivity.showDialogBox(getApplicationContext(), dialog);
                                new HttpGetTask().execute();


                            }
                            else
                                pref.setCountdown("End");
                                sendAgain.setText("Δεν έχεις δικάιωμα για άλλα μηνύματα");
                                sendAgain.setPaintFlags(sendAgain.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));



                        }
                    });

                }
            }.start();
        }

    };

    //kaleitai kai otan to kinhto einai to idio kai otan einai allh suskeuh
    private BroadcastReceiver mTokenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            token=intent.getStringExtra("token");
            pin0.setText("" + token.charAt(0));
            pin1.setText(""+token.charAt(1));
            pin2.setText(""+token.charAt(2));
            pin3.setText(""+token.charAt(3));

            new CountDownTimer(4000, 5000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    pref.createLogin();
                    //apenergopoioume to SMSReceiver
                    HelperActivity.disableBroadcastReceiver(getApplicationContext());
                    Intent intent = new Intent(ConfirmNumber.this, Register.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }.start();

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.confirm_number);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.confirm);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dialog = new ProgressDialog(this);
        pref = new PrefManager(this);

        sendAgain = (TextView) findViewById(R.id.fourthMes);

        //registerReceiver(mTimerBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));

        LocalBroadcastManager.getInstance(this).registerReceiver(mTokenBroadcastReceiver,
                new IntentFilter("token"));

        pin0 = (EditText) findViewById(R.id.pin0);
        pin1 = (EditText) findViewById(R.id.pin1);
        pin2 = (EditText) findViewById(R.id.pin2);
        pin3 = (EditText) findViewById(R.id.pin3);

        TextView number = (TextView) findViewById(R.id.thirdMes);
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");
        number.setText("+30 " + phone);

        HelperActivity.showDialogBox(getApplicationContext(), dialog);
        new HttpGetTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        Log.e("Resume", "Resume");
        Log.e("Countdown",""+pref.getCountdown());

        switch (pref.getCountdown()) {
            case "first":
                TimerIntent = new Intent(this, TimerService.class);
                TimerIntent.putExtra("attempt","first");
                pref.setCountdown("firstRunning");
                LocalBroadcastManager.getInstance(this).registerReceiver(mTimerBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
                startService(TimerIntent);
                break;
            case "firstRunning":
                sendAgain.setOnClickListener(null);
                LocalBroadcastManager.getInstance(this).registerReceiver(mTimerBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
                break;
            case "second":
                Log.e("Resume","second");
                LocalBroadcastManager.getInstance(this).registerReceiver(mTimerBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
                sendAgain.setText(getString(R.string.conf_send_again));
                sendAgain.setPaintFlags(sendAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                sendAgain.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                            pref.setCountdown("secondRunning");
                            TimerIntent = new Intent(getApplicationContext(), TimerService.class);

                        TimerIntent.putExtra("attempt", "second");
                        startService(TimerIntent);
                        sendAgain.setPaintFlags(sendAgain.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                        second = true;
                        HelperActivity.showDialogBox(getApplicationContext(), dialog);
                        new HttpGetTask().execute();
                        }

                });
                break;

            case "secondRunning":
                sendAgain.setOnClickListener(null);
                LocalBroadcastManager.getInstance(this).registerReceiver(mTimerBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
                break;
            case "Last":
                LocalBroadcastManager.getInstance(this).registerReceiver(mTimerBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));
                sendAgain.setText(getString(R.string.conf_send_again));
                sendAgain.setPaintFlags(sendAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                sendAgain.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        pref.setCountdown("End1");
                        sendAgain.setText("Δεν έχεις δικάιωμα για άλλα μηνύματα");
                        sendAgain.setPaintFlags(sendAgain.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                        sendAgain.setOnClickListener(null);
                        second  = true;
                        HelperActivity.showDialogBox(getApplicationContext(), dialog);
                        new HttpGetTask().execute();
                    }

                });
                break;
            case "End":
                sendAgain.setText("Δεν έχεις δικάιωμα για άλλα μηνύματα");
                sendAgain.setPaintFlags(sendAgain.getPaintFlags() & (~Paint.UNDERLINE_TEXT_FLAG));
                sendAgain.setOnClickListener(null);
                break;

            default:
                break;
        }



    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        if(pref.getCountdown().equals("firstRunning")||pref.getCountdown().equals("secondRunning"))
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimerBroadcastReceiver);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass method first
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTokenBroadcastReceiver);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {

            if (HelperActivity.isOnline(getApplicationContext())) {
                HelperActivity.showDialogBox(getApplicationContext(), dialog);
                String p1 = pin0.getText().toString().trim();
                String p2 = pin1.getText().toString().trim();
                String p3 = pin2.getText().toString().trim();
                String p4 = pin3.getText().toString().trim();



                if (p1.equals("") || p2.equals("") || p3.equals("") || p4.equals("")) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.fill_all_boxes), Toast.LENGTH_LONG).show();
                }
                else {
                    Intent httpIntent = new Intent(getApplicationContext(), VerifyService.class);
                    httpIntent.putExtra("otp", p1+p2+p3+p4);
                    if (pref.getOldUser())
                        httpIntent.putExtra("oldUser", "yes");
                    else
                        httpIntent.putExtra("oldUser", "no");

                    startService(httpIntent);
                }
            } else
                HelperActivity.httpErrorToast(getApplicationContext(), 1);
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpGetTask extends AsyncTask<Void, Void, JSONObject> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;

        @Override
        protected JSONObject doInBackground(Void... arg0) {
            String URL = "http://givmed.com:81/reg/";
            String data = "";
            JSONObject out = new JSONObject();

            String request = URL;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(request);
                String urlParameters = "userPhone="   + phone +
                                        "&os=A";


                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn.getInputStream());
                data = HelperActivity.readStream(in);
                returnCode = conn.getResponseCode();
                if (!second && returnCode==202)
                    pref.setOldUser(true);
                Log.e("data", data);

                //JSONObject obj = new JSONObject(data);
                //Log.e("thlefwno", obj.getString("userPhone"));


            } catch (ProtocolException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "ProtocolException");
            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "IOException"+e);
            } finally {
                if (null != conn)
                    conn.disconnect();
            }
            try {
                Log.e(TAG, "phone"+phone);
                out.put("phone",phone);
            } catch (JSONException e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
            return out;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                regCount = pref.getRegCount() + 1;
                pref.setRegCount(regCount);
                try {
                    pref.setMobileNumber(result.get("phone").toString());
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
                //kanoume update thn vash tou kinitou me tis plhrofories
                //kai ton stelnoume sto SMS validation
                //apo8hkeuoume to kinhto tou xrhsth ston prefmanager
            }

            dialog.dismiss();
        }
    }
}