package com.ltzk.mbsf.utils;

import android.annotation.TargetApi;
import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RoundViewOutlineProvider extends ViewOutlineProvider {
    /**
     * 圆角弧度
     */
    private final float mRadius;

    public RoundViewOutlineProvider(float radius) {
        this.mRadius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        // 绘制区域
        Rect selfRect = new Rect(0, 0, view.getWidth(), view.getHeight());
        outline.setRoundRect(selfRect, mRadius);
    }
}