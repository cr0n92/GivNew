package com.givmed.android;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.givmed.android.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Elleipseis extends HelperActivity
{
    private final String TAG = "Ellepseis";
    public static NeedAdapter mAdapter;
    private static TextView msgView;
    private static Button nameButton, regionButton;
    DBHandler db;

    private Tracker mTracker;

    public static void changeButtonsLayout(Button pressed, Button unpressed, int presDraw, int unpresDraw) {
        pressed.setTextColor(Color.WHITE);
        pressed.setBackgroundResource(presDraw);
        unpressed.setTextColor(Color.BLACK);
        unpressed.setBackgroundResource(unpresDraw);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.eleipseis, R.string.elleipseis, false);

        msgView = (TextView) findViewById(R.id.secondMes);
        nameButton = (Button) findViewById(R.id.nameButton);
        regionButton = (Button) findViewById(R.id.regionButton);
        msgView.setText(getResources().getString(R.string.need_left_half_msg) + " (0) " + getResources().getString(R.string.need_right_half_msg_plural));

        nameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(nameButton, regionButton, R.drawable.button_pressed_left, R.drawable.button_unpressed_right);
            }
        });

        regionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                changeButtonsLayout(regionButton, nameButton, R.drawable.button_pressed_right, R.drawable.button_unpressed_left);
            }
        });

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());

        Log.i(TAG, "Setting screen name: Elleipseis");
        mTracker.setScreenName("Image~");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mAdapter = new NeedAdapter(getApplicationContext());
        ListView list = (ListView)findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        //registerForContextMenu(getListView());
        list.setAdapter(mAdapter);

        db = new DBHandler(getApplicationContext());

        if (isOnline())
            new HttpGetTask().execute();
        else
            ;

        int count = db.getAllNeeds(mAdapter, "region");
        int right_msg = (count == 1) ? R.string.need_right_half_msg_single : R.string.need_right_half_msg_plural;
        msgView.setText(getResources().getString(R.string.need_left_half_msg) + " " + count + " " + getResources().getString(right_msg));
    }

    private class HttpGetTask extends AsyncTask<Void, Void, JSONArray> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;

        @Override
        protected JSONArray doInBackground(Void... arg0) {

            String URL = server + "/needs/";

            String data = "";
            JSONArray out = null;

            String request = URL;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(request);
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection

                //conn.setConnectTimeout(10* 1000);          // 10 s.
                //conn.connect();

                conn.setDoInput(true);

                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                data = HelperActivity.readStream(in);

                out = new JSONArray(data);

            } catch (JSONException e) {
                Log.e(TAG, "JsonException");
            } catch (ProtocolException e) {
                error = 1;
                Log.e(TAG, "ProtocolException");
            } catch (MalformedURLException exception) {
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                error = 1;
                exception.printStackTrace();
                Log.e(TAG, "IOException");
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return out;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            if (error > 0) {
                Toast.makeText(getApplicationContext(), (error == 1)? "No internet connection" : "Nothing to show",
                        Toast.LENGTH_LONG).show();
            } else {
                db.deleteAll();
                try {
                    for(int i=0; i<result.length(); i++) {
                        JSONObject json = result.getJSONObject(i);
                        Log.i("etsi pou les", "" + json);
                        Need needo = new Need();
                        needo.setNeedName(json.getString("needMedName"));
                        needo.setPhone(json.getString("needPhone"));
                        needo.setAddress(json.getString("needAddress"));
                        needo.setName("ellhnikou");
                        needo.setRegion("lelos");
                        db.addNeed(needo);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}