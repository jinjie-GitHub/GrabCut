package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.appcompat.widget.AppCompatImageView;
import com.ltzk.mbsf.utils.BitmapHelper;
import com.ltzk.mbsf.utils.BitmapUtils;
import com.ltzk.mbsf.utils.CropUtils;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.MySPUtils;

/**
 * Created by JinJie
 */
public class TouchImageView extends AppCompatImageView {
    private static final String TAG = "TouchImageView";

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mMode = NONE;

    private final PointF mPointF = new PointF();
    private final Matrix mMatrix = new Matrix();
    private final Matrix mMatrixNow = new Matrix();
    private final Matrix mSavedMatrix = new Matrix();
    private final Paint mPaint = new Paint();
    private final Paint mCircle = new Paint();

    private float mLastX = 0;
    private float mLastY = 0;
    private float mLastDist = 1;
    private int mWidthScreen;
    private int mHeightScreen;

    private Bitmap mBitmap;
    private Bitmap mTempBitmap;
    private Runnable mRunnable;
    private long mLastOperatedTime = 0;

    private int mStatus = 0;
    private boolean hasFocus = false;
    private boolean isRotate = false;
    private boolean isColorImage = false;
    private float mScale = 0.7f;
    private float sx = 0, sy = 0;

    private RotateGestureDetector mRotateDetector;

    public TouchImageView(Context context) {
        super(context);
        init();
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TouchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        sx = MySPUtils.getMatrixSX(getContext());
        sy = MySPUtils.getMatrixSY(getContext());
        hasFocus = MySPUtils.getGravityState(getContext());
        mWidthScreen = CropUtils.getScreenWidth(getContext());
        mHeightScreen = mWidthScreen;
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mCircle.setColor(MySPUtils.getPaintColor(getContext()));

        mRotateDetector = new RotateGestureDetector(mRotateListener);
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public boolean isRotate() {
        return isRotate;
    }

    public boolean isCompared() {
        return mBitmap != null;
    }

    public void setClick(Runnable r) {
        this.mRunnable = r;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        this.mStatus = status;
    }

    public void setColorImage(boolean colorImage) {
        isColorImage = colorImage;
    }

    public void setPaintAlpha(int a) {
        mPaint.setAlpha(a);
        invalidate();
    }

    public void setPaintColor(int color) {
        mPaint.setColor(color);
        mBitmap = tintBitmap(mBitmap, color);
        invalidate();
    }

    public void setGravity(boolean isFocus) {
        this.hasFocus = isFocus;
        mCircle.setAntiAlias(true);
        mCircle.setStyle(Paint.Style.FILL);
        mCircle.setColor(MySPUtils.getPaintColor(getContext()));
        setImageBitmap(mTempBitmap);
    }

    public void updateGravity() {
        if (hasFocus) {
            setGravity(true);
        }
    }

    public void setMirrorRotate(boolean isChecked) {
        isRotate = isChecked;
        mBitmap = convertBitmap(mBitmap);
        invalidate();
    }

    public void setImageBitmap(final Bitmap bitmap) {
        if (bitmap != null) {
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }
            mTempBitmap = bitmap;
            mBitmap = newBitmap(bitmap, mScale);
            if (isRotate) {
                mBitmap = convertBitmap(mBitmap);
            }
            if (sx != 0 && sy != 0) {
                final int width = mBitmap.getWidth();
                final int height = mBitmap.getHeight();
                mMatrix.postScale(sx, sy);
                mMatrix.postTranslate((mWidthScreen - sx * width) / 2, (mHeightScreen - sy * height) / 2);
                sx = sy = 0;
            }
            invalidate();
        }
    }

