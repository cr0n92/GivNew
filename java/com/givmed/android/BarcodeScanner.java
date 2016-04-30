package com.givmed.android;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import com.givmed.android.R;
import java.util.List;

public class BarcodeScanner extends AppCompatActivity {

    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private float mLightQuantity;
    private boolean hasSensor = false;
    SensorEventListener mListener;

    ImageScanner scanner;
    private boolean previewing = true;

    static {
        System.loadLibrary("iconv");
    }

    public void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.barcode_scanner);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar mToolBar = (Toolbar) findViewById(R.id.tool_bar);
        mToolBar.setTitle(R.string.scan);
        mToolBar.setNavigationIcon(R.drawable.ic_arrows);
        setSupportActionBar(mToolBar);

        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //getActionBar().hide();

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        if (mCamera == null) {
            Toast.makeText(getApplicationContext(), R.string.camera_error, Toast.LENGTH_LONG).show();
            releaseCamera();
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }

        if (hasFlash()) {
            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
                hasSensor = true;
                enableSensorAndFlash();
            }
        }

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout)findViewById(R.id.cameraPreview);
        preview.addView(mPreview);

        RelativeLayout text = (RelativeLayout) findViewById(R.id.relative);
        text.bringToFront();

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_simple, menu);
        return true;
    }

    /** A safe way to get an instance of the Camera object. */
    public Camera getCameraInstance()
    {
        Camera camera = null;
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    camera = Camera.open(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    //  throws runtime exception :"Failed to connect to camera service"
                }
            }
        }
        return camera;
    }

    public boolean hasFlash() {
        if (mCamera == null)
            return false;

        Camera.Parameters parameters = mCamera.getParameters();

        if (parameters.getFlashMode() == null)
            return false;

        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
        if (supportedFlashModes == null || supportedFlashModes.isEmpty() || supportedFlashModes.size() == 1 && supportedFlashModes.get(0).equals(Camera.Parameters.FLASH_MODE_OFF))
            return false;

        return true;
    }

    private void enableSensorAndFlash() {
        // Obtain references to the SensorManager and the Light Sensor
        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        //Implement a listener to receive updates
        mListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                mLightQuantity = event.values[0];
                Log.i("Light Mesurment: ", " " + mLightQuantity);

                if (mLightQuantity < 10) {
                    Camera.Parameters param = mCamera.getParameters();
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(param);
                }
                else {
                    Camera.Parameters param = mCamera.getParameters();
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mCamera.setParameters(param);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

        // Register the listener with the light sensor -- choosing
        // one of the SensorManager.SENSOR_DELAY_* constants.
        mSensorManager.registerListener(mListener, mLightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void releaseCamera()
    {
        if (mCamera != null)
        {
            if (hasSensor)
                mSensorManager.unregisterListener(mListener);

            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    PreviewCallback previewCb = new PreviewCallback()
    {
        public void onPreviewFrame(byte[] data, Camera camera)
        {
            Camera.Parameters parameters = camera.getParameters();
            Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0)
            {
                SymbolSet syms = scanner.getResults();

                for (Symbol sym : syms)
                {
                    String answer = sym.getData();
                    Log.i("<<<BARCODE>>>", answer);

                    if (!answer.startsWith("280")) {
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("BARCODE", sym.getData());
                        setResult(RESULT_OK, returnIntent);
                        releaseCamera();
                        finish();
                    }
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    AutoFocusCallback autoFocusCB = new AutoFocusCallback()
    {
        public void onAutoFocus(boolean success, Camera camera)
        {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    private Runnable doAutoFocus = new Runnable()
    {
        public void run()
        {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    public void onRestart() {
        super.onRestart();
        onBackPressed();
    }

    public void onPause()
    {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onBackPressed() {
        releaseCamera();
        Intent intent = new Intent();
        intent.putExtra("BARCODE", "NULL");
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

}