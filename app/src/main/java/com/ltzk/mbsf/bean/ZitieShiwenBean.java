package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：字帖注释文本
 * 作者： on 2018/8/21 20:49
 * 邮箱：499629556@qq.com
 */

public class ZitieShiwenBean extends BaseBean {
    private int _page;            //字帖编号
    private String _text;           //注释文

    public int get_page() {
        return _page;
    }

    public void set_page(int _page) {
        this._page = _page;
    }

    public String get_text() {
        return _text;
    }

    public void set_text(String _text) {
        this._text = _text;
    }
}
