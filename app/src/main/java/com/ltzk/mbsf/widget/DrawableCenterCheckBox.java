package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.CheckBox;

/**
 * Created by JinJie on 2021/11/26
 */
public class DrawableCenterCheckBox extends CheckBox {

    public DrawableCenterCheckBox(Context context) {
        super(context);
    }

    public DrawableCenterCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableCenterCheckBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable[] drawables = getCompoundDrawables();
        Drawable drawable = drawables[0];
        int gravity = getGravity();
        int left = 0;
        if (gravity == Gravity.CENTER) {
            left = ((int) (getWidth()
                    - drawable.getIntrinsicWidth()
                    - getPaint().measureText(getText().toString())) / 2);
        }
        drawable.setBounds(left, 0, left + drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
}