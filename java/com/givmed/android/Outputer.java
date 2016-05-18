package com.givmed.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioGroup;

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

public class Outputer extends HelperActivity {
    private EditText mName, mExp, mBarcode, mNotes;
    private RadioGroup mConditionGroup;
    JSONObject obj = null;
    private String server_date = "", name = "", date = "", barcode = "", eofcode = "", state = "", price = "", notes = "";
    ProgressDialog dialog;
    AlertDialog alert;
    PrefManager pref;
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        super.setMenu(R.menu.menu_main);
        super.helperOnCreate(R.layout.outputs, R.string.outputer, true);

        db = new DBHandler(getApplicationContext());
        pref = new PrefManager(this);
        dialog = new ProgressDialog(this);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        server_date = intent.getStringExtra("server_date");
        date = intent.getStringExtra("date");
        eofcode = intent.getStringExtra("eofcode");
        barcode = intent.getStringExtra("barcode");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.out_med_registered))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
//                        Intent TedxIntent = new Intent(getApplicationContext(), ImFine.class);
////                        TedxIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                        TedxIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        TedxIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//                        startActivity(TedxIntent);
//                        finish();
                    }
                });
        alert = builder.create();

        mName = (EditText) findViewById(R.id.name);
        mExp = (EditText) findViewById(R.id.expiration);
        mBarcode = (EditText) findViewById(R.id.barcode);
        mConditionGroup = (RadioGroup) findViewById(R.id.conditionGroup);
        mNotes = (EditText) findViewById(R.id.notes);

        mName.setText(name);
        mExp.setText(date);
        mBarcode.setText(barcode);

        mName.setKeyListener(null);
        mExp.setKeyListener(null);
        mBarcode.setKeyListener(null);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        GivmedApplication.getInstance().trackScreenView("Outputer");



    }

    @Override
    public void onBackPressed()
    {
        //do whatever you want the 'Back' button to do
        //as an example the 'Back' button is set to start a new Activity named 'NewActivity'
        Intent newIntent = new Intent(Outputer.this, Inputter.class);
        newIntent.putExtra("barcode",barcode);
        this.startActivity(newIntent);
        finish();
        return;
    }

    private boolean isOpen() {

        switch (mConditionGroup.getCheckedRadioButtonId()) {
            case R.id.closed: {
                return false;
            }
            default: {
                return true;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_tick) {
            if (isOnline(getApplicationContext())) {
                showDialogBox(getApplicationContext(), dialog);

                state = (isOpen()) ? "O" : "C";
                notes = mNotes.getText().toString().trim();
                new HttpOutputer().execute();
            } else
                HelperActivity.httpErrorToast(getApplicationContext(), 1);
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpOutputer extends AsyncTask<Object, Void, Integer> {

        private static final String TAG = "HttpGetTask_Outputer";
        private int error = -1;
        private int result ;

        @Override
        protected Integer doInBackground(Object... input) {
            String data = "";
            java.net.URL url = null;
            HttpURLConnection conn2 = null;
            String URL = server + "/med_check/" + pref.getMobileNumber() + "/";

            try {
                url = new URL(URL);
                String urlParameters =
                        "state=" + state +
                        "&notes=" + notes +
                        "&name=" + name +
                        "&expirationDate=" + server_date +
                        "&eofcode=" + eofcode +
                        "&barcode=" + barcode;

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn2 = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn2.setRequestMethod("POST");
                conn2.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(conn2.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn2.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                data = readStream(in);

                Log.e(TAG, "Komple? " + data);

                result = conn2.getResponseCode();

                if (result == 201)
                    obj = new JSONObject(data);

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
                if (null != conn2)
                    conn2.disconnect();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0) {
                httpErrorToast(getApplicationContext(), error);
            } else {
                if (result == 204) {
                    dialog.dismiss();
                    alert.show();
                    return;
                }

                if (result == 201) {
                    String status = "U";
                    try {
                        if (!obj.getBoolean("medIsDonatable"))
                            status = "D";
                        else if (state.equals("O") && !obj.getBoolean("medIsDonatableIfOpen"))
                            status = "SN";
                        else if (obj.getBoolean("medIsDonatableIfOpen"))
                            status = "SU";

                        db.addMed(new Medicine(barcode, eofcode, name, date, obj.getString("medPrice"), notes, state,
                            obj.getString("medSubstance"), obj.getString("medCategory"), status), firstWord(name));

                    } catch (JSONException e) {
                        Crashlytics.logException(e);
                        e.printStackTrace();
                        dialog.dismiss();
                        httpErrorToast(getApplicationContext(), 2);
                        return;
                    }

                    Intent showItemIntent = new Intent(getApplicationContext(), ImFine.class);
                    // If set, and the activity being launched is already running in the current task, then instead of
                    // launching a new instance of that activity, all of the other activities on top of it will be closed and
                    // this Intent will be delivered to the (now on top) old activity as a new Intent.
                    //showItemIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    // If set, the activity will not be launched if it is already running at the top of the history stack.
                    //showItemIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    showItemIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(showItemIntent);
                    finish();
                }
            }
            dialog.dismiss();
        }
    }

}