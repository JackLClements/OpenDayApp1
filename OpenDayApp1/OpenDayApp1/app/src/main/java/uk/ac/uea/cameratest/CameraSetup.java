package uk.ac.uea.cameratest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import uk.ac.uea.framework.implementation.AndroidCameraFactory;
import uk.ac.uea.framework.implementation.AndroidCompass;
import uk.ac.uea.framework.implementation.AndroidOrientation;
import uk.ac.uea.framework.implementation.AutofitTextureView;

import java.io.File;

import uk.ac.uea.framework.implementation.AndroidCamera;
import uk.ac.uea.framework.implementation.AutofitTextureView;

/**
 * Created by jackc on 29/11/2015.
 */
public class CameraSetup extends Fragment implements View.OnClickListener{
    boolean init;
    View view;
    AndroidCameraFactory camera;
    AndroidCompass compass;
    AutofitTextureView preview;

    @TargetApi(23)
    public CameraSetup(){
        camera = new AndroidCameraFactory();
        compass = new AndroidCompass();
    }

    public void addTexture(AutofitTextureView texture){
        preview = texture;
        //then pass this to the camera so it can be attached to a surfacelistener
    }

    public void onStart(){
        super.onStart();
        init = true;
        if(isAdded()){

            if(view != null){
                System.out.println("Ayyyy");
            }
            AutofitTextureView newView = (AutofitTextureView) view;

            camera.setPreview(newView);
            camera.addActivity(getActivity());
            compass.setActivity(getActivity());
            compass.setupSensor();
            System.out.println("Fragment attached to activity!");
            // camera.createCameraPreview();
            //camera.openCamera(400, 400);
            //camera.createCameraPreview();
            //camera.chooseBestCameraSize(400, 400);
        }
        else{

        }
    }


    public void onCreate(Bundle saveStateInstance) {
        super.onCreate(saveStateInstance);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_cameratest, container, false);
    }

    public void onViewCreated(final View view, Bundle savedInstanceState){
        if(view.findViewById(R.id.picture) != null){
            view.findViewById(R.id.picture).setOnClickListener(this);
        }
        if(view.findViewById(R.id.info) != null){
            view.findViewById(R.id.info).setOnClickListener(this);
        }
        if(view.findViewById(R.id.texture) != null){
            this.view = view.findViewById(R.id.texture);
            //camera.setmSurface((AutofitTextureView) view.findViewById(R.id.texture));
        }
    }

    //@param savedInstanceState used to pass data between activities
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        File tempFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");
    }


    public void onResume(){
        super.onResume();
        System.out.println(camera.textureViewStatus());
        if(camera.textureViewStatus()){
            System.out.println("REOPENING CAMERA");
            camera.openCamera(1920, 1080);
        }
        else{
            camera.setSurfaceTextureListener();
        }
        compass.registerListener();
    }

    public void onPause(){
        super.onPause();
        camera.closeCamera();
        compass.unregisterListener();
    }

    public void onClick(View view){

    }
}
