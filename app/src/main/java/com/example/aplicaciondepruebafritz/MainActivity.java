package com.example.aplicaciondepruebafritz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.LruCache;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ai.fritz.core.Fritz;
import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionModels;
import ai.fritz.vision.FritzVisionOrientation;
import ai.fritz.vision.ImageOrientation;
import ai.fritz.vision.ImageRotation;
import ai.fritz.vision.ModelVariant;
import ai.fritz.vision.poseestimation.FritzVisionPosePredictor;
import ai.fritz.vision.poseestimation.FritzVisionPosePredictorOptions;
import ai.fritz.vision.poseestimation.FritzVisionPoseResult;
import ai.fritz.vision.poseestimation.HumanSkeleton;
import ai.fritz.vision.poseestimation.Pose;
import ai.fritz.vision.poseestimation.PoseOnDeviceModel;

public class MainActivity extends AppCompatActivity {

    private FritzVisionPosePredictor posePredictor;
    private FritzVisionPoseResult poseResult;

    Context con;
    ImageView imageView;

    ImageView imageViewSuper;

    // A sensitivity parameters we'll use later.
    private float minPoseThreshold = 0.6f;

    List<Pose> arrayPose;

   public Bitmap posesOnImage;
    ImageOrientation imageRotation;

    File pictureFile;
    Bitmap bit;

    //Camera

    /*
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera camera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;
     */


    //Camara2
    /*
    //private static final String TAG = "CameraPreview";


     // Id of the camera to access. 0 is the first camera.

    private static final Integer CAMERA_ID = 0;

    private CameraPreview mPreview;
    private Camera mCamera;


    private CameraManager cam;

    public ImageOrientation imageRotationFromCamera;
    */

    //Camara3
    private static final String TAG = "AndroidCameraApi";
    private Button takePictureButton;
    private TextureView textureView;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    private String cameraId;
    protected CameraDevice cameraDevice;
    protected CameraCaptureSession cameraCaptureSessions;
    protected CaptureRequest captureRequest;
    protected CaptureRequest.Builder captureRequestBuilder;
    private Size imageDimension;
    private ImageReader imageReader;
    private File file;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private boolean mFlashSupported;
    private Handler mBackgroundHandler;
    private HandlerThread mBackgroundThread;
    private Bitmap bitmap;

    private LruCache<String, Bitmap> mMemoryCache;


    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

    // Use 1/8th of the available memory for this memory cache.
    final int cacheSize = maxMemory / 8;

    private static MainActivity instance;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Camera
       /* preview=(SurfaceView)findViewById(R.id.preview);
        previewHolder=preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        */

        con = getApplication();


        Fritz.configure(this, "4f1d35d761a24d328e07e0014c1cd515");

        imageView = this.findViewById(R.id.txtImg);
      //  imageViewSuper= this.findViewById(R.id.imageViewSuperpuesta);

        // For accurate
        PoseOnDeviceModel onDeviceModel = FritzVisionModels.getHumanPoseEstimationOnDeviceModel(ModelVariant.ACCURATE);
        posePredictor = FritzVision.PoseEstimation.getPredictor(onDeviceModel);


        //bit = getBitmapFromURL("https://firebasestorage.googleapis.com/v0/b/proyecto-final-637d2.appspot.com/o/Calor%2FSaltos-sin-cuerda.gif?alt=media&token=87755abf-de08-4735-a222-49e954e164f0");

      /*  bit = BitmapFactory.decodeResource(this.getResources(), R.drawable.mio6);
        FritzVisionImage visionImage = FritzVisionImage.fromBitmap(bit);
        poseResult = posePredictor.predict(visionImage);
        arrayPose = poseResult.getPoses();
        Log.d("resultadoArray", String.valueOf(poseResult));
        Bitmap posesOnImage = visionImage.overlaySkeletons(arrayPose);
        //imageView.setImageBitmap(posesOnImage);

       */

