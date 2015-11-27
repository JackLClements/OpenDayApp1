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

    //TODO
    //Method code
    //Any fields
    //Constructors
    //remember, you may need singleton behaviour, this will require some thought

    public void handlePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        //
    }

    @TargetApi(19)
    public void chooseBestCameraSize(int height, int width){
        //CHANGE OLD CAMERA API VIEWPORT WITH THESE PARAMETERS
    }

    public void createCameraPreview(){
        //
    }

    public void openCamera(){

    }

    public void closeCamera(){
        //
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
