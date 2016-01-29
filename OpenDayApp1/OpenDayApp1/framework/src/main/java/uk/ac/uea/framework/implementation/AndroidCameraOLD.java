package uk.ac.uea.framework.implementation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Size;
import android.view.Surface;
import android.widget.Toast;
import android.view.TextureView;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import uk.ac.uea.framework.Camera;

/**
 * Created by Jack L. Clements on 24/11/2015.
 */
@TargetApi(21)
public class AndroidCameraOLD extends Fragment implements Camera {
    //Fields
    //Manager
    //Surface
    //Device
    //Capture request (from template)
    //Capture Request Session
    //Submit a CaptureRequest to CaptureRequestSession (setReapeatingRequest())
    //Data comes back through CameraCaptureSession.CaptureCallback
    private Context con; //App context, used to access hardware
    private CameraManager mManager; //Manager class for all cameras on device
    private AutofitTextureView mSurface; //Display surface for preview, auto-scales
    private CameraDevice mDevice; //Device object (camera)
    private CaptureRequest.Builder mBuilder; //Request Builder
    private CaptureRequest mRequest; //Request
    private CameraCaptureSession mSession; //Session
    private Size cameraSize; //Size object for final photo
    private Handler mBackgroundHandler;
    private File mFile;
    private ImageReader mImageReader;

    //Fields passed in from main Java/XML
    private int deviceHeight; //height from hardware
    private int deviceWidth; //width from hardware

    /**
     * Singleton constructor, behaviour will be implemented in abstract factory {@Link AndroidCameraFactory}
     * @return a new AndroidCameraOLD
     */
    public static AndroidCameraOLD getInstance(){
        return new AndroidCameraOLD();
    }

    //Nested Classes
    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mDevice = cameraDevice;
            //createCameraPreview();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            cameraDevice.close();
            mDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            cameraDevice.close();
            mDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            //mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile));
        }

    };

    /**
     * Nested inner class used for defining behaviour in the frame. Not needed as we are not capturing photos
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback = new CameraCaptureSession.CaptureCallback() {
        private void process(CaptureResult result) {
            //Would need to process this normally, but as it's just a preview I think we're good.
        }
    };

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */
    private final CameraCaptureSession.StateCallback mPreviewCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
            // The camera is already closed
            if (null == mDevice) {
                return;
            }

            // When the session is ready, we start displaying the preview.
            mSession = cameraCaptureSession;
            try {
                // Auto focus should be continuous for camera preview.
                mBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // Flash is automatically enabled when necessary.
                mBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                // Finally, we start displaying the camera preview.
                mRequest = mBuilder.build();
                mSession.setRepeatingRequest(mRequest, mCaptureCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(
                @NonNull CameraCaptureSession cameraCaptureSession) {
            captureMessage("Failed");
        }
    };

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };


    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * Call this first - without a context you are buggered, puts context to class
     * @param context
     */
    public void setContext(Context context){
        con = context;
    }

    public void handlePermissions(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        //Still need to do this
    }

    /**
     * Collects all supported resolutions, picks smallest given size of screen and size of JPG
     * @param height
     * @param width
     */
    public void chooseBestCameraSize(int height, int width){
        try{
            // Collect the supported resolutions that are at least as big as the preview Surface
            CameraCharacteristics characteristics = mManager.getCameraCharacteristics(mDevice.getId());
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if(map == null){
                cameraSize = new Size(400, 800);
            }
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
            Size [] posSizes = map.getOutputSizes(SurfaceTexture.class);
            int h = largest.getHeight();
            int w = largest.getWidth();

            ArrayList<Size> sizes = new ArrayList<>();

            for(int i = 0; i < posSizes.length; i++){
                if(posSizes[i].getHeight() == posSizes[i].getWidth() * h / w && posSizes[i].getWidth() >= width && posSizes[i].getHeight() >= height){
                    sizes.add(posSizes[i]);
                }
            }
            if(!sizes.isEmpty()){
                cameraSize = Collections.min(sizes, new CompareSizesByArea());
            }

        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }
    }

    /**
     * Creates the camera preview
     */
    public void createCameraPreview(){
        try{
            SurfaceTexture texture = mSurface.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(mSurface.getWidth(), mSurface.getHeight());
            Surface surface = new Surface(texture);
            //Surface [] surfaces = new Surface[1];
            //surfaces[0] = surface;
            mBuilder = mDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mBuilder.addTarget(surface);
            // mDevice.createCaptureSession(Arrays.asList(surfaces), mPreviewCallback, null);
            // Here, we create a CameraCaptureSession for camera preview.
            mDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                mBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);

                                // Finally, we start displaying the camera preview.
                                mRequest = mBuilder.build();
                                mSession.setRepeatingRequest(mRequest, mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            //showToast("Failed");
                        }
                    }, null
            );
        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }


    }

    /**
     * Opens the camera on first use
     */
    public void openCamera(){
        mManager = (CameraManager) con.getSystemService(Context.CAMERA_SERVICE);
        try{
            String [] cameras = mManager.getCameraIdList();
            String backCamera = cameras[0];
            for(int i = 0; i < cameras.length; i++){
                if(mManager.getCameraCharacteristics(cameras[i]).get(CameraCharacteristics.LENS_FACING) == CameraCharacteristics.LENS_FACING_BACK){
                    backCamera = cameras[i];
                }
            }
            //chooseBestCameraSize(deviceWidth, deviceHeight);
            //mSurface.setAspectRatio(cameraSize.getHeight(), cameraSize.getWidth());
            mSurface = new AutofitTextureView(con);
            mSurface.setAspectRatio(400, 400);
            mManager.openCamera(backCamera, mStateCallback, null);
        }
        catch(CameraAccessException e){
            e.printStackTrace();
        }

    }

    /**
     * Closes the camera
     */
    public void closeCamera(){
        mDevice.close();
    }

    /**
     * Sets screen height
     * @param height
     */
    public void setHeight(int height){
        deviceHeight = height;
    }

    /**
     * Sets screen width
     * @param width
     */
    public void setWidth(int width){
        deviceWidth = width;
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

    public AutofitTextureView getSurface(){
        return mSurface;
    }

    public void setmSurface(AutofitTextureView view){
        mSurface = view;
    }

    public TextureView.SurfaceTextureListener getmSurfaceTextureListener(){
        return mSurfaceTextureListener;
    }
}
