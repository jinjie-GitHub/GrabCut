package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

import java.util.List;


/**
 * 描述：字帖
 * 作者： on 2018/8/21 20:49
 * 邮箱：499629556@qq.com
 */

public class ZitieBean extends BaseBean {
    private String _zitie_id;            //字帖编号
    private String _name;           //字帖名
    private String _subtitle;           //副标题
    private String _free;  //是否为免费字帖 1是。
    private String _hd; //是否为高清全图
    private int _focus_page;      //焦点页面，打开字帖后直接跳转至该页面(可能为NSNotFound)
    private String _cover_url;       //封面图片
    private List<Boolean> _annotations; //标注
    private String _author;         //作者
    private String _dynasty;        //朝代
    private List<String> _images;//字帖分页图片（不包括第0页）
    private List<String> _sizes;      //页面图片尺寸
    private List<String> _thumbs;       //字帖分页图片缩略图（不包括第0页）
    private List<Integer> _favs;     //被当前用户收藏的页面

    public int _video;
    public String _zuopin_id;
    public List<ZuoPin> _zuopin_list;

    public String get_zitie_id() {
        return _zitie_id;
    }

    public void set_zitie_id(String _zitie_id) {
        this._zitie_id = _zitie_id;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public String get_subtitle() {
        return _subtitle;
    }

    public void set_subtitle(String _subtitle) {
        this._subtitle = _subtitle;
    }

    public String get_author() {
        return _author;
    }

    public void set_author(String _author) {
        this._author = _author;
    }

    public String get_dynasty() {
        return _dynasty;
    }

    public void set_dynasty(String _dynasty) {
        this._dynasty = _dynasty;
    }

    public String get_cover_url() {
        return _cover_url;
    }

    public void set_cover_url(String _cover_url) {
        this._cover_url = _cover_url;
    }



    public List<String> get_images() {
        return _images;
    }

    public void set_images(List<String> _images) {
        this._images = _images;
    }

    public List<String> get_sizes() {
        return _sizes;
    }

    public void set_sizes(List<String> _sizes) {
        this._sizes = _sizes;
    }


    public List<String> get_thumbs() {
        return _thumbs;
    }

    public void set_thumbs(List<String> _thumbs) {
        this._thumbs = _thumbs;
    }

    public List<Integer> get_favs() {
        return _favs;
    }

    public void set_favs(List<Integer> _favs) {
        this._favs = _favs;
    }

    public int get_focus_page() {
        return _focus_page;
    }

    public void set_focus_page(int _focus_page) {
        this._focus_page = _focus_page;
    }

    public String get_free() {
        return _free;
    }

    public void set_free(String _free) {
        this._free = _free;
    }

    public String get_hd() {
        return _hd;
    }

    public void set_hd(String _hd) {
        this._hd = _hd;
    }

    public List<Boolean> get_annotations() {
        return _annotations;
    }

    public void set_annotations(List<Boolean> _annotations) {
        this._annotations = _annotations;
    }

    public static final class ZuoPin {
        public String _zuopin_id;
        public String _name;
        public String _author;
        public int _focus_page;
    }
}
