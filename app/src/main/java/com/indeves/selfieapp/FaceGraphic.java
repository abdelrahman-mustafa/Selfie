package com.indeves.selfieapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;


import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

class FaceGraphic extends GraphicOverlay.Graphic {
   // private static final float FACE_POSITION_RADIUS = 10.0f;
    private static final float ID_TEXT_SIZE = 40.0f;
    //private static final float ID_Y_OFFSET = 50.0f;
    //private static final float ID_X_OFFSET = -50.0f;
    private static final float BOX_STROKE_WIDTH = 5.0f;
    private static final int COLOR_CHOICES[] = {
            Color.YELLOW
    };
    private int mFaceId;

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;
    private static final float EMOJI_SCALE_FACTOR = .9f;
   // public static Canvas canvasSelected;
    private static int mCurrentColorIndex = 0;
    Context context;
    int i;
   // private Canvas canvas;
    List<CameraButtons> list = new ArrayList<>();
    private Drawable mHatGraphic;
    private volatile Face mFace;

    // This variable may be written to by one of many threads. By declaring it as volatile,
    // we guarantee that when we read its contents, we're reading the most recent "write"
    // by any thread.
    private volatile FaceData mFaceData;
/*
    FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing, List<CameraButtons> list) {
        super(overlay, context);
        this.context = context;
        mIsFrontFacing = isFrontFacing;

        this.list = addImage();

    }*/

    FaceGraphic(GraphicOverlay overlay, Context context, int i,boolean isFrontFacing) {
        super(overlay, context);
        this.context = context;
        this.i = i;
        this.list = addImage();
        Resources resources = context.getResources();
        initializePaints(resources);
        initializeGraphics(resources);

        mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
        final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];

        Paint mFacePositionPaint = new Paint();
        mFacePositionPaint.setColor(selectedColor);

        Paint mIdPaint = new Paint();
        mIdPaint.setColor(selectedColor);
        mIdPaint.setTextSize(ID_TEXT_SIZE);

        Paint mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);

    }

/*


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Bitmap addBitmapToFace(Bitmap backgroundBitmap) {

        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());


        canvasSel.getHeight();
        canvasSel.getWidth();
        return resultBitmap;
    }
*/


    private Emoji getEmoji(Face face) {
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

    private void initializeGraphics(Resources resources) {
        //Drawable mPigNoseGraphic = resources.getDrawable(R.drawable.pig_nose_emoji);
        //Drawable mMustacheGraphic = resources.getDrawable(R.drawable.mustache);
        //Drawable mHappyStarGraphic = resources.getDrawable(R.drawable.happy_star);
        mHatGraphic = resources.getDrawable(R.drawable.red_hat);
    }

    void update(FaceData faceData) {
        mFaceData = faceData;
        //  postInvalidate(); // Trigger a redraw of the graphic (i.e. cause draw() to be called).
    }

    void setId(int id) {
        mFaceId = id;
    }

    /**
     * Updates the face instance from the detection of the most recent frame.  Invalidates the
     * relevant portions of the overlay to trigger a redraw.
     */
    void updateFace(Face face, int i) {
        mFace = face;
        this.i = i;
    }

    /**
     * Draws the face annotations for position on the supplied canvas.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void draw(Canvas canvas) {
        Face face = mFace;
        if (face == null) {
            return;
        }

        FaceData faceData = mFaceData;
        PointF detectPosition = faceData.getPosition();
        PointF detectLeftEyePosition = faceData.getLeftEyePosition();
        PointF detectRightEyePosition = faceData.getRightEyePosition();
        PointF detectNoseBasePosition = faceData.getNoseBasePosition();
        PointF detectMouthLeftPosition = faceData.getMouthLeftPosition();
        PointF detectMouthBottomPosition = faceData.getMouthBottomPosition();
        PointF detectMouthRightPosition = faceData.getMouthRightPosition();
        {
            if ((detectPosition == null) ||
                    (detectLeftEyePosition == null) ||
                    (detectRightEyePosition == null) ||
                    (detectNoseBasePosition == null) ||
                    (detectMouthLeftPosition == null) ||
                    (detectMouthBottomPosition == null) ||
                    (detectMouthRightPosition == null)) {
                return;
            }
        }

        // If we've made it this far, it means that the face data *is* available.
        // It's time to translate camera coordinates to view coordinates.

        // Face position, dimensions, and angle
        PointF position = new PointF(translateX(detectPosition.x),
                translateY(detectPosition.y));
        float width = scaleX(faceData.getWidth());
        float height = scaleY(faceData.getHeight());

        // Eye coordinates
        PointF leftEyePosition = new PointF(translateX(detectLeftEyePosition.x),
                translateY(detectLeftEyePosition.y));
        PointF rightEyePosition = new PointF(translateX(detectRightEyePosition.x),
                translateY(detectRightEyePosition.y));

   /*     // Eye state
        boolean leftEyeOpen = faceData.isLeftEyeOpen();
        boolean rightEyeOpen = faceData.isRightEyeOpen();
*/
        // Nose coordinates
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));


        // Mouth coordinates
        PointF mouthLeftPosition = new PointF(translateX(detectMouthLeftPosition.x),
                translateY(detectMouthLeftPosition.y));
        PointF mouthRightPosition = new PointF(translateX(detectMouthRightPosition.x),
                translateY(detectMouthRightPosition.y));
