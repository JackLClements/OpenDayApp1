package uk.ac.uea.framework;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Surface;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Jack L. Clements on 23/11/2015.
 */
public interface Camera{
    public enum Orientations{
        UPRIGHT(Surface.ROTATION_0, 0),
        RIGHT(Surface.ROTATION_90, 90),
        DOWN(Surface.ROTATION_180, 180),
        LEFT(Surface.ROTATION_270, 270);

        private final int rotation;
        private final int value;

        private Orientations(int rotation, int value){
            this.rotation = rotation;
            this.value = value;
        }

        public int getRotation(){
            return this.rotation;
        }

        public int getValue(){
            return this.value;
        }

    }

    //Implement singleton behaviour for camera object? I can't enforce this behaviour in this, it's static

    //These methods determine program behaviour, extended from {@Link Activity}, thus want to be implemented in the program behaviour, not the framework
    /*
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void onViewCreated(final View view, Bundle savedInstanceState);

    //@param savedInstanceState used to pass data between activities

    public void onActivityCreated(Bundle savedInstanceState);

    public void onResume();

    public void onPause();

    public void onClick(View view);
    */
    //private method needed to request camera, this shouldn't be public, if used out of scope it could be a DISASTER
    public void handlePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

    //Actual methods for the program
    public void chooseBestCameraSize(int height, int width);

    public void createCameraPreview();

    public void closeCamera();

    public void preCapture();

    public void capturePicture();

    public void unlockFocus();

    public void captureMessage();

    //Either static class or method to set permission for camera





}
