package com.ltzk.mbsf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import androidx.annotation.Nullable;

/**
 * Created by JinJie on 2020/11/9
 */
public class BoldTextView extends TextView {

    public BoldTextView(Context context) {
        this(context, null);
    }

    public BoldTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoldTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (getPaint() != null) {
            getPaint().setFakeBoldText(true);
        }
    }
}