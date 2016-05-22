package com.givmed.android;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

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

import io.fabric.sdk.android.Fabric;

/**
 * Created by agroikos on 2/5/2016.
 */
public class SplashActivity extends AppCompatActivity {
    private String needDate, pharDate;
    private PrefManager pref;
    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        db = new DBHandler(getApplicationContext());
        //db.printAllMeds();

        pref = new PrefManager(this);
        pref.setMobileNumber("12345");
        pharDate = pref.getPharDate();
        needDate = pref.getNeedDate();

        new HttpPharmacies().execute();

        Intent intent;
        startService(new Intent(this, AlarmService.class));
        switch (pref.getNextSplash()) {
            case "Tutorial":
                intent = new Intent(this, Tutorial.class);
                break;
            case "Register":
                intent = new Intent(this, Register.class);
                break;
            default:
                intent = new Intent(this, TwoButtons.class);
        }
        startActivity(intent);
        finish();
    }

    private class HttpPharmacies extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;
        String pharms;

        @Override
        protected Integer doInBackground(Void... arg0) {
            String URL = HelperActivity.server + "/pharmacies_date/" + pharDate + "/";
            Integer out = 0;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(URL);
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
                conn.setDoInput(true);
                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                pharms = HelperActivity.readStream(in);
                out = conn.getResponseCode();

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
        protected void onPostExecute(Integer result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 200) {
                    try {
                        JSONObject obj = new JSONObject(pharms);
                        pref.setPharDate(obj.getString("newDate"));
                        JSONArray objs = new JSONArray(obj.getString("data"));
                        db.deletePharmacies();

                        for (int i = 0; i < objs.length(); i++) {
                            JSONObject json = objs.getJSONObject(i);

                            db.addPharmacy(json.getString("pharmacyPhone"), json.getString("pharmacyAddress"),
                                    json.getString("openTime"), json.getString("pharmacyName"), json.getString("pharmacyNameGen"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    new HttpGetNeeds().execute();
                }
                else if (result == 204)
                    new HttpGetNeeds().execute();
            }
        }
    }

    private class HttpGetNeeds extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "HttpGetTask";
        String needs;
        private int error = -1;

        @Override
        protected Integer doInBackground(Void... arg0) {
            String URL = HelperActivity.server + "/needs_date/" + needDate + "/";
            Integer out = 0;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(URL);
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
                conn.setDoInput(true);
                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                needs = HelperActivity.readStream(in);
                out = conn.getResponseCode();

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
        protected void onPostExecute(Integer result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 200) {
                    try {
                        JSONObject obj = new JSONObject(needs);
                        pref.setNeedDate(obj.getString("newDate"));
                        JSONArray objs = new JSONArray(obj.getString("data"));
                        db.deleteNeeds();

                        for (int i = 0; i < objs.length(); i++) {
                            JSONObject json = objs.getJSONObject(i);
                            Need needo = new Need();
                            needo.setNeedName(json.getString("needMedName"));
                            needo.setPhone(json.getString("needPhone"));
                            db.addNeed(needo);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}