package com.ltzk.mbsf.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ltzk.mbsf.MainApplication;

public final class ViewUtil {

    public static float pxToDp(float px) {
        float densityDpi = Resources.getSystem().getDisplayMetrics().densityDpi;
        return px / (densityDpi / 160f);
    }

    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static int dpToPx(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public static float dpToPxF(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return dp * density;
    }
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    public static float getWidth() {
        WindowManager wm = (WindowManager) MainApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
           return wm.getDefaultDisplay().getWidth();
    }

    public static float getHeight() {
        WindowManager wm = (WindowManager) MainApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

    /**
     * @param widthTotal 可用空间
     * @param tempSize   大概宽度
     * @param spce       间距
     * @param outSide    外边距
     * @return
     */
    public static int[] getItemWidth(int widthTotal,int tempSize,int spce,int outSide) {
        int[] item = new int[2];
        widthTotal = widthTotal - 2*outSide;
        int num = (widthTotal+spce)/(tempSize+spce);
        tempSize = widthTotal/num - spce;
        item[0] = num;
        item[1] = tempSize;
        return item;
    }

    /**
     * 通过反射，获取包含虚拟键的整体屏幕高度
     *
     * @return
     */
    public static int getHasVirtualKey() {
        int heightPixels = 0;
        try {
            WindowManager w = (WindowManager) MainApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
            Display d = w.getDefaultDisplay();
            android.graphics.Point realSize = new android.graphics.Point();
            Display.class.getMethod("getRealSize", android.graphics.Point.class)
                    .invoke(d, realSize);
            d.getRealSize(realSize);
            heightPixels = realSize.y;
        } catch (Exception e) {
            Log.e("heightPixels", "", e);
        }
        return heightPixels;
    }

    /**
     * 获取虚拟导航的高度
     * @return
     */
    public static int getVirtualBarHeight() {
        int vh = 0;
        WindowManager windowManager = (WindowManager) MainApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            display.getRealMetrics(dm);
            vh = dm.heightPixels - display.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    public static boolean isAll(Activity activity){
        int v = activity.getWindow().getAttributes().flags;
        Log.e("=========",""+v);
        // 全屏 66816 - 非全屏 65792
        if ( (activity.getWindow().getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            return true;
        }else {
            return false;
        }
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        return dm.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);
        return dm.heightPixels;
    }
}
