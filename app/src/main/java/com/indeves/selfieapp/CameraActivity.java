package com.indeves.selfieapp;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;

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
import java.util.List;


public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    ImageID imageID = new ImageID();
    Button capture;
    private CameraSourcePreview mPreview;
    private GraphicOverlay mGraphicOverlay;
    private Context context = CameraActivity.this;
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    String photo = "";
    int i = 0;
    private FaceGraphic faceGraphic;
    RecyclerView recyclerView;
    FaceDetector detector ;
    private static final String KEY_LAYOUT_MANAGER = "layoutManager";
    private static final int SPAN_COUNT = 2;

public  static List<CameraButtons> listOfImages = new ArrayList<>();
public  static  int imageSelectNum ;

    private enum LayoutManagerType {
        GRID_LAYOUT_MANAGER,
        LINEAR_LAYOUT_MANAGER
    }

    protected RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);


    CameraAdaptor cameraAdaptor;
    protected LayoutManagerType mCurrentLayoutManagerType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        mPreview = findViewById(R.id.preview);
        mGraphicOverlay = findViewById(R.id.faceOverlay);
        GraphicOverlay.setContext(getApplicationContext());

        recyclerView = findViewById(R.id.rec);



       // setRecyclerViewLayoutManager(mCurrentLayoutManagerType);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();

        } else {
            requestCameraPermission();
        }
/*
        capture = findViewById(R.id.capture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("button", "detected");

                imageID.setId(1);


             *//*   mCameraSource.takePicture(null, new CameraSource.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] bytes) {

                        Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        image = getResizedBitmap(image, 150, 150);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] imageBytes = baos.toByteArray();

                    }
                });*//*


            }
        }); // take image function together with later main processing part.*/
        cameraAdaptor = new CameraAdaptor(addImage());
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(CameraActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setAdapter(cameraAdaptor);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                     imageID.setId(position);
                    }
                })
        );

    }


    private void requestCameraPermission() {
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
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };


    }


    public List <CameraButtons> addImage() {
        List <CameraButtons> list = new ArrayList<CameraButtons>();
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
        CameraButtons cameraButtons6 = new CameraButtons();
        cameraButtons6.setImagePath(R.drawable.tarposh);
        list.add(cameraButtons6);

        return  list;
    }


    // Face detection creation
    private void createCameraSource() {
        imageID.setId(0);

   createDetection();
// open the camera front ot back

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(40.f)
                .build();

    }
    private void createDetection(){
        Context context = getApplicationContext();
         detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new GraphicFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            Log.w(TAG, "Face detector dependencies are not yet available.");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // we have permission, so create the camerasource
            createCameraSource();
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));


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
        private GraphicOverlay mOverlay;
        private FaceGraphic mFaceGraphic;
        Context mContext = getBaseContext();

        GraphicFaceTracker(GraphicOverlay overlay, Context context) {
            context.getApplicationContext();
            Log.isLoggable("Hi", imageID.getId());
            mOverlay = overlay;
            mFaceGraphic = new FaceGraphic(overlay, mContext, imageID.getId(),addImage());

        }


        // Start tracking the detected face instance within the face overlay.
        @Override
        public void onNewItem(int faceId, Face item) {
            mFaceGraphic.setId(faceId);
        }


        //Update the position/characteristics of the face within the overlay.
        //Take action on face detected

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(mFaceGraphic);
            mFaceGraphic.updateFace(face,imageID.getId());

        }


        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(mFaceGraphic);
        }


        @Override
        public void onDone() {
            mOverlay.remove(mFaceGraphic);
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



}
