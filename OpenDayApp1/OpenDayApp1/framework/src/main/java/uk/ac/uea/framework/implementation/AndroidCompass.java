package uk.ac.uea.framework.implementation;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import uk.ac.uea.framework.Orientation;

/**
 * Created by Jack L. Clements on 30/01/2016.
 */
public class AndroidCompass implements Orientation {
    /**Manager object for all sensors on a device */
    private SensorManager sensorM;
    /**Sensor object representing the device accelerometer */
    private Sensor magneticFieldSensor;
    private Activity activity;

    /**
     * Nested class implementation of the SensorEventListener interface.
     * A listener object designed to detect and react to changes in the {@link Sensor} object
     */
    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Sensor mySensor = sensorEvent.sensor;
            if(mySensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                //code to handle magnetic field
            } //test values before calculating anything else
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public AndroidCompass(){

    }

    /**
     * Establishes sensors - specifically the accelerometer
     */
    public void setupSensor(){
        sensorM = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        magneticFieldSensor = sensorM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    /**
     * Registers the listener object to the sensor, to detect events
     */
    public void registerListener(){
        sensorM.registerListener(mSensorEventListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregisters the listener object from the sensor, to be used when pausing or closing the app
     */
    public void unregisterListener(){
        sensorM.unregisterListener(mSensorEventListener);
    }

    /**
     * Sets the {@link Activity} context to grab system hardware specifics
     * @param activity
     */
    public void setActivity(Activity activity){
        this.activity = activity;
    }
}