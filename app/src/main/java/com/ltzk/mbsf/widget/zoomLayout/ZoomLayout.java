package com.ltzk.mbsf.widget.zoomLayout;

/**
 * 描述：
 * 作者： on 2019-7-4 17:50
 * 邮箱：499629556@qq.com
 */
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import com.shopgun.android.utils.NumberUtils;
import com.shopgun.android.utils.log.L;
import com.shopgun.android.zoomlayout.ScrollerCompat;
import com.shopgun.android.zoomlayout.ZoomUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class ZoomLayout extends FrameLayout {

    public static final String TAG = ZoomLayout.class.getSimpleName();

    private static final int DEF_ZOOM_DURATION = 250;
    public boolean DEBUG = false;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private SimpleOnGlobalLayoutChangedListener mSimpleOnGlobalLayoutChangedListener;

    private Matrix mScaleMatrix = new Matrix();
    private Matrix mScaleMatrixInverse = new Matrix();
    private Matrix mTranslateMatrix = new Matrix();
    private Matrix mTranslateMatrixInverse = new Matrix();
    // helper array to save heap
    private float[] mMatrixValues = new float[9];

    private float mFocusY;
    private float mFocusX;

    // Helper array to save heap
    private float[] mArray = new float[6];

    // for set scale
    private boolean mAllowOverScale = true;

    RectF mDrawRect = new RectF();
    RectF mViewPortRect = new RectF();

    private FlingRunnable mFlingRunnable;
    private AnimatedZoomRunnable mAnimatedZoomRunnable;
    private Interpolator mAnimationInterpolator = new DecelerateInterpolator();
    private int mZoomDuration = DEF_ZOOM_DURATION;

    // allow parent views to intercept any touch events that we do not consume
    boolean mAllowParentInterceptOnEdge = true;
    // allow parent views to intercept any touch events that we do not consume even if we are in a scaled state
    boolean mAllowParentInterceptOnScaled = false;
    // minimum scale of the content
    private float mMinScale = 1.0f;
    // maximum scale of the content
    private float mMaxScale = 3.0f;

    private boolean mAllowZoom = true;

    // Listeners
    private ZoomDispatcher mZoomDispatcher = new ZoomDispatcher();
    private PanDispatcher mPanDispatcher = new PanDispatcher();
    private List<OnZoomListener> mOnZoomListeners;
    private List<OnPanListener> mOnPanListeners;
    private List<OnTouchListener> mOnTouchListeners;
    private List<OnTapListener> mOnTapListeners;
    private List<OnDoubleTapListener> mOnDoubleTapListeners;
    private List<OnLongTapListener> mOnLongTapListeners;

    public ZoomLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ZoomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ZoomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(21)
    public ZoomLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mGestureListener = new GestureListener();
        mScaleDetector = new ScaleGestureDetector(context, mGestureListener);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            // Quick scale doesn't play nice with the current implementation
            mScaleDetector.setQuickScaleEnabled(false);
        }
        mGestureDetector = new GestureDetector(context, mGestureListener);
        mSimpleOnGlobalLayoutChangedListener = new SimpleOnGlobalLayoutChangedListener();
        getViewTreeObserver().addOnGlobalLayoutListener(mSimpleOnGlobalLayoutChangedListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        ZoomLayout.removeGlobal(this, mSimpleOnGlobalLayoutChangedListener);
        super.onDetachedFromWindow();
    }

    public static void removeGlobal(View v, ViewTreeObserver.OnGlobalLayoutListener listener) {
        ViewTreeObserver obs = v.getViewTreeObserver();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            obs.removeOnGlobalLayoutListener(listener);
        } else {
            obs.removeGlobalOnLayoutListener(listener);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(-getPosX(), -getPosY());
        float scale = getScale();
        canvas.scale(scale, scale, mFocusX, mFocusY);
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {
            // Few issues here, ignore for now.
            // NullPointerException
            // StackOverflowError when drawing childViews
        }
        if (DEBUG) {
            ZoomUtils.debugDraw(canvas, getContext(), getPosX(), getPosY(), mFocusX, mFocusY, getMatrixValue(mScaleMatrixInverse, Matrix.MSCALE_X));
        }
        canvas.restore();
    }

    /**
     * Although the docs say that you shouldn't override this, I decided to do
     * so because it offers me an easy way to change the invalidated area to my
     * likening.
     */
    /*@Override
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        scaledPointsToScreenPoints(dirty);
        float scale = getScale();
        location[0] *= scale;
        location[1] *= scale;
        return super.invalidateChildInParent(location, dirty);
    }*/

    public void scaledPointsToScreenPoints(Rect rect) {
        ZoomUtils.setArray(mArray, rect);
        mArray = scaledPointsToScreenPoints(mArray);
        ZoomUtils.setRect(rect, mArray);
    }

    private void scaledPointsToScreenPoints(RectF rect) {
        ZoomUtils.setArray(mArray, rect);
        mArray = scaledPointsToScreenPoints(mArray);
        ZoomUtils.setRect(rect, mArray);
    }

    public float[] scaledPointsToScreenPoints(float[] a) {
        mScaleMatrix.mapPoints(a);
        mTranslateMatrix.mapPoints(a);
        return a;
    }

    private float[] screenPointsToScaledPoints(float[] a){
        mTranslateMatrixInverse.mapPoints(a);
        mScaleMatrixInverse.mapPoints(a);
        return a;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mArray[0] = ev.getX();
        mArray[1] = ev.getY();
        screenPointsToScaledPoints(mArray);
        ev.setLocation(mArray[0], mArray[1]);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mAllowZoom;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mArray[0] = ev.getX();
        mArray[1] = ev.getY();
        scaledPointsToScreenPoints(mArray);
        ev.setLocation(mArray[0], mArray[1]);

        if (!mAllowZoom) {
            return false;
        }

        final int action = ev.getAction() & MotionEvent.ACTION_MASK;
        dispatchOnTouch(action, ev);

        boolean consumed = mScaleDetector.onTouchEvent(ev);
        consumed = mGestureDetector.onTouchEvent(ev) || consumed;
        if (action == MotionEvent.ACTION_UP) {
            // manually call up
            consumed = mGestureListener.onUp(ev) || consumed;
        }
        return consumed;
    }

    class GestureListener implements ScaleGestureDetector.OnScaleGestureListener,
            GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        private float mScaleOnActionDown;
        private boolean mScrolling = false;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            dispatchOnTab(e);
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            switch (e.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    // re-enable this when quick scale is correctly implemented
//                    if (NumberUtils.isEqual(getScale(), mScaleOnActionDown)) {
                    dispatchOnDoubleTap(e);
//                    }
                    break;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (!mScaleDetector.isInProgress()) {
                dispatchOnLongTap(e);
            }
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            boolean consumed = false;
            if (e2.getPointerCount() == 1 && !mScaleDetector.isInProgress()) {
                // only drag if we have one pointer and aren't already scaling
                if (!mScrolling) {
                    mPanDispatcher.onPanBegin();
                    mScrolling = true;
                }
                consumed = internalMoveBy(distanceX, distanceY, true);
                if (consumed) {
                    mPanDispatcher.onPan();
                }
                if (mAllowParentInterceptOnEdge && !consumed && (!isScaled() || mAllowParentInterceptOnScaled)) {
                    requestDisallowInterceptTouchEvent(false);
                }
            }
            return consumed;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float scale = getScale();
            float newScale = NumberUtils.clamp(mMinScale, scale, mMaxScale);
            if (NumberUtils.isEqual(newScale, scale)) {
                // only fling if no scale is needed - scale will happen on ACTION_UP
                mFlingRunnable = new FlingRunnable(getContext());
                mFlingRunnable.fling((int) velocityX, (int) velocityY);
                ViewCompat.postOnAnimation(ZoomLayout.this, mFlingRunnable);
                return true;
            }
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onDown(MotionEvent e) {
            mScaleOnActionDown = getScale();
            requestDisallowInterceptTouchEvent(true);
            cancelFling();
            cancelZoom();
            return false;
        }

        boolean onUp(MotionEvent e) {
            boolean consumed = false;
            if (mScrolling) {
                mPanDispatcher.onPanEnd();
                mScrolling = false;
                consumed = true;
            }
            if (mAnimatedZoomRunnable == null || mAnimatedZoomRunnable.mFinished) {
                mAnimatedZoomRunnable = new AnimatedZoomRunnable();
                consumed = mAnimatedZoomRunnable.runValidation() || consumed;
            }
            return consumed;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mZoomDispatcher.onZoomBegin(getScale());
            fixFocusPoint(detector.getFocusX(), detector.getFocusY());
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = getScale() * detector.getScaleFactor();
            float scaleFactor = detector.getScaleFactor();
            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor))
                return false;

            internalScale(scale, mFocusX, mFocusY);
            mZoomDispatcher.onZoom(scale);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mAnimatedZoomRunnable = new AnimatedZoomRunnable();
            mAnimatedZoomRunnable.runValidation();
            mZoomDispatcher.onZoomEnd(getScale());
        }
    }

    /**
     * When setting a new focus point, the translations on scale-matrix will change,
     * to counter that we'll first read old translation values, then apply the new focus-point
     * (with the old scale), then read the new translation values. Lastly we'll ensureTranslation
     * out ensureTranslation-matrix by the delta given by the scale-matrix translations.
     * @param focusX focus-focusX in screen coordinate
     * @param focusY focus-focusY in screen coordinate
     */
    private void fixFocusPoint(float focusX, float focusY) {
        mArray[0] = focusX;
        mArray[1] = focusY;
        screenPointsToScaledPoints(mArray);
        // The first scale event translates the content, so we'll counter that ensureTranslation
        float x1 = getMatrixValue(mScaleMatrix, Matrix.MTRANS_X);
        float y1 = getMatrixValue(mScaleMatrix, Matrix.MTRANS_Y);
        internalScale(getScale(), mArray[0], mArray[1]);
        float dX = getMatrixValue(mScaleMatrix, Matrix.MTRANS_X)-x1;
        float dY = getMatrixValue(mScaleMatrix, Matrix.MTRANS_Y)-y1;
        internalMove(dX + getPosX(), dY + getPosY(), false);
    }

    private void cancelFling() {
        if (mFlingRunnable != null) {
            mFlingRunnable.cancelFling();
            mFlingRunnable = null;
        }
    }

    private void cancelZoom() {
        if (mAnimatedZoomRunnable != null) {
            mAnimatedZoomRunnable.cancel();
            mAnimatedZoomRunnable = null;
        }
    }

    /**
     * The rectangle representing the location of the view inside the ZoomView. including scale and translations.
     */
    public RectF getDrawRect() {
        return new RectF(mDrawRect);
    }

    public boolean isAllowOverScale() {
        return mAllowOverScale;
    }

    public void setAllowOverScale(boolean allowOverScale) {
        mAllowOverScale = allowOverScale;
    }

    public boolean isAllowParentInterceptOnEdge() {
        return mAllowParentInterceptOnEdge;
    }

    public void setAllowParentInterceptOnEdge(boolean allowParentInterceptOnEdge) {
        mAllowParentInterceptOnEdge = allowParentInterceptOnEdge;
    }

    public boolean isAllowParentInterceptOnScaled() {
        return mAllowParentInterceptOnScaled;
    }

    public void setAllowParentInterceptOnScaled(boolean allowParentInterceptOnScaled) {
        mAllowParentInterceptOnScaled = allowParentInterceptOnScaled;
    }

    public int getZoomDuration() {
        return mZoomDuration;
    }

    public void setZoomDuration(int zoomDuration) {
        mZoomDuration = zoomDuration < 0 ? DEF_ZOOM_DURATION : zoomDuration;
    }

    public void setZoomInterpolator(Interpolator zoomAnimationInterpolator) {
        mAnimationInterpolator = zoomAnimationInterpolator;
    }

    public float getMaxScale() {
        return mMaxScale;
    }

    public void setMaxScale(float maxScale) {
        mMaxScale = maxScale;
        if (mMaxScale < mMinScale) {
            setMinScale(maxScale);
        }
    }

    public float getMinScale() {
        return mMinScale;
    }

    public void setMinScale(float minScale) {
        mMinScale = minScale;
        if (mMinScale > mMaxScale) {
            setMaxScale(mMinScale);
        }
    }

    public boolean isAllowZoom() {
        return mAllowZoom;
    }

    public void setAllowZoom(boolean allowZoom) {
        mAllowZoom = allowZoom;
    }

    public float getScale() {
        return getMatrixValue(mScaleMatrix, Matrix.MSCALE_X);
    }

    public void setScale(float scale) {
        setScale(scale, true);
    }

    public void setScale(float scale, boolean animate) {
        final View c = getChildAt(0);
        setScale(scale, getRight()/2, getBottom()/2, animate);
    }

    public boolean isTranslating() {
        return mGestureListener.mScrolling;
    }

    public boolean isScaling() {
        return mScaleDetector.isInProgress();
    }

    public boolean isScaled() {
        return !NumberUtils.isEqual(getScale(), 1.0f, 0.05f);
    }

    public void setScale(float scale, float focusX, float focusY, boolean animate) {
        if (!mAllowZoom) {
            return;
        }
        fixFocusPoint(focusX, focusY);
        if (!mAllowOverScale) {
            scale = NumberUtils.clamp(mMinScale, scale, mMaxScale);
        }
        if (animate) {
            mAnimatedZoomRunnable = new AnimatedZoomRunnable();
            mAnimatedZoomRunnable.scale(getScale(), scale, mFocusX, mFocusY, true);
            ViewCompat.postOnAnimation(ZoomLayout.this, mAnimatedZoomRunnable);
        } else {
            mZoomDispatcher.onZoomBegin(getScale());
            internalScale(scale, mFocusX, mFocusY);
            mZoomDispatcher.onZoom(scale);
            mZoomDispatcher.onZoomEnd(scale);
        }
    }

    public boolean moveBy(float dX, float dY) {
        return moveTo(dX + getPosX(), dY + getPosY());
    }

    public boolean moveTo(float posX, float posY) {
        mPanDispatcher.onPanBegin();
        if (internalMove(posX, posY, true)) {
            mPanDispatcher.onPan();
        }
        mPanDispatcher.onPanEnd();
        return true;
    }

    private boolean internalMoveBy(float dx, float dy, boolean clamp) {
        float tdx = dx;
        float tdy = dy;
        if (clamp) {
            RectF bounds = getTranslateDeltaBounds();
            tdx = NumberUtils.clamp(bounds.left, dx, bounds.right);
            tdy = NumberUtils.clamp(bounds.top, dy, bounds.bottom);
        }
//        L.d(TAG, String.format(Locale.US, "clamp: x[ %.2f -> %.2f ], y[ %.2f -> %.2f ]", dx, tdx, dy, tdy));
        float posX = tdx + getPosX();
        float posY = tdy + getPosY();
        if (!NumberUtils.isEqual(posX, getPosX()) ||
                !NumberUtils.isEqual(posY, getPosY())) {
            mTranslateMatrix.setTranslate(-posX, -posY);
            matrixUpdated();
            invalidate();
            return true;
        }
        return false;
    }

    private boolean internalMove(float posX, float posY, boolean clamp) {
//        L.d(TAG, String.format(Locale.US, "internalMove: x[ %.2f -> %.2f ], y[ %.2f -> %.2f ]", getPosX(), posX, getPosY(), posY));
        return internalMoveBy(posX - getPosX(), posY - getPosY(), clamp);
    }

    private RectF getTranslateDeltaBounds() {
        RectF r = new RectF();
        float maxDeltaX = mDrawRect.width() - mViewPortRect.width();
        if (maxDeltaX < 0) {
            float leftEdge = Math.round((mViewPortRect.width() - mDrawRect.width()) / 2);
            if (leftEdge > mDrawRect.left) {
                r.left = 0;
                r.right = leftEdge - mDrawRect.left;
            } else {
                r.left = leftEdge - mDrawRect.left;
                r.right = 0;
            }
        } else {
            r.left = mDrawRect.left - mViewPortRect.left;
            r.right = r.left + maxDeltaX;
        }

        float maxDeltaY = mDrawRect.height() - mViewPortRect.height();
        if (maxDeltaY < 0) {
            float topEdge = Math.round((mViewPortRect.height() - mDrawRect.height()) / 2f);
            if (topEdge > mDrawRect.top) {
                r.top = mDrawRect.top - topEdge;
                r.bottom = 0;
            } else {
                r.top = topEdge - mDrawRect.top;
                r.bottom = 0;
            }
        } else {
            r.top = mDrawRect.top - mViewPortRect.top;
            r.bottom = r.top + maxDeltaY;
        }

        return r;
    }

    /**
     * Gets the closest valid translation point, to the current {@link #getPosX() x} and
     * {@link #getPosY() y} coordinates.
     * @return the closest point
     */
    private PointF getClosestValidTranslationPoint() {
        PointF p = new PointF(getPosX(), getPosY());
        if (mDrawRect.width() < mViewPortRect.width()) {
            p.x += mDrawRect.centerX() - mViewPortRect.centerX();
        } else if (mDrawRect.right < mViewPortRect.right) {
            p.x += mDrawRect.right - mViewPortRect.right;
        } else if (mDrawRect.left > mViewPortRect.left) {
            p.x += mDrawRect.left - mViewPortRect.left;
        }
        if (mDrawRect.height() < mViewPortRect.height()) {
            p.y += mDrawRect.centerY() - mViewPortRect.centerY();
        } else if (mDrawRect.bottom < mViewPortRect.bottom) {
            p.y += mDrawRect.bottom - mViewPortRect.bottom;
        } else if (mDrawRect.top > mViewPortRect.top) {
            p.y += mDrawRect.top - mViewPortRect.top;
        }
        return p;
    }

    private void internalScale(float scale, float focusX, float focusY) {
        mFocusX = focusX;
        mFocusY = focusY;
        mScaleMatrix.setScale(scale, scale, mFocusX, mFocusY);
        matrixUpdated();
        requestLayout();
        invalidate();
    }

    /**
     * Update all variables that rely on the Matrix'es.
     */
    private void matrixUpdated() {
        // First inverse matrixes
        mScaleMatrix.invert(mScaleMatrixInverse);
        mTranslateMatrix.invert(mTranslateMatrixInverse);
        // Update DrawRect - maybe this should be viewPort.left instead of 0?
        ZoomUtils.setRect(mViewPortRect, 0, 0, getWidth(), getHeight());

        final View child = getChildAt(0);
        if (child != null) {
            ZoomUtils.setRect(mDrawRect, child.getLeft(), child.getTop(), child.getRight(), child.getBottom());
            scaledPointsToScreenPoints(mDrawRect);
        } else {
            // If no child is added, then center the drawrect, and let it be empty
            float x = mViewPortRect.centerX();
            float y = mViewPortRect.centerY();
            mDrawRect.set(x, y, x, y);
        }
    }

    /**
     * Get the current x-translation
     */

    public float getPosX() {
        return -getMatrixValue(mTranslateMatrix, Matrix.MTRANS_X);
    }

    /**
     * Get the current y-translation
     */
    public float getPosY() {
        return -getMatrixValue(mTranslateMatrix, Matrix.MTRANS_Y);
    }

    /**
     * Read a specific value from a given matrix
     * @param matrix The Matrix to read a value from
     * @param value The value-position to read
     * @return The value at a given position
     */
    private float getMatrixValue(Matrix matrix, int value) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[value];
    }

    private class AnimatedZoomRunnable implements Runnable {

        boolean mCancelled = false;
        boolean mFinished = false;

        private long mStartTime;
        private float mZoomStart, mZoomEnd, mFocalX, mFocalY;
        private float mStartX, mStartY, mTargetX, mTargetY;

        AnimatedZoomRunnable() {
            mStartTime = System.currentTimeMillis();
        }

        boolean doScale() {
            return !NumberUtils.isEqual(mZoomStart, mZoomEnd);
        }

        boolean doTranslate() {
            return !NumberUtils.isEqual(mStartX, mTargetX) || !NumberUtils.isEqual(mStartY, mTargetY);
        }

        boolean runValidation() {
            float scale = getScale();
            float newScale = NumberUtils.clamp(mMinScale, scale, mMaxScale);
            scale(scale, newScale, mFocusX, mFocusY, true);
            if (mAnimatedZoomRunnable.doScale() || mAnimatedZoomRunnable.doTranslate()) {
                ViewCompat.postOnAnimation(ZoomLayout.this, mAnimatedZoomRunnable);
                return true;
            }
            return false;
        }

        AnimatedZoomRunnable scale(float currentZoom, float targetZoom, float focalX, float focalY, boolean ensureTranslations) {
            mFocalX = focalX;
            mFocalY = focalY;
            mZoomStart = currentZoom;
            mZoomEnd = targetZoom;
            if (doScale()) {
//                log(String.format("AnimatedZoomRunnable.Scale: %s -> %s", mZoomStart, mZoomEnd));
                mZoomDispatcher.onZoomBegin(getScale());
            }
            if (ensureTranslations) {
                mStartX = getPosX();
                mStartY = getPosY();
                boolean scale = doScale();
                if (scale) {
                    mScaleMatrix.setScale(mZoomEnd, mZoomEnd, mFocalX, mFocalY);
                    matrixUpdated();
                }
                PointF p = getClosestValidTranslationPoint();
                mTargetX = p.x;
                mTargetY = p.y;
                if (scale) {
                    mScaleMatrix.setScale(mZoomStart, mZoomStart, ZoomLayout.this.mFocusX, ZoomLayout.this.mFocusY);
                    matrixUpdated();
                }
                if (doTranslate()) {
//                    log(String.format(Locale.US, "AnimatedZoomRunnable.ensureTranslation x[%.0f -> %.0f], y[%.0f -> %.0f]", mStartX, mTargetX, mStartY, mTargetY));
                    mPanDispatcher.onPanBegin();
                }
            }
            return this;
        }

        void cancel() {
            mCancelled = true;
            finish();
        }

        private void finish() {
            if (!mFinished) {
                if (doScale()) {
                    mZoomDispatcher.onZoomEnd(getScale());
                }
                if (doTranslate()) {
                    mPanDispatcher.onPanEnd();
                }
            }
            mFinished = true;
        }

        @Override
        public void run() {

            if (mCancelled || (!doScale() && !doTranslate())) {
                return;
            }

            float t = interpolate();
            if (doScale()) {
                float newScale = mZoomStart + t * (mZoomEnd - mZoomStart);
//                log(String.format(Locale.US, "AnimatedZoomRunnable.run.scale %.2f", newScale));
                internalScale(newScale, mFocalX, mFocalY);
                mZoomDispatcher.onZoom(newScale);
            }
            if (doTranslate()) {
                float x = mStartX + t * (mTargetX - mStartX);
                float y = mStartY + t * (mTargetY - mStartY);
//                log(String.format(Locale.US, "AnimatedZoomRunnable.run.translate x:%.0f, y:%.0f", x, y));
                internalMove(x, y, false);
                mPanDispatcher.onPan();
            }

            // We haven't hit our target scale yet, so post ourselves again
            if (t < 1f) {
                ViewCompat.postOnAnimation(ZoomLayout.this, this);
            } else {
                finish();
            }
        }

        private float interpolate() {
            float t = 1f * (System.currentTimeMillis() - mStartTime) / mZoomDuration;
            t = Math.min(1f, t);
            return mAnimationInterpolator.getInterpolation(t);
        }

    }

    private class FlingRunnable implements Runnable {

        private final ScrollerCompat mScroller;
        private int mCurrentX, mCurrentY;
        private boolean mFinished = false;

        FlingRunnable(Context context) {
            mScroller = ScrollerCompat.getScroller(context);
        }

        void fling(int velocityX, int velocityY) {

            final int startX = Math.round(mViewPortRect.left);
            final int minX, maxX;
            if (mViewPortRect.width() < mDrawRect.width()) {
                minX = Math.round(mDrawRect.left);
                maxX = Math.round(mDrawRect.width() - mViewPortRect.width());
            } else {
                minX = maxX = startX;
            }

            final int startY = Math.round(mViewPortRect.top);
            final int minY, maxY;
            if (mViewPortRect.height() < mDrawRect.height()) {
                minY = Math.round(mDrawRect.top);
                maxY = Math.round(mDrawRect.bottom - mViewPortRect.bottom);
            } else {
                minY = maxY = startY;
            }

            mCurrentX = startX;
            mCurrentY = startY;

//            log(String.format("fling. x[ %s - %s ], y[ %s - %s ]", minX, maxX, minY, maxY));
            // If we actually can move, fling the scroller
            if (startX != maxX || startY != maxY) {
                mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0);
                mPanDispatcher.onPanBegin();
            } else {
                mFinished = true;
            }

        }

        void cancelFling() {
            mScroller.forceFinished(true);
            finish();
        }

        private void finish() {
            if (!mFinished) {
                mPanDispatcher.onPanEnd();
            }
            mFinished = true;
        }

        public boolean isFinished() {
            return mScroller.isFinished();
        }

        @Override
        public void run() {
            if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {

                final int newX = mScroller.getCurrX();
                final int newY = mScroller.getCurrY();

//                log(String.format("mCurrentX:%s, newX:%s, mCurrentY:%s, newY:%s", mCurrentX, newX, mCurrentY, newY));
                if (internalMoveBy(mCurrentX - newX, mCurrentY - newY, true)) {
                    mPanDispatcher.onPan();
                }

                mCurrentX = newX;
                mCurrentY = newY;

                // Post On animation
                ViewCompat.postOnAnimation(ZoomLayout.this, FlingRunnable.this);
            } else {
                finish();
            }
        }
    }

    public void addOnTouchListener(OnTouchListener l) {
        if (mOnTouchListeners == null) {
            mOnTouchListeners = new ArrayList<>();
        }
        mOnTouchListeners.add(l);
    }

    public void removeOnTouchListeners(OnTouchListener listener) {
        if (mOnTouchListeners != null) {
            mOnTouchListeners.remove(listener);
        }
    }

    public void clearOnTouchListener() {
        if (mOnTouchListeners != null) {
            mOnTouchListeners.clear();
        }
    }

    private void dispatchOnTouch(int action, MotionEvent ev) {
        if (mOnTouchListeners != null) {
            for (int i = 0, z = mOnTouchListeners.size(); i < z; i++) {
                OnTouchListener listener = mOnTouchListeners.get(i);
                if (listener != null) {
                    listener.onTouch(ZoomLayout.this, action, new TapInfo(ZoomLayout.this, ev));
                }
            }
        }
    }

    public void addOnTapListener(OnTapListener l) {
        if (mOnTapListeners == null) {
            mOnTapListeners = new ArrayList<>();
        }
        mOnTapListeners.add(l);
    }

    public void removeOnTouchListener(OnTapListener listener) {
        if (mOnTapListeners != null) {
            mOnTapListeners.remove(listener);
        }
    }

    public void clearOnTabListeners() {
        if (mOnTapListeners != null) {
            mOnTapListeners.clear();
        }
    }

    private void dispatchOnTab(MotionEvent ev) {
        if (mOnTapListeners != null) {
            for (int i = 0, z = mOnTapListeners.size(); i < z; i++) {
                OnTapListener listener = mOnTapListeners.get(i);
                if (listener != null) {
                    listener.onTap(ZoomLayout.this, new TapInfo(ZoomLayout.this, ev));
                }
            }
        }
    }

    public void addOnDoubleTapListener(OnDoubleTapListener l) {
        if (mOnDoubleTapListeners == null) {
            mOnDoubleTapListeners = new ArrayList<>();
        }
        mOnDoubleTapListeners.add(l);
    }

    public void removeOnDoubleTapListener(OnDoubleTapListener listener) {
        if (mOnDoubleTapListeners != null) {
            mOnDoubleTapListeners.remove(listener);
        }
    }

    public void clearOnDoubleTapListeners() {
        if (mOnDoubleTapListeners != null) {
            mOnDoubleTapListeners.clear();
        }
    }

    private void dispatchOnDoubleTap(MotionEvent ev) {
        if (mOnDoubleTapListeners != null) {
            for (int i = 0, z = mOnDoubleTapListeners.size(); i < z; i++) {
                OnDoubleTapListener listener = mOnDoubleTapListeners.get(i);
                if (listener != null) {
                    listener.onDoubleTap(ZoomLayout.this, new TapInfo(ZoomLayout.this, ev));
                }
            }
        }
    }

    public void addOnLongTapListener(OnLongTapListener l) {
        if (mOnLongTapListeners == null) {
            mOnLongTapListeners = new ArrayList<>();
        }
        mOnLongTapListeners.add(l);
    }

    public void removeOnLongTapListener(OnLongTapListener listener) {
        if (mOnLongTapListeners != null) {
            mOnLongTapListeners.remove(listener);
        }
    }

    public void clearOnLongTapListeners() {
        if (mOnLongTapListeners != null) {
            mOnLongTapListeners.clear();
        }
    }

    private void dispatchOnLongTap(MotionEvent ev) {
        if (mOnLongTapListeners != null) {
            for (int i = 0, z = mOnLongTapListeners.size(); i < z; i++) {
                OnLongTapListener listener = mOnLongTapListeners.get(i);
                if (listener != null) {
                    listener.onLongTap(ZoomLayout.this, new TapInfo(ZoomLayout.this, ev));
                }
            }
        }
    }

    public void addOnZoomListener(OnZoomListener l) {
        if (mOnZoomListeners == null) {
            mOnZoomListeners = new ArrayList<>();
        }
        mOnZoomListeners.add(l);
    }

    public void removeOnZoomListener(OnZoomListener listener) {
        if (mOnZoomListeners != null) {
            mOnZoomListeners.remove(listener);
        }
    }

    public void clearOnZoomListeners() {
        if (mOnZoomListeners != null) {
            mOnZoomListeners.clear();
        }
    }

    public void addOnPanListener(OnPanListener l) {
        if (mOnPanListeners == null) {
            mOnPanListeners = new ArrayList<>();
        }
        mOnPanListeners.add(l);
    }

    public void removeOnPanListener(OnPanListener listener) {
        if (mOnPanListeners != null) {
            mOnPanListeners.remove(listener);
        }
    }

    public void clearOnPanListeners() {
        if (mOnPanListeners != null) {
            mOnPanListeners.clear();
        }
    }

    public interface OnZoomListener {
        void onZoomBegin(ZoomLayout view, float scale);
        void onZoom(ZoomLayout view, float scale);
        void onZoomEnd(ZoomLayout view, float scale);
    }

    public interface OnPanListener {
        void onPanBegin(ZoomLayout view);
        void onPan(ZoomLayout view);
        void onPanEnd(ZoomLayout view);
    }

    public interface OnTouchListener {
        boolean onTouch(ZoomLayout view, int action, TapInfo info);
    }

    public interface OnTapListener {
        boolean onTap(ZoomLayout view, TapInfo info);
    }

    public interface OnDoubleTapListener {
        boolean onDoubleTap(ZoomLayout view, TapInfo info);
    }

    public interface OnLongTapListener {
        void onLongTap(ZoomLayout view, TapInfo info);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        throw new IllegalStateException("Cannot set OnClickListener, please use OnTapListener.");
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        throw new IllegalStateException("Cannot set OnLongClickListener, please use OnLongTabListener.");
    }

    @Override
    public void setOnTouchListener(View.OnTouchListener l) {
        throw new IllegalStateException("Cannot set OnTouchListener.");
    }

    public static class TapInfo {

        private static final String STRING_FORMAT = "TapInfo[ absX:%.0f, absY:%.0f, relX:%.0f, relY:%.0f, percentX:%.2f, percentY:%.2f, contentClicked:%s ]";

        View mView;
        float mSreenY;
        float mSreenX;
        float mAbsoluteX;
        float mAbsoluteY;
        float mRelativeX;
        float mRelativeY;
        float mPercentX;
        float mPercentY;
        boolean mContentClicked;

        private TapInfo() {
        }

        private TapInfo(ZoomLayout zoomLayout, MotionEvent e) {
            mView = zoomLayout;
            mSreenY = e.getRawY();
            mSreenX = e.getRawX();
            mAbsoluteX = e.getX();
            mAbsoluteY = e.getY();
            zoomLayout.mArray[0] = mAbsoluteX;
            zoomLayout.mArray[1] = mAbsoluteY;
            zoomLayout.screenPointsToScaledPoints(zoomLayout.mArray);
            View v = zoomLayout.getChildAt(0);
            mRelativeX = zoomLayout.mArray[0]-v.getLeft();
            mRelativeY = zoomLayout.mArray[1]-v.getTop();
            mPercentX = mRelativeX / (float) v.getWidth();
            mPercentY = mRelativeY / (float) v.getHeight();
            mContentClicked = zoomLayout.mDrawRect.contains(mAbsoluteX, mAbsoluteY);
        }

        public TapInfo(View view, float x, float y, float relativeX, float relativeY, float percentX, float percentY, boolean contentClicked) {
            mView = view;
            mAbsoluteX = x;
            mAbsoluteY = y;
            mRelativeX = relativeX;
            mRelativeY = relativeY;
            mPercentX = percentX;
            mPercentY = percentY;
            mContentClicked = contentClicked;
        }

        public TapInfo(TapInfo info) {
            this(info.mView,
                    info.mAbsoluteX, info.mAbsoluteY,
                    info.mRelativeX, info.mRelativeY,
                    info.mPercentX, info.mPercentY,
                    info.mContentClicked);
        }

        public View getView() {
            return mView;
        }


        /**
         * 在布局内的绝对横向坐标 0到布局控件度
         * @return
         */
        public float getX() {
            return mAbsoluteX;
        }

        /**
         * 在布局内的绝对纵向向坐标 0到布局控件高度
         * @return
         */
        public float getY() {
            return mAbsoluteY;
        }


        /**
         * 相对位置，去掉移动和缩放之后的的相对位置
         * @return
         */
        public float getRelativeX() {
            return mRelativeX;
        }
        /**
         * 相对位置，去掉移动和缩放之后的的相对位置
         * @return
         */
        public float getRelativeY() {
            return mRelativeY;
        }

        public float getPercentX() {
            return mPercentX;
        }

        public float getPercentY() {
            return mPercentY;
        }

        public float getmSreenY() {
            return mSreenY;
        }

        public void setmSreenY(float mSreenY) {
            this.mSreenY = mSreenY;
        }

        public float getmSreenX() {
            return mSreenX;
        }

        public void setmSreenX(float mSreenX) {
            this.mSreenX = mSreenX;
        }

        public boolean isContentClicked() {
            return mContentClicked;
        }

        @Override
        public String toString() {
            return String.format(Locale.US, STRING_FORMAT,
                    mAbsoluteX, mAbsoluteY, mRelativeX, mRelativeY, mPercentX, mPercentY, mContentClicked);
        }
    }

    private class ZoomDispatcher {

        int mCount = 0;

        void onZoomBegin(float scale) {
            if (mCount++ == 0) {
                if (mOnZoomListeners != null) {
                    for (int i = 0, z = mOnZoomListeners.size(); i < z; i++) {
                        OnZoomListener listener = mOnZoomListeners.get(i);
                        if (listener != null) {
                            listener.onZoomBegin(ZoomLayout.this, scale);
                        }
                    }
                }
            }
        }

        void onZoom(float scale) {
            if (mOnZoomListeners != null) {
                for (int i = 0, z = mOnZoomListeners.size(); i < z; i++) {
                    OnZoomListener listener = mOnZoomListeners.get(i);
                    if (listener != null) {
                        listener.onZoom(ZoomLayout.this, scale);
                    }
                }
            }
        }

        void onZoomEnd(float scale) {
            if (--mCount == 0) {
                if (mOnZoomListeners != null) {
                    for (int i = 0, z = mOnZoomListeners.size(); i < z; i++) {
                        OnZoomListener listener = mOnZoomListeners.get(i);
                        if (listener != null) {
                            listener.onZoomEnd(ZoomLayout.this, scale);
                        }
                    }
                }
            }
        }
    }

    private class PanDispatcher {

        int mCount = 0;

        void onPanBegin() {
            if (mCount++ == 0) {
                if (mOnPanListeners != null) {
                    for (int i = 0, z = mOnPanListeners.size(); i < z; i++) {
                        OnPanListener listener = mOnPanListeners.get(i);
                        if (listener != null) {
                            listener.onPanBegin(ZoomLayout.this);
                        }
                    }
                }
            }
        }

        void onPan() {
            if (mOnPanListeners != null) {
                for (int i = 0, z = mOnPanListeners.size(); i < z; i++) {
                    OnPanListener listener = mOnPanListeners.get(i);
                    if (listener != null) {
                        listener.onPan(ZoomLayout.this);
                    }
                }
            }
        }

        void onPanEnd() {
            if (--mCount == 0) {
                if (mOnPanListeners != null) {
                    for (int i = 0, z = mOnPanListeners.size(); i < z; i++) {
                        OnPanListener listener = mOnPanListeners.get(i);
                        if (listener != null) {
                            listener.onPanEnd(ZoomLayout.this);
                        }
                    }
                }
            }
        }
    }

    private class SimpleOnGlobalLayoutChangedListener implements ViewTreeObserver.OnGlobalLayoutListener {

        private int mLeft, mTop, mRight, mBottom;

        @Override
        public void onGlobalLayout() {
            int oldL = mLeft;
            int oldT = mTop;
            int oldR = mRight;
            int oldB = mBottom;
            mLeft = getLeft();
            mTop = getTop();
            mRight = getRight();
            mBottom = getBottom();
            boolean changed = oldL != mLeft || oldT != mTop || oldR != mRight || oldB != mBottom;
            if (changed) {
                matrixUpdated();
                PointF p = getClosestValidTranslationPoint();
                internalMove(p.x, p.y, false);
            }
        }

    }

    private void log(String msg) {
        if (DEBUG) {
            L.d(TAG, msg);
        }
    }

    private void printMatrixInfo(String tag, boolean regulare, boolean inverted) {
        if (regulare) {
            log(String.format("%s: mScaleMatrix            %s", tag, ZoomUtils.getMatrixBasicInfo(mScaleMatrix)));
            log(String.format("%s: mTranslateMatrix        %s", tag, ZoomUtils.getMatrixBasicInfo(mTranslateMatrix)));
        }
        if (inverted) {
            log(String.format("%s: mScaleMatrixInverse     %s", tag, ZoomUtils.getMatrixBasicInfo(mScaleMatrixInverse)));
            log(String.format("%s: mTranslateMatrixInverse %s", tag, ZoomUtils.getMatrixBasicInfo(mTranslateMatrixInverse)));
        }
    }

    private void printViewRects(String tag) {
        log(ZoomUtils.getViewRectInfo(tag, "ViewRect", mViewPortRect));
        log(ZoomUtils.getViewRectInfo(tag, "DrawRect", mDrawRect));
    }

}