/*
        PointF mouthBottomPosition = new PointF(translateX(detectMouthBottomPosition.x),
                translateY(detectMouthBottomPosition.y));

        boolean smiling = faceData.isSmiling();
*/

        // Head tilt
    //    float eulerY = faceData.getEulerY();
        float eulerZ = faceData.getEulerZ();


        // Calculate the distance between the eyes using Pythagoras' formula,
        // and we'll use that distance to set the size of the eyes and irises.
//        final float EYE_RADIUS_PROPORTION = 0.45f;
     /*   final float IRIS_RADIUS_PROPORTION = EYE_RADIUS_PROPORTION / 2.0f;
        float distance = (float) Math.sqrt(
                (rightEyePosition.x - leftEyePosition.x) * (rightEyePosition.x - leftEyePosition.x) +
                        (rightEyePosition.y - leftEyePosition.y) * (rightEyePosition.y - leftEyePosition.y));
*//*
        float eyeRadius = EYE_RADIUS_PROPORTION * distance;
        float irisRadius = IRIS_RADIUS_PROPORTION * distance;

*/
        // Draw the hat only if the subject's head is titled at a
        // sufficiently jaunty angle.
        // Draw the eyes.
       /* PointF leftIrisPosition = mLeftPhysics.nextIrisPosition(leftEyePosition, eyeRadius, irisRadius);
        //drawEye(canvas, leftEyePosition, eyeRadius, leftIrisPosition, irisRadius, leftEyeOpen, smiling);
        PointF rightIrisPosition = mRightPhysics.nextIrisPosition(rightEyePosition, eyeRadius, irisRadius);
       */ //drawEye(canvas, rightEyePosition, eyeRadius, rightIrisPosition, irisRadius, rightEyeOpen, smiling);

        // Draw the nose.
       // drawNose(canvas, noseBasePosition, leftEyePosition, rightEyePosition, width);

        // Draw the mustache.
        //drawMustache(canvas, noseBasePosition, mouthLeftPosition, mouthRightPosition);

        // Draw the hat only if the subject's head is titled at a
        // sufficiently jaunty angle.
        final float HEAD_TILT_HAT_THRESHOLD = 20.0f;
        if (Math.abs(eulerZ) > HEAD_TILT_HAT_THRESHOLD) {
            drawHat(canvas, position, width, height, noseBasePosition);
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


  /*      float x = translateX(face.getPosition().x + face.getWidth() / 2);
        float y = translateY(face.getPosition().y + face.getHeight() / 2);

        float xOffset = scaleX(face.getWidth() / 2);
        float yOffset = scaleY(face.getHeight() / 2);
        float left = x - xOffset;
        float top = y - yOffset;
        float right = x + xOffset;
        float bottom = y + yOffset;*/


        Drawable buttonBitmap;
        if (i > 0 && i<6) {
            buttonBitmap = getBit(i);
            float noseWidth = width * scaleFactor;
            int left = (int) (noseBasePosition.x - (noseWidth / 2));
            int right = (int) (noseBasePosition.x + (noseWidth / 2));
            int top = (int) (leftEyePosition.y + rightEyePosition.y) / 8 - 90;
            int bottom = (int) noseBasePosition.y / 2;

            buttonBitmap.setBounds(left, top, right, bottom);
            buttonBitmap.draw(canvas);
            Log.d("button", "detected ---1");

        } else if (i == 0) {
            canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);


        }else if (i > 9 ){
            buttonBitmap = getBit(i);
            int left = (int) mouthLeftPosition.x;
            int top = (int) noseBasePosition.y;
            int right = (int) mouthRightPosition.x;
            int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);

            buttonBitmap.setBounds(left, top, right, bottom);
            buttonBitmap.draw(canvas);
        }else if (i >5 && i <7){
            buttonBitmap = getBit(i);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) buttonBitmap;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            Bitmap or = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            Drawable drawable = new BitmapDrawable(or);

            float noseWidth = width * scaleFactor;
            int left = (int) (noseBasePosition.x - (noseWidth / 2));
            int right = (int) (noseBasePosition.x + (noseWidth / 2));
            int top = (int) noseBasePosition.y / 3 ;
            int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y)/2+ 200;

            drawable.setBounds(left, top, right, bottom);
            drawable.draw(canvas);
            Log.d("button", "detected ---1");
        }else if (i >6 && i <9){

            buttonBitmap = getBit(i);   final float NOSE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
            float noseWidth = width * NOSE_FACE_WIDTH_RATIO;
            int left = (int) (noseBasePosition.x - (noseWidth ));
            int right = (int) (noseBasePosition.x + (noseWidth ));
            int top = (int) (leftEyePosition.y + rightEyePosition.y)/2 -80 ;
            int bottom = (int) (noseBasePosition).y-120;
            buttonBitmap.setBounds(left, top, right, bottom);


            buttonBitmap.draw(canvas);
        }else if (i == 9){
            buttonBitmap = getBit(i);   final float NOSE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
            float noseWidth = width * NOSE_FACE_WIDTH_RATIO;
            int left = (int) (noseBasePosition.x - (noseWidth )-70);
            int right = (int) (noseBasePosition.x + (noseWidth )+70);
            int top = (int) (leftEyePosition.y + rightEyePosition.y)/2 -80 ;
            int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);
            buttonBitmap.setBounds(left, top, right, bottom);


            buttonBitmap.draw(canvas);

        }

