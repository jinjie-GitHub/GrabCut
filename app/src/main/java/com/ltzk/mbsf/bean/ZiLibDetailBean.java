package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2020-5-12 18:00
 * 邮箱：499629556@qq.com
 */
public class ZiLibDetailBean extends BaseBean {

    private int num;
    private ZiBean glyph;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public ZiBean getGlyph() {
        return glyph;
    }

    public void setGlyph(ZiBean glyph) {
        this.glyph = glyph;
    }

    public static ZiLibDetailBean newZiLibDetailBean(ZiLibDetailBean old, ZiBean now) {
        ZiLibDetailBean bean = new ZiLibDetailBean();
        bean.setNum(old.getNum());
        bean.setGlyph(now);
        return bean;
    }
}
