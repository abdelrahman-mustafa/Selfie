package com.indeves.selfieapp;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CameraAdaptor extends RecyclerView.Adapter<CameraAdaptor.MyViewHolder> {

    List<CameraButtons> list = new ArrayList<>();
    Context context;

    public CameraAdaptor(List<CameraButtons> list, Context context) {
        this.list = list;
        this.context = context;

    }

    public List<CameraButtons> getList() {
        return list;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            imageView = view.findViewById(R.id.image);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.camera_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        CameraButtons cameraButtons = list.get(position);
        Log.d("h",cameraButtons.toString());
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        Bitmap bm = BitmapFactory.decodeResource( context.getResources(), cameraButtons.getImagePath());
        bm = getResizedBitmap(bm,80,80);
       // myViewHolder.imageView.setImageResource(cameraButtons.getImagePath());
        myViewHolder.imageView.setImageBitmap(bm);

    }

    @Override
    public int getItemCount() {

        return list.size();
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
