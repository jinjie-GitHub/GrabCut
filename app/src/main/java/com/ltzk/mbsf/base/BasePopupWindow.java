package com.ltzk.mbsf.base;

import android.app.Activity;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * 描述：
 * 作者： on 2019/1/22 21:59
 * 邮箱：499629556@qq.com
 */

public class BasePopupWindow extends PopupWindow {
    protected Activity activity;

    /**
     * 控制弹出popupView屏幕变暗与恢复
     */
    protected void darkenBackgroud(Activity activity, Float bgcolor) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = bgcolor;
        if (bgcolor == 1) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,可能出现黑屏的bug
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug
        }
        activity.getWindow().setAttributes(lp);
    }
}
