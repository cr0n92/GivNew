package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.givmed.android.R;

public class Share extends HelperActivity {
    private final String TAG = "Share";
    private static Button msgButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setMenu(R.menu.menu_main_simple);
        super.helperOnCreate(R.layout.share, R.string.share, true);

        msgButton = (Button) findViewById(R.id.msgButton);

        msgButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                sendIntent.setType("text/plain");
                startActivity(Intent.createChooser(sendIntent, getString(R.string.share_with)));
                finish();
            }
        });
    }
}