package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * Created by JinJie on 2020/6/7
 */
public class DetailsBean extends BaseBean {
    public String _id;
    public String _hanzi;
    public String _font;
    public String _author;
    public String _color_image;
    public String _clear_image;
    public String _zitie_id;
    public String _name;
    public int _page;
    public String _frame;

    @Deprecated
    public boolean _has_video;
    public int _video_count;

    public static DetailsBean newDetails() {
        return new DetailsBean();
    }

    public static DetailsBean newDetails(String name) {
        final DetailsBean bean = new DetailsBean();
        bean._name = name;
        return bean;
    }

    public static DetailsBean newDetails(String _id, String _hanzi) {
        final DetailsBean bean = new DetailsBean();
        bean._id = _id;
        bean._hanzi = _hanzi;
        return bean;
    }

    @Override
    public String toString() {
        return "DetailsBean{" +
                "_id='" + _id + '\'' +
                ", _hanzi='" + _hanzi + '\'' +
                ", _font='" + _font + '\'' +
                ", _author='" + _author + '\'' +
                ", _color_image='" + _color_image + '\'' +
                ", _clear_image='" + _clear_image + '\'' +
                ", _zitie_id='" + _zitie_id + '\'' +
                ", _name='" + _name + '\'' +
                ", _page='" + _page + '\'' +
                ", _frame='" + _frame + '\'' +
                '}';
    }
}
