package uk.ac.uea.framework.implementation;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Toast;

import uk.ac.uea.framework.R;
import uk.ac.uea.framework.implementation.AutofitTextureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.uea.framework.Camera;

/**
 * Created by Jack L. Clements on 24/11/2015.
 */
@TargetApi(21)
public class AndroidCamera implements Camera {
    //Fields

    //FINAL FIELDS - easy enum values
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_WAITING_LOCK = 1;
    private static final int STATE_WAITING_PRECAPTURE = 2;
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;
    private static final int STATE_PICTURE_TAKEN = 4;


    //Fields
    CameraManager manager;
    String cameraId;
    CameraDevice device;
    CameraCharacteristics cameraInfo;
    CameraCaptureSession session;
    CaptureRequest mPreviewRequest;
    int state;
    Activity activity;

    //For preview texture
    AutofitTextureView preview;

    //Thread stuff
    Handler mBackgroundHandler;

    //File capture
    ImageReader mImageReader;
    File file;

    //Builder classes
    CaptureRequest.Builder mPreviewRequestBuilder;


    //Inner class methods
    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}. //Should be attached to the texture at some point
     */
    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(height, width); //NOTE TO SELF - test ths when you get home, I think it might be wrong?
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width, height);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback(){

        @Override
        public void onOpened(@NonNull CameraDevice cDevice){
            //release camera lock
            device = cDevice; //set camera
            createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cDevice){
            //release lock
            cDevice.close();
            device = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cDevice, int error){
            //release lock
            cDevice.close();
            device = null;
            //Activity activity = getActivity();
            if(activity != null){
                activity.finish();
            }
        }
    };

    private final ImageReader.OnImageAvailableListener mOnAvailableListener = new ImageReader.OnImageAvailableListener(){
        @Override
        public void onImageAvailable(ImageReader reader){
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), file));
        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback(){
        public void process(CaptureResult result){
            switch(state){
                case STATE_PREVIEW:
                    break;
                case STATE_WAITING_LOCK:
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if(afState == null){
                        //capture image
                    }

            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureResult partialResult){
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result){
            process(result);
        }
    };

    /**
     * Static nested class
     */
    private static class ImageSaver implements Runnable {
        private final Image image;
        private final File tempFile;

        public ImageSaver(Image nImage, File file){
            image = nImage;
            tempFile = file;
        }
        @Override
        public void run(){
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try{
                output = new FileOutputStream(tempFile);
                output.write(bytes);
            }
            catch(IOException e){
                e.printStackTrace();
            }
            image.close();
            if(output != null){
                try{
                    output.close();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }



    //Methods

    //FIRST SET UP MANAGER
    //THEN CAMERA CONTEXT


    /**
     * Attaches preview texture to the parameter, I will update this javadoc later, we then need to set the listener
     * @param texture
     */
    public void setPreview(AutofitTextureView texture){
        System.out.println("TEXTURE SET");
        preview = texture;
    }

    //capture request, should use ImageReader
    public void capture(){

    }


    //private method needed to request camera, this shouldn't be public, if used out of scope it could be a DISASTER
    public void handlePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){

    }

    //Actual methods for the program
    public void chooseBestCameraSize(int height, int width){

    }

    public void createCameraPreview(){
        try{
            SurfaceTexture texture = preview.getSurfaceTexture();
            assert texture != null;
            if(!textureViewStatus()){
                setSurfaceTextureListener();
            }
            //must set up surface texture listener
            texture.setDefaultBufferSize(1920, 1080);
            Surface surface = new Surface(texture);
            //Set up CaptureRequest.Builder
            mPreviewRequestBuilder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            //Create capture session
            device.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                //On Configured
                @Override
                public void onConfigured(@NonNull CameraCaptureSession captureSession){
                    //if camera is already closed
                    if(device == null){
                        return;
                    }

                    //display preview
                    session = captureSession;
                    try{
                        mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                        mPreviewRequest = mPreviewRequestBuilder.build();
                        session.setRepeatingRequest(mPreviewRequest, mCaptureCallback, mBackgroundHandler);
                    }
                    catch(CameraAccessException e){
                        e.printStackTrace();
                    }

                }
                //On Configure Failed
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession){
                    //show failed msg.
                }
                //

            }, null);
        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    public void openCamera(){

    }

    public void openCamera(int height, int width){

        //if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //request camera access
          //  return; //good design?
        //}
        //get camera context, from cameramanager
        cameraSetUp();
        //configure transform matrix, set to mTextureView
        configureTransform(width, height);

        //Open the camera
        //Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try{
            //to open the camera via the manager
            //if(!true){
            //    throw new RuntimeException("Time out waiting to lock camera opening.");
            //}
            manager.openCamera(cameraId, mStateCallback, mBackgroundHandler);
        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }
        //catch(InterruptedException e){
        //    throw new RuntimeException("Interrupted while trying to lock camera opening", e);
        //}
    }

    public void cameraSetUp(){
        //Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try{
            String [] cameras = manager.getCameraIdList();
            for(int i = 0; i < cameras.length; i++){
                CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameras[i]);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING); //used for not null operation
                if(facing != null && facing != CameraCharacteristics.LENS_FACING_FRONT){
                    cameraId = cameras[i];
                }
            }
            //Get max size
            //set up image reader
            //set up listener for readermImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
            mImageReader = ImageReader.newInstance(1920, 1080, ImageFormat.JPEG, 2);
            mImageReader.setOnImageAvailableListener(mOnAvailableListener, mBackgroundHandler);

        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }
        catch(NullPointerException e){
            e.printStackTrace();
        }
    }

    public void configureTransform(int width, int height){
        //Activity activity = getActivity();
        //checking for null? May be needed
        Matrix matrix = new Matrix();
        RectF viewR = new RectF(0, 0, width, height);
        RectF bufferR = new RectF(0, 0, 1920, 1080); //may want to change to optimal size
        float centerX = viewR.centerX();
        float centerY = viewR.centerY();
        //scale
        bufferR.offset(centerX - bufferR.centerX(), centerY - bufferR.centerY());
        matrix.setRectToRect(viewR, bufferR, Matrix.ScaleToFit.FILL);
        float scale = Math.max((float) height / 1080, (float) width / 1920);
        matrix.postScale(scale, scale, centerX, centerY);
        preview.setTransform(matrix);
    }

    public void closeCamera(){
        if(session != null){
            session.close();
            session = null;
            device.close();
        }
    }

    public void captureMessage(String string){

    }

    public boolean textureViewStatus(){
        System.out.println("PREVIEW STATUS " + preview.isAvailable());
        if(preview.isAvailable()){
            return true;
        }
        else{
            return false;
        }
    }

    public void addActivity(Activity nActivity){
        activity = nActivity;
    }

    public void setSurfaceTextureListener(){
        preview.setSurfaceTextureListener(mSurfaceTextureListener);
    }
}
