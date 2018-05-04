package com.indeves.selfieapp;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FramesAdapter extends RecyclerView.Adapter<FramesAdapter.ViewHolder> {

    private ArrayList<CameraButtons> cameraButtons;
    private Context context;

    public FramesAdapter(Context context, ArrayList<CameraButtons> cameraButtons) {
        this.cameraButtons = cameraButtons;
        this.context = context;
    }

    @Override
    public FramesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.frame_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CameraButtons cameraButto = cameraButtons.get(position);

           Picasso.with(context).load(cameraButto.getImagePath()).resize(300, 240).onlyScaleDown().into(holder.img_android);
/*
        CameraButtons cameraButto = cameraButtons.get(position);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),cameraButto.getImagePath() );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 50, out);
        Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
*/

        // myViewHolder.imageView.setImageResource(cameraButtons.getImagePath());
    //    holder.img_android.setImageBitmap(decoded);
    }
    @Override
    public int getItemCount() {
        return cameraButtons.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView img_android;

        @SuppressLint("ResourceAsColor")
        public ViewHolder(View view) {
            super(view);
            img_android = (ImageView) view.findViewById(R.id.img_android);

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


}