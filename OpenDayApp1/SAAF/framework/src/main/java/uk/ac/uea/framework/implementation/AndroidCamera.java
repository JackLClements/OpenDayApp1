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
public class AndroidCamera extends Fragment implements Camera {

    //TODO
    //Method code
    //Any fields
    //Constructors
    //remember, you may need singleton behaviour, this will require some thought

    public void handlePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
       //
    }

    @TargetApi(21)
    public void chooseBestCameraSize(int height, int width){
        Size size = new Size(height, width);
        //YOU THEN NEED TO DO SOMETHING WITH THIS
    }

    public void createCameraPreview(){
        //
    }

    public void closeCamera(){
        //
    }

    public void preCapture(){
        //
    }

    public void capturePicture(){
        //
    }

    public void unlockFocus(){
        //
    }

    public void captureMessage(){
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "Picture Saved!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
