package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.ltzk.mbsf.R;

/**
 * 描述：定位遮盖图
 * 作者： on 2018/12/8 14:27
 * 邮箱：499629556@qq.com
 */

public class ZhegaiView extends View {

    private static final String TAG = "ZhegaiView";
    Context context;
    int mWidth,mHeight;
    private Paint paint_bg;
    private Rect rect_bg;

    private Rect rect;
    private int left;
    private int top;
    private int right;
    private int bottom;

    public ZhegaiView(Context context){
        super(context);
        this.context = context;
    }

    public ZhegaiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPaint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }


    /**
     * 外部调用的接口
     * */
    public void setRect(int left,int top,int right,int bottom){
        this.left=left;
        this.top=top;
        this.right=right;
        this.bottom=bottom;

        invalidate();//更新调用onDraw重新绘制
    }


    /**
     * 初始化
     */
    private void initPaint(){
        paint_bg = new Paint();
        paint_bg.setColor(context.getResources().getColor(R.color.transparentBlack));
        paint_bg.setStyle(Paint.Style.FILL);
        left = 0;
        top = 0;
        right = 0;
        bottom = 0;
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //左
        rect_bg=new Rect(0,0,left,mHeight);
        canvas.drawRect(rect_bg,paint_bg);
        //上
        rect_bg=new Rect(left,0,right,top);
        canvas.drawRect(rect_bg,paint_bg);
        //下
        rect_bg=new Rect(left,bottom,right,mHeight);
        canvas.drawRect(rect_bg,paint_bg);
        //右
        rect_bg=new Rect(right,0,mWidth,mHeight);
        canvas.drawRect(rect_bg,paint_bg);
    }

}
