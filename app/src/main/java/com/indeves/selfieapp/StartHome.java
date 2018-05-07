package com.indeves.selfieapp;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;


public class StartHome extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_HANDLE_CAMERA_PERM = 2;
    Button track, emojify, pickForPhone;
    RelativeLayout emojifyMe, trackMyFace, pickFromGallery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_home);
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
        }
        track = findViewById(R.id.home_button_camera_track);
        emojify = findViewById(R.id.home_button_emojify);
        track.setOnClickListener(this);
        emojify.setOnClickListener(this);
        pickForPhone = findViewById(R.id.homeScreen_pickFromPhone_button);
        pickForPhone.setOnClickListener(this);
        emojifyMe = findViewById(R.id.emojify_layout);
        emojifyMe.setOnClickListener(this);
        trackMyFace = findViewById(R.id.track_face_layout);
        trackMyFace.setOnClickListener(this);
        pickFromGallery = findViewById(R.id.pick_from_gallery_layout);
        pickFromGallery.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestCameraPermission();
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

    @Override
    protected void onResume() {
        super.onResume();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }


        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

    }

    @Override
    public void onClick(View v) {
        if (v == emojify || v == emojifyMe) {
            startActivity(new Intent(StartHome.this, Emojify.class));

        } else if (v == track || v == trackMyFace) {
            startActivity(new Intent(StartHome.this, CameraActivity.class));
            finish();

        } else if (v == pickForPhone || v == pickFromGallery) {
            startActivity(new Intent(StartHome.this, MainActivity.class));
            finish();

        }

    }
}
