package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2018/8/18 12:46
 * 邮箱：499629556@qq.com
 */

public class UserBean extends BaseBean {
    private String avatar;//做数据接收用，其他地方未用
    private String _avatar;
    private long _expire;
    private String _name;
    private String _nickname;
    private String _phone;
    private int _role;
    private String _token;
    private int _wx_bind;

    public String _id;
    public String _ad_link;
    public String _ad_title;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String get_avatar() {
        return _avatar;
    }

    public void set_avatar(String _avatar) {
        this._avatar = _avatar;
    }

    public long get_expire() {
        return _expire;
    }

    public void set_expire(long _expire) {
        this._expire = _expire;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_nickname() {
        return _nickname;
    }

    public void set_nickname(String _nickname) {
        this._nickname = _nickname;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_token() {
        return _token;
    }

    public void set_token(String _token) {
        this._token = _token;
    }

    public int get_wx_bind() {
        return _wx_bind;
    }

    public void set_wx_bind(int _wx_bind) {
        this._wx_bind = _wx_bind;
    }

    public int get_role() {
        return _role;
    }

    public void set_role(int _role) {
        this._role = _role;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
