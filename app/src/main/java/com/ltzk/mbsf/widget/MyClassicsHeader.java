package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ltzk.mbsf.R;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;


/**
 * 描述：
 * 作者： on 2020-3-30 11:44
 * 邮箱：499629556@qq.com
 */
public class MyClassicsHeader extends ClassicsHeader {


    public MyClassicsHeader(Context context) {
        super(context);
        mTitleText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTitleText.setTextColor(context.getResources().getColor(R.color.silver));

        mLastUpdateText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mLastUpdateText.setTextColor(context.getResources().getColor(R.color.silver));
    }

    public MyClassicsHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



}
