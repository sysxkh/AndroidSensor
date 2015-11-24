package com.android.isd.androidsensor;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;


public class MainActivity extends Activity  {
    Handler handler = new Handler();
    SensorManager sensorManager;
    TextView myTextView;
    float appliedAcceleration = 0;
    float currentAcceleration = 0;
    float velocity = 0;
    Date lastUpdate;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        myTextView = (TextView) findViewById(R.id.myTextView);
        lastUpdate = new Date(System.currentTimeMillis());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(sensorListener,
                SensorManager.SENSOR_ACCELEROMETER,
                SensorManager.SENSOR_DELAY_FASTEST);

        Timer updateTimer = new Timer("velocityUpdate");
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateGUI();
            }
        }, 0, 1000);
    }

    private void updateGUI() {
        handler.post(new Runnable() {
            public void run() {
                myTextView.setText(velocity + "");
            }
        });
    }

    private void updateVelocity() {
        Date timeNow = new Date(System.currentTimeMillis());
        long timeDelta = timeNow.getTime() - lastUpdate.getTime();
        lastUpdate.setTime(timeNow.getTime());

        float deltaVelocity = appliedAcceleration * (timeDelta / 1000);
        appliedAcceleration = currentAcceleration;

        velocity += deltaVelocity;
    }

    private final SensorListener sensorListener = new SensorListener() {

        double calibration = Double.NaN;

        public void onSensorChanged(int sensor, float[] values) {
            double x = values[SensorManager.DATA_X];
            double y = values[SensorManager.DATA_Y];
            double z = values[SensorManager.DATA_Z];

            double a = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)
                    + Math.pow(z, 2));

            if (calibration == Double.NaN)
                calibration = a;
            else {
                updateVelocity();
                currentAcceleration = (float) a;
            }
        }

        public void onAccuracyChanged(int sensor, int accuracy) {
        }
    };


}
