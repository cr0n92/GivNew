package com.givmed.android;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.givmed.android.R;

public class TwoButtons extends HelperActivity
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.mainee, R.string.two_buttons, true);

        final Button scanButton = (Button) findViewById(R.id.button1);
        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PackageManager pm = getApplicationContext().getPackageManager();

                // koitame an to kinito exei kamera kai an den exei deixnoume toast
                if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))
                    Toast.makeText(getApplicationContext(), "Device has no camera!", Toast.LENGTH_SHORT).show();
                else {
                    Intent intent = new Intent(getApplicationContext(), BarcodeScanner.class);
                    startActivityForResult(intent, 1);
                }
            }
        });

        final Button userButton = (Button) findViewById(R.id.button2);
        userButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), Inputter.class);
                startActivity(myIntent);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if(requestCode == 1 && resultCode == RESULT_OK)
        {
            String barcode = intent.getStringExtra("BARCODE");

            if (barcode.equals("NULL"))
                Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT).show();
            else {
                Intent showItemIntent = new Intent(getApplicationContext(), Inputter.class);
                showItemIntent.putExtra("barcode", barcode);
                startActivity(showItemIntent);
            }
        }
    }
}