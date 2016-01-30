package uk.ac.uea.framework.implementation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import uk.ac.uea.framework.Orientation;

/**
 * Created by Jack L. Clements on 30/01/2016.
 */
public class AndroidOrientation implements Orientation{

    private SensorManager sensorM;
    private Sensor accelorometer;
    private Activity activity;

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;
            if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                System.out.println(x + " " + y + " " + z);
            } //test values before calculating anything else
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    //Constructor

    public AndroidOrientation(){
    }

    public void setupSensor(){
        sensorM = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        accelorometer = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void registerListener(){
        sensorM.registerListener(mSensorEventListener, accelorometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener(){
        sensorM.unregisterListener(mSensorEventListener);
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }


}
