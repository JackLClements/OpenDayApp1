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
    private Sensor accelerometerSensor;
    private Activity activity;
    private float[] magneticValues;
    private float[] accelValues;
    Double degToInt;

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
                for(int i = 0; i < 3; i++){
                    magneticValues[i] = sensorEvent.values[i];
                }

                //System.out.println("MAGNETS X - " + magneticValues[0] + " Y - " + magneticValues[1] + " Z - " + magneticValues[2]);
            } //test values before calculating anything else
            if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER){

                for(int i = 0; i < 3; i++){
                    accelValues[i] = sensorEvent.values[i];
                }
            }
            calculateNorth();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public AndroidCompass(){
        magneticValues = new float[3];
        accelValues = new float[3];
    }

    /**
     * Establishes sensors - specifically the accelerometer
     */
    public void setupSensor(){
        sensorM = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        magneticFieldSensor = sensorM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        accelerometerSensor = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * Registers the listener object to the sensor, to detect events
     */
    public void registerListener(){
        sensorM.registerListener(mSensorEventListener, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorM.registerListener(mSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Unregisters the listener object from the sensor, to be used when pausing or closing the app
     */
    public void unregisterListener(){
        sensorM.unregisterListener(mSensorEventListener, magneticFieldSensor);
        sensorM.unregisterListener(mSensorEventListener, accelerometerSensor);
    }

    /**
     * Sets the {@link Activity} context to grab system hardware specifics
     * @param activity
     */
    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void calculateNorth(){
        float[] rotR = new float[9];
        float[] rotI = new float[9];
        float[] oritentation = new float[3];
        sensorM.getRotationMatrix(rotR, rotI, accelValues, magneticValues);
        sensorM.getOrientation(rotR, oritentation);
        double azimuthInDegress = Math.toDegrees(oritentation[0]);
        if (azimuthInDegress < 0.0f) {
            azimuthInDegress += 360.0f;
        }
        degToInt = (Double) azimuthInDegress;
        System.out.println("YOU ARE POINTING - " + degToInt.intValue());
    }

    public int getAngle(){
        return degToInt.intValue();
    }
}
