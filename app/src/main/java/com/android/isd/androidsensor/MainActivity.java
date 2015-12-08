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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity  {
    Handler handler = new Handler();
    SensorManager sensorManager;
    TextView myTextView;
    Toast toast;
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
//            Button button1 = (Button) findViewById(R.id.button2);
//            final Button buttonX = (Button) findViewById(R.id.buttonX);
//            final Button buttonZ = (Button) findViewById(R.id.buttonZ);
//
//            buttonX.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v) {
//                    sensorManager.unregisterListener(sensorListenerZ);
//                    sensorManager.registerListener(sensorListenerX, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
//                    buttonX.setEnabled(false);
//                    buttonZ.setEnabled(true);
//                }
//            });
//
//            buttonZ.setOnClickListener(new View.OnClickListener()
//            {
//                @Override
//                public void onClick(View v) {
//                    sensorManager.unregisterListener(sensorListenerX);
//                    sensorManager.registerListener(sensorListenerZ, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
//                    buttonX.setEnabled(true);
//                    buttonZ.setEnabled(false);
//                }
//            });

//            button1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    distance = 0;
//                    lastV = 0;
//                }
//            });

            button.setOnTouchListener(new View.OnTouchListener() {
                  @Override
                  public boolean onTouch(View v, MotionEvent event)
                  {
                      switch (event.getAction())
                      {
                          case MotionEvent.ACTION_DOWN:
                          {
                              isTouch = true;
                              distance = 0;
                              lastV = 0;
                              toast = Toast.makeText(getApplicationContext(), "Measuring...", Toast.LENGTH_SHORT);
                              toast.setGravity(Gravity.TOP,0,0);
                              toast.show();
                              break;
                          }
                          case MotionEvent.ACTION_MOVE:
                          {
                              break;
                          }
                          case MotionEvent.ACTION_UP:
                          {
                              isTouch = false;
                              toast = Toast.makeText(getApplicationContext(), "Done!", Toast.LENGTH_SHORT);
                              toast.setGravity(Gravity.TOP,0,0);
                              toast.show();
                              break;
                          }
                      }
                      return false;
                  }
              }

            );
        //    buttonZ.setEnabled(false);
            sensorManager.registerListener(sensorListenerZ,
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
        float deltaDistance = lastV*timeDelta/1000 +  deltaVelocity * timeDelta / 2000;
        appliedAcceleration = currentAcceleration;


        if(isTouch)
        {
            distance += deltaDistance;
            lastV = lastV + deltaVelocity;
        }
    }


    private final SensorEventListener sensorListenerZ = new SensorEventListener() {

        double calibration = Double.NaN;

        public void onSensorChanged(SensorEvent event) {
            double z = event.values[2];

        //    double a = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
            double a = z;
            if(a<0.5 && a>-0.5)
                a = 0;


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
//
//    private final SensorEventListener sensorListenerX = new SensorEventListener() {
//
//        double calibration = Double.NaN;
//
//        public void onSensorChanged(SensorEvent event) {
//            double x = event.values[0];
//
//            double a = x;
//            if(a<0.5 && a>-0.5)
//                a = 0;
//
//            if (calibration == Double.NaN)
//                calibration = a;
//            else {
//                updateVelocity();
//                currentAcceleration = (float) a;
//            }
//        }
//
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    };


}
