package com.ltzk.mbsf.bean;

import android.graphics.RectF;


public class PointBean{
    float startX;
    float startY;
    RectF rect;


    public PointBean(float startX, float startY, RectF rect) {
        this.startX = startX;
        this.startY = startY;
        this.rect = rect;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }


    private static final int DEF_PADDING = 0;//为文字增加点击区域 相当于padding

    public boolean isIn(float x, float y) {
        float endX = startX + Math.abs(rect.right - rect.left) + DEF_PADDING;
        float endY = startY + Math.abs(rect.bottom - rect.top) + DEF_PADDING;
        float startX = getStartX() - DEF_PADDING;
        float startY = getStartY() - DEF_PADDING;
        return startX < x && x < endX && startY < y && y < endY;
    }
}
