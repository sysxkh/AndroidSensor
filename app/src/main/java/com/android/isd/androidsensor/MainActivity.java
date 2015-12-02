package com.android.isd.androidsensor;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity  {
    Handler handler = new Handler();
    SensorManager sensorManager;
    TextView myTextView;
    float appliedAcceleration = 0;
    float currentAcceleration = 0;
    float distance = 0;
    boolean isTouch = false;
    float lastV = 0;
    Date lastUpdate;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_main);

        myTextView = (TextView) findViewById(R.id.myTextView);
        lastUpdate = new Date(System.currentTimeMillis());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        if(allSensors==null || allSensors.size()==0 || !allSensors.contains(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)))
        {
            myTextView.setText("No ACCELEROMETER Sensor !");
        }
        else
        {
            Button button = (Button) findViewById(R.id.button);
            Button button1 = (Button) findViewById(R.id.button2);

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    distance = 0;
                }
            });

            button.setOnTouchListener(new View.OnTouchListener() {
                  @Override
                  public boolean onTouch(View v, MotionEvent event)
                  {
                      switch (event.getAction())
                      {
                          case MotionEvent.ACTION_DOWN:
                          {
                              isTouch = true;
                              break;
                          }
                          case MotionEvent.ACTION_MOVE:
                          {
                              break;
                          }
                          case MotionEvent.ACTION_UP:
                          {
                              isTouch = false;
                              break;
                          }
                      }
                      return false;
                  }
              }

            );

            sensorManager.registerListener(sensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                    SensorManager.SENSOR_DELAY_FASTEST);
            Timer updateTimer = new Timer("velocityUpdate");
            updateTimer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    updateGUI();
                }
            }, 0, 100);
        }
    }

    private void updateGUI() {
        handler.post(new Runnable() {
            public void run() {
                myTextView.setText("The distance between your move is : "+distance+" m");
            }
        });
    }

    private void updateVelocity() {
        Date timeNow = new Date(System.currentTimeMillis());
        long timeDelta = timeNow.getTime() - lastUpdate.getTime();
        lastUpdate.setTime(timeNow.getTime());

        float deltaVelocity = appliedAcceleration * timeDelta / 1000;
        float deltaDistance = lastV*timeDelta/1000 + (float) 0.5 * deltaVelocity * timeDelta / 1000;
        appliedAcceleration = currentAcceleration;
        lastV = lastV + deltaVelocity;

        if(isTouch)
        {
            distance += deltaDistance;
        }
    }


    private final SensorEventListener sensorListener = new SensorEventListener() {

        double calibration = Double.NaN;

        public void onSensorChanged(SensorEvent event) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            double a = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));

            if (calibration == Double.NaN)
                calibration = a;
            else {
                updateVelocity();
                currentAcceleration = (float) a;
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            
        }
    };


}
