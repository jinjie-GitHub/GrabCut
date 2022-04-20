package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2020-5-9 21:56
 * 邮箱：499629556@qq.com
 */
public class MenuItemBean extends BaseBean {

    private int res;
    private String name;
    private int type;

    public MenuItemBean(int res, String name) {
        this.res = res;
        this.name = name;
    }

    public int getRes() {
        return res;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
