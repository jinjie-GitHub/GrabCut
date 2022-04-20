package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.ltzk.mbsf.utils.ViewUtil;

/**
 * Created by JinJie on 2021/11/17
 */
public class SimpleDraw extends View {
    public static final int COLOR_GRAY      = Color.parseColor("#ffc0c0c0");
    public static final int COLOR_BLUE      = Color.parseColor("#ff0070C9");

    public static final int STYLE_IN        = 0;
    public static final int STYLE_INTERSECT = 1;
    public static final int STYLE_OUT       = 2;

    private Paint mPaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mStyle = STYLE_IN;

    public SimpleDraw(Context context) {
        this(context, null);
    }

    public SimpleDraw(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleDraw(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(COLOR_GRAY);
        mPaintRect.setStrokeWidth(2);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setStrokeCap(Paint.Cap.SQUARE);// 线帽，即画的线条两端是否带有圆角

        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(COLOR_GRAY);
        mPaintLine.setStrokeWidth(2);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeCap(Paint.Cap.BUTT);
        mPaintLine.setStrokeMiter(0f);
        mPaintLine.setStrokeJoin(Paint.Join.MITER);
        mPaintLine.setPathEffect(new DashPathEffect(new float[]{10, 5, 10, 5}, 0));
    }

    public void setPaintColor(int color) {
        mPaintLine.setColor(color);
        mPaintRect.setColor(color);
        invalidate();
    }

    public void setType(int style) {
        mStyle = style;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int viewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        final int viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int smaLen = ViewUtil.dpToPx(12);
        final int bigLen = ViewUtil.dpToPx(30);
        int dy = 0;

        switch (mStyle) {
            case STYLE_IN:
                dy = 0;
                break;
            case STYLE_INTERSECT:
                dy = smaLen / 2;
                break;
            case STYLE_OUT:
                dy = smaLen;
                break;
        }

        float left1 = ViewUtil.dpToPx(6);
        float top1 = ViewUtil.dpToPx(10) + dy;
        float right1 = left1 + bigLen - dy;
        float bottom1 = top1 + bigLen - dy;

        canvas.drawRect(left1, top1, right1, bottom1, mPaintRect);

        if (mStyle == STYLE_IN) {
            final float left2 = right1 - smaLen - 6;
            final float top2 = top1 + 6;
            final float right2 = right1 - 6;
            final float bottom2 = top2 + smaLen;
            canvas.drawRect(left2, top2, right2, bottom2, mPaintLine);
        } else if (mStyle == STYLE_INTERSECT) {
            final float top2 = top1 - smaLen / 2;
            final float left2 = right1 - smaLen / 2;
            final float right2 = left2 + smaLen;
            final float bottom2 = top2 + smaLen;
            canvas.drawRect(left2, top2, right2, bottom2, mPaintLine);
        } else if (mStyle == STYLE_OUT) {
            final float left2 = right1;
            final float top2 = top1 - smaLen;
            final float right2 = left2 + smaLen;
            final float bottom2 = top2 + smaLen;
            canvas.drawRect(left2, top2, right2, bottom2, mPaintLine);
        }
    }
}