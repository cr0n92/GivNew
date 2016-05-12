package com.givmed.android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;


import java.util.Calendar;
import java.util.Date;


public class DwreaVolunteer extends AppCompatActivity {

    private static EditText dateChoose1, dateChoose2, dateChoose3, dateChoosed;
    public static String serverDate;
    private static int datesCnt = 1;
    public ProgressDialog dialog;
    public Donation donation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dwrea_volunteer);

        Intent intent = getIntent();
        if (intent != null)

        dialog = new ProgressDialog(this);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.volunteer);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        dateChoosed = dateChoose1 = (EditText) findViewById(R.id.edit1);
        dateChoose2 = (EditText) findViewById(R.id.edit2);
        dateChoose3 = (EditText) findViewById(R.id.edit3);

        Date date1 = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date1);
        setDateString(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        dateChoose1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dateChoosed = dateChoose1;
                showDatePicker();
            }
        });

        dateChoose2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dateChoosed = dateChoose2;
                showDatePicker();
            }
        });

        dateChoose3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dateChoosed = dateChoose3;
                showDatePicker();
            }
        });

        final TextView addDate = (TextView) findViewById(R.id.addMore);
        addDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (datesCnt) {
                    case 1:
                        dateChoose2.setVisibility(View.VISIBLE);
                        datesCnt++;
                        break;
                    case 2:
                        dateChoose3.setVisibility(View.VISIBLE);
                        addDate.setVisibility(View.GONE);
                        datesCnt++;
                        break;
                }
            }
        });

        EditText mName = (EditText) findViewById(R.id.name);
        EditText mForeas = (EditText) findViewById(R.id.foreas);
        EditText mAdddress = (EditText) findViewById(R.id.address);

        //mName.setText(name);
        //mExp.setText(date);
        //mPhone.setText(phone);

        mName.setKeyListener(null);
        mForeas.setKeyListener(null);

        final Button doneButton = (Button) findViewById(R.id.button1);
        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO: vazoume to farmako stis oloklhrwmenes, to leme ston server kai diagrafoume to famrmako topika
//                Intent intent = new Intent(getApplicationContext(), BarcodeScanner.class);
//                startActivity(intent);
//                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_tick) {

//            if () {
//                Toast.makeText(getApplicationContext(), getString(R.string.inp_error_msg), Toast.LENGTH_LONG).show();
//            }
//            else {
//
//                if (isOnline(getApplicationContext())) {
//                    dialog.setMessage(getString(R.string.loading_msg));
//                    dialog.setCancelable(false);
//                    dialog.setCanceledOnTouchOutside(false);
//                    dialog.show();
//
//                    new HttpGetTask().execute(code);
//                } else
//                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
//            }
        }

        return super.onOptionsItemSelected(item);
    }

    private static void setDateString(int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        String mon = "" + monthOfYear;
        String day = "" + dayOfMonth;

        if (monthOfYear < 10)
            mon = "0" + monthOfYear;
        if (dayOfMonth < 10)
            day = "0" + dayOfMonth;

        serverDate = year + "-" + mon + "-" + day;
        String dateString = day + "/" + mon + "/" + (year % 100);
        dateChoosed.setText(dateString);
    }

    public void showDatePicker() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "pickDate");
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the current date as the default date in the picker

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            setDateString(year, monthOfYear, dayOfMonth);
        }
    }
}
