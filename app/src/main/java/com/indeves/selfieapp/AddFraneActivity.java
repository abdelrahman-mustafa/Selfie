package com.indeves.selfieapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.dgreenhalgh.android.simpleitemdecoration.grid.GridDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AddFraneActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FramesAdapter framesAdapter;

ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_frane);
        recyclerView = findViewById(R.id.card_recycler_view);
        back = findViewById(R.id.back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddFraneActivity.this,CameraActivity.class);
                intent.putExtra("position",0);
                startActivity(intent);
            }
        });
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(AddFraneActivity.this, 3);
        recyclerView.setLayoutManager(layoutManager);

        framesAdapter = new FramesAdapter(AddFraneActivity.this, addImage());
//        recyclerView.setAdapter(lastWorkImagesAdapter);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(
                getApplicationContext()
        ));


        Drawable horizontalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider);
        Drawable verticalDivider = ContextCompat.getDrawable(this, R.drawable.line_divider);
        recyclerView.addItemDecoration(new GridDividerItemDecoration(horizontalDivider, verticalDivider, 3));

        recyclerView.setAdapter(framesAdapter);
        framesAdapter.notifyDataSetChanged();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(AddFraneActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(AddFraneActivity.this,CameraActivity.class);
                        intent.putExtra("position",position);
                        startActivity(intent);
                        finish();
                    }
                })
        );

    }
    public ArrayList<CameraButtons> addImage() {
        ArrayList<CameraButtons> list = new ArrayList<CameraButtons>();
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




    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
    public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

        private final int verticalSpaceHeight;

        public VerticalSpaceItemDecoration(int verticalSpaceHeight) {
            this.verticalSpaceHeight = verticalSpaceHeight;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            outRect.bottom = verticalSpaceHeight;
        }
    }

}
