package com.givmed.android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;


public class DwreaUser extends AppCompatActivity {

    private static String barcode="",medname="",pharmname="",todayDate="",  date1 = "";;
    private static EditText dateChoose;
    public static String serverDate;
    public ProgressDialog dialog;
    AlertDialog alert;
    private String[] pharInfo, donationInfo;
    DBHandler db;
    private PrefManager pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dwrea_user);

        db = new DBHandler(getApplicationContext());
        dialog = new ProgressDialog(this);

        Intent intent = getIntent();
        if (intent != null) {
            barcode = intent.getStringExtra("barcode");
            pharmname = intent.getStringExtra("pharName");
            medname = intent.getStringExtra("medName");

        }

        donationInfo = new String[7];
        db.getProgDonation(barcode, donationInfo);

        pharInfo = new String[5];
        db.getPharmacy(pharmname, pharInfo);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.choo_monos_eyxaristoume))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), Dwrees.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    }
                });
        alert = builder.create();

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.user);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        dateChoose = (EditText) findViewById(R.id.edit1);

        if (donationInfo[2].equals(";")) {
            Date date1 = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date1);
            setDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));
        }
        else {
            dateChoose.setText(donationInfo[2]);
        }

        dateChoose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), "pickDate");
            }
        });

        EditText mName = (EditText) findViewById(R.id.name);
        EditText mForeas = (EditText) findViewById(R.id.foreas);
        EditText mPhone = (EditText) findViewById(R.id.phone);

        mName.setText(medname);
        String foreas = pharmname + "," + pharInfo[1] + "," + pharInfo[2];
        mForeas.setText(foreas); // + wres leitourgias kai pws pame ekei
        mPhone.setText(pharInfo[0]);

        mName.setKeyListener(null);
        mForeas.setKeyListener(null);
        mPhone.setKeyListener(null);

        final Button doneButton = (Button) findViewById(R.id.button1);
        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (HelperActivity.isOnline(getApplicationContext())) {
                    HelperActivity.showDialogBox(getApplicationContext(), dialog);

                    Date date1 = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date1);
                    todayDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);

                    new HttpDone().execute();
                }
                else
                    HelperActivity.httpErrorToast(getApplicationContext(), 1);
            }
        });

        final Button voluButton = (Button) findViewById(R.id.button2);
        voluButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), Inputter.class);
//                startActivity(intent);
//                finish();
            }
        });
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

                if (!(dateChoose.getText().toString().trim().isEmpty())) {
                    date1 = transformDate(dateChoose.getText().toString());
                }

                new HttpUser().execute();
            } else
                HelperActivity.httpErrorToast(getApplicationContext(), 1);
        }

        return super.onOptionsItemSelected(item);
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        serverDate = year + "-" + mon + "-" + day;
        String dateString = day + "/" + mon + "/" + (year % 100);
        dateChoose.setText(dateString);
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setDateString(year, monthOfYear, dayOfMonth);
        }
    }

    public String transformDate(String date) {
        String[] splitted = date.split("/");
        return "20" + splitted[2] + "-" + splitted[1] + "-" + splitted[0];
    }


    private class HttpDone extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;
        private int result;

        @Override
        protected Integer doInBackground(Void... arg0) {
            String data = "";

            String URL = HelperActivity.server + "/add_done_donation/" + barcode + "/";
            Integer out = 0;
            java.net.URL url = null;
            HttpURLConnection conn = null;
            pref = new PrefManager(getApplicationContext());

            try {
                url = new URL(URL);
                String urlParameters =
                        "doneUser="+ pref.getMobileNumber() +
                        "&doneDeliveryType=U" +
                        "&doneDate=" + todayDate +
                        "&donePharPhone=" + pharInfo[0];

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn.getInputStream());
                data = HelperActivity.readStream(in);
                result = conn.getResponseCode();
                Log.e(TAG, "Komple? " + data);


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
                    db.progToDoneDonation(barcode, pharmname, todayDate, medname);
                    dialog.dismiss();
                    alert.show();
                    return;
                } else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }


    private class HttpUser extends AsyncTask<Object, Void, Integer> {

        private static final String TAG = "HttpGetTask_Volunteer";
        private int error = -1;
        private int result;

        @Override
        protected Integer doInBackground(Object... input) {
            String data = "";
            java.net.URL url = null;
            HttpURLConnection conn = null;
            String URL = HelperActivity.server + "/donations/";

            try {
                url = new URL(URL);
                String urlParameters =
                        "donationBarcode=" + barcode +
                        "&donatedPhone=" + pharInfo[0] +
                        "&deliveryDate1=" + date1 +
                        "&donationType=U";

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                data = HelperActivity.readStream(in);

                Log.e(TAG, "Komple? " + data);
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
                    db.updateProgDonation(barcode, pharInfo[0], date1, ";", ";", "U", ";");
                    dialog.dismiss();
                    alert.show();
                    return;
                } else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }
}
