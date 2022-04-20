package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2018/9/22 12:52
 * 邮箱：499629556@qq.com
 */

public class ZitieDetailitemBean extends BaseBean {
    private int page;
    private String zid;

    private String _thumb;
    private String _image;
    private int width;
    private int height;
    public int pos = -1;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getZid() {
        return zid;
    }

    public void setZid(String zid) {
        this.zid = zid;
    }

    public String get_thumb() {
        return _thumb;
    }

    public void set_thumb(String _thumb) {
        this._thumb = _thumb;
    }

    public String get_image() {
        return _image;
    }

    public void set_image(String _image) {
        this._image = _image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
