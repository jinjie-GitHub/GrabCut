package com.ltzk.mbsf.bean;

import android.text.TextUtils;

import com.ltzk.mbsf.base.BaseBean;

import java.util.List;

/**
 * 描述：
 * 作者： on 2020-5-10 22:49
 * 邮箱：499629556@qq.com
 */
public class ZilibBean extends BaseBean {
    public static final int OWN_ME = 1;

    /**
     * _id : 6ad9ba70608394ba
     * _title : 王羲之行书
     * _author : 王羲之
     * _kind : 1
     * _summary :
     * _access : 1
     * "_own":1
     */

    private String _id;
    private String _title;
    private String _author;
    private int _kind;
    private String _font;
    private String _summary;
    private int _access;
    private List<String> _thumbs;
    private int _own;
    private String _descrip;

    public int _free;// 0:收费， 1:免费
    public int _video;// 0:无视频， 1:有视频

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }

    public String get_author() {
        return _author;
    }

    public void set_author(String _author) {
        this._author = _author;
    }

    public int get_kind() {
        return _kind;
    }

    public void set_kind(int _kind) {
        this._kind = _kind;
    }

    public String get_font() {
        return _font;
    }

    public void set_font(String _font) {
        this._font = _font;
    }

    public String get_summary() {
        return _summary;
    }

    public void set_summary(String _summary) {
        this._summary = _summary;
    }

    public int get_access() {
        return _access;
    }

    public void set_access(int _access) {
        this._access = _access;
    }

    public List<String> get_thumbs() {
        return _thumbs;
    }

    public void set_thumbs(List<String> _thumbs) {
        this._thumbs = _thumbs;
    }

    public int get_own() {
        return _own;
    }

    public void set_own(int _own) {
        this._own = _own;
    }

    public String get_descrip() {
        if(_descrip == null){
            _descrip = "";
        }
        return _descrip;
    }

    public void set_descrip(String _descrip) {
        this._descrip = _descrip;
    }

}
