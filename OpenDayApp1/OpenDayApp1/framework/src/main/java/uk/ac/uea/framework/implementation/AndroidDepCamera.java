package uk.ac.uea.framework.implementation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.widget.Toast;
import android.hardware.Camera;

/**
 * Created by Jack L. Clements on 24/11/2015.
 */
public class AndroidDepCamera extends Fragment implements uk.ac.uea.framework.Camera {
    android.hardware.Camera cam;
    Activity activity;
    AutofitTextureView preview;

    //TODO
    //Method code
    //Any fields
    //Constructors
    //http://developer.android.com/guide/topics/media/camera.html


    private final TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(height, width);
            try{
                cam.setPreviewTexture(texture);
            }
            catch(Exception e){
                e.printStackTrace();
            }
            cam.startPreview();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            //configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            cam.stopPreview();
            cam.release();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }
    };

    //Static methods

    /**
     * Returns an instance of the first, back-facing camera on a device. Depricated, but for use on old phones.
     * @return a camera instance if one exists, otherwise null
     */
    public static android.hardware.Camera getCameraInstance(){
        android.hardware.Camera camera = null;
        try{
            camera = android.hardware.Camera.open();
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return camera;
    }


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

    }

    public void openCamera(int height, int width){
        cam = AndroidDepCamera.getCameraInstance();
        cam = android.hardware.Camera.open();
    }

    public void closeCamera(){
        cam.release();
    }

    public void addActivity(Activity activity){
        this.activity = activity;
    }

    public void setPreview(AutofitTextureView texture){
        preview = texture;
    }

    public void setSurfaceTextureListener(){
        preview.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    public boolean textureViewStatus(){
        if(preview.isAvailable()){
            return true;
        }
        else{
            return false;
        }
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
