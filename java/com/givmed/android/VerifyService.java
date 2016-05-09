package com.givmed.android;

/**
 * Created by agroikos on 29/12/2015.
 */

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

/**
 * Created by Ravi on 04/04/15.
 */
public class VerifyService extends IntentService {

    private static String TAG = VerifyService.class.getSimpleName();
    private PrefManager pref;

    public VerifyService() {
        super(VerifyService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            String old_user = intent.getStringExtra("oldUser");


            verifyOtp(otp,old_user);
        }
    }

    /**
     * Posting the OTP to server and activating the user
     *
     * @param otp otp received in the SMS
     */
    private void verifyOtp(final String otp,final String oldUser) {

        String URL = "http://givmed.com:81/verify/";
        String data = "";
        int code;

        String request = URL;
        java.net.URL url ;
        HttpURLConnection conn = null;
        pref = new PrefManager(this);

        try {
            url = new URL(request);
            String urlParameters = "otp="+ otp +
                                   "&phone=" + pref.getMobileNumber() +
                                    "&oldUser=" + oldUser;

            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            data = HelperActivity.readStream(in);
            code = conn.getResponseCode();

            if (code==202 || code==201) {
                PrefManager pref = new PrefManager(getApplicationContext());
                pref.createLogin();
                Intent TokenIntent = new Intent("token");
                TokenIntent.putExtra("token",otp);
                if (code==201) {
                    JSONObject obj = new JSONObject(data);
                    pref.setUsername(obj.getString("username"));
                    //TokenIntent.putExtra("username",obj.getString("username"));


                }


                LocalBroadcastManager.getInstance(this).sendBroadcast(TokenIntent);

            }

            else{
                //deikse ston xrhsth oti ta mageirepsame kai dn vghke
                //ara prepei na to valei monos tou
            }


            Log.e("django response", ""+code);


        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException");
        } catch (MalformedURLException exception) {
            Log.e(TAG, "MalformedURLException");
        } catch (IOException exception) {
            Log.e(TAG, "IOException");

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (null != conn)
                conn.disconnect();
        }
    }




}