        // Draw the pose to the canvas.
       /* Canvas canvas = new Canvas(posesOnImage);
        for (Pose pose : arrayPose) {
            pose.draw(canvas);
        }
        */



/*
        Intent callingIntent = getIntent();
        // Set some sensitivity options
        FritzVisionPosePredictorOptions options = new FritzVisionPosePredictorOptions();
        options.minPoseThreshold= minPoseThreshold;
        options.buildInterpreterOptions();


        // Initialize the model and predictor.


        PoseOnDeviceModel poseEstimationOnDeviceModel = PoseOnDeviceModel.buildFromModelConfigFile("pose_recording_model.json", new HumanSkeleton());
        posePredictor = FritzVision.PoseEstimation.getPredictor(poseEstimationOnDeviceModel);
        // The rest of your onCreate function...


        bit = getBitmapFromURL("https://firebasestorage.googleapis.com/v0/b/proyecto-final-637d2.appspot.com/o/Calor%2FSaltos-sin-cuerda.gif?alt=media&token=87755abf-de08-4735-a222-49e954e164f0");

        storeImage(bit);

        Image m = leer();

        onImageAvailable();
        */


//Camara2
        /*

        // Open an instance of the first camera and retrieve its info.
        mCamera = getCameraInstance(CAMERA_ID);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(CAMERA_ID, cameraInfo);


        if (mCamera == null) {
// Camera is not available, display error message
            Toast.makeText(this, "Camera is not available.", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_main);
        } else {

            setContentView(R.layout.activity_main);

// Get the rotation of the screen to adjust the preview image accordingly.
            final int displayRotation = getWindowManager().getDefaultDisplay()
                    .getRotation();

// Create the Preview view and set it as the content of this Activity.
            mPreview = new CameraPreview(this, mCamera, cameraInfo, displayRotation);
            FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
            preview.addView(mPreview);
        }
 */



        //Camara3
        textureView = (TextureView) findViewById(R.id.texture);
        assert textureView != null;
        textureView.setSurfaceTextureListener(textureListener);
        takePictureButton = (Button) findViewById(R.id.btn_takepicture);
        assert takePictureButton != null;
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        instance = this;

    }



    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
