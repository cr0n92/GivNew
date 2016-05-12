package com.givmed.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.crashlytics.android.Crashlytics;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;

import io.fabric.sdk.android.Fabric;

public class DisplayMed extends AppCompatActivity {
    private EditText mName, mExp, mBarcode, mNotes, mCategory;
    private RadioGroup mConditionGroup, mDonationGroup;
    private RadioButton mBefore, mNow, mNo, mOpen, mClose;
    private String phone, notes, state, forDonation;
    private static Medicine med;
    AlertDialog sirupAlert, deleteAlert;
    ProgressDialog dialog;
    DBHandler db;
    private PrefManager pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.display_med);

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
        pref = new PrefManager(this);
        phone = pref.getMobileNumber();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("barcode"))
            med = db.getMed(intent.getStringExtra("barcode"));
        else
            finish();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.out_is_sirup))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        sirupAlert = builder.create();

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage(getString(R.string.delete_sure))
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
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        deleteAlert = builder.create();

        mName = (EditText) findViewById(R.id.name);
        mExp = (EditText) findViewById(R.id.expiration);
        mCategory = (EditText) findViewById(R.id.category);
        mBarcode = (EditText) findViewById(R.id.barcode);
        mConditionGroup = (RadioGroup) findViewById(R.id.conditionGroup);
        mOpen = (RadioButton) findViewById(R.id.opend);
        mClose = (RadioButton) findViewById(R.id.closed);
        mDonationGroup = (RadioGroup) findViewById(R.id.donationGroup);
        mNow = (RadioButton) findViewById(R.id.now);
        mBefore = (RadioButton) findViewById(R.id.before);
        mNo = (RadioButton) findViewById(R.id.no);
        mNotes = (EditText) findViewById(R.id.notes);

        mName.setKeyListener(null);
        mExp.setKeyListener(null);
        mBarcode.setKeyListener(null);
        mCategory.setKeyListener(null);

        setConditionRadioGroup();
        setDonationRadioGroup();

        mName.setText(med.getName());
        mCategory.setText(med.getCategory());
        mExp.setText(med.getDate());
        mBarcode.setText(med.getBarcode());
        mNotes.setText(med.getNotes());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_delete, menu);
        return true;
    }

    private void setConditionRadioGroup() {
        switch (med.getState()) {
            case "O":
                mClose.setChecked(false);
                mOpen.setChecked(true);
                break;
            case "C":
                mClose.setChecked(true);
                mOpen.setChecked(false);
                break;
        }
    }

    private void setDonationRadioGroup() {
        switch (med.getStatus()) {
            case "D":
                mNow.setEnabled(false);
                mBefore.setEnabled(false);
                return;
            case "N":
            case "SN":
                mNow.setChecked(false);
                mBefore.setChecked(false);
                mNo.setChecked(true);
                break;
            case "Y":
            case "SY":
                mNow.setChecked(true);
                mBefore.setChecked(false);
                mNo.setChecked(false);
                break;
            case "B":
            case "SB":
                mNow.setChecked(false);
                mBefore.setChecked(true);
                mNo.setChecked(false);
                break;
        }
    }

    private boolean isOpen() {

        switch (mConditionGroup.getCheckedRadioButtonId()) {
            case R.id.closed:
                return false;
            default:
                return true;
        }
    }

    private void isDonatedNow() {

        switch (mDonationGroup.getCheckedRadioButtonId()) {
            case R.id.before:
                forDonation = "B";
                return;
            case R.id.now:
                forDonation = "Y";
                return;
            default:
                forDonation = "N";
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete) {
            deleteAlert.show();
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
                        if ((forDonation.equals("Y") || forDonation.equals("B")) && isOpen()) {
                            dialog.dismiss();
                            sirupAlert.show();
                            return true;
                        }
                        forDonation = "S" + forDonation;
                    }
                }

                new HttpUpdateMedTask().execute();
            } else
                HelperActivity.httpErrorToast(getApplicationContext(), 1);
        }

        return super.onOptionsItemSelected(item);
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
                    // TODO: diagrafoume kai apo programmatismenes an yparxei
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

        @Override
        protected Integer doInBackground(Object... input) {
            java.net.URL url = null;
            HttpURLConnection conn = null;
            String URL = HelperActivity.server + "/med_check/" + "12345/";

            try {
                url = new URL(URL);
                String urlParameters =
                        "state=" + state +
                        "&forDonation=" + forDonation +
                        "&notes=" + notes +
                        "&barcode=" + med.getBarcode();

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                conn.getInputStream();//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.

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
                e.printStackTrace();
            } catch (IOException e) {
                Crashlytics.logException(e);
                error = 2;
                Log.e(TAG, "IOException");
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

                    //TODO: an kanei to med pros dwrea na kanoume match
                }
                else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }
}
