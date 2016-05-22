package com.givmed.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
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
import java.util.Calendar;

import io.fabric.sdk.android.Fabric;

public class Register extends HelperActivity {
    private EditText mDate, mEmail, mUsername;
    private ImageView male, female;
    private TextInputLayout nameLayout, emailLayout;
    private PrefManager pref;
    ProgressDialog dialog;
    DBHandler db;
    AlertDialog alert,alert1,alert2;
    String username = "", email = "", date = "", sex = "", phone = "", mDate1="";
    Boolean is_male = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Fabric.with(this, new Crashlytics());
        super.setMenu(R.menu.menu_main);
        super.helperOnCreate(R.layout.register, R.string.profile, false);

        db = new DBHandler(getApplicationContext());




        dialog = new ProgressDialog(this);
        pref = new PrefManager(this);



        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.prof_age_warning))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alert = builder.create();

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(getString(R.string.prof_get_old_data))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new HttpGetData().execute();

                    }
                });
        alert1 = builder1.create();

        AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
        builder2.setMessage(getString(R.string.prof_get_old_data_no_internet))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        System.exit(0);
                    }
                });
        alert2 = builder2.create();

        mUsername = (EditText) findViewById(R.id.username_text);
        mEmail = (EditText) findViewById(R.id.email_text);
        mDate = (EditText) findViewById(R.id.birth_text);

        female = (ImageView) findViewById(R.id.femaleButton);
        male = (ImageView) findViewById(R.id.maleButton);

        // prepei sto editText na valoume android:focusable="false" giati alliws den douleuei
        // me to prwto click alla me to deutero kai einai ligo asxhmo (coockings)
        female.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                is_male = false;
                female.setImageResource(R.drawable.ic_female_pressed);
                male.setImageResource(R.drawable.ic_male);
            }
        });

        male.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                is_male = true;
                male.setImageResource(R.drawable.ic_male_pressed);
                female.setImageResource(R.drawable.ic_female);
            }
        });

        //an einai palios xrhsths kai mpainoume prwth fora sto profil
        //if (pref.getOldUser() && pref.getNextSplash().equals("Register")) {
            if (isOnline(getApplicationContext()))
                alert1.show();
            else {
                alert2.show();

            }

       // }

        phone = pref.getMobileNumber();
        username = pref.getUsername();
        email = pref.getEmail();
        date = pref.getBirthDate();
        sex = pref.getSex();

        if (!(username.isEmpty() || username.equals(phone))) mUsername.setText(username);

        if (!date.isEmpty()) mDate.setText(date);

        if (!email.isEmpty()) mEmail.setText(email);

        if (sex.equals("M")) {
            is_male = true;
            male.setImageResource(R.drawable.ic_male_pressed);
            female.setImageResource(R.drawable.ic_female);
        }
        else {
            is_male = false;
            female.setImageResource(R.drawable.ic_female_pressed);
            male.setImageResource(R.drawable.ic_male);
        }

        nameLayout = (TextInputLayout) findViewById(R.id.username);
        emailLayout = (TextInputLayout) findViewById(R.id.email);

        mEmail.addTextChangedListener(new MyTextWatcher(mEmail));
        mUsername.addTextChangedListener(new MyTextWatcher(mUsername));
    }

    private String dateWithSlash(String date,boolean isMed) {
        if (date.equals("null"))
            return ";";

        String[] help = date.split("-");

        if (isMed) {
            return help[1] + "/" + help[0];
        }
        else {
            return help[2] + "/" + help[1] + "/" + help[0].charAt(2) + "" + help[0].charAt(3);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {
                if (allIsEmpty()) {
                    if (pref.getNextSplash().equals("Register"))
                        pref.setNextSplash("TwoButtons");

                    Intent intent = new Intent(getApplicationContext(), TwoButtons.class);
                    startActivity(intent);
                    finish();
                    return true;
                }

                if (submitForm()) {
                    if (isOnline(getApplicationContext())) {
                        showDialogBox(getApplicationContext(), dialog);

                        username = mUsername.getText().toString();
                        email = mEmail.getText().toString();
                        sex = (is_male) ? "M" : "F";
                        mDate1 = mDate.getText().toString().trim();
                        int year = Calendar.getInstance().get(Calendar.YEAR);

                        if (!(mDate1.equals(""))) {
                            if (year - Integer.parseInt(mDate1) < 18) {
                                dialog.dismiss();
                                alert.show();
                                return super.onOptionsItemSelected(item);
                            }

                            date = mDate.getText().toString().trim() + "-1-1";
                        }
                        if (username.equals(""))
                            username = phone;

                        new HttpGetTask().execute();
                    }
                    else
                        httpErrorToast(getApplicationContext(), 1);
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpGetTask extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;

        @Override
        protected Integer doInBackground(Void... arg0) {
            String URL = server + "/profile/";
            String data = "";
            Integer out = 0;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(URL);
                String urlParameters =
                        "userPhone=" + phone +
                        "&sex=" + sex;

                if (!(username == null || username.trim().equals("")))
                    urlParameters += "&username=" + username;

                if (!(email == null || email.trim().equals("")))
                    urlParameters += "&email=" + email;

                if (!(date == null || date.trim().equals("")))
                    urlParameters += "&birthDate=" + date;

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("PUT");
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in = new BufferedInputStream(conn.getInputStream());
                data = readStream(in);
                out = conn.getResponseCode();

            } catch (ProtocolException e) {
                error = 1;
                Crashlytics.logException(e);
                Log.e(TAG, "ProtocolException");
            } catch (MalformedURLException e) {
                error = 1;
                Crashlytics.logException(e);
                Log.e(TAG, "MalformedURLException");
            } catch (IOException e) {
                error = 1;
                Crashlytics.logException(e);
                Log.e(TAG, "IOException"+e);
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return out;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0)
                httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 202) {
                    nameLayout.setError(getString(R.string.prof_name_exists));
                    requestFocus(mUsername);
                }
                else {
                    pref.setUsername(username);
                    pref.setBirthDate(date);
                    pref.setSex(sex);
                    pref.setEmail(email);

                    if (pref.getNextSplash().equals("Register"))
                        pref.setNextSplash("TwoButtons");

                    Intent intent = new Intent(getApplicationContext(), TwoButtons.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
            dialog.dismiss();
        }
    }

    private class HttpGetData extends AsyncTask<Void, Void, Integer> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;
        String data ;


        @Override
        protected Integer doInBackground(Void... arg0) {
            //String URL = server + "/data/" + pref.getMobileNumber() + "/";
            String URL = server + "/data/6946229038/";
            Integer out = 0;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(URL);

                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setRequestProperty("Connection", "keep-alive");

                InputStream in = new BufferedInputStream(conn.getInputStream());
                data = readStream(in);
                out = conn.getResponseCode();

            } catch (ProtocolException e) {
                error = 1;
                Crashlytics.logException(e);
                Log.e(TAG, "ProtocolException");
            } catch (MalformedURLException e) {
                error = 1;
                Crashlytics.logException(e);
                Log.e(TAG, "MalformedURLException");
            } catch (IOException e) {
                error = 1;
                Crashlytics.logException(e);
                Log.e(TAG, "IOException"+e);
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return out;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0) {
                httpErrorToast(getApplicationContext(), error);
            } else {
                try {

                    JSONObject obj = new JSONObject(data);
                    JSONArray meds = obj.getJSONArray("meds");
                    JSONArray done_donations = obj.getJSONArray("done_donations");
                    JSONArray donations = obj.getJSONArray("donations");

                    for(int i = 0; i < meds.length(); i++)
                    {

                        JSONObject object = meds.getJSONObject(i);
                        String what1,what2;

                        what1 = object.getString("medSubstance");

                        what2 = object.getString("medCategory");



                        db.addMed(new Medicine(object.getString("barcode"), object.getString("eofcode"), object.getString("medName"),
                               dateWithSlash(object.getString("expirationDate"), true), object.getString("medPrice"), object.getString("notes"),
                                object.getString("state"),what1,what2,
                                object.getString("forDonation")),firstWord(object.getString("medName")));


                    }

                    for(int i = 0; i < done_donations.length(); i++)
                    {

                        JSONObject object = done_donations.getJSONObject(i);
                        db.addDoneDonation(object.getString("donePrice"), firstWord(object.getString("doneName")),
                                object.getString("pharmacyName"), dateWithSlash(object.getString("doneDate"),false));
                    }

                    for(int i = 0; i < donations.length(); i++)
                    {

                        JSONObject object = donations.getJSONObject(i);
                        db.addDonation(object.getString("donationBarcode"), object.getString("donatedPhone"),
                                dateWithSlash(object.getString("deliveryDate1"),false),dateWithSlash(object.getString("deliveryDate2"),false),
                                dateWithSlash(object.getString("deliveryDate3"),false), object.getString("deliveryType"),
                                object.getString("donationAddress"));
                    }




                } catch (JSONException e) {

                    Crashlytics.logException(e);
                    e.printStackTrace();
                    dialog.dismiss();
                    httpErrorToast(getApplicationContext(), 2);
                    return;
                }



            }
            dialog.dismiss();
        }
    }

    private boolean submitForm() {
        if (!validateName())
            return false;

        if (!validateEmail())
            return false;

        return true;
    }



    private boolean allIsEmpty() {
        if (mUsername.getText().toString().trim().isEmpty() && mEmail.getText().toString().trim().isEmpty() &&
                mDate.getText().toString().trim().isEmpty())
			return true;
        else
           return false;


    }

    private boolean validateName() {
        if (mUsername.getText().toString().trim().isEmpty()) {
            nameLayout.setError(getString(R.string.prof_name_warning));
            requestFocus(mUsername);
            return false;
        } else {
            nameLayout.setError(null);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = mEmail.getText().toString().trim();

        if (!isValidEmail(email)) {
            emailLayout.setError(getString(R.string.prof_email_warning));
            requestFocus(mEmail);
            return false;
        } else {
            emailLayout.setError(null);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        if (email.isEmpty() ) return true;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.username_text:
                    validateName();
                    break;
                case R.id.email_text:
                    validateEmail();
                    break;
            }
        }
    }
}