/*
    public void onImageAvailable(final ImageReader reader) {
        // The FritzVisionImage class makes it easy to manipulate images used as model inputs.
        Image image = reader.acquireLatestImage();
        final FritzVisionImage fritzVisionImage = FritzVisionImage.fromMediaImage(image, imageRotation);
        image.close();

        // Run the model to find poses in the image.
        poseResult = posePredictor.predict(fritzVisionImage);
        // Draw the result.

        arrayPose = poseResult.getPosesByThreshold(minPoseThreshold);





    }

 */

    private void storeImage(Bitmap image) {
        pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TAG",
                    "Error while creating media file, Please ask for storage permission");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
    }

    private File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");


        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

   /* public Image leer () {
        Image m;

        FileInputStream f = new FileInputStream(pictureFile);

        m = (Image)f.read();

        return m;
    }

    */




   //Camera
  /* @Override
   public void onResume() {
       super.onResume();
       try {
               camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);

       } catch (Exception e) {
           Log.e(getString(R.string.app_name), "failed to open Camera");
           e.printStackTrace();
       }

       camera=Camera.open();
       startPreview();
   }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera=null;
        inPreview=false;

        super.onPause();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width<=width && size.height<=height) {
                if (result==null) {
                    result=size;
                }
                else {
                    int resultArea=result.width*result.height;
                    int newArea=size.width*size.height;

                    if (newArea>resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private void initPreview(int width, int height) {
        if (camera!=null && previewHolder.getSurface()!=null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("PreviewDemo",
                        "Exception in setPreviewDisplay()"+ t.toString());
                Toast
                        .makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG)
                        .show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters=camera.getParameters();
                Camera.Size size=getBestPreviewSize(width, height,
                        parameters);

                if (size!=null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured=true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera!=null) {
            camera.startPreview();
            inPreview=true;
        }
    }

    SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder,
                                   int format, int width,
                                   int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };


   */


  //Camera2
    /*
  @Override
  public void onPause() {
      super.onPause();
// Stop camera access
      releaseCamera();
  }


    // A safe way to get an instance of the Camera object.
    private Camera getCameraInstance(int cameraId) {
        Camera c = null;
        try {
            c = Camera.open(cameraId); // attempt to get a Camera instance
        } catch (Exception e) {
// Camera is not available (in use or does not exist)
            Toast.makeText(this, "Camera " + cameraId + " is not available: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        return c; // returns null if camera is unavailable
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    public void Capturar () {
        // Get the system service for the camera manager
        final CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

// Gets the first camera id

// Determine the rotation on the FritzVisionImage from the camera orientaion and the device rotation.
// "this" refers to the calling Context (Application, Activity, etc)









    }


    @Override
    public void onImageAvailable(ImageReader reader) {
        String cameraId =  CAMERA_ID.toString();
        ImageOrientation imageRotationFromCamera = FritzVisionOrientation.getImageOrientationFromCamera(this, cameraId);
        Image image = reader.acquireLatestImage();
        FritzVisionImage visionImage = FritzVisionImage.fromMediaImage(image, imageRotationFromCamera);

    }
*/




    //Camera3
    TextureView.SurfaceTextureListener textureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            //open your camera here
            openCamera();
        }
        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
            // Transform you image captured size according to the surface width and height
        }
        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }
        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    };
    private final CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            //This is called when the camera is open
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }
        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }
        @Override
        public void onError(CameraDevice camera, int error) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };
    final CameraCaptureSession.CaptureCallback captureCallbackListener = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
            createCameraPreview();
        }
    };
    protected void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("Camera Background");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }
    protected void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    protected void takePicture() {
        if(null == cameraDevice) {
            Log.e(TAG, "cameraDevice is null");
            return;
        }
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if (characteristics != null) {
                jpegSizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP).getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }
            ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<Surface>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            captureBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            final File file = new File(Environment.getExternalStorageDirectory()+"/pic.jpg");
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    Image image = null;
                    CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                    try {
                        image = reader.acquireLatestImage();

                        String cameraId = manager.getCameraIdList()[0];
                        ImageOrientation imageRotationFromCamera = FritzVisionOrientation.getImageOrientationFromCamera(MainActivity.this, cameraId);
                        FritzVisionImage visionImage = FritzVisionImage.fromMediaImage(image,imageRotationFromCamera);
                        poseResult = posePredictor.predict(visionImage);
                        arrayPose = poseResult.getPoses();
                        Log.d("resultadoArray", String.valueOf(poseResult));
                        posesOnImage = visionImage.overlaySkeletons(arrayPose);
/*
                        bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
                        Canvas can = new Canvas(bitmap);
                        for (Pose pose : arrayPose) {
                            pose.draw(can);
                        }

                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.RED);
                        can.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, 100, paint);
*/

                        //dibujarPose(posesOnImage);

                       // imageView.setImageBitmap(posesOnImage);
                     //   textureView.setVisibility(View.GONE);
                     //   takePictureButton.setVisibility(View.GONE);


                      //  ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                       // byte[] bytes = new byte[buffer.capacity()];
                       // buffer.get(bytes);
                        //save(bytes);



                        //} catch (FileNotFoundException e) {
                       // e.printStackTrace();
                        // } catch (IOException e) {
                       // e.printStackTrace();
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                            dibujarPose(posesOnImage);

                        }
                    }

                }



                private void save(byte[] bytes) throws IOException {
                    OutputStream output = null;
                    try {
                        output = new FileOutputStream(file);
                        output.write(bytes);
                    } finally {
                        if (null != output) {
                            output.close();
                        }
                    }
                }
            };

            reader.setOnImageAvailableListener(readerListener, mBackgroundHandler);

            final CameraCaptureSession.CaptureCallback captureListener = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(MainActivity.this, "Saved:" + file, Toast.LENGTH_SHORT).show();
                    createCameraPreview();
                }
            };
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener, mBackgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                }
            }, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    protected void createCameraPreview() {
        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            texture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
            Surface surface = new Surface(texture);
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(surface);
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback(){
                @Override
                public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                    //The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }
                    // When the session is ready, we start displaying the preview.
                    cameraCaptureSessions = cameraCaptureSession;
                    updatePreview();
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
                    Toast.makeText(MainActivity.this, "Configuration change", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void openCamera() {
        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        Log.e(TAG, "is camera open");
        try {
            cameraId = manager.getCameraIdList()[0];
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];
            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            manager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "openCamera X");
    }
    protected void updatePreview() {
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        try {
            cameraCaptureSessions.setRepeatingRequest(captureRequestBuilder.build(), null, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
    private void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        startBackgroundThread();
        if (textureView.isAvailable()) {
            openCamera();
        } else {
            textureView.setSurfaceTextureListener(textureListener);
        }
    }
    @Override
    protected void onPause() {
        Log.e(TAG, "onPause");
        //closeCamera();
        stopBackgroundThread();
        super.onPause();
    }



    public void dibujarPose(Bitmap dibujo){
       // imageView.setImageBitmap(dibujo);
        //textureView.setVisibility(View.GONE);
        //takePictureButton.setVisibility(View.GONE);

/*
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        dibujo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        Bundle b = new Bundle();
        b.putByteArray("Foto",byteArray);


 */

        addBitmapToMemoryCache("FotoMarcada", dibujo);

        Intent i = new Intent(this, MostrarFoto.class);


      //  i.putExtras(b);
        startActivity(i);
    }

    public void dibujarEnCamara(Bitmap bit){
       // imageViewSuper.setBackgroundColor(Color.TRANSPARENT);
        imageViewSuper.setImageDrawable(new BitmapDrawable(getResources(), bit));
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    public static MainActivity getInstance() {
        return instance;
    }

}




