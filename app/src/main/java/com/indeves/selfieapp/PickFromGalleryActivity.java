package com.indeves.selfieapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PickFromGalleryActivity extends AppCompatActivity {
    private static int REQUEST_GET_IMAGE_FROM_PHONE = 1;
    //    private static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider";
//    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private ImageView mImageView;
    private Button mEmojifyButton;
    private FloatingActionButton mShareFab;
    private FloatingActionButton mSaveFab;
    private FloatingActionButton mClearFab;
    private TextView mTitleTextView;
    //    private String mTempPhotoPath;
    private Bitmap mResultsBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_from_gallery);
        mImageView = (ImageView) findViewById(R.id.image_view);
        mEmojifyButton = (Button) findViewById(R.id.emojify_button);
        mShareFab = (FloatingActionButton) findViewById(R.id.share_button);
        mSaveFab = (FloatingActionButton) findViewById(R.id.save_button);
        mClearFab = (FloatingActionButton) findViewById(R.id.clear_button);
        mTitleTextView = (TextView) findViewById(R.id.title_text_view);
        mEmojifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                emojifyMe(view);
                Intent intent = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GET_IMAGE_FROM_PHONE);
            }
        });
        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                shareMe(view);
            }
        });
        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageInByte = baos.toByteArray();


                Intent intent = new Intent(PickFromGalleryActivity.this, ShareActivity.class);
                intent.putExtra("inage",imageInByte);
            }
        });

        mClearFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                clearImage(view);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GET_IMAGE_FROM_PHONE && resultCode == Activity.RESULT_OK && null != data && data.getData() != null) {
            Uri filePath = data.getData();

            try {
                mResultsBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                processAndSetImage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void processAndSetImage() {

        // Toggle Visibility of the views
        mEmojifyButton.setVisibility(View.GONE);
        mTitleTextView.setVisibility(View.GONE);
        mSaveFab.setVisibility(View.VISIBLE);
        mShareFab.setVisibility(View.VISIBLE);
        mClearFab.setVisibility(View.VISIBLE);

        // Detect the faces and overlay the appropriate emoji
      //  mResultsBitmap = Emojifier.detecFaces(this, mResultsBitmap, 0, null, null);

        // Set the new bitmap to the ImageView
        mImageView.setImageBitmap(mResultsBitmap);

        Bitmap bitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageInByte = baos.toByteArray();


    }

}
