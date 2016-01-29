package uk.ac.uea.framework.implementation;

import android.support.annotation.NonNull;

import uk.ac.uea.framework.Camera;

/**
 * Created by Jack L. Clements on 24/11/2015.
 */
public class AndroidCameraFactory implements Camera{
    private Camera camera;
    public AndroidCameraFactory(){
        if(android.os.Build.VERSION.SDK_INT >= 21){
            camera = new AndroidCameraOLD();
        }
        else{
            camera = new AndroidDepCamera();
        }
    }

    //THESE NEED IMPLEMENTING
    public void handlePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        camera.handlePermissions(requestCode, permissions, grantResults);
    }

    public void chooseBestCameraSize(int height, int width){
       camera.chooseBestCameraSize(height, width);
    }

    public void createCameraPreview(){
        camera.createCameraPreview();
    }

    public void openCamera(){
        camera.openCamera();
    }

    public void closeCamera(){
        camera.closeCamera();
    }

    public void captureMessage(String string){
        camera.captureMessage(string);
    }


    public Camera getCamera(){
        return camera;
    }
    /*
    public AndroidCameraFactory getInstance(){
        if
    }*/
}
