package com.givmed.android;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Einai h klassh pou kanei extend to AppCompatActivity kai to xrhshmopoioume
 * se ka8e activity ws voithikh gia elaxistopoihsh kwdika
 * ylopoiei epishs to plaino menu kai synarthseis pou xrhshmopoioume
 * se diafora activities.
 */

public class HelperActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    public static String server = "http://www.givmed.com:81";
    public static int timeoutTime = 10000;
    public int myMenu;
    private PrefManager pref;
    private DBHandler db;



    public void setMenu(int myMenu) {
        this.myMenu = myMenu;
    }

    public void helperOnCreate(int activityXML, int title, boolean has_arrow) {
        setContentView(activityXML);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(title);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);

        try {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            Field mDragger = drawer.getClass().getDeclaredField("mLeftDragger"); //mRightDragger for right obviously
            mDragger.setAccessible(true);

            ViewDragHelper draggerObj = (ViewDragHelper) mDragger.get(drawer);

            Field mEdgeSize = draggerObj.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);

            int edge = 0;
            edge = mEdgeSize.getInt(draggerObj);
            mEdgeSize.setInt(draggerObj, edge * 2); //optimal value as for me, you may set any constant in dp
            // You can set it even to the value you want like mEdgeSize.setInt(draggerObj, 150); for 150dp

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, (has_arrow) ? mToolBar : null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.setDrawerIndicatorEnabled(!has_arrow);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // gia na phgainei pisw me to velaki prepei na mpei edw meta to toggle.serDrawerIndicatorEnabled()
        // giati alliws to kanei overide, to parapanw xreiazetai gia na mas deixnei to hamburger icon
        if (has_arrow) {
            mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(this.myMenu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);  // OPEN DRAWER
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        String class_name = this.getClass().getSimpleName();
        boolean started = false;

        // If set, and the activity being launched is already running in the current task, then instead of
        // launching a new instance of that activity, all of the other activities on top of it will be closed and
        // this Intent will be delivered to the (now on top) old activity as a new Intent.
        //showItemIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // If set, the activity will not be launched if it is already running at the top of the history stack.
        //showItemIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (id == R.id.nav_personal_pharmacy && !class_name.equals("Farmakeio")) {
            Intent FIntent = new Intent(getApplicationContext(), Farmakeio.class);
            startActivity(FIntent);
            FIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            FIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            started = true;
        } else if (id == R.id.nav_register_med && !class_name.equals("TwoButtons")) {
            startActivity(new Intent(getApplicationContext(), TwoButtons.class));
        } else if (id == R.id.nav_needs && !class_name.equals("Elleipseis")) {
            startActivity(new Intent(getApplicationContext(), Elleipseis.class));
            started = true;
        } else if (id == R.id.nav_donations && !class_name.equals("Dwrees")) {
            startActivity(new Intent(getApplicationContext(), BlueRedList.class));
            started = true;
        } else if (id == R.id.nav_profile && !class_name.equals("Register")) {
            startActivity(new Intent(getApplicationContext(), Register.class));
            started = true;
        } else if (id == R.id.nav_communication && !class_name.equals("Communication")) {
            startActivity(new Intent(getApplicationContext(), Communication.class));
            started = true;
        } else if (id == R.id.nav_share && !class_name.equals("Share")) {
            startActivity(new Intent(getApplicationContext(), Share.class));
            started = true;
        } else if (id == R.id.nav_settings && !class_name.equals("Settings")) {
            startActivity(new Intent(getApplicationContext(), Settings.class));
            started = true;
        } else if (id == R.id.nav_odhgies && !class_name.equals("Tutorial")) {
            startActivity(new Intent(getApplicationContext(), Tutorial.class));
            started = true;
        } else if (id == R.id.nav_faq && !class_name.equals("Faq")) {
            startActivity(new Intent(getApplicationContext(), Faq.class));
            started = true;
        } else if (id == R.id.nav_oroi_xrhshs && !class_name.equals("OroiXrhshs")) {
            startActivity(new Intent(getApplicationContext(), OroiXrhshs.class));
            started = true;
        }

        if (started && !class_name.equals("TwoButtons"))
            finish();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //Gia na tsekaroume pio grhgora an prepei na paroume ta dedomena apo thn SQLite
    //Alliws argei upervolika
    //Allh enallaktikh:InetAddress.getByName(host).isReachable(timeOut)->den douleuei panta kala
    //Isws timeout sto http connection
    //http://stackoverflow.com/questions/1443166/android-how-to-check-if-the-server-is-available
    public static boolean isOnline(Context context) {
//        ConnectivityManager cm =
//                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();

        // TODO: na tsekaroume thn apo katw synarthsh mipws einai kalyterh
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }

    // synarthsh pou allazei ta xrwmata sta koumpia otan ta patame
    public static void changeButtonsLayout(Button pressed, Button unpressed, int presDraw, int unpresDraw, int presColor, int unpresColor) {
        pressed.setTextColor(presColor);
        pressed.setBackgroundResource(presDraw);
        unpressed.setTextColor(unpresColor);
        unpressed.setBackgroundResource(unpresDraw);
    }

    // Synarthsh pou pairnei mono thn prwth le3h enos string
    public static String firstWord(String name) {
        String sclearName = null;

        if (name.contains(" "))
            sclearName = name.substring(0, name.indexOf(" "));
        else
            sclearName = name;

        return sclearName;
    }

    public static void showDialogBox(Context context, ProgressDialog dialog) {
        dialog.setMessage(context.getString(R.string.loading_msg));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public static void httpErrorToast(Context context, int error) {
        Toast.makeText(context, (error == 1) ? context.getString(R.string.no_internet) : context.getString(R.string.no_connection), Toast.LENGTH_LONG).show();
    }

    public static void enableBroadcastReceiver(Context context){
        ComponentName receiver = new ComponentName(context, SMSReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void disableBroadcastReceiver(Context context){
        ComponentName receiver = new ComponentName(context, SMSReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer("");

        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                data.append(line);
            }
        } catch (IOException e) {
            Log.e("ReadStream", "IOException");
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }

    public  class HttpGetNeedsPharms extends AsyncTask<Object, Void, Integer> {

        private static final String TAG = "HttpGetTask";
        String data;
        private int error = -1;



        @Override
        protected Integer doInBackground(Object... values) {
            db = (DBHandler) values[0];

            pref = (PrefManager) values[1];
            String needDate = pref.getNeedDate().replaceAll("\\s+","");
            String pharmDate = pref.getPharDate().replaceAll("\\s+", "");

            String URL = HelperActivity.server + "/needs-pharm_date/" + pharmDate + "/" + needDate + "/";
            Integer out = 0;
            java.net.URL url = null;
            HttpURLConnection conn = null;

            try {
                url = new URL(URL);
                conn = (HttpURLConnection) url.openConnection();//Obtain a new HttpURLConnection
                conn.setConnectTimeout(HelperActivity.timeoutTime);
                conn.setReadTimeout(HelperActivity.timeoutTime);
                conn.setDoInput(true);
                InputStream in = new BufferedInputStream(conn.getInputStream());//The response body may be read from the stream returned by getInputStream(). If the response has no body, that method returns an empty stream.
                data = HelperActivity.readStream(in);
                out = conn.getResponseCode();

            } catch (ProtocolException e) {
                error = 1;
                Log.e(TAG, "ProtocolException");
            } catch (MalformedURLException exception) {
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                error = 1;
                exception.printStackTrace();
                Log.e(TAG, "IOException");
            } finally {
                if (null != conn)
                    conn.disconnect();
            }

            return out;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0)
                HelperActivity.httpErrorToast(getApplicationContext(), error);
            else {
                if (result == 200) {
                    try {
                        JSONObject obj = new JSONObject(data);
                        JSONArray pharm = obj.getJSONArray("pharm");
                        JSONArray need = obj.getJSONArray("need");
                        pref.setNeedDate(obj.getString("needDate"));
                        pref.setNeedDate(obj.getString("pharmDate"));

                        db.deleteNeeds();

                        for (int i = 0; i < need.length(); i++) {
                            JSONObject json = need.getJSONObject(i);
                            Need needo = new Need();
                            needo.setNeedName(json.getString("needMedName"));
                            needo.setPhone(json.getString("needPhone"));
                            db.addNeed(needo);
                        }

                        db.deletePharmacies();

                        for (int i = 0; i < pharm.length(); i++) {
                            JSONObject json = pharm.getJSONObject(i);
                            db.addPharmacy(json.getString("pharmacyPhone"), json.getString("pharmacyAddress"),
                                    json.getString("openTime"), json.getString("pharmacyName"), json.getString("pharmacyNameGen"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
