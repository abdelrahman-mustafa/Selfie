package com.indeves.selfieapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CameraActivity extends AppCompatActivity {
    //reverted
    private static final String TAG = "CamiraActivity";
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;
    public static List<CameraButtons> listOfImages = new ArrayList<>();
    public static int imageSelectNum;
    protected RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    protected LayoutManagerType mCurrentLayoutManagerType;
    ImageID imageID = new ImageID();
    ImageView imageView;
    ImageButton capture;
    FaceGraphic mFaceGraphic;
    // permission request codes need to be < 256
    String photo = "";
    int i = 0;
    RecyclerView recyclerView;
    FaceDetector detector;
    CameraAdaptor cameraAdaptor;
    private CameraSource mCameraSource = null;
    private FaceData mFaceData = new FaceData();
    private Map<Integer, PointF> mPreviousLandmarkPositions = new HashMap<>();
    // As with facial landmarks, we keep track of the eye’s previous open/closed states
    // so that we can use them during those moments when they momentarily go undetected.
    private boolean mPreviousIsLeftEyeOpen = true;
    private boolean mPreviousIsRightEyeOpen = true;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private Context context = CameraActivity.this;
    LinearLayout linearLayout;
    private boolean mIsFrontFacing = true;
    private FaceGraphic faceGraphic;
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

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);

        GraphicOverlay.setContext(getApplicationContext());
        imageView = findViewById(R.id.image);
        imageView.setVisibility(View.GONE);
        linearLayout = findViewById(R.id.linear);
        recyclerView = findViewById(R.id.rec);
        recyclerView.setBackground(getDrawable(R.drawable.white));
        Drawable background = recyclerView.getBackground();
        background.setAlpha(80);

        linearLayout.setDrawingCacheEnabled(true);

        final ImageButton button = (ImageButton) findViewById(R.id.flipButton);
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
        capture = findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("button", "detected");

                mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] bytes) {

                        Bitmap image1 = Bitmap.createBitmap(linearLayout.getDrawingCache());
                        mCameraSource.release();
                        Bitmap image2 = Bitmap.createBitmap(linearLayout.getDrawingCache());
                        imageView.setImageBitmap(image2);
                        // imageView.setVisibility(View.VISIBLE);

/*

                        Bitmap image2 = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image = Emojifier.detecFaces(CameraActivity.this,image,i,mFaceData,mFaceGraphic.getCanvasSel());
                        mPreview.setVisibility(View.GONE);
                        Matrix matrix = new Matrix();
                        float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
                        Matrix matrixMirrorY = new Matrix();
                        matrixMirrorY.setValues(mirrorY);
                        matrix.postConcat(matrixMirrorY);
//                        image2 = Bitmap.createBitmap(image, 0, 0, image2.getWidth(), image2.getHeight(), matrix, true);
                        Bitmap bmOverlay = Bitmap.createBitmap(image2.getWidth(), image.getHeight(), image.getConfig());
                        Bitmap mutableBitmap = image2.copy(Bitmap.Config.ARGB_8888, true);
                        Bitmap mutableBitmap2 = image.copy(Bitmap.Config.ARGB_8888, true);

                        Canvas canvas = new Canvas(mutableBitmap);
                        canvas.drawBitmap(mutableBitmap2, new Matrix(), null);
                        canvas.drawBitmap(mutableBitmap, 0, 0, null);

                        //imageView.setImageDrawable(new BitmapDrawable(getResources(), bmOverlay));
                        imageView.setImageBitmap(mutableBitmap);
                        imageView.setVisibility(View.VISIBLE);
*/


                    }
                });


            }
        }); // take image function together with later main processing part.
        cameraAdaptor = new CameraAdaptor(addImage(), CameraActivity.this);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(CameraActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(cameraAdaptor);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        i = position;
                        imageID.setId(position);
                    }
                })
        );

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("IsFrontFacing", mIsFrontFacing);
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission not acquired. Requesting permission.");

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
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // We have permission to access the camera, so create the camera source.
            Log.d(TAG, "Camera permission granted - initializing camera source.");
            createCameraSource();
            return;
        }

        // If we've reached this part of the method, it means that the user hasn't granted the app
        // access to the camera. Notify the user and exit.
        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
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
        List<CameraButtons> list = new ArrayList<CameraButtons>();
        CameraButtons cameraButtons0 = new CameraButtons();
        cameraButtons0.setImagePath(R.drawable.frown);
        list.add(cameraButtons0);
        CameraButtons cameraButtons6 = new CameraButtons();
        cameraButtons6.setImagePath(R.drawable.tarposh);
        list.add(cameraButtons6);
        CameraButtons cameraButtons = new CameraButtons();
        cameraButtons.setImagePath(R.drawable.borneta);
        list.add(cameraButtons);
        CameraButtons cameraButtons1 = new CameraButtons();
        cameraButtons1.setImagePath(R.drawable.tartor);
        list.add(cameraButtons1);
        CameraButtons cameraButtons2 = new CameraButtons();
        cameraButtons2.setImagePath(R.drawable.oaaal);
        list.add(cameraButtons2);
        CameraButtons cameraButtons3 = new CameraButtons();
        cameraButtons3.setImagePath(R.drawable.shanb2);
        list.add(cameraButtons3);
        CameraButtons cameraButtons4 = new CameraButtons();
        cameraButtons4.setImagePath(R.drawable.shanb);
        list.add(cameraButtons4);
        CameraButtons cameraButtons5 = new CameraButtons();
        cameraButtons5.setImagePath(R.drawable.fionka);
        list.add(cameraButtons5);


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
                .setRequestedPreviewSize(320, 240)
                .setRequestedFps(30.0f)
                .setAutoFocusEnabled(false)
                .build();


    }

    private void createDetection() {
        Context context = getApplicationContext();
        detector = new FaceDetector.Builder(context)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setTrackingEnabled(false)
                .setMode(FaceDetector.FAST_MODE)
                .setProminentFaceOnly(mIsFrontFacing)
                .setMinFaceSize(mIsFrontFacing ? 0.35f : 0.15f)
                .build();


        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");

            // Check the device's storage.  If there's little available storage, the native
            // face detection library will not be downloaded, and the app won't work,
            // so notify the user.
            IntentFilter lowStorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowStorageFilter) != null;

            if (hasLowStorage) {
                Log.w(TAG, getString(R.string.low_storage_error));
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
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition = ((LinearLayoutManager) recyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition();
        }

        switch (layoutManagerType) {
            case GRID_LAYOUT_MANAGER:
                mLayoutManager = new GridLayoutManager(getBaseContext(), SPAN_COUNT);
                mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                break;
            case LINEAR_LAYOUT_MANAGER:
                mLayoutManager = new LinearLayoutManager(getBaseContext());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                break;
            default:
                mLayoutManager = new LinearLayoutManager(getBaseContext());
                mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
        }

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }


    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
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
            Log.isLoggable("Hi", imageID.getId());
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay, context, imageID.getId(), addImage());

        }


        // Start tracking the detected face instance within the face overlay.
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic = new FaceGraphic(mOverlay, mContext, mIsFrontFacing, addImage());
            mFaceGraphic.setId(faceId);
        }


        //Update the position/characteristics of the face within the overlay.
        //Take action on face detected

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);

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
                mFaceData.setLeftEyeOpen(mPreviousIsLeftEyeOpen);
            } else {
                mFaceData.setLeftEyeOpen(leftOpenScore > EYE_CLOSED_THRESHOLD);
                mPreviousIsLeftEyeOpen = mFaceData.isLeftEyeOpen();
            }
            float rightOpenScore = face.getIsRightEyeOpenProbability();
            if (rightOpenScore == Face.UNCOMPUTED_PROBABILITY) {
                mFaceData.setRightEyeOpen(mPreviousIsRightEyeOpen);
            } else {
                mFaceData.setRightEyeOpen(rightOpenScore > EYE_CLOSED_THRESHOLD);
                mPreviousIsRightEyeOpen = mFaceData.isRightEyeOpen();
            }

            // See if there's a smile!
            // Determine if person is smiling.
            final float SMILING_THRESHOLD = 0.8f;
            mFaceData.setSmiling(face.getIsSmilingProbability() > SMILING_THRESHOLD);
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
           // mOverlay.remove(mFaceGraphic);
        }


        @Override
        public void onDone() {
         //   mOverlay.remove(mFaceGraphic);
        }
    }


}
