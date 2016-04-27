package com.givmed.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

/**
 * Einai h klassh pou kanei extend to AppCompatActivity kai to xrhshmopoioume
 * se ka8e activity ws voithikh gia elaxistopoihsh kwdika
 * ylopoiei epishs to plaino menu kai synarthseis pou xrhshmopoioume
 * se diafora activities.
 */

public class HelperActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener
{
    public String server = "http://www.givmed.com:81";

    public int myMenu;

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

        if (id == R.id.nav_personal_pharmacy && !class_name.equals("Farmakeio")) {
            Intent FIntent = new Intent(getApplicationContext(), Farmakeio.class);
            startActivity(FIntent);
            FIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            FIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            started = true;
        } else if (id == R.id.nav_register_med && !class_name.equals("TwoButtons")) {
            startActivity(new Intent(getApplicationContext(), TwoButtons.class));
            started = true;
        } else if (id == R.id.nav_needs && !class_name.equals("Elleipseis")) {
            startActivity(new Intent(getApplicationContext(), Elleipseis.class));
        } else if (id == R.id.nav_donations && !class_name.equals("Dwrees")) {
            startActivity(new Intent(getApplicationContext(), Dwrees.class));
            started = true;
        } else if (id == R.id.nav_profile && !class_name.equals("Profile")) {
            //startActivity(new Intent(getApplicationContext(), Profile.class));
            //started = true;
        } else if (id == R.id.nav_communication && !class_name.equals("Communication")) {
            startActivity(new Intent(getApplicationContext(), Email.class));
            started = true;
        } else if (id == R.id.nav_share && !class_name.equals("Share")) {
            startActivity(new Intent(getApplicationContext(), Share.class));
            started = true;
        }

        if (started && !class_name.equals("Elleipseis"))
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
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    // synarthsh pou allazei ta xrwmata sta koumpia otan ta patame
    public static void changeButtonsLayout(Button pressed, Button unpressed, int presDraw, int unpresDraw) {
        pressed.setTextColor(Color.WHITE);
        pressed.setBackgroundResource(presDraw);
        unpressed.setTextColor(Color.BLACK);
        unpressed.setBackgroundResource(unpresDraw);
    }

    // Synarthsh pou pairnei mono thn prwth le3h enos string
    public String firstWord(String name) {
        String sclearName = null;

        if (name.contains(" "))
            sclearName = name.substring(0, name.indexOf(" "));
        else
            sclearName = name;

        return sclearName;
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
}
