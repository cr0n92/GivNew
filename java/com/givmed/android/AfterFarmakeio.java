package com.givmed.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.givmed.android.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AfterFarmakeio extends HelperActivity {

    public static MedicineAdapter mAdapter;
    public static Medicine delMed;
    public static String name;
    DBHandler db;

    // IDs for menu items
    private static final int MENU_DELETE = Menu.FIRST;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.pharmacy, R.string.farmakeio, true);

        db = new DBHandler(getApplicationContext());

        final FloatingActionButton floatingBut = (FloatingActionButton) findViewById(R.id.fab);
        floatingBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), TwoButtons.class));
            }
        });

        mAdapter = new MedicineAdapter(getApplicationContext());
        ListView list = (ListView)findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        registerForContextMenu(list);
        list.setAdapter(mAdapter);

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        db.getAllMedsToAdapter(name, mAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu,View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete medicine");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        } catch (ClassCastException e) {
            Log.e("farmakeio", "bad menuInfo", e);
            return false;
        }

        switch (item.getItemId()) {
            case MENU_DELETE:
                AlertDialog alertDialog = new AlertDialog.Builder(getApplicationContext()).create();
                alertDialog.setTitle("Delete entry");
                alertDialog.setMessage("Are you sure you want to delete this entry?");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // diagrafoume prwta to farmako apo ton server kai meta an diagrafei epityxws
                                // to diagrafoume kai eswterika kai ananewnoume to text view
                                delMed = (Medicine) mAdapter.getItem(info.position);
                                new HttpGetTask().execute(delMed.getBarcode());
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                alertDialog.show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private class HttpGetTask extends AsyncTask<String, Void, Integer> {

        private static final String TAG = "HttpGetTask_Pharmacy";
        private int error = -1;
        private int result;

        @Override
        protected Integer doInBackground(String... input) {
            java.net.URL url = null;
            HttpURLConnection conn2 = null;

            String URL = server + "/med_delete/" + input[0] + "/";

            try {
                url = new URL(URL);

                conn2 = (HttpURLConnection) url.openConnection();
                conn2.setRequestMethod("DELETE");

                result = conn2.getResponseCode();
                Log.e(TAG, "Received HTTP response: " + result);

            } catch (ProtocolException e) {
                error = 1;
                Log.e(TAG, "ProtocolException");
                e.printStackTrace();
            } catch (MalformedURLException exception) {
                error = 1;
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                error = 2;
                Log.e(TAG, "IOException");
            } finally {
                if (null != conn2)
                    conn2.disconnect();

            }

            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (error > 0) {
                Toast.makeText(getApplicationContext(), (error == 1) ? "No internet connection" : "Server error",
                        Toast.LENGTH_LONG).show();
            }
            else {
                db.deleteMed(delMed, "depon");
                mAdapter.remove(delMed);
            }
        }
    }
}
