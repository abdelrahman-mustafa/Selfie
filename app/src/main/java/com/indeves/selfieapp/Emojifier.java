package com.indeves.selfieapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.FaceDetector;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Emojifier extends GraphicOverlay.Graphic {
    private static final float EMOJI_SCALE_FACTOR = 1.4f;

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;
    Drawable buttonBitmap;

    List<CameraButtons> list = new ArrayList<>();
Context context;

    static int id;

    public Emojifier(GraphicOverlay overlay, Context mcontext) {
        super(overlay, mcontext);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Bitmap detecFaces(Context context, Bitmap bitmap, int h, FaceData faceData , Canvas rect) {

        this.context = context;
        this.list = addImage();
        id = h;
        // start detection method
        com.google.android.gms.vision.face.FaceDetector detector = new com.google.android.gms.vision.face.FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(com.google.android.gms.vision.face.FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        // build the frame contains the pic

        Frame frame = new Frame.Builder().setBitmap(bitmap).build();


        Bitmap resultBitmap = bitmap;


        // detect the number of faces
        SparseArray<Face> faces = detector.detect(frame);    //faces size

        for (int i = 0; i < faces.size(); ++i) {
            Face face = faces.valueAt(i);

                resultBitmap = addBitmapToFace(context,resultBitmap, face, faceData,h);

            detector.release();
        }

        return resultBitmap;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Drawable getBit(int id) {
        Drawable bitmap;
        CameraButtons cameraButtons = new CameraButtons();
        cameraButtons = list.get(id);
        bitmap = context.getDrawable(cameraButtons.getImagePath());
        return bitmap;
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

    @Override
    public void draw(Canvas canvas) {

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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Bitmap addBitmapToFace(Context context, Bitmap backgroundBitmap, Face face, FaceData faceDat, int i) {





        Bitmap resultBitmap = backgroundBitmap;
        Canvas canvas = new Canvas(resultBitmap);





        FaceData faceData = faceDat;
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

        // Eye state
        boolean leftEyeOpen = faceData.isLeftEyeOpen();
        boolean rightEyeOpen = faceData.isRightEyeOpen();

        // Nose coordinates
        PointF noseBasePosition = new PointF(translateX(detectNoseBasePosition.x),
                translateY(detectNoseBasePosition.y));


        // Mouth coordinates
        PointF mouthLeftPosition = new PointF(translateX(detectMouthLeftPosition.x),
                translateY(detectMouthLeftPosition.y));
        PointF mouthRightPosition = new PointF(translateX(detectMouthRightPosition.x),
                translateY(detectMouthRightPosition.y));
        PointF mouthBottomPosition = new PointF(translateX(detectMouthBottomPosition.x),
                translateY(detectMouthBottomPosition.y));


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


        if (i > 0 && i<6) {
            buttonBitmap = getBit(i);
            float noseWidth = width * scaleFactor;

            int left = (int) (noseBasePosition.x - (noseWidth / 2))-100;
            int right = (int) (noseBasePosition.x + (noseWidth / 2)+100);
            int top = (int) (leftEyePosition.y + rightEyePosition.y) / 8 - 200;
            int bottom = (int) noseBasePosition.y / 2 + 200;

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
            BitmapDrawable bitmapDrawable = (BitmapDrawable)buttonBitmap;
            Bitmap bitmap = bitmapDrawable.getBitmap();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
            Bitmap or = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
            Drawable drawable = new BitmapDrawable(or);

            float noseWidth = width * scaleFactor;
            int left = (int) (noseBasePosition.x - (noseWidth / 2));
            int right = (int) (noseBasePosition.x + (noseWidth / 2));
            int top = (int) (int) noseBasePosition.y / 3 ;
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





        return resultBitmap;


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

}