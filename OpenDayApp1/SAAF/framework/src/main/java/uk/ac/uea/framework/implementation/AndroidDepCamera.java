package uk.ac.uea.framework.implementation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Size;
import android.widget.Toast;

import uk.ac.uea.framework.Camera;

/**
 * Created by Jack L. Clements on 24/11/2015.
 */
public class AndroidDepCamera extends Fragment implements Camera {
    android.hardware.Camera cam;
    //TODO
    //Method code
    //Any fields
    //Constructors
    //remember, you may need singleton behaviour, this will require some thought

    public void handlePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        //Still needs implementing app-wide. might be better off putting in factory class?
    }

    @TargetApi(19)
    public void chooseBestCameraSize(int height, int width){
        //May need to do some old stuff to choose best size, although the getParameters() function
        //does most of this for us.
    }

    public void createCameraPreview(){
        //
    }

    public void openCamera(){
       cam = android.hardware.Camera.open();
        //get settings from getParameters();
        //No real need to modify? Could add later
        //setDisplayOrientation
        //Pass an initialised SurfaceHolder to setPreviewDisplay(surface)
        //Call startPreview()
        //When quitting call stopPreview()
    }

    public void closeCamera(){
        cam.release();
    }

    public void captureMessage(String string){
        final Activity activity = getActivity();
        final String str = string;
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, str, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
