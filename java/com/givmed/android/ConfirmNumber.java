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
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

public class ConfirmNumber extends AppCompatActivity {
    private String phone;
    private String token;
    private EditText pin0, pin1, pin2, pin3;
    private TextView sendAgain;
    private PrefManager pref;
    private Intent TimerIntent;
    ProgressDialog dialog;

    private BroadcastReceiver mTimerBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long countdown=intent.getLongExtra("countdown",1L);
            new CountDownTimer(countdown, 1000) {

                public void onTick(long millisUntilFinished) {
                    sendAgain.setText("seconds remaining: " + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    sendAgain.setText(getString(R.string.conf_send_again));
                    sendAgain.setPaintFlags(sendAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    sendAgain.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getApplicationContext(), OroiXrhshs.class));
                        }
                    });
                    stopService(TimerIntent);
                    pref.setCountdown(false);
                }
            }.start();        }

    };


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
                    Intent intent = new Intent(ConfirmNumber.this, Elleipseis.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }.start();

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        pref = new PrefManager(this);


        sendAgain = (TextView) findViewById(R.id.fourthMes);
        TimerIntent = new Intent(this, TimerService.class);
        if(pref.getCountdown())
            startService(TimerIntent);
        else {
            sendAgain.setText(getString(R.string.conf_send_again));
            sendAgain.setPaintFlags(sendAgain.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            sendAgain.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), OroiXrhshs.class));
                }
            });
        }
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




        //final TextView sendAgain = (TextView) findViewById(R.id.fourthMes);


        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading, Please Wait...");
        dialog.show();
        new HttpGetTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(pref.getCountdown())
            LocalBroadcastManager.getInstance(this).registerReceiver(mTimerBroadcastReceiver, new IntentFilter(TimerService.BROADCAST_ACTION));

    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        if(pref.getCountdown())
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mTimerBroadcastReceiver);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();  // Always call the superclass method first
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTokenBroadcastReceiver);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_simple, menu);
        return true;
    }

    private class HttpGetTask extends AsyncTask<Void, Void, JSONObject> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;

        @Override
        protected JSONObject doInBackground(Void... arg0) {
            String URL = "https://givmed.com:444/reg/";
            String data = "";
            JSONObject out = new JSONObject();

            String request = URL;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(request);
                String urlParameters = "userPhone="   + phone ;


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
                Log.e("data", data);

                //JSONObject obj = new JSONObject(data);
                //Log.e("thlefwno", obj.getString("userPhone"));


            } catch (ProtocolException e) {
                error = 1;
                Log.e(TAG, "ProtocolException");
            } catch (MalformedURLException exception) {
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                error = 1;
                Log.e(TAG, "IOException"+exception);
            } finally {
                if (null != conn)
                    conn.disconnect();
            }
            try {
                Log.e(TAG, "phone"+phone);
                out.put("phone",phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return out;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (error > 0) {
                Toast.makeText(getApplicationContext(), (error == 1) ? "skata" : "Nothing to show",
                        Toast.LENGTH_LONG).show();
            }
            else {
                try {
                    pref.setMobileNumber(result.get("phone").toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //kanoume update thn vash tou kinitou me tis plhrofories
                //kai ton stelnoume sto SMS validation
                //apo8hkeuoume to kinhto tou xrhsth ston prefmanager
            }

            dialog.dismiss();
            dialog = null;
        }
    }
}