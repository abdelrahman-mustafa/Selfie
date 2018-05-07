package com.indeves.selfieapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;

import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraActivity extends AppCompatActivity {
    //reverted
   // private  final String TAG = "CamiraActivity";
    private  final int RC_HANDLE_CAMERA_PERM = 2;
  //  private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    //private static final int SPAN_COUNT = 2;
    //public static List<CameraButtons> listOfImages = new ArrayList<>();
   // public static int imageSelectNum;
    //protected RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    //protected LayoutManagerType mCurrentLayoutManagerType;
    ImageID imageID = new ImageID();
    //ImageView imageView;
   // RelativeLayout lable;
  //  ImageButton capture;
    FaceGraphic mFaceGraphic;
  //  ImageButton getFrame;
    // permission request codes need to be < 256
   // String photo = "";
    //Camera.ShutterCallback shutterCallback;
    //int i = 0;
   // RecyclerView recyclerView;
    FaceDetector detector;
    //CameraAdaptor cameraAdaptor;
    private CameraSource mCameraSource = null;
  //  private FaceData mFaceData = new FaceData();
    @SuppressLint("UseSparseArrays")
    private Map<Integer, PointF> mPreviousLandmarkPositions = new HashMap<>();
    // As with facial landmarks, we keep track of the eye’s previous open/closed states
    // so that we can use them during those moments when they momentarily go undetected.
   /* private boolean mPreviousIsLeftEyeOpen = true;
    private boolean mPreviousIsRightEyeOpen = true;
   */ private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private Context context = CameraActivity.this;
    //FrameLayout linearLayout;
    private boolean mIsFrontFacing = true;
    //private FaceGraphic faceGraphic;
   // ProgressDialog progress;

    //FaceData faceData;
    //Face face;

    private View.OnClickListener mSwitchCameraButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            mIsFrontFacing = !mIsFrontFacing;


            if (mCameraSource != null) {
                mCameraSource.release();
                mCameraSource = null;
            }

            createCameraSource();
            startCameraSource();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        System.gc();
        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
       // RelativeLayout lable = findViewById(R.id.lable);
        GraphicOverlay.setContext(getApplicationContext());
        ImageView imageView = findViewById(R.id.image);
        imageView.setVisibility(View.GONE);
        //recyclerView = findViewById(R.id.rec);
        ImageButton getFrame = findViewById(R.id.get_frame);
        getFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CameraActivity.this, AddFraneActivity.class));
                finish();
            }
        });
//        recyclerView.setBackground(getDrawable(R.drawable.white));

//        linearLayout.setDrawingCacheEnabled(true);

        final ImageButton button = findViewById(R.id.flipButton);
        button.setOnClickListener(mSwitchCameraButtonListener);

        if (savedInstanceState != null) {
            mIsFrontFacing = savedInstanceState.getBoolean("IsFrontFacing");
        }

        // setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();

        } else {
            requestCameraPermission();
        }
        int position = getIntent().getIntExtra("position", 0);
        imageID.setId(position);
     /*   progress = new ProgressDialog(this);
        progress.setTitle("");
        progress.setMessage("Wait while Processing...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
      */ ImageButton  capture = findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {/*mCameraSource.release();*/
              //  captureImage();
//                Log.d("button", "detected");

              //  progress.show();

                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] bytes) {

                  /*   mCameraSource.release();
                        View v = lable.getRootView();
                        v.setDrawingCacheEnabled(true);
                        Bitmap b = v.getDrawingCache();
                        mCameraSource.release();
                        imageView.setImageBitmap(b);

                        imageView.setImageBitmap(emojifier.detecFaces(getApplicationContext(),flip(image2),imageID.getId(),mFaceData,null));*//**//**//**//*
*/
                        // Generate the Face Bitmap
                       // BitmapFactory.Options options = new BitmapFactory.Options();
                        Bitmap face = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, new BitmapFactory.Options());

                        // Generate the Eyes Overlay Bitmap
                        mPreview.setDrawingCacheEnabled(true);
                        //Bitmap overlay = mPreview.getDrawingCache();


                        Bitmap result = mergeBitmaps(flip(face), mPreview.getDrawingCache());
                     //   mPreview.setVisibility(View.GONE);
                       // imageView.setVisibility(View.VISIBLE);
                       // imageView.setImageBitmap(result);

                        String re = saveInInternalStorage(result);
                        Intent intent = new Intent(CameraActivity.this,ShareActivity.class);
                        intent.putExtra("image",re);
                      //  progress.dismiss();
                        startActivity(intent);
                        finish();


                    }

                });

            }
        });

    }

    private String  saveInInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        Date date = new Date();
        File mypath = new File(directory, String.valueOf(date.getDate()) +"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mypath.getAbsolutePath();
    }

    public Bitmap mergeBitmaps(Bitmap face, Bitmap overlay) {
        // Create a new image with target size
        int width = face.getWidth();
        int height = face.getHeight();
        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Rect faceRect = new Rect(0,0,width,height);
        Rect overlayRect = new Rect(0,0,overlay.getWidth(),overlay.getHeight()-350);

        // Draw face and then overlay (Make sure rects are as needed)
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(face, faceRect, faceRect, null);
        canvas.drawBitmap(overlay, overlayRect, faceRect, null);
        return newBitmap;
    }





