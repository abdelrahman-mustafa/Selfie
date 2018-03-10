package com.indeves.selfieapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;


import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Emojifier {
    private static final float EMOJI_SCALE_FACTOR = .9f;

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;


    static int id;

    static Bitmap detecFaces(Context context, Bitmap bitmap, int h, FaceData faceData) {


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
            if (h == 0) {

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
                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face,faceData);
            } else {
                Bitmap bitmap1;
                switch (h) {
                    case 1:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.frown);
                        break;
                    case 2:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.borneta);
                        break;
                    case 3:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.tartor);
                        break;
                    case 4:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.oaaal);
                        break;
                    case 5:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.shanb2);
                        break;
                    case 6:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.shanb);
                        break;
                    case 7:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.fionka);
                        break;
                    case 8:
                        bitmap1 = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.tarposh);
                        break;
                    default:
                        bitmap1 = null;
                }
                resultBitmap = addBitmapToFace(resultBitmap, bitmap1, face, faceData);
            }

            detector.release();
        }

        return resultBitmap;
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

    public static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face, FaceData faceDat) {

        int left = 0;
        int right;
        int top =0;
        int bottom;
        if (faceDat != null) {
  /*          left = (int) faceDat.getLeftEarTipPosition().x;
             right = (int) faceDat.getRightEarTipPosition().y;
            top = (int) face.getHeight();
            bottom = (int) Math.min(faceDat.getLeftEyePosition().y, faceDat.getRightEyePosition().y);

   */     }
        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face

        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);

        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;
        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        if (id >= 0 && id < 4) {

            float eulerY = faceDat.getEulerY();
            float eulerZ = faceDat.getEulerZ();
            //A.M- Match the selected item with the face



            //A.M - draw the selected item over the face

            canvas.drawBitmap(emojiBitmap, face.getWidth(), face.getHeight(), null);

        } else {
            canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);
        }

        return resultBitmap;
    }


}