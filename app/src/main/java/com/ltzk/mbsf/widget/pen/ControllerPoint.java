package com.ltzk.mbsf.widget.pen;

/***
 * 每个点的控制，关心三个因素：笔的宽度，坐标,透明数值
 * Created by JinJie on 2020/9/22
 */
public class ControllerPoint {
    public float x;
    public float y;

    public float width;
    public int alpha = 255;

    public ControllerPoint() {

    }

    public ControllerPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.width = w;
    }

    public void set(ControllerPoint point) {
        this.x = point.x;
        this.y = point.y;
        this.width = point.width;
    }
}