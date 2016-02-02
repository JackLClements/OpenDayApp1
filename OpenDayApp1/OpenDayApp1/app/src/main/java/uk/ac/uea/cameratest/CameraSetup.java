package uk.ac.uea.cameratest;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.SurfaceTexture;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import uk.ac.uea.framework.implementation.AndroidCameraFactory;
import uk.ac.uea.framework.implementation.AndroidCompass;
import uk.ac.uea.framework.implementation.AndroidGPS;
import uk.ac.uea.framework.implementation.AndroidOrientation;
import uk.ac.uea.framework.implementation.AutofitTextureView;

import java.io.File;
import android.os.Handler;

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
    TextView text;
    TextView text2;
    int angle;
    AndroidGPS gps;
    Location currentLocation;


    @TargetApi(23)
    public CameraSetup(){
        camera = new AndroidCameraFactory();
        compass = new AndroidCompass();
        angle = compass.getAngle();
    }

    public void addTexture(AutofitTextureView texture){
        preview = texture;
        //then pass this to the camera so it can be attached to a surfacelistener
    }

    public void onStart(){
        super.onStart();
        init = true;
        if(isAdded()){
            AutofitTextureView newView = (AutofitTextureView) view;

            camera.setPreview(newView);
            camera.addActivity(getActivity());
            compass.setActivity(getActivity());
            compass.setupSensor();
            //gps.startUpdates();
            //System.out.println(gps.getCurrentLocation().toString());
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
        gps = new AndroidGPS(getActivity(), 1);
        gps.startUpdates();
        final Handler mHandler = new Handler();
        Runnable updateUI = new Runnable() {
            @Override
            public void run() {
                currentLocation = gps.getCurrentLocation();
                angle = compass.getAngle();
                if((currentLocation.getLatitude() >= 52.6126 && currentLocation.getLatitude() <= 52.6218) && (currentLocation.getLongitude() >= 1.2408 && currentLocation.getLongitude() <= 1.2411)){
                    if((angle >= 330 && angle <= 360) || angle <= 10){
                        text.setText("Multifaith Centre");
                    }
                    if(angle >= 85 && angle <= 150){
                        text.setText("Union Bar");
                    }
                    if(angle >= 160 && angle <= 200){
                        text.setText("The Street");
                    }
                    if(angle >= 201 && angle <= 310){
                        text.setText("Campus Kitchen");
                    }
                    if(angle >= 50 && angle <= 80){
                        text.setText("LCR");
                    }
                    else{
                        text.setText(" ");
                    }
                }
                if((currentLocation.getLatitude() >= 52.6208 && currentLocation.getLatitude() <= 52.6210) && (currentLocation.getLongitude() >= 1.2405 && currentLocation.getLongitude() <= 1.2407)){
                    if((angle >= 300 && angle <= 360) || angle <= 10){
                        text.setText("Lecture Theatres 1-4");
                    }
                    if(angle >= 120 && angle <= 170){
                        text.setText("Library");
                    }
                    if(angle >= 200 && angle <= 270){
                        text.setText("Norfolk & Suffolk Terrace");
                    }
                    else{
                        text.setText("");
                    }
                }
                if((currentLocation.getLatitude() >= 52.6216 && currentLocation.getLatitude() <= 52.6218) && (currentLocation.getLongitude() >= 1.2368 && currentLocation.getLongitude() <= 1.2370)){
                    if((angle >= 0 && angle <= 60) || (angle >= 330 && angle <= 360)){
                        text.setText("The Outside");
                    }
                    if(angle >= 200 && angle <= 270){
                        text.setText("Computers");
                    }
                    if(angle >= 75 && angle <= 100){
                        text.setText("More computers");
                    }
                    else{
                        text.setText("");
                    }
                }
                if((currentLocation.getLatitude() >= 52.6290 && currentLocation.getLatitude() <= 52.6300) && (currentLocation.getLongitude() >= 1.2378 && currentLocation.getLongitude() <= 1.2380)){
                    if(angle >= 30 && angle <= 60){
                        text.setText("Wardrobe");
                    }
                    if(angle >= 200 && angle <= 250){
                        text.setText("Computer");
                    }
                }
                //text.setText(String.valueOf(angle));
                //String value = currentLocation.getLatitude() + " " + currentLocation.getLongitude();
                //text2.setText(value);
                mHandler.postDelayed(this, 500); //this may be updating too frequently?
            }
        };
        mHandler.post(updateUI);
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View myInflatedView = inflater.inflate(R.layout.fragment_cameratest, container, false);
        return myInflatedView;
    }

    public void onViewCreated(final View view, Bundle savedInstanceState){
        if(view.findViewById(R.id.info) != null){
            view.findViewById(R.id.info).setOnClickListener(this);
        }
        if(view.findViewById(R.id.texture) != null){
            this.view = view.findViewById(R.id.texture);
            //camera.setmSurface((AutofitTextureView) view.findViewById(R.id.texture));
        }
        if(view.findViewById(R.id.Text) != null){
            text = (TextView) view.findViewById(R.id.Text);
        }
        if(view.findViewById(R.id.Text2) != null){
            text2 = (TextView) view.findViewById(R.id.Text2);
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
        gps.startUpdates();
    }

    public void onPause(){
        super.onPause();
        camera.closeCamera();
        compass.unregisterListener();
        gps.endUpdates();
    }

    public void onClick(View view){

    }
}
