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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class AddFraneActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    FramesAdapter framesAdapter;

ImageButton back;

    public class SeparatorDecoration extends RecyclerView.ItemDecoration {

        private final Paint mPaint;

        /**
         * Create a decoration that draws a line in the given color and width between the items in the view.
         *
         * @param context  a context to access the resources.
         * @param color    the color of the separator to draw.
         * @param heightDp the height of the separator in dp.
         */
        public SeparatorDecoration(@NonNull Context context, @ColorInt int color,
                                   @FloatRange(from = 0, fromInclusive = false) float heightDp) {
            mPaint = new Paint();
            mPaint.setColor(color);
            final float thickness = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    heightDp, context.getResources().getDisplayMetrics());
            mPaint.setStrokeWidth(thickness);
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

            // we want to retrieve the position in the list
            final int position = params.getViewAdapterPosition();

            // and add a separator to any view but the last one
            if (position < state.getItemCount()) {
                outRect.set(0, 0, 0, (int) mPaint.getStrokeWidth()); // left, top, right, bottom
            } else {
                outRect.setEmpty(); // 0, 0, 0, 0
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            // we set the stroke width before, so as to correctly draw the line we have to offset by width / 2
            final int offset = (int) (mPaint.getStrokeWidth() / 2);

            // this will iterate over every visible view
            for (int i = 0; i < parent.getChildCount(); i++) {
                // get the view
                final View view = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();

                // get the position
                final int position = params.getViewAdapterPosition();

                // and finally draw the separator
                if (position < state.getItemCount()) {
                    c.drawLine(view.getLeft(), view.getBottom() + offset, view.getRight(), view.getBottom() + offset, mPaint);
                }
            }
        }
    }
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

        recyclerView.setAdapter(framesAdapter);
        framesAdapter.notifyDataSetChanged();
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(AddFraneActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(AddFraneActivity.this,CameraActivity.class);
                        intent.putExtra("position",position);
                        startActivity(intent);
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

}
