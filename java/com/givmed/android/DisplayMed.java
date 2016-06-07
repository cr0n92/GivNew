package com.givmed.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class DisplayMed extends AppCompatActivity {
    private EditText mNotes;
    private Button mBefore, mNow, mOpen, mClose;
    private String phone, notes, state, forDonation, pharPhone, firstName;
    private String[] progDonation = null;
    private static Medicine med;
    private int needsCnt = -1;
    public boolean isOpen;
    ProgressDialog dialog;
    AlertDialog.Builder builder;
    AlertDialog matched;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.display_med);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.farmako);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = new DBHandler(getApplicationContext());
        dialog = new ProgressDialog(this);
        builder = new AlertDialog.Builder(this);

        PrefManager pref = new PrefManager(this);
        phone = pref.getMobileNumber();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("barcode"))
            med = db.getMed(intent.getStringExtra("barcode"));
        else
            finish();

        firstName = HelperActivity.firstWord(med.getName());

        EditText mName = (EditText) findViewById(R.id.name);
        EditText mExp = (EditText) findViewById(R.id.expiration);
        EditText mCategory = (EditText) findViewById(R.id.category);
        EditText mBarcode = (EditText) findViewById(R.id.barcode);
        mOpen = (Button) findViewById(R.id.opend);
        mClose = (Button) findViewById(R.id.closed);
        mNow = (Button) findViewById(R.id.now);
        mBefore = (Button) findViewById(R.id.before);
        mNotes = (EditText) findViewById(R.id.notes);

        mName.setKeyListener(null);
        mExp.setKeyListener(null);
        mBarcode.setKeyListener(null);
        mCategory.setKeyListener(null);

        setCondition();
        setDonation();

        mName.setText(med.getName());
        mCategory.setText(med.getCategory());
        mExp.setText(med.getDate());
        mBarcode.setText(med.getBarcode());
        mNotes.setText(med.getNotes());

        // koitame an yparxei programmatismenh dwrea gia auto to farmako
        // an gyrisei null den yparxei alliws yparxei
        progDonation = db.getProgDonation(med.getBarcode());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_delete, menu);
        return true;
    }

    private void setCondition() {
        switch (med.getState()) {
            case "O":
                isOpen = true;
                HelperActivity.changeButtonsLayout(mOpen, mClose, R.drawable.button_gray_pressed,
                        R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
                break;
            case "C":
                isOpen = false;
                HelperActivity.changeButtonsLayout(mClose, mOpen, R.drawable.button_gray_pressed,
                        R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
                break;
        }

        mClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isOpen = false;
                HelperActivity.changeButtonsLayout(mClose, mOpen, R.drawable.button_gray_pressed,
                        R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
            }
        });

        mOpen.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isOpen = true;
                HelperActivity.changeButtonsLayout(mOpen, mClose, R.drawable.button_gray_pressed,
                        R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
            }
        });
    }

    private void setDonation() {
        switch (med.getStatus()) {
            case "D":
                mNow.setEnabled(false);
                mBefore.setEnabled(false);
                return;
            case "Y":
            case "SY":
                HelperActivity.changeButtonsLayout(mNow, mBefore, R.drawable.button_gray_pressed,
                        R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
                break;
            case "B":
            case "SB":
                HelperActivity.changeButtonsLayout(mBefore, mNow, R.drawable.button_gray_pressed,
                        R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
                break;
        }

        mNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mBefore.getCurrentTextColor() == Color.WHITE)
                    HelperActivity.changeButtonsLayout(mNow, mBefore, R.drawable.button_gray_pressed,
                        R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
                else {
                    if (mNow.getCurrentTextColor() == Color.WHITE) {
                        mNow.setTextColor(Color.GRAY);
                        mNow.setBackgroundResource(R.drawable.button_gray_unpressed);
                    }
                    else {
                        mNow.setTextColor(Color.WHITE);
                        mNow.setBackgroundResource(R.drawable.button_gray_pressed);
                    }
                }
            }
        });

        mBefore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mNow.getCurrentTextColor() == Color.WHITE)
                    HelperActivity.changeButtonsLayout(mBefore, mNow, R.drawable.button_gray_pressed,
                            R.drawable.button_gray_unpressed, Color.WHITE, Color.GRAY);
                else {
                    if (mBefore.getCurrentTextColor() == Color.WHITE) {
                        mBefore.setTextColor(Color.GRAY);
                        mBefore.setBackgroundResource(R.drawable.button_gray_unpressed);
                    }
                    else {
                        mBefore.setTextColor(Color.WHITE);
                        mBefore.setBackgroundResource(R.drawable.button_gray_pressed);
                    }
                }
            }
        });
    }

    private boolean isOpen() {
        return isOpen;
    }

    private void isDonatedNow() {
        if (mBefore.getCurrentTextColor() == Color.WHITE)
            forDonation = "B";
        else if (mNow.getCurrentTextColor() == Color.WHITE)
            forDonation = "Y";
        else
            forDonation = "N";
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            builder.setMessage((progDonation == null) ? getString(R.string.delete_sure) : getString(R.string.out_unmatched))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2, int id) {
                            if (HelperActivity.isOnline(getApplicationContext())) {
                                HelperActivity.showDialogBox(getApplicationContext(), dialog);
                                new HttpDeleteMedTask().execute(med.getBarcode());
                            } else
                                HelperActivity.httpErrorToast(getApplicationContext(), 1);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2, int id) {
                            if (db.checkMedSubscribe(firstName, false)) {
                                ArrayList<String> topics = new ArrayList<String>();
                                topics.add(firstName);
                                Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
                                serviceIntent.putExtra("subscribe", false);
                                serviceIntent.putStringArrayListExtra("topic", topics);
                                startService(serviceIntent);
                            }
                        }
                    });
            AlertDialog deleteAlert = builder.create();
            deleteAlert.show();
            return true;
        }
        else if (id == R.id.action_tick) {
            if (HelperActivity.isOnline(getApplicationContext())) {
                HelperActivity.showDialogBox(getApplicationContext(), dialog);

                notes = mNotes.getText().toString().trim();
                state = (isOpen()) ? "O" : "C";

                forDonation = med.getStatus();
                if (!forDonation.equals("D")) {
                    isDonatedNow();

                    if (med.getStatus().charAt(0) == 'S') {
                        if (med.getState().equals("C") && isOpen()) {
                           if( db.checkMedSubscribe(firstName, false)) {
                               ArrayList<String> topics = new ArrayList<String>();
                               topics.add(firstName);
                               Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
                               serviceIntent.putExtra("subscribe", false);
                               serviceIntent.putStringArrayListExtra("topic", topics);
                               startService(serviceIntent);
                           }

                        }


                        if (isOpen() && progDonation != null) {
                            builder.setMessage(getString(R.string.out_unmatched_sirup) + " " + firstName + ".")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog2, int id) {
                                            if (HelperActivity.isOnline(getApplicationContext())) {
                                                HelperActivity.showDialogBox(getApplicationContext(), dialog);
                                                forDonation = "S" + forDonation;
                                                new HttpDonationDelete().execute();
                                            } else
                                                HelperActivity.httpErrorToast(getApplicationContext(), 1);
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog2, int id) {

                                        }
                                    });
                            AlertDialog sirupUnmatchAlert = builder.create();

                            dialog.dismiss();
                            sirupUnmatchAlert.show();
                            return true;
                        }

                        if ((forDonation.equals("Y") || forDonation.equals("B")) && isOpen()) {
                            builder.setMessage(getString(R.string.out_is_sirup))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog2, int id) {

                                        }
                                    });
                            AlertDialog sirupAlert = builder.create();

                            dialog.dismiss();
                            sirupAlert.show();
                            return true;
                        }
                        forDonation = "S" + forDonation;
                    }

                    if (!(forDonation.equals("SY") || forDonation.equals("Y"))) {
                        if (progDonation != null) {
                            builder.setMessage(getString(R.string.out_unmatched))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog2, int id) {
                                            if (HelperActivity.isOnline(getApplicationContext())) {
                                                HelperActivity.showDialogBox(getApplicationContext(), dialog);
                                                new HttpDonationDelete().execute();
                                            } else
                                                HelperActivity.httpErrorToast(getApplicationContext(), 1);
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog2, int id) {

                                        }
                                    });
                            AlertDialog progExistsAlert = builder.create();

                            dialog.dismiss();
                            progExistsAlert.show();
                            return true;
                        }
                        //edw mpainoume an htan Y/SY kai twra egine otidhpote allo
                        else if (med.getStatus().equals("Y") || med.getStatus().equals("SY")){
                            if( db.checkMedSubscribe(firstName, false)) {
                                ArrayList<String> topics = new ArrayList<String>();
                                topics.add(firstName);
                                Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
                                serviceIntent.putExtra("subscribe", false);
                                serviceIntent.putStringArrayListExtra("topic", topics);
                                startService(serviceIntent);
                            }

                        }
                    }

                    if ((forDonation.equals("SY") || forDonation.equals("Y")) && (progDonation == null)) {
                        Object[] info = db.matchExists(firstName);
                        needsCnt = (int) info[0];
                        pharPhone = (String) info[1];

                        if (needsCnt > 0) {
                            builder.setMessage(getString(R.string.out_matched_left) + " " + firstName + getString(R.string.out_matched_right))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog2, int id) {
                                            Intent afterdel = new Intent(getApplicationContext(), Dwrees.class);
                                            afterdel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            afterdel.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                            startActivity(afterdel);
                                            finish();
                                        }
                                    });
                            matched = builder.create();

                            new HttpAddDonation().execute();
                            return true;
                        }
                        //an to farmako ginei h htan (cook alert) Y/SY kai dn uparxei match tote kanoume subscribe
                        else {
                            if( db.checkMedSubscribe(firstName,true)) {
                                ArrayList<String> topics = new ArrayList<String>();
                                topics.add(firstName);
                                Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
                                serviceIntent.putExtra("subscribe", true);
                                serviceIntent.putStringArrayListExtra("topic", topics);
                                startService(serviceIntent);
                            }
                        }
                    }
                }

                new HttpUpdateMedTask().execute();
            } else
                HelperActivity.httpErrorToast(getApplicationContext(), 1);
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpAddDonation extends AsyncTask<Object, Void, Integer> {

        private static final String TAG = "HttpGetTask_Volunteer";
        private int error = -1;
        private int result;
        private String category, price;

        @Override
        protected Integer doInBackground(Object... input) {
            java.net.URL url = null;
            HttpURLConnection conn = null;
            String URL = HelperActivity.server + "/donation_add_and_update_med/";

            try {
                url = new URL(URL);
                String urlParameters =
                        "state=" + state +
                        "&forDonation=" + forDonation +
                        "&notes=" + notes +
                        "&donationBarcode=" + med.getBarcode() +
                        "&deliveryType=A";

				if (needsCnt == 1)
					urlParameters += "&donatedPhone=" + pharPhone;

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                String data = HelperActivity.readStream(in);

                Log.e(TAG, "Komple? " + data);
                result = conn.getResponseCode();
                if (result == 201) {
                    JSONObject obj = new JSONObject(data);
                    price = obj.getString("price");
                    category = obj.getString("category");
                }
                Log.e(TAG, "Received HTTP response: " + result);

            } catch (ProtocolException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "ProtocolException");
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Crashlytics.logException(e);
                error = 2;
                Log.e(TAG, "IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                Crashlytics.logException(e);
                error = 2;
                e.printStackTrace();
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 201) {
                    db.addDonation(med.getBarcode(), pharPhone, ";", ";", ";", "A", ";");

                    med.setState(state);
                    med.setNotes(notes);
                    med.setStatus(forDonation);

                    db.updateMed(med);
                    db.updateEofStuff(med.getEofcode(), price, category);

                    dialog.dismiss();
                    matched.show();
                    return;
                } else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }

    private class HttpDonationDelete extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "HttpDelete";
        private int error = -1;
        private int result;
        private String price, category;

        @Override
        protected Integer doInBackground(String... input) {
            java.net.URL url = null;
            HttpURLConnection conn = null;

            String URL = HelperActivity.server + "/donation_delete_and_update_med/" + med.getBarcode() + "/";

            try {
                url = new URL(URL);

                String urlParameters =
                        "state=" + state +
                        "&forDonation=" + forDonation +
                        "&notes=" + notes;

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                String data = HelperActivity.readStream(in);//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.

                result = conn.getResponseCode();
                if (result == 201 || result == 200) {
                    JSONObject obj = new JSONObject(data);
                    price = obj.getString("price");
                    category = obj.getString("category");
                }
                Log.e(TAG, "Received HTTP response: " + result);

            } catch (ProtocolException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "ProtocolException");
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException e) {
                Crashlytics.logException(e);
                error = 2;
                Log.e(TAG, "IOException");
            } catch (JSONException e) {
                Crashlytics.logException(e);
                error = 2;
                e.printStackTrace();
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 200 || result == 201) {
                    db.deleteProgDonation(med.getBarcode());

                    med.setState(state);
                    med.setNotes(notes);
                    med.setStatus(forDonation);

                    db.updateMed(med);
                    db.updateEofStuff(med.getEofcode(), price, category);

                    Intent afterdel = new Intent(getApplicationContext(), Farmakeio.class);
                    afterdel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    afterdel.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(afterdel);
                    finish();
                } else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }

    private class HttpDeleteMedTask extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "HttpDeleteMedTask";
        private int error = -1;
        private int result;

        @Override
        protected Integer doInBackground(String... input) {
            java.net.URL url = null;
            HttpURLConnection conn = null;

            String URL = HelperActivity.server + "/med_delete/" + input[0] + "/";

            try {
                url = new URL(URL);

                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
                conn.setRequestMethod("DELETE");
                result = conn.getResponseCode();
                Log.e(TAG, "Received HTTP response: " + result);

            } catch (ProtocolException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "ProtocolException");
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException e) {
                Crashlytics.logException(e);
                error = 2;
                Log.e(TAG, "IOException");
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 200 || result == 204) {
                    db.deleteMed(med, HelperActivity.firstWord(med.getName()));

                    Intent afterdel = new Intent(getApplicationContext(), Farmakeio.class);
                    afterdel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    afterdel.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(afterdel);
                    finish();
                } else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }

    private class HttpUpdateMedTask extends AsyncTask<Object, Void, Integer> {

        private static final String TAG = "HttpUpdateMedTask";
        private int error = -1;
        private int result ;
        private String price, category;

        @Override
        protected Integer doInBackground(Object... input) {
            java.net.URL url = null;
            HttpURLConnection conn = null;
            String URL = HelperActivity.server + "/med_check/" + phone + "/";

            try {
                url = new URL(URL);
                String urlParameters =
                        "state=" + state +
                        "&forDonation=" + forDonation +
                        "&notes=" + notes +
                        "&barcode=" + med.getBarcode();

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                String data = HelperActivity.readStream(in);

                result = conn.getResponseCode();
                if (result == 201) {
                    JSONObject obj = new JSONObject(data);
                    price = obj.getString("price");
                    category = obj.getString("category");
                }
                Log.e(TAG, "Received HTTP response: " + result);

            } catch (ProtocolException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "ProtocolException");
                e.printStackTrace();
            } catch (MalformedURLException e) {
                Crashlytics.logException(e);
                error = 1;
                Log.e(TAG, "MalformedURLException");
                e.printStackTrace();
            } catch (IOException e) {
                Crashlytics.logException(e);
                error = 2;
                Log.e(TAG, "IOException");
                e.printStackTrace();
            } catch (JSONException e) {
                Crashlytics.logException(e);
                error = 2;
                e.printStackTrace();
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 201) {
                    med.setState(state);
                    med.setNotes(notes);
                    med.setStatus(forDonation);

                    db.updateMed(med);
                    db.updateEofStuff(med.getEofcode(), price, category);

                    Intent afterdel = new Intent(getApplicationContext(), Farmakeio.class);
                    afterdel.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    afterdel.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(afterdel);
                    finish();
                }
                else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }
}
