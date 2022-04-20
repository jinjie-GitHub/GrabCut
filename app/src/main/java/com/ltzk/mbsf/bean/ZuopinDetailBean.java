package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2019-10-13 21:17
 * 邮箱：499629556@qq.com
 */
public class ZuopinDetailBean extends BaseBean {

    private String _zuopin_id;
    private String _name;

    public String get_zuopin_id() {
        return _zuopin_id;
    }

    public void set_zuopin_id(String _zuopin_id) {
        this._zuopin_id = _zuopin_id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }
}
