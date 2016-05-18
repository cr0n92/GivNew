package com.givmed.android;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.widget.TextView;
import android.widget.Toast;

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


public class DwreaVolunteer extends AppCompatActivity {

    private static EditText dateChoose1, dateChoose2, dateChoose3, dateChoosed, mAddress;
    public static String medName =  "", barcode = "", pharPhone = "", pharName = "", address = "", pharNameGen = "";
    private static String sdate1 = "", sdate2 = "", sdate3 = "", todayDate = "", todayDateAndroid = "";
    private static String date1 = "", date2 = "", date3 = "";
    private static int datesCnt = 1;
    public ProgressDialog dialog;
    public AlertDialog alert, doneAlert;
    public PrefManager pref;
    public DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dwrea_volunteer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        boolean showDoneButton = false;
        Intent intent = getIntent();

        if (intent != null) {
            pharName = intent.getStringExtra("pharName");
            barcode = intent.getStringExtra("barcode");
            medName = intent.getStringExtra("medName");

            if (intent.hasExtra("secondTime")) showDoneButton = true;
        }

        dialog = new ProgressDialog(this);
        pref = new PrefManager(this);
        db = new DBHandler(getApplicationContext());

        String[] pharInfo = new String[5];
        db.getPharmacy(pharName, pharInfo);
        pharPhone = pharInfo[0];
        pharName = pharInfo[3];
        pharNameGen = pharInfo[4];

        String[] donationInfo = new String[7];
        db.getProgDonation(barcode, donationInfo);
        date1 = donationInfo[2];
        date2 = donationInfo[3];
        date3 = donationInfo[4];
        address = donationInfo[6];

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.choo_volu_call))
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

        builder.setMessage(getString(R.string.choo_done_volu_msg) + " " + pharNameGen + "!");
        doneAlert = builder.create();

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.volunteer);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dateChoosed = dateChoose1 = (EditText) findViewById(R.id.edit1);
        dateChoose2 = (EditText) findViewById(R.id.edit2);
        dateChoose3 = (EditText) findViewById(R.id.edit3);

        dateChoose1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dateChoosed = dateChoose1;
                showDatePicker();
            }
        });

        dateChoose2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dateChoosed = dateChoose2;
                showDatePicker();
            }
        });

        dateChoose3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dateChoosed = dateChoose3;
                showDatePicker();
            }
        });

        final TextView addDate = (TextView) findViewById(R.id.addMore);
        addDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (datesCnt) {
                    case 1:
                        dateChoose2.setVisibility(View.VISIBLE);
                        datesCnt++;
                        break;
                    case 2:
                        dateChoose3.setVisibility(View.VISIBLE);
                        addDate.setVisibility(View.GONE);
                        datesCnt++;
                        break;
                }
            }
        });

        if (!date1.equals(";"))
            dateChoose1.setText(date1);

        if (!date2.equals(";")) {
            dateChoose2.setVisibility(View.VISIBLE);
            dateChoose2.setText(date2);
            datesCnt++;
        }

        if (!date3.equals(";")) {
            dateChoose3.setVisibility(View.VISIBLE);
            dateChoose3.setText(date3);
            addDate.setVisibility(View.GONE);
            datesCnt++;
        }

        EditText mName = (EditText) findViewById(R.id.name);
        EditText mForeas = (EditText) findViewById(R.id.foreas);
        mAddress = (EditText) findViewById(R.id.address);

        mName.setText(medName);
        mForeas.setText(pharName);

        if (address.equals(";"))
            address = pref.getAddress();
        mAddress.setText(address);

        mName.setKeyListener(null);
        mForeas.setKeyListener(null);

        final Button doneButton = (Button) findViewById(R.id.button1);

        if (showDoneButton) {
            doneButton.setVisibility(View.VISIBLE);

            doneButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (HelperActivity.isOnline(getApplicationContext())) {
                        HelperActivity.showDialogBox(getApplicationContext(), dialog);

                        Date date1 = new Date();
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(date1);
                        todayDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DATE);
                        todayDateAndroid = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + (cal.get(Calendar.DATE) % 100);

                        new HttpDone().execute();
                    } else
                        HelperActivity.httpErrorToast(getApplicationContext(), 1);
                }
            });
        }
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

                date1 = dateChoose1.getText().toString().trim();
                date2 = dateChoose2.getText().toString().trim();
                date3 = dateChoose3.getText().toString().trim();

                if (date1.isEmpty() && date2.isEmpty() && date3.isEmpty()) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.choo_one_date), Toast.LENGTH_LONG).show();
                    return true;
                }

                sdate1 = transformDate(date1);
                sdate2 = transformDate(date2);
                sdate3 = transformDate(date3);

                address = mAddress.getText().toString().trim();
                if (address.isEmpty()) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.choo_empty_address), Toast.LENGTH_LONG).show();
                    return true;
                }

                new HttpVolunteer().execute();
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

        String dateString = day + "/" + mon + "/" + (year % 100);
        dateChoosed.setText(dateString);
    }

    public void showDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "pickDate");
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
        if (date.isEmpty())
            return ";";

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
                String urlParameters = "doneUser="+ pref.getMobileNumber() +
                        "&doneDeliveryType=V" +
                        "&doneDate=" + todayDate +
                        "&donePharPhone=" + pharPhone;

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
                    db.progToDoneDonation(barcode, pharName, todayDateAndroid, medName);
                    dialog.dismiss();
                    doneAlert.show();
                    return;
                } else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }

    private class HttpVolunteer extends AsyncTask<Object, Void, Integer> {

        private static final String TAG = "HttpGetTask_Volunteer";
        private int error = -1;
        private int result ;

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
                        "&donatedPhone=" + pharPhone +
                        "&donationAddress=" + address +
                        "&donationType=V";

                if (!sdate1.equals(";")) urlParameters += "&donationDate1=" + sdate1;
                else date1 = ";";

                if (!sdate2.equals(";")) urlParameters += "&donationDate2=" + sdate2;
                else date2 = ";";

                if (!sdate3.equals(";")) urlParameters += "&donationDate3=" + sdate3;
                else date3 = ";";

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
                    pref.setAddress(address);
                    db.updateProgDonation(barcode, pharPhone, date1, date2, date3, "V", address);
                    dialog.dismiss();
                    alert.show();
                    return;
                }
                else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }
}
