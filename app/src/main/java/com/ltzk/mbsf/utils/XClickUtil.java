package com.ltzk.mbsf.utils;

import android.view.View;

/**
 * 描述：
 * 作者： on 2019/3/16 22:39
 * 邮箱：499629556@qq.com
 */

public class XClickUtil {

    public static final long intervalMillis = 1000;

    /**
     * 最近一次点击的时间
     */
    private static long mLastClickTime;
    /**
     * 最近一次点击的控件ID
     */
    private static int mLastClickViewId;

    /**
     * 是否是快速点击
     *
     * @param v  点击的控件
     * @param intervalMillis  时间间期（毫秒）
     * @return  true:是，false:不是
     */
    public static boolean isFastDoubleClick(View v, long intervalMillis) {
        int viewId = v.getId();
        long time = System.currentTimeMillis();
        long timeInterval = Math.abs(time - mLastClickTime);
        if (timeInterval < intervalMillis && viewId == mLastClickViewId) {
            return true;
        } else {
            mLastClickTime = time;
            mLastClickViewId = viewId;
            return false;
        }
    }

    public static boolean isFastDoubleClick(View v) {
        return isFastDoubleClick(v,intervalMillis);
    }
}
