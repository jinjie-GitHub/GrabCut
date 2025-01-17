package com.ltzk.mbsf.widget.pen;

/***
 * 触摸点信息
 * Created by JinJie on 2020/9/22
 */
public class MotionElement {

    public float x;
    public float y;
    /**
     * 压力值  物理设备决定的，和设计的设备有关系
     */
    public float pressure;
    /**
     * 绘制的工具是否是手指或者是笔（触摸笔）
     */
    public int toolType;

    public MotionElement(float mx, float my, float mp, int ttype) {
        x = mx;
        y = my;
        pressure = mp;
        toolType = ttype;
    }
}