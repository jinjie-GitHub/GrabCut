package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2018/8/25 12:26
 * 邮箱：499629556@qq.com
 */

public class Bus_ZilibAdd extends BaseBean {

    private String kind;
    private String font;

    public Bus_ZilibAdd(String kind, String font) {
        this.kind = kind;
        this.font = font;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
