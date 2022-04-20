package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

import java.util.List;

/**
 * 描述：
 * 作者： on 2018/10/13 16:09
 * 邮箱：499629556@qq.com
 */

public class JiziBean extends BaseBean {
    private String _id;//集字 id
    private String _title;//集字标题
    private List<String> _thumbs;//缩略图列表
    private String _json;//集字的字对象集合json格式,提交到后台的

    public String _layout;
    public String _direction;
    public String _color;
    public String _tmpl;

    //app新增
    private List<String> fomartUrls;//用来存放集字首页格式后的集合顺序

    private String text;//集字的拼接的字

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public List<String> get_thumbs() {
        return _thumbs;
    }

    public void set_thumbs(List<String> _thumbs) {
        this._thumbs = _thumbs;
    }


    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_json() {
        return _json;
    }

    public void set_json(String _json) {
        this._json = _json;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getFomartUrls() {
        return fomartUrls;
    }

    public void setFomartUrls(List<String> fomartUrls) {
        this.fomartUrls = fomartUrls;
    }
}
