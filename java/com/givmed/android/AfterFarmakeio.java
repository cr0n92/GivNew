package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class AfterFarmakeio extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static MedicineAdapter mAdapter;
    public static TextView msgView;
    public static String name = "";
    DBHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.pharmacy);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("name"))
            name = intent.getStringExtra("name");

        msgView = (TextView) findViewById(R.id.secondMes);
        msgView.setText(name);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.farmako);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        db = new DBHandler(getApplicationContext());

        mAdapter = new MedicineAdapter(getApplicationContext());
        ListView list = (ListView)findViewById(R.id.list);
        list.setFooterDividersEnabled(true);
        list.setOnItemClickListener(this);
        list.setAdapter(mAdapter);

        if (mAdapter.getCount() == 0)
            db.getAllMedsToAdapter(name, mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_simple, menu);
        return true;
    }

    public void onItemClick(AdapterView<?> l, View v, int position, long id) {
        Intent intent = new Intent(getApplicationContext(), DisplayMed.class);
        intent.putExtra("barcode", (String) mAdapter.getItem(position));
        startActivity(intent);
    }
}
