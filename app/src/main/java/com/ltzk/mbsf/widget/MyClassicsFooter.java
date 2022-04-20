package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.ltzk.mbsf.R;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

/**
 * 描述：
 * 作者： on 2020-3-30 13:19
 * 邮箱：499629556@qq.com
 */
public class MyClassicsFooter extends ClassicsFooter {

    public MyClassicsFooter(Context context) {
        super(context);
        mTitleText.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        mTitleText.setTextColor(context.getResources().getColor(R.color.silver));
    }

    public MyClassicsFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCount(int cur,int total){
        mTextRelease = mTextRelease + cur+" / "+total;
        mTextPulling = mTextPulling + cur+" / "+total;
    }

}
