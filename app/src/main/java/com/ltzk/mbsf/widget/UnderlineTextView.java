package com.ltzk.mbsf.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;


import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.ViewUtil;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * 描述：
 * 作者： on 2019-10-12 17:49
 * 邮箱：499629556@qq.com
 */
public class UnderlineTextView extends AppCompatTextView {

    //Paint即画笔，在绘图过程中起到了极其重要的作用，画笔主要保存了颜色，
    //样式等绘制信息，指定了如何绘制文本和图形，画笔对象有很多设置方法，
    //大体上可以分为两类，一类与图形绘制相关，一类与文本绘制相关
    private final Paint paint = new Paint();
    //下划线高度
    private int underlineHeight = 0;
    private int underlinePadding = ViewUtil.dpToPx(5);
    private int underlineDash = ViewUtil.dpToPx(3);
    //下划线颜色
    private int underLineColor;
    //通过new创建实例是调用这个构造函数
    //这种情况下需要添加额外的一些函数供外部来控制属性，如set*(...);
    public UnderlineTextView(Context context) {
        this(context, null);
    }
    //通过XML配置但不定义style时会调用这个函数
    public UnderlineTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        //获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UnderlineTextView);
        //获取具体属性值
        underLineColor = typedArray.getColor(R.styleable.UnderlineTextView_underline_color, getTextColors().getDefaultColor());
        underlineHeight = (int)typedArray.getDimension(R.styleable.UnderlineTextView_underline_height,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        /*underlinePadding = (int)typedArray.getDimension(R.styleable.UnderlineTextView_underline_padding,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        underlineDash = (int)typedArray.getDimension(R.styleable.UnderlineTextView_underline_dash,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));*/


    }
    //通过XML配置且定义样式时会调用这个函数
    public UnderlineTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    Paint mPaint;
    Path mPath;
    {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //默认使用textview当前颜色
        mPaint.setColor(getResources().getColor(R.color.gray));
        mPaint.setStrokeWidth(ViewUtil.dpToPx(1));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[] {underlineDash, underlineDash}, 0));
        mPath = new Path();
        //设置虚线距离
        setPadding(0,underlinePadding,0,underlinePadding);
    }


    //绘制下划线
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        int centerY = getHeight();
        mPath.reset();
        mPath.moveTo(0, centerY);
        mPath.lineTo(getWidth(), centerY);
        canvas.drawPath(mPath, mPaint);
    }
}
