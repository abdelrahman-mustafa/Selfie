package com.indeves.selfieapp;


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

    public CameraAdaptor(List<CameraButtons> list) {
        this.list = list;

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

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        CameraButtons cameraButtons = list.get(position);
        Log.d("h",cameraButtons.toString());
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.imageView.setImageResource(cameraButtons.getImagePath());

    }

    @Override
    public int getItemCount() {

        return list.size();
    }
}
