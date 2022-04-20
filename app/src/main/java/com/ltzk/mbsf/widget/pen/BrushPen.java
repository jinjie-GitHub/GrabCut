package com.ltzk.mbsf.widget.pen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import com.ltzk.mbsf.R;

/**
 * 毛笔
 * Created by JinJie on 2020/9/22
 */
public class BrushPen extends BasePen {

    private Bitmap mBitmap;
    private Rect mSrcRect = new Rect();
    private RectF mDstRect = new RectF();
    private Context mContext;

    public BrushPen(Context context) {
        this.mContext = context;
    }

    @Override
    public void setPaint(Paint paint) {
        super.setPaint(paint);
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.brush_00);
        mBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmap);
        Paint penPaint = new Paint();
        penPaint.setColorFilter(new PorterDuffColorFilter(mPaint.getColor(), PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, 0, 0, penPaint);
        mSrcRect.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
    }

    @Override
    protected void doPreDraw(Canvas canvas) {
        for (int i = 1; i < mHWPointList.size(); i++) {
            ControllerPoint point = mHWPointList.get(i);
            drawToPoint(canvas, point, mPaint);
            mCurPoint = point;
        }
    }

    //TODO add
    private ControllerPoint getWithPointAlphaPoint(ControllerPoint point) {
        ControllerPoint nPoint = new ControllerPoint();
        nPoint.x = point.x;
        nPoint.y = point.y;
        nPoint.width = point.width;
        return nPoint;
    }

    @Override
    protected void doMove(double curDis) {
        int steps = 1 + (int) curDis / STEP_FACTOR;
        double step = 1.0 / steps;
        for (double t = 0; t < 1.0; t += step) {
            ControllerPoint point = mBezier.getPoint(t);
            //point = getWithPointAlphaPoint(point);
            mHWPointList.add(point);
        }
    }

    @Override
    protected void doDraw(Canvas canvas, ControllerPoint point, Paint paint) {
        /*drawLine(canvas,
                mCurPoint.x, mCurPoint.y, mCurPoint.width, mCurPoint.alpha,
                point.x, point.y, point.width, point.alpha,
                paint);*/
        drawLine(canvas,
                mCurPoint.x, mCurPoint.y, mCurPoint.width,
                point.x, point.y, point.width,
                paint);
    }

    //TODO add
    private void drawLine(Canvas canvas, double x0, double y0, double w0, int a0, double x1, double y1, double w1, int a1, Paint paint) {
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int factor = 2;
        if (paint.getStrokeWidth() < 6) {
            factor = 1;
        } else if (paint.getStrokeWidth() > 60) {
            factor = 3;
        }
        int steps = 1 + (int) (curDis / factor);
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double deltaA = (a1 - a0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;
        double a = a0;

        for (int i = 0; i < steps; i++) {
            if (w < 1.5) {
                w = 1.5;
            }
            mDstRect.set((float) (x - w / 2.0f),
                    (float) (y - w / 2.0f),
                    (float) (x + w / 2.0f),
                    (float) (y + w / 2.0f));
            paint.setAlpha((int) (a / 1.0f));
            canvas.drawBitmap(mBitmap, mSrcRect, mDstRect, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
            a += deltaA;
        }
    }

    /**
     * 绘制方法，实现笔锋效果
     */
    @Deprecated
    private void drawLine(Canvas canvas, double x0, double y0, double w0, double x1, double y1, double w1, Paint paint) {
        //求两个数字的平方根 x的平方+y的平方在开方记得X的平方+y的平方=1，这就是一个圆
        double curDis = Math.hypot(x0 - x1, y0 - y1);
        int steps;
        //绘制的笔的宽度是多少，绘制多少个椭圆
        if (paint.getStrokeWidth() < 6) {
            steps = 1 + (int) (curDis / 2);
        } else if (paint.getStrokeWidth() > 60) {
            steps = 1 + (int) (curDis / 4);
        } else {
            steps = 1 + (int) (curDis / 3);
        }
        double deltaX = (x1 - x0) / steps;
        double deltaY = (y1 - y0) / steps;
        double deltaW = (w1 - w0) / steps;
        double x = x0;
        double y = y0;
        double w = w0;

        for (int i = 0; i < steps; i++) {
            RectF oval = new RectF();
            float top = (float) (y - w / 2.0f);
            float left = (float) (x - w / 2.0f);
            float right = (float) (x + w / 2.0f);
            float bottom = (float) (y + w / 2.0f);
            oval.set(left, top, right, bottom);
            //最基本的实现，通过点控制线，绘制椭圆
            //canvas.drawOval(oval, paint);
            //最基本的实现，通过点控制线，纹理贴片
            canvas.drawBitmap(mBitmap, mSrcRect, oval, paint);
            x += deltaX;
            y += deltaY;
            w += deltaW;
        }
    }
}