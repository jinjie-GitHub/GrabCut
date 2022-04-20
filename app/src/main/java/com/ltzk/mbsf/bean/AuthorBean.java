package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：作者
 * 作者： on 2018/8/21 20:49
 * 邮箱：499629556@qq.com
 */

public class AuthorBean extends BaseBean {

    /**
     * _name : 安中
     * _zpnum : 1
     * _hi : 0
     */

    private String _name;
    private String _zpnum;
    private String _hi;

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_zpnum() {
        return _zpnum;
    }

    public void set_zpnum(String _zpnum) {
        this._zpnum = _zpnum;
    }

    public String get_hi() {
        return _hi;
    }

    public void set_hi(String _hi) {
        this._hi = _hi;
    }
}
