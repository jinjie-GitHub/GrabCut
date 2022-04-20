package com.ltzk.mbsf.bean;

import android.text.TextUtils;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2020-3-30 16:21
 * 邮箱：499629556@qq.com
 */
public class ZiAuthorBean extends BaseBean {


    /**
     * _name : 王羲之
     * _num : 123
     */

    private String _name;
    private int _num;
    private int _fav;

    public String get_name() {
        if(TextUtils.isEmpty(_name)){
            return "";
        }
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public int get_num() {
        return _num;
    }

    public void set_num(int _num) {
        this._num = _num;
    }

    public void set_fav(int fav) {
        this._fav = fav;
    }

    //1-已收藏，0-未收藏
    public boolean isFav() {
        return _fav == 1;
    }
}
