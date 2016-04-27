package com.givmed.android;

/**
 * Created by agroikos on 22/11/2015.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Inputter extends HelperActivity {
    private EditText mEditText;
    private String date, eof, name, code;
    ProgressDialog dialog;
    AlertDialog alert;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main);
        super.helperOnCreate(R.layout.input_byhand, R.string.inputter, true);

        mEditText = (EditText) findViewById(R.id.edit1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Το φάρμακο έχει λήξει!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent TedxIntent = new Intent(getApplicationContext(), Farmakeio.class);
                        TedxIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        TedxIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(TedxIntent);

                    }
                });
        alert = builder.create();

        Intent intent = getIntent();
        if (intent.hasExtra("barcode")) {
            mEditText.setText(intent.getStringExtra("barcode"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {
            code = mEditText.getText().toString();

            if (code.length() != 12) {
                Toast.makeText(getApplicationContext(), "Το μήκος του barcode δεν είναι έγκυρο",
                        Toast.LENGTH_LONG).show();
            }
            else {
                mEditText.setError(null);
                dialog = new ProgressDialog(this);
                dialog.setMessage("Loading, Please Wait...");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                if (isOnline())
                    new HttpGetTask().execute(code);
                else
                    Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class HttpGetTask extends AsyncTask<String, Void, String[]> {

        private static final String TAG = "HttpGetTask_Inputer";
        private int error = -1;

        @Override
        protected String[] doInBackground(String... input) {
            String URL = "http://services.eof.gr/labelsearch/";
            String[] results = new String[3];

            Pattern jsess_pattern = Pattern.compile("jsessionid=([0-9a-z]*)/?");
            Pattern state_pattern = Pattern.compile("javax.faces.ViewState\" value=\"([0-9-:]*)/?");
            Matcher jsessid, viewState;

            String request = URL;
            java.net.URL url = null;
            HttpURLConnection conn = null, conn1 = null;
            String value = input[0], data = "", data2 = "", state = "", cookie = "";

            try {
                url = new URL(request);

                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setDoInput(true);
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("Host", "services.eof.gr");
                conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                conn.setRequestProperty("DNT", "1");
                conn.setRequestProperty("Connection", "keep-alive");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("charset", "utf-8");
                conn.setUseCaches(false);

                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                data = readStream(in);

                jsessid = jsess_pattern.matcher(data);
                if (!jsessid.find()) throw new Exception("wrong barcode");
                cookie = jsessid.group(1);

                viewState = state_pattern.matcher(data);
                if (!viewState.find()) throw new Exception("wrong barcode");
                state = viewState.group(1).replaceAll(":", "%3A");

                String urlParameters =
                        "javax.faces.partial.ajax=true&javax.faces.source=dlSearch%3Aj_idt8%3Aj_idt10&" +
                                "javax.faces.partial.execute=%40all&javax.faces.partial.render=dlSearch&" +
                                "dlSearch%3Aj_idt8%3Aj_idt10=dlSearch%3Aj_idt8%3Aj_idt10&dlSearch%3Aj_idt8=dlSearch%3Aj_idt8&" +
                                "dlSearch%3Aj_idt8%3AtxtLbarcode_input=" + value + "&javax.faces.ViewState=" + state +
                                "&javax.faces.RenderKitId=PRIMEFACES_MOBILE";

//                String urlParameters =
//                        "javax.faces.partial.ajax=true&javax.faces.source=dlSearch%3Aj_idt8%3Aj_idt10&" +
//                        "javax.faces.partial.execute=%40all&javax.faces.partial.render=dlSearch&" +
//                        "dlSearch%3Aj_idt10%3Aj_idt14=dlSearch%3Aj_idt10%3Aj_idt14&dlSearch%3Aj_idt10=dlSearch%3Aj_idt10&" +
//                        "dlSearch%3Aj_idt10%3AtxtLbarcode_input=" + value + "&javax.faces.ViewState=" + state +
//                        "&javax.faces.RenderKitId=PRIMEFACES_MOBILE";

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                int postDataLength = postData.length;

                conn1 = (HttpURLConnection) url.openConnection();
                conn1.setDoOutput(true);
                conn1.setRequestProperty("Faces-Request", "partial/ajax");
                conn1.setRequestProperty("charset", "utf-8");
                conn1.setRequestProperty("X-Requested-With", "XMLHttpRequest");
                conn1.setRequestProperty("Connection", "keep-alive");
                conn1.setRequestProperty("Content-Length", Integer.toString(postDataLength));
                conn1.setRequestProperty("Cookie", "JSESSIONID=" + cookie);
                conn1.setUseCaches(false);

                DataOutputStream wr = new DataOutputStream(conn1.getOutputStream());//Transmit data by writing to the stream returned by getOutputStream().
                wr.write(postData);
                InputStream in2 = new BufferedInputStream(conn1.getInputStream());
                data2 = readStream(in2);

                Pattern brand_pattern = Pattern.compile("<span title=\"Περιγραφή συσκ.\">Περιγραφή συσκ.</span></td><td role=\"gridcell\"><span title=\"Περιγραφή συσκ.\">([^<]*)");
                Pattern date_pattern = Pattern.compile("<span title=\"Ημ/νία λήξης\">Ημ/νία λήξης</span></td><td role=\"gridcell\"><span title=\"Ημ/νία λήξης\">([^<]*)");
                Pattern eof_pattern = Pattern.compile("<span title=\"Barcode συσκ.\">Barcode συσκ.</span></td><td role=\"gridcell\"><span title=\"Barcode συσκ.\">([^<]*)");
                Matcher brand, date, eof;

                date = date_pattern.matcher(data2);
                if (!date.find()) throw new Exception("wrong barcode");
                results[0] = date.group(1);

                brand = brand_pattern.matcher(data2);
                if (!brand.find()) throw new Exception("wrong barcode");
                results[1] = brand.group(1);

                eof = eof_pattern.matcher(data2);
                if (!eof.find()) throw new Exception("wrong barcode");
                results[2] = eof.group(1);

            } catch (ProtocolException e) {
                error = 1;
                Log.e(TAG, "ProtocolException");
            } catch (MalformedURLException exception) {
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                error = 1;
                Log.e(TAG, "IOException");
            } catch (Exception exp) {
                error = 2;
                Log.e(TAG, "Wrong barcode");
            } finally {
                if (null != conn)
                    conn.disconnect();

                if (null != conn1)
                    conn1.disconnect();
            }

            return results;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (error > 0) {
                Toast.makeText(getApplicationContext(), (error == 1)? "No internet connection" : "Wrong barcode number",
                        Toast.LENGTH_LONG).show();
            }
            else {
                date = result[0];
                name = result[1];
                eof = result[2];

                Date date1 = new Date(); // your date
                Calendar cal = Calendar.getInstance();
                cal.setTime(date1);
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH) + 1;

                String month1 = date.substring(0, 2);
                String year1 = date.substring(5, 9);
                date = year1 + "-" + month1 + "-01";

                int month2 = Integer.parseInt(month1);
                int year2 = Integer.parseInt(year1);

                if (year2 * 12 + month2 < year * 12 + month) {
                    dialog.dismiss();
                    dialog = null;
                    alert.show();
                    return;
                }

                Intent showItemIntent = new Intent(getApplicationContext(), Outputer.class);
                showItemIntent.putExtra("name", name);
                showItemIntent.putExtra("server_date", date);
                showItemIntent.putExtra("date", month2 + "/" + year2);
                showItemIntent.putExtra("barcode", code);
                showItemIntent.putExtra("eofcode", eof);
                startActivity(showItemIntent);

            }

            dialog.dismiss();
            dialog = null;
        }
    }
}