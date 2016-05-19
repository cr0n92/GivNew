package com.givmed.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

import java.util.ArrayList;

public class BlueRedList extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private final String TAG = "BlueRedList";
    ProgressDialog dialog;
    public static BlueRedAdapter mAdapter;
    public static boolean inRed = false;
    private TextView firstMes;
    private SpannableStringBuilder redBuilder, blueBuilder;
    AlertDialog matchAlert;
    DBHandler db;

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

        firstMes = (TextView) findViewById(R.id.firstMes);
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
            if (inRed)
                firstMes.setText(redBuilder, TextView.BufferType.SPANNABLE);
            else
                firstMes.setText(blueBuilder, TextView.BufferType.SPANNABLE);

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
                matching();
            } else {
                inRed = true;
                firstMes.setText(redBuilder, TextView.BufferType.SPANNABLE);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (inRed) {
            inRed = false;
            firstMes.setText(blueBuilder, TextView.BufferType.SPANNABLE);
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
        int ret, matchedMeds = 0;

        //Kanoume subscribe sta topics gia kathe onoma farmakou sthn mple lista
        //Kanoume subscribe mono gia ayta pou den exoune ginei match
        ArrayList<String> topics = new ArrayList<String>();

        Intent serviceIntent = new Intent(getApplicationContext(), SubscribeService.class);
        serviceIntent.putExtra("subscribe", true);

        dialog = new ProgressDialog(this);
        dialog.setMessage(getString(R.string.matching));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        for (BlueRedItem item : mAdapter.mItems) {
            Log.e("Med name",item.getName());
            ret = db.updateMedAndMatch(item);

            if (ret == -1)
                topics.add(item.getName());
            else if (ret == 1)
                matchedMeds++;
        }
        serviceIntent.putStringArrayListExtra("topic", topics);
        startService(serviceIntent);

        dialog.dismiss();
        dialog = null;

        if (matchedMeds > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.after_matching_left) + " " + matchedMeds + " " + getString(R.string.after_matching_right))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog2, int id) {
                            Intent toDonations = new Intent(getApplicationContext(), Dwrees.class);
                            startActivity(toDonations);
                            finish();
                        }
                    });
            matchAlert = builder.create();
            matchAlert.show();
        }
        else {
            Intent toFarmakeio = new Intent(getApplicationContext(), Farmakeio.class);
            startActivity(toFarmakeio);
            finish();
        }
    }
}