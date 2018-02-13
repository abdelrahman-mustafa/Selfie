package com.indeves.selfieapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;


import com.estimote.coresdk.repackaged.retrofit_v1_9_0.retrofit.RestAdapter;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.ArrayList;
import java.util.List;

class FaceGraphic extends GraphicOverlay.Graphic {
    private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    private static final float ID_Y_OFFSET = 50.0f;
    private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    Bitmap emojiBitmap;
    private static final int COLOR_CHOICES[] = {
            Color.YELLOW
    };

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    private static int mCurrentColorIndex = 0;
    private static final float EMOJI_SCALE_FACTOR = .9f;

    private Paint mFacePositionPaint;
    private Paint mIdPaint;
    private Paint mBoxPaint;
    Bitmap buttonBitmap;


    Context context;
    private volatile Face mFace;

    private int mFaceId;
    int i;
    private float mFaceHappiness;
    List<CameraButtons> list = new ArrayList<>();

    FaceGraphic(GraphicOverlay overlay, Context context, int i,List<CameraButtons> list) {
        super(overlay, context);
        this.context = context;
        this.i = i;
        this.list = list;
        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

    }


    void setId(int id) {
        mFaceId = id;
    }


    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face,int i) {
        mFace = face;
        postInvalidate();
        this.i = i ;
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        float scaleFactor = EMOJI_SCALE_FACTOR;

        Bitmap emojiBitmap;

        // Log the classification probabilities for each face.

        switch (getEmoji(face)) {
            case SMILE:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.smile);
                break;
            case FROWN:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.frown);
                break;
            case LEFT_WINK:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.leftwink);
                break;
            case RIGHT_WINK:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.rightwink);
                break;
            case LEFT_WINK_FROWN:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.leftwinkfrown);
                break;
            case RIGHT_WINK_FROWN:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.rightwinkfrown);
                break;
            case CLOSED_EYE_SMILE:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.closed_smile);
                break;
            case CLOSED_EYE_FROWN:
                emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.closed_frown);
                break;
            default:
                emojiBitmap = null;
        }
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        Log.d("id", String.valueOf(i));




        // Scale the emoji


        // Draws a circle at the position of the detected face, with the face's track id below.
        float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);
        //canvas.drawCircle(x, y, FACE_POSITION_RADIUS, mFacePositionPaint);

    /*    canvas.drawText("id: " + mFaceId, x + ID_X_OFFSET, y + ID_Y_OFFSET, mIdPaint);
        canvas.drawText("happiness: " + String.format("%.2f", face.getIsSmilingProbability()), x - ID_X_OFFSET, y - ID_Y_OFFSET, mIdPaint);
        canvas.drawText("right eye: " + String.format("%.2f", face.getIsRightEyeOpenProbability()), x + ID_X_OFFSET * 2, y + ID_Y_OFFSET * 2, mIdPaint);
        canvas.drawText("left eye: " + String.format("%.2f", face.getIsLeftEyeOpenProbability()), x - ID_X_OFFSET*2, y - ID_Y_OFFSET*2, mIdPaint);
     */   //Draws a boundi


        float xOffset = scaleX(face.getWidth() / 2);
        float yOffset = scaleY(face.getHeight() / 2);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;



        if (i != 0) {
            buttonBitmap = getBit(i);
            Log.d("button", "detected ---1");
            buttonBitmap = Bitmap.createScaledBitmap(buttonBitmap, newEmojiWidth, newEmojiHeight, false);
            canvas.drawBitmap(buttonBitmap, (left+right)/2, top, null);

        } else {
            canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        }


        canvas.drawRect(left, top, right, bottom, mBoxPaint);

    }

    public static Emoji getEmoji(Face face) {
        Log.d("smile", String.valueOf(face.getIsSmilingProbability()));

        Log.d("rightEye", String.valueOf(face.getIsRightEyeOpenProbability()));

        Log.d("leftEye", String.valueOf(face.getIsLeftEyeOpenProbability()));

        Boolean smile = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;
        Boolean leftEye = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        Boolean rightEye = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;

        Emoji emoji;
        if (smile) {
            if (leftEye && !rightEye) {
                emoji = Emoji.LEFT_WINK;
            } else if (rightEye && !leftEye) {
                emoji = Emoji.RIGHT_WINK;
            } else if (leftEye) {
                emoji = Emoji.CLOSED_EYE_SMILE;
            } else {
                emoji = Emoji.SMILE;
            }
        } else {
            if (leftEye && !rightEye) {
                emoji = Emoji.LEFT_WINK_FROWN;
            } else if (rightEye && !leftEye) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEye) {
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }


        return emoji;
    }

    private enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }


    private Bitmap getBit (int id){
        Bitmap bitmap = null;
        CameraButtons cameraButtons = new CameraButtons();
        cameraButtons = list.get(id);
        switch (id){
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        cameraButtons.getImagePath());
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        cameraButtons.getImagePath());
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        cameraButtons.getImagePath());
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        cameraButtons.getImagePath());
                break;
            case 4:
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        cameraButtons.getImagePath());
                break;
            case 5:
                break;
            case 6:
                bitmap = BitmapFactory.decodeResource(context.getResources(),
                        cameraButtons.getImagePath());
                break;
        }
        return bitmap;
    }

}