    /**
     * 对比图片
     */
    public void setImageBitmap2(final Bitmap bitmap) {
        if (bitmap != null) {
            hasFocus = false;
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }
            mBitmap = newBitmap(bitmap, 0.8f);
            invalidate();
        }
    }

    public void setImageBitmap3(final Bitmap bitmap) {
        final float scale = 0.696f;
        mScale = scale;
        setImageBitmap(bitmap);
        mScale = 0.7f;
    }

    private Bitmap newBitmap(Bitmap srcBitmap, float scale) {
        final int width = srcBitmap.getWidth();
        final int height = srcBitmap.getHeight();
        final float[] point = getGravityPoint(srcBitmap);
        if (mStatus == 1) {
            srcBitmap = BitmapHelper.INSTANCE.bitmap2Gray(srcBitmap);
        }

        if (hasFocus) {
            Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawBitmap(srcBitmap, new Matrix(), paint);
            canvas.drawCircle(point[0], point[1], 30, mCircle);
            srcBitmap = newBitmap;
        }

        if (scale != 1.0f) {
            float scaleWidth = ((float) mWidthScreen) / width;
            float scaleHeight = ((float) mHeightScreen) / height;
            scale = Math.min(scaleWidth * mScale, scaleHeight * mScale);
        }

        Bitmap bitmap = Bitmap.createBitmap(mWidthScreen, mHeightScreen, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(mWidthScreen / 2 - scale * width / 2, mHeightScreen / 2 - scale * height / 2);
        canvas.drawBitmap(srcBitmap, matrix, mPaint);
        return bitmap;
    }

    private Bitmap convertBitmap(Bitmap srcBitmap) {
        Canvas canvas = new Canvas();
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        Bitmap newBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, mWidthScreen, mHeightScreen, matrix, true);
        canvas.drawBitmap(newBitmap, matrix, mPaint);
        if (srcBitmap != null) {
            srcBitmap.recycle();
            srcBitmap = null;
        }
        return newBitmap;
    }

    public Bitmap convertBitmap2(Bitmap srcBitmap) {
        Canvas canvas = new Canvas();
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1);
        Bitmap newBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
        canvas.drawBitmap(newBitmap, matrix, mPaint);
        return newBitmap;
    }

    public Bitmap tintBitmap(Bitmap inBitmap, int tintColor) {
        if (inBitmap == null) {
            return null;
        }
        Bitmap outBitmap = Bitmap.createBitmap(inBitmap.getWidth(), inBitmap.getHeight(), inBitmap.getConfig());
        Canvas canvas = new Canvas(outBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(inBitmap, 0, 0, paint);
        if (inBitmap != null) {
            inBitmap.recycle();
            inBitmap = null;
        }
        return outBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidthScreen, mHeightScreen);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, mMatrix, mPaint);
        }
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || mBitmap == null) {
            return false;
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastOperatedTime = System.currentTimeMillis();
                mMode = DRAG;
                mLastX = event.getX();
                mLastY = event.getY();
                mSavedMatrix.set(mMatrix);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mMode = ZOOM;
                mLastDist = distance(event);
                mSavedMatrix.set(mMatrix);
                midPoint(mPointF, event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == ZOOM) {
                    if (isRotateEnable) mRotateDetector.onTouchEvent(event);
                    mMatrixNow.set(mSavedMatrix);
                    final float newDist = distance(event);
                    final float scale = newDist / mLastDist;
                    mMatrixNow.postScale(scale, scale, mPointF.x, mPointF.y);
                    if (matrixCheck()) {
                        mMatrix.set(mMatrixNow);
                        invalidate();
                    }
                } else if (mMode == DRAG) {
                    if (isRotateEnable) mRotateDetector.onTouchEvent(event);
                    mMatrixNow.set(mSavedMatrix);
                    mMatrixNow.postTranslate(event.getX() - mLastX, event.getY() - mLastY);
                    if (matrixCheck()) {
                        mMatrix.set(mMatrixNow);
                    } else {
                        mSavedMatrix.set(mMatrix);
                        mMatrixNow.set(mMatrix);
                        mLastX = event.getX();
                        mLastY = event.getY();
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                Logger.d(String.valueOf(System.currentTimeMillis() - mLastOperatedTime));
                if (System.currentTimeMillis() - mLastOperatedTime < 200) {
                    if (mRunnable != null) {
                        mRunnable.run();
                    }
                }
                break;
        }
        return true;
    }

    /**
     * 边界检测
     */
    private final boolean matrixCheck() {
        final float[] poi = new float[9];
        mMatrixNow.getValues(poi);

        float x1 = poi[0] * 0 + poi[1] * 0 + poi[2];
        float y1 = poi[3] * 0 + poi[4] * 0 + poi[5];

        float x2 = poi[0] * mBitmap.getWidth() + poi[1] * 0 + poi[2];
        float y2 = poi[3] * mBitmap.getWidth() + poi[4] * 0 + poi[5];

        float x3 = poi[0] * 0 + poi[1] * mBitmap.getHeight() + poi[2];
        float y3 = poi[3] * 0 + poi[4] * mBitmap.getHeight() + poi[5];

        float x4 = poi[0] * mBitmap.getWidth() + poi[1] * mBitmap.getHeight() + poi[2];
        float y4 = poi[3] * mBitmap.getWidth() + poi[4] * mBitmap.getHeight() + poi[5];

        double width = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        if (width < mWidthScreen / 3 || width > mWidthScreen * 2) {
            return false;
        }

        //左边
        if (x2 <= mWidthScreen * 0.5) {
            return false;
        }

        //右边
        if (x1 >= mWidthScreen * 0.5) {
            return false;
        }

        //上边
        if (y3 <= mWidthScreen * 0.5) {
            return false;
        }

        //下边
        if (y1 >= mWidthScreen * 0.5) {
            return false;
        }

        MySPUtils.setMatrixSX(getContext(), poi[0]);
        MySPUtils.setMatrixSY(getContext(), poi[4]);
        return true;
    }

    /**
     * 计算两点之间距离
     */
    private float distance(final MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 记录两手指中间距离
     */
    private void midPoint(final PointF pointF, final MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        pointF.set(x / 2, y / 2);
    }

    /**
     * 只统计字的坐标，不统计背景
     */
    private final float[] getGravityPoint(Bitmap bitmap) {
        if (!isColorImage) {
            bitmap = BitmapUtils.getWhiteBgBitmap(bitmap);
        }
        final int sWidth = bitmap.getWidth();
        final int sHeight = bitmap.getHeight();
        int sumX1 = 0, sumY1 = 0, sumX2 = 0, sumY2 = 0;
        int n1 = 0, n2 = 0;

        int[] sPixels = new int[sWidth * sHeight];
        bitmap.getPixels(sPixels, 0, sWidth, 0, 0, sWidth, sHeight);

        int sIndex = 0;
        for (int sRow = 0; sRow < sHeight; sRow++) {
            sIndex = sRow * sWidth;
            for (int sCol = 0; sCol < sWidth; sCol++) {
                int color = sPixels[sIndex];
                int r = Color.red(color);
                int g = Color.green(color);
                int b = Color.blue(color);
                int gray = (int) ((float) r * 0.3 + (float) g * 0.59 + (float) b * 0.11);
                if (gray <= BitmapHelper.INSTANCE.mThresh) {
                    sumX1 += sCol;
                    sumY1 += sRow;
                    n1++;
                } else {
                    sumX2 += sCol;
                    sumY2 += sRow;
                    n2++;
                }
                sIndex++;
            }
        }

        if (n1 == 0 || n2 == 0) {
            return new float[]{mWidthScreen / 2, mHeightScreen / 2};
        }

        final float cx = n1 < n2 ? (sumX1 / n1) : (sumX2 / n2);
        final float cy = n1 < n2 ? (sumY1 / n1) : (sumY2 / n2);
        return new float[]{cx, cy};
    }

    //-------2021/11/15增加图片旋转---------
    private boolean isRotateEnable = false;

    /**
     * 启用旋转功能
     */
    public void enableRotate() {
        isRotateEnable = true;
    }

    /**
     * 禁用旋转功能
     */
    public void disableRotate() {
        isRotateEnable = false;
    }

    private OnRotateListener mRotateListener = (degrees, focusX, focusY) -> {
        mSavedMatrix.postRotate(degrees, focusX, focusY);
    };
}