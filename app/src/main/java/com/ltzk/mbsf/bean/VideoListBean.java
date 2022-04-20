package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

import java.util.List;

/**
 * Created by JinJie on 2020/6/14
 */
public class VideoListBean extends BaseBean {
    public int total;
    public List<Videos> list;

    public static final class Videos {
        public String _id;
        public String _dir;
        public String _glyph_id;
        public int _published;//0：私密， 1：公开
        public int _review_stat;//0：被拒绝， 1：审核成功， -2：待发布， -1：待审核
        public int _disabled;//1：封禁， 0：正常
        public int _locked;//0：未锁定， 1：锁定
        public int _free;//0：免费， 1：收费
        public String _url;
        public String _cover;
        public Author _author;
        public Glyph _glyph;
    }

    public static final class Author {
        public String _id;
        public String _avatar;
        public String _ad_title;
        public String _ad_link;
        public String _nickname;
    }

    public static final class Glyph {
        public String _id;
        public String _hanzi;
        public String _color_image;
        public String _clear_image;
    }
}