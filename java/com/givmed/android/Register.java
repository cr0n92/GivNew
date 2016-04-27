package com.givmed.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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


public class Register extends AppCompatActivity {
    private EditText mDate, mPhone, mEmail, mAddress, mUsername;
    private TextInputLayout nameLayout, emailLayout, addressLayout, phoneLayout, dateLayout;
    private RadioGroup mSexGroup;
    private RadioButton mSexButton;
    private PrefManager pref;

    ProgressDialog dialog;

    String username = "", phone = "", password = "", email = "", age = "", sex = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.register);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);

        mUsername = (EditText) findViewById(R.id.username_text);
        mPhone = (EditText) findViewById(R.id.phone_text);
        mEmail = (EditText) findViewById(R.id.email_text);
        mAddress = (EditText) findViewById(R.id.pickup_addr_text);
        mDate = (EditText) findViewById(R.id.birth_date_text);
        mSexButton = (RadioButton) findViewById(R.id.female);
        mSexGroup = (RadioGroup) findViewById(R.id.sexGroup);

        nameLayout = (TextInputLayout) findViewById(R.id.username);
        phoneLayout = (TextInputLayout) findViewById(R.id.phone);
        emailLayout = (TextInputLayout) findViewById(R.id.email);
        addressLayout = (TextInputLayout) findViewById(R.id.pickup_addr);
        dateLayout = (TextInputLayout) findViewById(R.id.birth_date);

        mUsername.addTextChangedListener(new MyTextWatcher(mUsername));
        mEmail.addTextChangedListener(new MyTextWatcher(mEmail));
        mAddress.addTextChangedListener(new MyTextWatcher(mAddress));

        pref = new PrefManager(this);

        // Checking for user session
        // if user is already logged in, take him to elleipseis
        if (pref.isLoggedIn()) {
  //          pref.clearSession();
            Intent intent = new Intent(Register.this, Elleipseis.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {
            if (submitForm()) {
                username = mUsername.getText().toString();
                password = mAddress.getText().toString();
                phone = mPhone.getText().toString();
                email = mEmail.getText().toString();
                age = mDate.getText().toString();
                sex = getSex();

                dialog = new ProgressDialog(this);
                dialog.setMessage("Loading, Please Wait...");
                dialog.show();
                new HttpGetTask().execute();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private String getSex() {

        switch (mSexGroup.getCheckedRadioButtonId()) {
            case R.id.female:
                return "F";
            default:
                return "M";
        }
    }

    private class HttpGetTask extends AsyncTask<Void, Void, JSONObject> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;

        @Override
        protected JSONObject doInBackground(Void... arg0) {
            String URL = "http://147.102.236.84:8080/reg/";
            String data = "";
            JSONObject out = new JSONObject();

            String request = URL;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(request);
                String urlParameters =
                        "username="     + username +
                        "&password="    + password +
                        "&userPhone="   + phone +
                        "&userEmail="   + email +
                        "&birthDate="   + age +
                        "&sex="         + sex;

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

    private boolean submitForm() {
        if (!validateName())
            return false;

        if (!validateEmail())
            return false;

        if (!validatePassword())
            return false;

        return true;
    }

    private boolean validateName() {
        if (mUsername.getText().toString().trim().isEmpty()) {
            nameLayout.setError(getString(R.string.err_msg_name));
            requestFocus(mUsername);
            return false;
        } else {
            nameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = mEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            emailLayout.setError(getString(R.string.err_msg_email));
            requestFocus(mEmail);
            return false;
        } else {
            emailLayout.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validatePassword() {
        if (mAddress.getText().toString().trim().isEmpty()) {
            addressLayout.setError(getString(R.string.err_msg_password));
            requestFocus(mAddress);
            return false;
        } else {
            addressLayout.setErrorEnabled(false);
        }

        return true;
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
                case R.id.pickup_addr_text:
                    validatePassword();
                    break;
            }
        }
    }
}