package com.givmed.android;


import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.givmed.android.R;

public class Communication extends HelperActivity {

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
                String url = "https://www.facebook.com/givmed";
                Uri uri = Uri.parse(url);
                try {
                    final PackageManager pm = getPackageManager();
                    ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);

                    if (applicationInfo.enabled)
                        uri = Uri.parse("fb://facewebmodal/f?href=" + url);

                } catch (PackageManager.NameNotFoundException ignored) {
                }

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(Intent.createChooser(browserIntent, getString(R.string.open_with)));
            }
        });

        TextView siteView = (TextView) findViewById(R.id.siteView);
        siteView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.com_site_full)));
                startActivity(Intent.createChooser(browserIntent, getString(R.string.open_with)));
            }
        });
    }
}