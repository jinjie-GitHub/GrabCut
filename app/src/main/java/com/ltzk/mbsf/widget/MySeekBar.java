package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.ViewUtil;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSeekBar;

/**
 * 描述：
 * 作者： on 2020-6-24 21:40
 * 邮箱：499629556@qq.com
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class MySeekBar extends AppCompatSeekBar {

    //
    Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint mPaintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
    Rect mRectBg = new Rect();
    int mWidthBg = 0;
    int mWidthArrow = 10;
    int mHeightArrow = 8;
    Paint.FontMetrics mFontMetrics ;

    final int textSize = 15;
    public MySeekBar (Context context) {
        super(context);
    }

    public MySeekBar (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MySeekBar (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        mPaintText.setColor(Color.WHITE);
        mPaintText.setTextSize(ViewUtil.sp2px(getContext(),textSize));

        mPaintBg.setColor(getContext().getColor(R.color.colorPrimary));
        mWidthBg = (int)mPaintText.measureText("000");
        mWidthArrow = ViewUtil.dpToPx(3);
        mHeightArrow = ViewUtil.dpToPx(2);

        //计算数值文本绘制的y坐标基线
         mFontMetrics = mPaintText.getFontMetrics();

        //setPadding(getPaddingLeft(),getPaddingTop()+(int)(mFontMetrics.ascent + mFontMetrics.descent + mFontMetrics.leading) + mHeightArrow ,getPaddingRight(),getPaddingBottom());
        setPadding(getPaddingLeft(),getPaddingTop()+(int)mPaintText.getTextSize() + mHeightArrow ,getPaddingRight(),getPaddingBottom());



    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        //当前数值字符串

        String progress_text = ""+getProgress();
        if(getProgress() == 0){
            progress_text = "1";
        }
        //当前滑块x
        int thumb_x = (int) (( (double)getProgress()/getMax() ) * (double)(getWidth() - getPaddingLeft() - getPaddingRight())) + getPaddingLeft();
        //当前数值内容宽度
        int width_text = (int)mPaintText.measureText(progress_text);
        //数值背景赋值

        mRectBg.top = 0;
        mRectBg.bottom = mRectBg.top + ViewUtil.dpToPx(textSize);
        mRectBg.left = thumb_x - mWidthBg/2;
        mRectBg.right = thumb_x + mWidthBg/2;
        //绘制数值背景
        c.drawRect(mRectBg,mPaintBg);

        Path path = new Path();
        path.moveTo(thumb_x - mWidthArrow/2,mRectBg.bottom);
        path.lineTo(thumb_x+mWidthArrow/2,mRectBg.bottom);
        path.lineTo(thumb_x,mRectBg.bottom+mHeightArrow);
        path.close();
        c.drawPath(path,mPaintBg);

        //计算数值文本开始绘制的x坐标
        int x = mRectBg.left + (mWidthBg - width_text)/2;
        //计算数值文本绘制的y坐标基线
        float baseLine = mRectBg.bottom - mFontMetrics.descent/2;
        //绘制数值
        c.drawText(progress_text, x , baseLine, mPaintText);

    }
}
