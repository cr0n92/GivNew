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
import android.widget.Button;
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


public class Register extends HelperActivity {
    private EditText mDate, mEmail, mUsername, mSex, male, female;
    private TextInputLayout nameLayout, emailLayout, dateLayout;
    private PrefManager pref;
    ProgressDialog dialog;

    String username = "", email = "", date = "", sex = "", phone = "";
    Boolean is_male = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main);
        super.helperOnCreate(R.layout.register, R.string.register, false);

        mUsername = (EditText) findViewById(R.id.username_text);
        mEmail = (EditText) findViewById(R.id.email_text);
        mDate = (EditText) findViewById(R.id.birth_text);

        mSex = (EditText) findViewById(R.id.sex_text);
        female = (EditText) findViewById(R.id.femaleButton);
        male = (EditText) findViewById(R.id.maleButton);

        // me auth thn synarthsh den kalleite to plhktrologio otan pataei panw sto edit text
        mSex.setKeyListener(null);
        female.setKeyListener(null);
        male.setKeyListener(null);

        // prepei sto editText na valoume android:focusable="false" giati alliws den douleuei
        // me to prwto click alla me to deutero kai einai ligo asxhmo (coockings)
        female.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                is_male = false;
                female.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female_pressed, 0, 0, 0);
                male.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_male, 0, 0, 0);
            }
        });

        male.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                is_male = true;
                male.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_male_pressed, 0, 0, 0);
                female.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female, 0, 0, 0);
            }
        });

        nameLayout = (TextInputLayout) findViewById(R.id.username);
        emailLayout = (TextInputLayout) findViewById(R.id.email);
        dateLayout = (TextInputLayout) findViewById(R.id.birth);

        mEmail.addTextChangedListener(new MyTextWatcher(mEmail));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {
            if (submitForm()) {
                username = mUsername.getText().toString();
                email = mEmail.getText().toString();
                sex = (is_male) ? "M" : "F";
                date = mDate.getText().toString();

                dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.loading_msg));
                dialog.show();
                new HttpGetTask().execute();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpGetTask extends AsyncTask<Void, Void, JSONObject> {

        private static final String TAG = "HttpGetTask";
        private int error = -1;

        @Override
        protected JSONObject doInBackground(Void... arg0) {
            String URL = server + "/reg/";
            String data = "";
            JSONObject out = new JSONObject();

            String request = URL;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(request);
                String urlParameters =
                        "username="     + username +
                        "&userPhone="   + phone +
                        "&userEmail="   + email +
                        "&birthDate="   + date +
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

        return true;
    }

    private boolean validateName() {
        if (mUsername.getText().toString().trim().isEmpty()) {
            //nameLayout.setError(getString(R.string.err_msg_name));
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
           // emailLayout.setError(getString(R.string.err_msg_email));
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