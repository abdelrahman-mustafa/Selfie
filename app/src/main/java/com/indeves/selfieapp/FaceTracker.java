package com.indeves.selfieapp;

import android.content.Context;
import android.graphics.PointF;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by khalid on 15/03/18.
 */
class FaceTracker extends Tracker<Face> {

    private static final String TAG = "FaceTracker";

    private GraphicOverlay mOverlay;
    private Context mContext;
    private boolean mIsFrontFacing;
    private FaceGraphic mFaceGraphic;
    private FaceData mFaceData;

    // Subjects may move too quickly to for the system to detect their detect features,
    // or they may move so their features are out of the tracker's detection range.
    // This map keeps track of previously detected facial landmarks so that we can approximate
    // their locations when they momentarily "disappear".
    private Map<Integer, PointF> mPreviousLandmarkPositions = new HashMap<>();

    FaceTracker(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
        mOverlay = overlay;
        mContext = context;
        mIsFrontFacing = isFrontFacing;
        mFaceData = new FaceData();
    }

    // Facial landmark utility methods
    // ===============================

    /**
     * Given a face and a facial landmark position,
     * return the coordinates of the landmark if known,
     * or approximated coordinates (based on prior data) if not.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (Landmark landmark : face.getLandmarks()) {
            if (landmark.getType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF landmarkPosition = mPreviousLandmarkPositions.get(landmarkId);
        if (landmarkPosition == null) {
            return null;
        }

        float x = face.getPosition().x + (landmarkPosition.x * face.getWidth());
        float y = face.getPosition().y + (landmarkPosition.y * face.getHeight());
        return new PointF(x, y);
    }

    private void updatePreviousLandmarkPositions(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getPosition().x) / face.getWidth();
            float yProp = (position.y - face.getPosition().y) / face.getHeight();
            mPreviousLandmarkPositions.put(landmark.getType(), new PointF(xProp, yProp));
        }
    }

    // 1
    @Override
    public void onNewItem(int id, Face face) {
        mFaceGraphic = new FaceGraphic(mOverlay, mContext, mIsFrontFacing);
    }

    // 2
    @Override
    public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
        mOverlay.add(mFaceGraphic);
        mFaceGraphic.update(face);

    }

    // 3
    @Override
    public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        mOverlay.remove(mFaceGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mFaceGraphic);
    }
}

