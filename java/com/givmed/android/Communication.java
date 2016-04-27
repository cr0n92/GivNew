package com.givmed.android;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.givmed.android.R;

public class Communication extends HelperActivity {
    private int REQUEST_CODE = 1923;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.communication, R.string.communication, true);

        TextView mailView = (TextView) findViewById(R.id.mailView);
        mailView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.com_mail)});
                intent.putExtra(Intent.EXTRA_SUBJECT, "");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, getString(R.string.send_with)));
            }
        });

        TextView fbView = (TextView) findViewById(R.id.fbView);
        fbView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/givmed"));
                startActivity(Intent.createChooser(browserIntent, getString(R.string.open_with)));
            }
        });

        TextView siteView = (TextView) findViewById(R.id.siteView);
        siteView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.com_site)));
                startActivity(Intent.createChooser(browserIntent, getString(R.string.open_with)));
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            Toast.makeText(getApplicationContext(), "Communication sent.", Toast.LENGTH_SHORT).show();
//
//            Intent showItemIntent = new Intent(getApplicationContext(), Elleipseis.class);
//            //If set, and the activity being launched is already running in the current task, then instead of
//            // launching a new instance of that activity, all of the other activities on top of it will be closed and
//            // this Intent will be delivered to the (now on top) old activity as a new Intent.
//            showItemIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            //If set, the activity will not be launched if it is already running at the top of the history stack.
//            showItemIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//            startActivity(showItemIntent);
//            finish();
//        }
//    }

//                Intent i = new Intent(Intent.ACTION_SEND);
//                i.setType("message/rfc822");
//                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"ore_gia@hotmail.com"});
//                i.putExtra(Intent.EXTRA_SUBJECT, mEditText1.getText().toString());
//                i.putExtra(Intent.EXTRA_TEXT, mEditText2.getText().toString());
//
//                try {
//                    startActivityForResult(Intent.createChooser(i, "Send mail..."), REQUEST_CODE);
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(getApplicationContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                }

}