//        canvas.drawRect(left, top, right, bottom, mBoxPaint);

    }

 /*   public Canvas getCanvasSel() {
        return canvasSel;
    }*/

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Drawable getBit(int id) {
        Drawable bitmap;
        CameraButtons cameraButtons;
        cameraButtons = list.get(id);
        bitmap = context.getDrawable(cameraButtons.getImagePath());
        return bitmap;
    }


    private void initializePaints(Resources resources) {
        Paint mHintTextPaint = new Paint();
        mHintTextPaint.setColor(resources.getColor(R.color.overlayHint));
        mHintTextPaint.setTextSize(resources.getDimension(R.dimen.textSize));

        Paint mHintOutlinePaint = new Paint();
        mHintOutlinePaint.setColor(resources.getColor(R.color.overlayHint));
        mHintOutlinePaint.setStyle(Paint.Style.STROKE);
        mHintOutlinePaint.setStrokeWidth(resources.getDimension(R.dimen.hintStroke));

        Paint mEyeWhitePaint = new Paint();
        mEyeWhitePaint.setColor(resources.getColor(R.color.eyeWhite));
        mEyeWhitePaint.setStyle(Paint.Style.FILL);

        Paint mIrisPaint = new Paint();
        mIrisPaint.setColor(resources.getColor(R.color.iris));
        mIrisPaint.setStyle(Paint.Style.FILL);

        Paint mEyeOutlinePaint = new Paint();
        mEyeOutlinePaint.setColor(resources.getColor(R.color.eyeOutline));
        mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
        mEyeOutlinePaint.setStrokeWidth(resources.getDimension(R.dimen.eyeOutlineStroke));

        Paint mEyelidPaint = new Paint();
        mEyelidPaint.setColor(resources.getColor(R.color.eyelid));
        mEyelidPaint.setStyle(Paint.Style.FILL);
    }


    // Cartoon feature draw routines
    // =============================

   /* private void drawEye(Canvas canvas,
                         PointF eyePosition, float eyeRadius,
                         PointF irisPosition, float irisRadius,
                         boolean eyeOpen, boolean smiling) {
        if (eyeOpen) {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeWhitePaint);
            if (smiling) {
                mHappyStarGraphic.setBounds(
                        (int) (irisPosition.x - irisRadius),
                        (int) (irisPosition.y - irisRadius),
                        (int) (irisPosition.x + irisRadius),
                        (int) (irisPosition.y + irisRadius));
                mHappyStarGraphic.draw(canvas);
            } else {
                canvas.drawCircle(irisPosition.x, irisPosition.y, irisRadius, mIrisPaint);
            }
        } else {
            canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyelidPaint);
            float y = eyePosition.y;
            float start = eyePosition.x - eyeRadius;
            float end = eyePosition.x + eyeRadius;
            canvas.drawLine(start, y, end, y, mEyeOutlinePaint);
        }
        canvas.drawCircle(eyePosition.x, eyePosition.y, eyeRadius, mEyeOutlinePaint);
    }

    private void drawNose(Canvas canvas,
                          PointF noseBasePosition,
                          PointF leftEyePosition, PointF rightEyePosition,
                          float faceWidth) {
        final float NOSE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
        float noseWidth = faceWidth * NOSE_FACE_WIDTH_RATIO;
        int left = (int) (noseBasePosition.x - (noseWidth / 2));
        int right = (int) (noseBasePosition.x + (noseWidth / 2));
        int top = (int) (leftEyePosition.y + rightEyePosition.y) / 2;
        int bottom = (int) noseBasePosition.y;

        mPigNoseGraphic.setBounds(left, top, right, bottom);
        mPigNoseGraphic.draw(canvas);
    }

    private void drawMustache(Canvas canvas,
                              PointF noseBasePosition,
                              PointF mouthLeftPosition, PointF mouthRightPosition) {
        int left = (int) mouthLeftPosition.x;
        int top = (int) noseBasePosition.y;
        int right = (int) mouthRightPosition.x;
        int bottom = (int) Math.min(mouthLeftPosition.y, mouthRightPosition.y);

        // We need to check which camera is being used because the mustache graphic's bounds
        // are based on the left and right corners of the mouth, from the subject's persepctive.
        // With the front camera, the subject's left will be on the *left* side of the view,
        // but with the back camera, the subject's left will be on the *right* side.
        if (mIsFrontFacing) {
            mMustacheGraphic.setBounds(left, top, right, bottom);
        } else {
            mMustacheGraphic.setBounds(right, top, left, bottom);
        }
        mMustacheGraphic.draw(canvas);
    }*/

    private void drawHat(Canvas canvas, PointF facePosition, float faceWidth, float faceHeight, PointF noseBasePosition) {
        final float HAT_FACE_WIDTH_RATIO = (float) (1.0 / 4.0);
        final float HAT_FACE_HEIGHT_RATIO = (float) (1.0 / 6.0);
        final float HAT_CENTER_Y_OFFSET_FACTOR = (float) (1.0 / 8.0);

        float hatCenterY = facePosition.y + (faceHeight * HAT_CENTER_Y_OFFSET_FACTOR);
        float hatWidth = faceWidth * HAT_FACE_WIDTH_RATIO;
        float hatHeight = faceHeight * HAT_FACE_HEIGHT_RATIO;

        int left = (int) (noseBasePosition.x - (hatWidth / 2));
        int right = (int) (noseBasePosition.x + (hatWidth / 2));
        int top = (int) (hatCenterY - (hatHeight / 2));
        int bottom = (int) (hatCenterY + (hatHeight / 2));
        mHatGraphic.setBounds(left, top, right, bottom);
        mHatGraphic.draw(canvas);
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


    // position of nose
  /*  buttonBitmap = getBit(i);   final float NOSE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
    float noseWidth = width * NOSE_FACE_WIDTH_RATIO;
    int left = (int) (noseBasePosition.x - (noseWidth / 2)+2);
    int right = (int) (noseBasePosition.x + (noseWidth / 2));
    int top = (int) (leftEyePosition.y + rightEyePosition.y) /4;
    int bottom = (int) (noseBasePosition).y-200;
            buttonBitmap.setBounds(left, top, right, bottom);
    */

  // position of Mustache
  /*  buttonBitmap = getBit(i);   final float NOSE_FACE_WIDTH_RATIO = (float) (1 / 5.0);
    float noseWidth = width * NOSE_FACE_WIDTH_RATIO;
    int left = (int) (noseBasePosition.x - (noseWidth / 2)+2);
    int right = (int) (noseBasePosition.x + (noseWidth / 2));
    int top = (int) (leftEyePosition.y + rightEyePosition.y)/2 -100 ;
    int bottom = (int) (noseBasePosition).y-180;
            buttonBitmap.setBounds(left, top, right, bottom);
*/



    private ArrayList<CameraButtons> addImage() {
        ArrayList<CameraButtons> list = new ArrayList<>();
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
}


