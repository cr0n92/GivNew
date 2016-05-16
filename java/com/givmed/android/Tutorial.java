package com.givmed.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

public class Tutorial extends AppCompatActivity {
    int whichTutorial = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.tutorial);

        final ImageView[] circles = new ImageView[4];
        final RelativeLayout[] layouts = new RelativeLayout[4];
        final int solid = R.drawable.circle, donut = R.drawable.circle_around;

        circles[0] = (ImageView) findViewById(R.id.img1);
        circles[1] = (ImageView) findViewById(R.id.img2);
        circles[2] = (ImageView) findViewById(R.id.img3);
        circles[3] = (ImageView) findViewById(R.id.img4);

        layouts[0] = (RelativeLayout) findViewById(R.id.tutorial1);
        layouts[1] = (RelativeLayout) findViewById(R.id.tutorial2);
        layouts[2] = (RelativeLayout) findViewById(R.id.tutorial3);
        layouts[3] = (RelativeLayout) findViewById(R.id.tutorial4);

        final TextView katwDexia = (TextView) findViewById(R.id.learn);
        final TextView katwAristera = (TextView) findViewById(R.id.skipper);
        katwAristera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                skipTutorial();
            }
        });;

        LinearLayout myLayout = (LinearLayout) findViewById(R.id.relativity);
        myLayout.setOnTouchListener(new OnSwipeTouchListener(getApplicationContext()) {
            public void onSwipeRight() {
                if (whichTutorial > 0) {
                    --whichTutorial;
                    circles[whichTutorial].setImageResource(solid);
                    circles[whichTutorial + 1].setImageResource(donut);

                    layouts[whichTutorial].setVisibility(View.VISIBLE);
                    layouts[whichTutorial + 1].setVisibility(View.GONE);

                    if (whichTutorial == 0) katwDexia.setText(getString(R.string.learn));
                }
            }

            public void onSwipeLeft() {
                if (whichTutorial == 3)
                    skipTutorial();
                else {
                    ++whichTutorial;
                    circles[whichTutorial].setImageResource(solid);
                    circles[whichTutorial - 1].setImageResource(donut);

                    layouts[whichTutorial].setVisibility(View.VISIBLE);
                    layouts[whichTutorial - 1].setVisibility(View.GONE);

                    katwDexia.setText(getString(R.string.next));
                }
            }
        });
    }

    public void skipTutorial() {
        PrefManager pref = new PrefManager(this);

        if (pref.isLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), TwoButtons.class));
            finish();
        }
        else {
            startActivity(new Intent(getApplicationContext(), Number.class));
            finish();
        }
    }
}