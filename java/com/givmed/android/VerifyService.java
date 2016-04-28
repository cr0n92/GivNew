package com.givmed.android;

/**
 * Created by agroikos on 29/12/2015.
 */
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

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

            verifyOtp(otp);
        }
    }

    /**
     * Posting the OTP to server and activating the user
     *
     * @param otp otp received in the SMS
     */
    private void verifyOtp(final String otp) {

        String URL = "https://givmed.com:444/verify/";
        String data = "";
        int code;

        String request = URL;
        java.net.URL url ;
        HttpURLConnection conn = null;
        pref = new PrefManager(this);

        try {
            url = new URL(request);
            String urlParameters = "otp="+ otp +
                                   "&phone=" + pref.getMobileNumber();


            byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
            int postDataLength = postData.length;

            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            InputStream in = new BufferedInputStream(conn.getInputStream());
            data = HelperActivity.readStream(in);
            code = conn.getResponseCode();

            if (code==202) {
                PrefManager pref = new PrefManager(getApplicationContext());
                pref.createLogin();
                Intent TokenIntent = new Intent("token");
                TokenIntent.putExtra("token",otp);

                LocalBroadcastManager.getInstance(this).sendBroadcast(TokenIntent);
//                Intent i = new Intent();
//                i.setClass(this, Elleipseis.class);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(i);
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
        } finally {
            if (null != conn)
                conn.disconnect();
        }
    }




}