package com.indeves.selfieapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zomato.photofilters.SampleFilters;
import com.zomato.photofilters.imageprocessors.Filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EffectActivity extends AppCompatActivity implements ThumbnailCallback, View.OnClickListener {
    private static int RESULT_LOAD_IMAGE = 1;
    //keep track of cropping intent
    final int PIC_CROP = 2;
    Bitmap thePic;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    Uri imageUri;
    Bitmap selectImage;
    Bitmap image;
    RelativeLayout thumbnailsContainer;
    RelativeLayout.LayoutParams layoutParams;
    private Activity activity;
    private RecyclerView thumbListView;
    private ImageView placeHolderImageView, clipPic, addEffect, writeOnPic, addButh, addFrame, rePickImage, back;
    private TextView actionBarTitle, save;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBarTitle = new TextView(getApplicationContext());
        back = findViewById(R.id.back_button);
        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) placeHolderImageView.getDrawable()).getBitmap();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);

                String path = saveInInternalStorage(bitmap);

                Intent intent = new Intent(EffectActivity.this, ShareActivity.class);

                intent.putExtra("inage", path);
                startActivity(intent);

            }
        });

        layoutParams = new RelativeLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
        actionBarTitle.setLayoutParams(layoutParams);
        actionBarTitle.setText(getResources().getString(R.string.app_name));
        actionBarTitle.setTextColor(Color.BLACK);
        actionBarTitle.setGravity(Gravity.CENTER);
        actionBarTitle.setTextSize(25);

        if (actionBar != null) {
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            actionBar.setCustomView(actionBarTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        activity = this;


        initUIWidgets();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.effect_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.effect_activity_next) {
            Toast.makeText(EffectActivity.this, "Next is clicked", Toast.LENGTH_SHORT).show();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //initialize recyclerView containing the edits
    private void initUIWidgets() {
        thumbListView = (RecyclerView) findViewById(R.id.thumbnails);
        placeHolderImageView = (ImageView) findViewById(R.id.place_holder_imageview);
        thumbnailsContainer = findViewById(R.id.thumbnails_container);
        thumbnailsContainer.setVisibility(View.GONE);
        clipPic = findViewById(R.id.effect_activity_clipPic_imageView);
        clipPic.setOnClickListener(this);
        addEffect = findViewById(R.id.effect_activity_addEffect_imageView);
        addEffect.setOnClickListener(this);
        writeOnPic = findViewById(R.id.effect_activity_writeOnPic_imageView);
        writeOnPic.setOnClickListener(this);
        addButh = findViewById(R.id.effect_activity_addButh_imageView);
        addButh.setOnClickListener(this);
        addFrame = findViewById(R.id.effect_activity_addFrame_imageView);
        addFrame.setOnClickListener(this);
        rePickImage = findViewById(R.id.effect_activity_repickImage_imageView);
        rePickImage.setOnClickListener(this);
        getImage();
        // get the original the image from gallary after that
        // placeHolderImageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getApplicationContext().getResources(), R.drawable.photo), 640, 640, false));
        // deploy the selected image overt the recyclerView items
    }

    private void initHorizontalList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.scrollToPosition(0);
        thumbListView.setLayoutManager(layoutManager);
        thumbListView.setHasFixedSize(true);
        bindDataToAdapter();
    }

    private void bindDataToAdapter() {
        final Context context = this.getApplication();
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                // we should get passed the selected photo to this method  to be edited
                Bitmap thumbImage = Bitmap.createScaledBitmap(selectImage, 640, 640, false);
                ThumbnailItem t1 = new ThumbnailItem();
                ThumbnailItem t2 = new ThumbnailItem();
                ThumbnailItem t3 = new ThumbnailItem();
                ThumbnailItem t4 = new ThumbnailItem();
                ThumbnailItem t5 = new ThumbnailItem();
                ThumbnailItem t6 = new ThumbnailItem();

                t1.image = thumbImage;
                t2.image = thumbImage;
                t3.image = thumbImage;
                t4.image = thumbImage;
                t5.image = thumbImage;
                t6.image = thumbImage;
                ThumbnailsManager.clearThumbs();
                ThumbnailsManager.addThumb(t1); // Original Image

                t2.filter = SampleFilters.getStarLitFilter();
                ThumbnailsManager.addThumb(t2);

                t3.filter = SampleFilters.getBlueMessFilter();
                ThumbnailsManager.addThumb(t3);

                t4.filter = SampleFilters.getAweStruckVibeFilter();
                ThumbnailsManager.addThumb(t4);

                t5.filter = SampleFilters.getLimeStutterFilter();
                ThumbnailsManager.addThumb(t5);

                t6.filter = SampleFilters.getNightWhisperFilter();
                ThumbnailsManager.addThumb(t6);

                List<ThumbnailItem> thumbs = ThumbnailsManager.processThumbs(context);

                ThumbnailsAdapter adapter = new ThumbnailsAdapter(thumbs, (ThumbnailCallback) activity);
                thumbListView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        };
        handler.post(r);
    }

    // change the selected image over the original one to be viewed
    @Override
    public void onThumbnailClick(Filter filter) {
        placeHolderImageView.setImageBitmap(filter.processFilter(Bitmap.createScaledBitmap(selectImage, placeHolderImageView.getMeasuredWidth(), placeHolderImageView.getMeasuredHeight(), false)));
    }

    private void getImage() {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK && reqCode != PIC_CROP) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectImage = BitmapFactory.decodeStream(imageStream);
                placeHolderImageView.setImageBitmap(Bitmap.createScaledBitmap(selectImage, selectImage.getWidth(), selectImage.getHeight(), false));
                initHorizontalList();


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(EffectActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else if (reqCode == PIC_CROP) {
            Bundle extras = data.getExtras();

//get the returned data
            if (resultCode != 0) {
//get the cropped bitmap
                selectImage = extras.getParcelable("data");
                placeHolderImageView.setImageBitmap(Bitmap.createScaledBitmap(selectImage, selectImage.getWidth(), selectImage.getHeight(), false));
                initHorizontalList();
            }

        } else {
            Toast.makeText(EffectActivity.this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == clipPic) {
            clipPic.setImageResource(R.drawable.clip_pic_clicked);
            addEffect.setImageResource(R.drawable.add_effect_unclicked);
            addButh.setImageResource(R.drawable.add_buth_unclicked);
            writeOnPic.setImageResource(R.drawable.write_on_pic_unclicked);
            addFrame.setImageResource(R.drawable.add_frame_unclicked);
            thumbnailsContainer.setVisibility(View.GONE);
            performCrop();


        } else if (v == addEffect) {
            thumbnailsContainer.setVisibility(View.VISIBLE);
            clipPic.setImageResource(R.drawable.clip_pic_unclicked);
            addEffect.setImageResource(R.drawable.add_effect_clicked);
            addButh.setImageResource(R.drawable.add_buth_unclicked);
            writeOnPic.setImageResource(R.drawable.write_on_pic_unclicked);
            addFrame.setImageResource(R.drawable.add_frame_unclicked);

        } else if (v == addButh) {
            clipPic.setImageResource(R.drawable.clip_pic_unclicked);
            addEffect.setImageResource(R.drawable.add_effect_unclicked);
            addButh.setImageResource(R.drawable.add_buth_active);
            writeOnPic.setImageResource(R.drawable.write_on_pic_unclicked);
            addFrame.setImageResource(R.drawable.add_frame_unclicked);
            thumbnailsContainer.setVisibility(View.GONE);

        } else if (v == addFrame) {
            clipPic.setImageResource(R.drawable.clip_pic_unclicked);
            addEffect.setImageResource(R.drawable.add_effect_unclicked);
            addButh.setImageResource(R.drawable.add_buth_unclicked);
            writeOnPic.setImageResource(R.drawable.write_on_pic_unclicked);
            addFrame.setImageResource(R.drawable.add_frame_clicked);
            thumbnailsContainer.setVisibility(View.GONE);

        } else if (v == writeOnPic) {
            clipPic.setImageResource(R.drawable.clip_pic_unclicked);
            addEffect.setImageResource(R.drawable.add_effect_unclicked);
            addButh.setImageResource(R.drawable.add_buth_unclicked);
            writeOnPic.setImageResource(R.drawable.write_on_pic_clicked);
            addFrame.setImageResource(R.drawable.add_frame_unclicked);
            thumbnailsContainer.setVisibility(View.GONE);

        } else if (v == rePickImage) {
            getImage();
        }

    }

    class SavePhotoTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... jpeg) {
            File photo =
                    new File(Environment.getExternalStorageDirectory(),
                            "photo.jpg");

            if (photo.exists()) {
                photo.delete();
            }

            try {
                FileOutputStream fos = new FileOutputStream(photo.getPath());

                fos.write(jpeg[0]);
                fos.close();
            } catch (java.io.IOException e) {
                Log.e("PictureDemo", "Exception in photoCallback", e);
            }

            return (null);
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveInInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    private void performCrop() {
        try {

            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(imageUri, "image/*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);

        } catch (ActivityNotFoundException anfe) {
            //display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


}

