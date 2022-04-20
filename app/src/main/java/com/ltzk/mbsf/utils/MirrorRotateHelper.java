package com.ltzk.mbsf.utils;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class MirrorRotateHelper {

    public static void postTranslate(final View v, final int degrees) {
        if (v != null) {
            final Rotate3dAnimation animation = new Rotate3dAnimation(
                    v.getWidth() / 2,
                    v.getHeight() / 2,
                    degrees);
            animation.setFillAfter(true);
            v.startAnimation(animation);
        }
    }

    private static final class Rotate3dAnimation extends Animation {
        private final float mCenterX;
        private final float mCenterY;
        private final Camera mCamera = new Camera();
        private volatile int mDegrees = 180;

        public Rotate3dAnimation(float centerX, float centerY, int degrees) {
            mCenterX = centerX;
            mCenterY = centerY;
            mDegrees = degrees;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final Matrix matrix = t.getMatrix();
            mCamera.save();
            mCamera.rotateY(mDegrees);
            mCamera.getMatrix(matrix);
            mCamera.restore();
            matrix.preTranslate(-mCenterX, -mCenterY);
            matrix.postTranslate(mCenterX, mCenterY);
        }
    }
}