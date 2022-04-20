package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2019-10-13 21:17
 * 邮箱：499629556@qq.com
 */
public class GalleryDetailBean extends BaseBean {

    private String _gallery_id;
    private String _title;

    public String get_gallery_id() {
        return _gallery_id;
    }

    public void set_gallery_id(String _gallery_id) {
        this._gallery_id = _gallery_id;
    }

    public String get_title() {
        return _title;
    }

    public void set_title(String _title) {
        this._title = _title;
    }
}