/*
    private void captureImage() {
        mPreview.setDrawingCacheEnabled(true);
        final Bitmap drawingCache = mPreview.getDrawingCache();

        mCameraSource.takePicture( null, new CameraSource.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes) {
               // int orientation = Exif.getOrientation(bytes);
                Bitmap temp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
               // Bitmap picture = rotateImage(temp,orientation);
                Bitmap overlay = Bitmap.createBitmap(mGraphicOverlay.getWidth(),mGraphicOverlay.getHeight(),temp.getConfig());
                Canvas canvas = new Canvas(overlay);

                Matrix matrix = new Matrix();

                matrix.setScale((float)overlay.getWidth()/(float)temp.getWidth(),(float)overlay.getHeight()/(float)temp.getHeight());

                // mirror by inverting scale and translating
                matrix.preScale(-1, 1);
                matrix.postTranslate(canvas.getWidth(), 0);

                Paint paint = new Paint();
                canvas.drawBitmap(temp,matrix,paint);
                canvas.drawBitmap(drawingCache,0,0,paint);

                try {
                    String mainpath = getExternalStorageDirectory() + separator + "MaskIt" + separator + "images" + separator;
                    File basePath = new File(mainpath);
                    if (!basePath.exists())
                        Log.d("CAPTURE_BASE_PATH", basePath.mkdirs() ? "Success": "Failed");
                    String path = mainpath + "photo_"+ ".jpg";
                    File captureFile = new File(path);
                    captureFile.createNewFile();
                    if (!captureFile.exists())
                        Log.d("CAPTURE_FILE_PATH", captureFile.createNewFile() ? "Success": "Failed");
                    FileOutputStream stream = new FileOutputStream(captureFile);
                    overlay.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.flush();
                    stream.close();
                    temp.recycle();
                    drawingCache.recycle();
                    mPreview.setDrawingCacheEnabled(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
*/


   /* public  static class Exif {
        private  final String TAG = "CameraExif";

        // Returns the degrees in clockwise. Values are 0, 90, 180, or 270.
        public static int getOrientation(byte[] jpeg) {
            if (jpeg == null) {
                return 0;
            }

            int offset = 0;
            int length = 0;

            // ISO/IEC 10918-1:1993(E)
            while (offset + 3 < jpeg.length && (jpeg[offset++] & 0xFF) == 0xFF) {
                int marker = jpeg[offset] & 0xFF;

                // Check if the marker is a padding.
                if (marker == 0xFF) {
                    continue;
                }
                offset++;

                // Check if the marker is SOI or TEM.
                if (marker == 0xD8 || marker == 0x01) {
                    continue;
                }
                // Check if the marker is EOI or SOS.
                if (marker == 0xD9 || marker == 0xDA) {
                    break;
                }

                // Get the length and check if it is reasonable.
                length = pack(jpeg, offset, 2, false);
                if (length < 2 || offset + length > jpeg.length) {
                    Log.e("gggg", "Invalid length");
                    return 0;
                }

                // Break if the marker is EXIF in APP1.
                if (marker == 0xE1 && length >= 8 &&
                        pack(jpeg, offset + 2, 4, false) == 0x45786966 &&
                        pack(jpeg, offset + 6, 2, false) == 0) {
                    offset += 8;
                    length -= 8;
                    break;
                }

                // Skip other markers.
                offset += length;
                length = 0;
            }


            // JEITA CP-3451 Exif Version 2.2
            if (length > 8) {
                // Identify the byte order.
                int tag = pack(jpeg, offset, 4, false);
                if (tag != 0x49492A00 && tag != 0x4D4D002A) {
                    Log.e("gg", "Invalid byte order");
                    return 0;
                }
                boolean littleEndian = (tag == 0x49492A00);

                // Get the offset and check if it is reasonable.
                int count = pack(jpeg, offset + 4, 4, littleEndian) + 2;
                if (count < 10 || count > length) {
                    Log.e("gg", "Invalid offset");
                    return 0;
                }
                offset += count;
                length -= count;

                // Get the count and go through all the elements.
                count = pack(jpeg, offset - 2, 2, littleEndian);
                while (count-- > 0 && length >= 12) {
                    // Get the tag and check if it is orientation.
                    tag = pack(jpeg, offset, 2, littleEndian);
                    if (tag == 0x0112) {
                        // We do not really care about type and count, do we?
                        int orientation = pack(jpeg, offset + 8, 2, littleEndian);
                        switch (orientation) {
                            case 1:
                                return 0;
                            case 3:
                                return 3;
                            case 6:
                                return 6;
                            case 8:
                                return 8;
                        }
                        Log.i("gggg", "Unsupported orientation");
                        return 0;
                    }
                    offset += 12;
                    length -= 12;
                }
            }

            Log.i("ggg", "Orientation not found");
            return 0;
        }

        private static int pack(byte[] bytes, int offset, int length,
                                boolean littleEndian) {
            int step = 1;
            if (littleEndian) {
                offset += length - 1;
                step = -1;
            }

            int value = 0;
            while (length-- > 0) {
                value = (value << 8) | (bytes[offset] & 0xFF);
                offset += step;
            }
            return value;
        }
    }
    private Bitmap rotateImage(Bitmap bm, int i) {
        Matrix matrix = new Matrix();
        switch (i) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bm;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bm;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            bm.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

*/

    public static Bitmap flip(Bitmap src) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();

        matrix.preScale(-1.0f, 1.0f);

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
/*
    public Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }


    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF landmarkPosition = mPreviousLandmarkPositions.get(landmarkId);
        if (landmarkPosition == null) {
            return null;
        }

        float x = face.getPosition().x + (landmarkPosition.x * face.getWidth());
        float y = face.getPosition().y + (landmarkPosition.y * face.getHeight());
        return new PointF(x, y);
    }
*/


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }

    private void requestCameraPermission() {
        //Log.w(TAG, "Camera permission not acquired. Requesting permission.");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions, RC_HANDLE_CAMERA_PERM);
            }
        };
        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
           // Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // We have permission to access the camera, so create the camera source.
           // Log.d(TAG, "Camera permission granted - initializing camera source.");
            createCameraSource();
            return;
        }

        // If we've reached this part of the method, it means that the user hasn't granted the app
        // access to the camera. Notify the user and exit./*
      /*  Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));*//*
     */   DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name)
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.disappointed_ok, listener)
                .show();
    }

    public List<CameraButtons> addImage() {
        List<CameraButtons> list = new ArrayList<>();
        CameraButtons cameraButtons0 = new CameraButtons();
        cameraButtons0.setImagePath(R.drawable.frown);
        list.add(cameraButtons0);
        CameraButtons cameraButtons1 = new CameraButtons();
        cameraButtons1.setImagePath(R.drawable.tarposh);
        list.add(cameraButtons1);

        CameraButtons cameraButtons2 = new CameraButtons();
        cameraButtons2.setImagePath(R.drawable.asset2);
        list.add(cameraButtons2);
        CameraButtons cameraButtons3 = new CameraButtons();
        cameraButtons3.setImagePath(R.drawable.borneta);
        list.add(cameraButtons3);
        CameraButtons cameraButtons4 = new CameraButtons();
        cameraButtons4.setImagePath(R.drawable.tartor);
        list.add(cameraButtons4);
        CameraButtons cameraButtons6 = new CameraButtons();
        cameraButtons6.setImagePath(R.drawable.asset1);
        list.add(cameraButtons6);
        CameraButtons cameraButtons5 = new CameraButtons();
        cameraButtons5.setImagePath(R.drawable.oaaal);
        list.add(cameraButtons5);


        CameraButtons cameraButtons7 = new CameraButtons();
        cameraButtons7.setImagePath(R.drawable.shanb2);
        list.add(cameraButtons7);
        CameraButtons cameraButtons8 = new CameraButtons();
        cameraButtons8.setImagePath(R.drawable.shanb);
        list.add(cameraButtons8);

        CameraButtons cameraButtons9 = new CameraButtons();
        cameraButtons9.setImagePath(R.drawable.asset);
        list.add(cameraButtons9);
        CameraButtons cameraButtons10 = new CameraButtons();
        cameraButtons10.setImagePath(R.drawable.fionka);
        list.add(cameraButtons10);
        CameraButtons cameraButtons11 = new CameraButtons();
        cameraButtons11.setImagePath(R.drawable.asset5);
        list.add(cameraButtons11);
        return list;
    }

    // Face detection creation
    private void createCameraSource() {
        imageID.setId(0);

        createDetection();
// open the camera front ot back


        int facing = CameraSource.CAMERA_FACING_FRONT;
        if (!mIsFrontFacing) {
            facing = CameraSource.CAMERA_FACING_BACK;
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setFacing(facing)
                .setRequestedPreviewSize(1280, 960)
                .setRequestedFps(30.0f)
                .build();

    }

    private void createDetection() {
        Context context = getApplicationContext();
        detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();


        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
           // Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check the device's storage.  If there's little available storage, the native
            // face detection library will not be downloaded, and the app won't work,
            // so notify the user.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                //Log.w(TAG, getString(R.string.low_storage_error));
                DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name)
                        .setMessage(R.string.low_storage_error)
                        .setPositiveButton(R.string.disappointed_ok, listener)
                        .show();
            }
        }
    }
