package com.ltzk.mbsf.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import com.ltzk.mbsf.R;

/**
 * Created by JinJie on 2021/03/16
 */
public class UnderLineRadioButton extends RadioButton {

    private Paint mLinePaint;
    private int   mWidth, mHeight;
    private       float mSpaceHeight       = 0f;
    private final int   COLOR_0070C9       = Color.parseColor("#0070C9");
    private final int   DEFAULT_TEXT_COLOR = Color.BLACK,
                        DEFAULT_LINE_COLOR = Color.TRANSPARENT;
    private final int CHECK_TEXT_COLOR     = COLOR_0070C9,
                         CHECK_LINE_COLOR  = COLOR_0070C9;

    private float mLineWidth, mLineHeight;
    private int mTextDefaultColor = -0x001,
                  mTextCheckColor = -0x001,
                mLineDefaultColor = -0x001,
                  mLineCheckColor = -0x001;
    private float mLineRadius;
    private final RectF mRectF = new RectF();

    public UnderLineRadioButton(Context context) {
        super(context);
        init(context, null);
    }

    public UnderLineRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public UnderLineRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.FILL);

        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UnderLineRadioBtn);
        try {
            mLineWidth = typedArray.getDimension(R.styleable.UnderLineRadioBtn_lineW, 0f);
        } catch (Exception e) {
            mLineWidth = typedArray.getFloat(R.styleable.UnderLineRadioBtn_lineW, 0f);
        }
        mLineHeight = typedArray.getDimension(R.styleable.UnderLineRadioBtn_lineH, 0f);

        mTextDefaultColor = typedArray.getColor(R.styleable.UnderLineRadioBtn_textDefaultColor, DEFAULT_TEXT_COLOR);
        mTextCheckColor = typedArray.getColor(R.styleable.UnderLineRadioBtn_textCheckColor, CHECK_TEXT_COLOR);
        mLineDefaultColor = typedArray.getColor(R.styleable.UnderLineRadioBtn_lineDefaultColor, DEFAULT_LINE_COLOR);
        mLineCheckColor = typedArray.getColor(R.styleable.UnderLineRadioBtn_lineCheckColor, CHECK_LINE_COLOR);
        mLineRadius = typedArray.getDimension(R.styleable.UnderLineRadioBtn_lineRadius, 0f);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

        if (mLineHeight > 0) {
            mHeight += mLineHeight;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (getButtonDrawable() == null) {
                    mSpaceHeight = 10f;
                }
            }
            mHeight += mSpaceHeight;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mTextCheckColor != -0x001 && mTextDefaultColor != -0x001) {
            setTextBold(isChecked());
            setTextColor(isChecked() ? mTextCheckColor : mTextDefaultColor);
        }

        if (mLineHeight <= 0)//控制下划线是否显示
            return;

        if (mLineWidth <= 0 || mLineWidth == 1) {
            mLineWidth = mWidth;
        } else if (mLineWidth < 1) {
            mLineWidth = mWidth * mLineWidth;
        }

        float left, top = mHeight - mLineHeight, right, bottom = mHeight;
        if (mLineWidth < mWidth) {
            left = (mWidth - mLineWidth) / 2;
            right = (mWidth - mLineWidth) / 2 + mLineWidth;
        } else {
            left = 0f;
            right = mWidth;
        }

        mLinePaint.setColor(isChecked() ? mLineCheckColor : mLineDefaultColor);

        if (mLineRadius > 0) {
            mRectF.set(left, top, right, bottom);
            canvas.drawRoundRect(mRectF, mLineRadius, mLineRadius, mLinePaint);
        } else {
            canvas.drawRect(left, top, right, bottom, mLinePaint);
        }
    }

    /**
     * 设置下划线的圆角
     */
    public void setLineRadius(float LineRadius) {
        mLineRadius = LineRadius;
        invalidate();
    }

    /**
     * 设置下划线的默认/选中颜色
     */
    public void setLineColor(int LineDefaultColor, int LineCheckColor) {
        mLineDefaultColor = LineDefaultColor;
        mLineCheckColor = LineCheckColor;
        invalidate();
    }

    /**
     * 设置下划线的选中颜色
     */
    public void setLineCheckColor(int LineCheckColor) {
        setLineColor(mLineDefaultColor, LineCheckColor);
    }

    /**
     * 设置下划线的默认颜色
     */
    public void setLineDefaultColor(int LineDefaultColor) {
        setLineColor(LineDefaultColor, mLineCheckColor);
    }

    /**
     * 设置文字的默认/选中颜色
     */
    public void setTextColor(int TextDefaultColor, int TextCheckColor) {
        mTextDefaultColor = TextDefaultColor;
        mTextCheckColor = TextCheckColor;
        invalidate();
    }

    /**
     * 设置文字的选中颜色
     */
    public void setTextCheckColor(int TextCheckColor) {
        setTextColor(mTextDefaultColor, TextCheckColor);
    }

    /**
     * 设置文字的默认颜色
     */
    public void setTextDefaultColor(int TextDefaultColor) {
        setTextColor(TextDefaultColor, mTextCheckColor);
    }

    /**
     * 设置线的宽度
     */
    public void setLineWidth(float lineWidth) {
        mLineWidth = lineWidth;
        invalidate();
    }

    /**
     * 设置线的高度
     */
    public void setLineHeight(float lineHeight) {
        mLineHeight = lineHeight;
        invalidate();
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        setTextBold(checked);
        invalidate();
    }

    private final void setTextBold(boolean isBold) {
        try {
            if (this != null) {
                final Paint paint = this.getPaint();
                if (paint != null) {
                    paint.setFakeBoldText(isBold);
                }
            }
        } catch (Throwable t) {}
    }
}