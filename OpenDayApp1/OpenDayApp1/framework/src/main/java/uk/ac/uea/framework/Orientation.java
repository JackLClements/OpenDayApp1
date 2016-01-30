package uk.ac.uea.framework;

import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
/**
 * Created by Jack L. Clements on 30/01/2016.
 * Interface designed to implement common set of methods used in orientation/sensors for smartphones
 * Can be used to implement gyroscope, accelerometer, etc.
 */
public interface Orientation{
    void setupSensor();

    void registerListener();

    void unregisterListener();

}