/*

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }
*/

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();

        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        mPreview.stop();
    }

    // Build Camera source which connect face detector with camera preview
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
          //  int RC_HANDLE_GMS = 9001;
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, 9001);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
              //  Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }





    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {

        // take action just detect any face

        @Override
        public Tracker<Face> create(Face face) {
/*
            final CameraSource.PictureCallback callbackPicture = new CameraSource.PictureCallback() {
                public void onPictureTaken(byte[] data) {
                    x = data;
                    Toast.makeText(getApplicationContext(), "Face detected", Toast.LENGTH_LONG).show();
                    mCameraSource.stop();

                }
            };
            mCameraSource.takePicture(null, callbackPicture);*/


            return new GraphicFaceTracker(mGraphicOverlay, context);

        }
    }

    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class GraphicFaceTracker extends Tracker<Face> {
        //private FaceGraphic mFaceGraphic;
        Context mContext = CameraActivity.this;
        private GraphicOverlay mOverlay;

        GraphicFaceTracker(GraphicOverlay overlay, Context context) {
            this.mContext = context;
           // Log.isLoggable("Hi", imageID.getId());
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay, context, imageID.getId(), mIsFrontFacing);

        }


        // Start tracking the detected face instance within the face overlay.
        @Override
        public void onNewItem(int faceId, Face item) {
           // mFaceGraphic.setId(faceId);
        }


        //Update the position/characteristics of the face within the overlay.
        //Take action on face detected

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
           //  boolean mPreviousIsLeftEyeOpen = true;
            // boolean mPreviousIsRightEyeOpen = true;
            FaceData mFaceData = new FaceData();
            updatePreviousLandmarkPositions(face);

            // Get face dimensions.
            mFaceData.setPosition(face.getPosition());
            mFaceData.setWidth(face.getWidth());
            mFaceData.setHeight(face.getHeight());

            // Get head angles.
            mFaceData.setEulerY(face.getEulerY());
            mFaceData.setEulerZ(face.getEulerZ());

            // Get the positions of facial landmarks.
            mFaceData.setLeftEyePosition(getLandmarkPosition(face, Landmark.LEFT_EYE));
            mFaceData.setRightEyePosition(getLandmarkPosition(face, Landmark.RIGHT_EYE));
            mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_CHEEK));
            mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_CHEEK));
            mFaceData.setNoseBasePosition(getLandmarkPosition(face, Landmark.NOSE_BASE));
            mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_EAR));
            mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.LEFT_EAR_TIP));
            mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_EAR));
            mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.RIGHT_EAR_TIP));
            mFaceData.setMouthLeftPosition(getLandmarkPosition(face, Landmark.LEFT_MOUTH));
            mFaceData.setMouthBottomPosition(getLandmarkPosition(face, Landmark.BOTTOM_MOUTH));
            mFaceData.setMouthRightPosition(getLandmarkPosition(face, Landmark.RIGHT_MOUTH));

            // Determine if eyes are open.
            final float EYE_CLOSED_THRESHOLD = 0.4f;
            float leftOpenScore = face.getIsLeftEyeOpenProbability();
            if (leftOpenScore == Face.UNCOMPUTED_PROBABILITY) {
                mFaceData.setLeftEyeOpen(true);
            } else {
                mFaceData.setLeftEyeOpen(leftOpenScore > EYE_CLOSED_THRESHOLD);
            }
            float rightOpenScore = face.getIsRightEyeOpenProbability();
            if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
                mFaceData.setRightEyeOpen(true);
            } else {
                mFaceData.setRightEyeOpen(rightOpenScore > EYE_CLOSED_THRESHOLD);
            }

            // See if there's a smile!
            // Determine if person is smiling.
           // final float SMILING_THRESHOLD = 0.8f;
            mFaceData.setSmiling(face.getIsSmilingProbability() > 0.8f);
            mFaceGraphic.update(mFaceData);
            mFaceGraphic.updateFace(face, imageID.getId());

        }

        private PointF getLandmarkPosition(Face face, int landmarkId) {
            for (Landmark landmark : face.getLandmarks()) {
                if (landmark.getType() == landmarkId) {
                    return landmark.getPosition();
                }
            }

            PointF landmarkPosition = mPreviousLandmarkPositions.get(landmarkId);
            if (landmarkPosition == null) {
                return null;
            }

            float x = face.getPosition().x + (landmarkPosition.x * face.getWidth());
            float y = face.getPosition().y + (landmarkPosition.y * face.getHeight());
            return new PointF(x, y);
        }

        private void updatePreviousLandmarkPositions(Face face) {
            for (Landmark landmark : face.getLandmarks()) {
                PointF position = landmark.getPosition();
                float xProp = (position.x - face.getPosition().x) / face.getWidth();
                float yProp = (position.y - face.getPosition().y) / face.getHeight();
                mPreviousLandmarkPositions.put(landmark.getType(), new PointF(xProp, yProp));
            }
        }


        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }


        @Override
        public void onDone() {
            //   mOverlay.remove(mFaceGraphic);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CameraActivity.this, StartHome.class));
    }


}

