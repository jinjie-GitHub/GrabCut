package com.ltzk.mbsf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.ltzk.mbsf.BuildConfig;
import com.ltzk.mbsf.utils.ViewUtil;

/**
 * Created by JinJie on 2020/11/9
 */
public class CurtainView extends FrameLayout implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private static final String TAG = "CurtainView";

    private static final int FLING_MIN_DISTANCE = 0;
    private static final int FLING_MIN_VELOCITY = 0;

    private float mLastX;
    private int mLastWidth = 0;
    private GestureDetector mGestureDetector;

    public CurtainView(Context context) {
        this(context, null);
    }

    public CurtainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurtainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(getContext(), this);
        setOnTouchListener(this);
        setLongClickable(true);
        mLastWidth = ViewUtil.dpToPx(50);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "--->onDown");
        mLastX = e.getRawX();
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "--->onSingleTapUp");
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "e1=" + e1.getRawX() + " e2=" + e2.getRawX() + " distanceX:" + distanceX);
        }

        if (e2.getRawX() > mLastX) {
            // Fling right
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = (int) (getWidth() + Math.abs(distanceX));
            setLayoutParams(params);
        } else {
            if (e2.getRawX() <= mLastWidth || getWidth() <= mLastWidth) {
                this.mLastX = e2.getRawX();
                return false;
            }

            // Fling left
            ViewGroup.LayoutParams params = getLayoutParams();
            params.width = (int) (getWidth() - Math.abs(distanceX));
            setLayoutParams(params);
        }

        this.mLastX = e2.getRawX();
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "--->onFling");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public void onLongPress(MotionEvent e) {

    }
}