package com.givmed.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import java.util.ArrayList;

public class BlueRedList extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "BlueRedList";
    ProgressDialog dialog,dialog1;
    public static BlueRedAdapter mAdapter;
    public static boolean inRed = false;
    private int matchedMeds;
    private TextView firstMes, secondMes;
    private ImageView image;
    private String amesa, prin;
    private String[][] dataArray;
    private SpannableStringBuilder redBuilder, blueBuilder;
    AlertDialog.Builder builder;
    DBHandler db;
    PrefManager pref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blue_red_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.two_buttons);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = new DBHandler(getApplicationContext());
        pref  = new PrefManager(this);
        builder = new AlertDialog.Builder(this);

        firstMes = (TextView) findViewById(R.id.firstMes);
        secondMes = (TextView) findViewById(R.id.texto);
        image = (ImageView) findViewById(R.id.imago);

        amesa = getString(R.string.br_now);
        prin = getString(R.string.br_exp);

        mAdapter = new BlueRedAdapter(getApplicationContext());
        ListView list = (ListView) findViewById(R.id.list);
        list.setOnItemClickListener(this);
        list.setFooterDividersEnabled(true);
        list.setAdapter(mAdapter);

        // gia na kanoume tis le3eis kokkino kai mple me to idio xrwma me ta onomata tous
        redBuilder = new SpannableStringBuilder();
        blueBuilder = new SpannableStringBuilder();

        String red = getString(R.string.br_first_msg_red);
        SpannableString redSpannable= new SpannableString(red);
        redSpannable.setSpan(new ForegroundColorSpan(Color.RED), red.length()-8, red.length()-1, 0);
        redBuilder.append(redSpannable);

        String blue = getString(R.string.br_first_msg_blue);
        SpannableString blueSpannable = new SpannableString(blue);
        blueSpannable.setSpan(new ForegroundColorSpan(Color.BLUE), blue.length()-5, blue.length()-1, 0);
        blueBuilder.append(blueSpannable);

        if(savedInstanceState == null || !savedInstanceState.containsKey("blueRedList")) {
            inRed = false;
            firstMes.setText(blueBuilder, TextView.BufferType.SPANNABLE);
            secondMes.setText(amesa);
            image.setImageResource(R.drawable.ic_tick_in_circle_blue);

            mAdapter.clear();
            boolean hasUnknown = db.getUnknownMedsToAdapter(mAdapter);
            if (!hasUnknown) {
                Intent toDonations = new Intent(getApplicationContext(), Dwrees.class);
                startActivity(toDonations);
                finish();
            }
        }
        else {
            inRed = savedInstanceState.getBoolean("inRed");
            if (inRed) {
                firstMes.setText(redBuilder, TextView.BufferType.SPANNABLE);
                secondMes.setText(prin);
                image.setImageResource(R.drawable.ic_tick_in_circle_red);
            }
            else {
                firstMes.setText(blueBuilder, TextView.BufferType.SPANNABLE);
                secondMes.setText(amesa);
                image.setImageResource(R.drawable.ic_tick_in_circle_red);
            }

            mAdapter.mItems = savedInstanceState.getParcelableArrayList("blueRedList");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        BlueRedItem item = (BlueRedItem) mAdapter.getItem(position);
        final ImageView statusView = (ImageView) v.findViewById(R.id.statusView);
        statusView.setImageResource(item.nextStatus());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {
            if (inRed) {
                dialog1 = new ProgressDialog(this);
                dialog1.setMessage(getString(R.string.update));
                dialog1.setCancelable(false);
                dialog1.setCanceledOnTouchOutside(false);
                dialog1.show();

                HelperActivity.HttpGetNeedsPharms service = new HelperActivity.HttpGetNeedsPharms(this, db, pref) {
                    @Override
                    public void onResponseReceived(Object result) {
                        dialog1.dismiss();

                        int error = (int) result;
                        if (error > 0) HelperActivity.httpErrorToast(getApplicationContext(), error);
                        else matching();
                    }
                };
                service.execute();
            } else {
                inRed = true;
                firstMes.setText(redBuilder, TextView.BufferType.SPANNABLE);
                secondMes.setText(prin);
                image.setImageResource(R.drawable.ic_tick_in_circle_red);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (inRed) {
            inRed = false;
            firstMes.setText(blueBuilder, TextView.BufferType.SPANNABLE);
            secondMes.setText(amesa);
            image.setImageResource(R.drawable.ic_tick_in_circle_blue);
            mAdapter.notifyDataSetChanged();
        } else
            super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putParcelableArrayList("blueRedList", mAdapter.mItems);
        savedInstanceState.putBoolean("inRed", inRed);
    }

    public void matching() {
        matchedMeds = 0;

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.matching));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        String data = "", barcode, forDonation, pharPhone = "";
        int cnt = 0;
        dataArray = new String[mAdapter.getCount()][2];

        for (BlueRedItem item : mAdapter.mItems) {
            barcode = item.getBarcode();
            forDonation = (item.getSirup()) ? "S" : "";

            if (item.getStatus() == R.drawable.ic_tick_in_circle_blue) {
                //Log.e("WW","TF");
                Object[] info = db.matchExists(item.getName());
                int ret = (int) info[0];
                pharPhone = (String) info[1];

                if (ret == -1) { //kanena match
                    pharPhone = "-";
                }else if (ret == 2) { //>1 match
                    matchedMeds++;
                    pharPhone = " ";
                } else if (ret == 1) { //1 match
                    matchedMeds++;
                }

                forDonation += "Y";
            }
            else {
                forDonation += (item.getStatus() == R.drawable.ic_tick_in_circle_red) ? "B" : "N";
            }

            // pharphone="-":kanena match
            //           " " >1 match
            //           "phone" 1 match
            dataArray[cnt][0] = pharPhone;
            dataArray[cnt][1] = forDonation;

            data += "med" + cnt++ + "={'barcode':'" + barcode + "', 'forDonation':'" + forDonation
                    + "', 'donationBarcode':'" + barcode + "', 'donatedPhone':'" + pharPhone + "'}&";
        }

        data = data.substring(0, data.length() - 1);
        new HttpBlueRedTask().execute(data);
    }

    private class HttpBlueRedTask extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "HttpGetTask_BlueRed";
        private int error = -1;
        private int result;

        @Override
        protected Integer doInBackground(String... input) {
            String data = "";
            java.net.URL url = null;
            HttpURLConnection conn = null;
            String URL = HelperActivity.server + "/br_list/";

            try {
                url = new URL(URL);
                String urlParameters = input[0];

                byte[] postData = urlParameters.getBytes(Charset.forName("UTF-8"));
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
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

                    //Kanoume subscribe sta topics gia kathe onoma farmakou sthn mple lista
                    //Kanoume subscribe mono gia ayta pou den exoune ginei match
                    ArrayList<String> topics = new ArrayList<String>();
                    String barcode, name;

                    for (int i = 0; i < dataArray.length; i++) {
                        barcode = ((BlueRedItem) mAdapter.getItem(i)).getBarcode();
                        name = ((BlueRedItem) mAdapter.getItem(i)).getName();

                        switch (dataArray[i][0]) {
                            //kammia elleipsh
                            case "-":

                                if (db.checkMedSubscribe(name, true)) topics.add(name);
                                break;

                            //den to exei epileksei mple
                            case "":
                               //Log.e("Den eimai mple","mple mwre");
                               break;
                            //uparxoun ellepseis
                            default:
                                //Log.e("Vazw donations to",""+barcode);
                                if (dataArray[i][1].equals("Y") || dataArray[i][1].equals("SY"))
                                    db.addDonation(barcode, dataArray[i][0], ";", ";", ";", "A", ";");
                                break;
                        }
                        db.updateMedForDonation(barcode, dataArray[i][1]);
                    }

                    if (!topics.isEmpty()) {

                        Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
                        serviceIntent.putExtra("subscribe", true);
                        serviceIntent.putStringArrayListExtra("topic", topics);
                        startService(serviceIntent);
                    }

                    dialog.dismiss();

                    if (matchedMeds > 0) {
                        builder.setMessage(getString(R.string.after_matching_left) + " " + matchedMeds + " " + getString(R.string.after_matching_right))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog2, int id) {
                                        Intent toDonations = new Intent(getApplicationContext(), Dwrees.class);
                                        startActivity(toDonations);
                                        finish();
                                    }
                                });
                        AlertDialog matchAlert = builder.create();
                        matchAlert.show();
                    }
                    else {
                        builder.setMessage(getString(R.string.after_no_matching))
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog2, int id) {
                                        Intent toFarmakeio = new Intent(getApplicationContext(), Farmakeio.class);
                                        startActivity(toFarmakeio);
                                        finish();
                                    }
                                });
                        AlertDialog matchAlert = builder.create();
                        matchAlert.show();
                    }

                    return;
                } else
                    HelperActivity.httpErrorToast(getApplicationContext(), 2);
            }
            dialog.dismiss();
        }
    }
}