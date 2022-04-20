package com.ltzk.mbsf.bean;

import android.text.TextUtils;
import android.view.TextureView;
import android.widget.TextView;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：字
 * 作者： on 2018/8/21 20:49
 * 邮箱：499629556@qq.com
 */

public class ZiBean extends BaseBean {

    /*_id	string	字形 id
    _type	int	类型
    _key	string	用户输入的关键字
    _hanzi	string	字形对应汉字
    _font	string	字体
    _author	string	作者
    _color_image	url	彩色字形原图
    _color_thumb	url	彩色字形缩略图
    _clear_image	url	透明背景字形原图
    _clear_thumb	url	透明背景字形缩略图
    _zitie_id	string	字形所在的字帖 id
    _name	string	字帖名
    _page	int	字形所在的页面序号（从1开始）
    _frame	string	字形在页面中的具体位置（x,y,w,h）*/

    private String _id;//字形 id
    private String _key;//用户输入的关键字
    private String _hanzi;//字形对应汉字
    private String _font_id;//如果是字库中的，代表字库id
    private String _from; //来源
    private String _font;//字体
    private String _author;//作者
    private int _type; //类型 0：无效图片 1：字体图片 2：真迹图片 3：单字图片 100：广告
    private String _zitie_id;//字形所在的字帖 id
    private int _page;//字形所在的页面序号（从1开始）
    private String _frame; //字形在页面中的具体位置（x,y,w,h）681,59.82,293,324.65

    private String _color_image;//彩色字形原图
    private String _color_thumb;//彩色字形缩略图
    private String _clear_image;//透明背景字形原图
    private String _clear_thumb;//透明背景字形缩略图
    private String _ref_glyph_id;

    public int _video_count;

    /*private String _thumb_url;
    private String _thumb_url2;*/
    /*private String _img_url;
    private String _img_url2;*/




    //app端新增，非后台返回
    private Frame_Zi frame_zi;//字形在页面中的具体位置
    private transient boolean isFirst;//是否是第一个汉字
    private int jizi_type;//1:字 2：补充的空白

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_hanzi() {
        return _hanzi;
    }

    public void set_hanzi(String _hanzi) {
        this._hanzi = _hanzi;
    }

    public String get_font() {
        return _font;
    }

    public void set_font(String _font) {
        this._font = _font;
    }

    public String get_author() {
        return _author;
    }

    public void set_author(String _author) {
        this._author = _author;
    }
/*
    public int get_type() {
        return _type;
    }

    public void set_type(int _type) {
        this._type = _type;
    }*/

    public String get_zitie_id() {
        return _zitie_id;
    }

    public void set_zitie_id(String _zitie_id) {
        this._zitie_id = _zitie_id;
    }

    public int get_page() {
        return _page;
    }

    public void set_page(int _page) {
        this._page = _page;
    }

    public String get_frame() {
        return _frame;
    }

    public void set_frame(String _frame) {
        this._frame = _frame;
    }

    public String get_color_image() {
        return _color_image;
    }

    public void set_color_image(String _color_image) {
        this._color_image = _color_image;
    }

    public String get_color_thumb() {
        return _color_thumb;
    }

    public void set_color_thumb(String _color_thumb) {
        this._color_thumb = _color_thumb;
    }

    public String get_clear_image() {
        return _clear_image;
    }

    public void set_clear_image(String _clear_image) {
        this._clear_image = _clear_image;
    }

    public String get_clear_thumb() {
        return _clear_thumb;
    }

    public void set_clear_thumb(String _clear_thumb) {
        this._clear_thumb = _clear_thumb;
    }

    public int getJizi_type() {
        return jizi_type;
    }

    public void setJizi_type(int jizi_type) {
        this.jizi_type = jizi_type;
    }

    public String get_key() {
        return _key;
    }

    public void set_key(String _key) {
        this._key = _key;
    }

    public String get_font_id() {
        return _font_id;
    }

    public void set_font_id(String _font_id) {
        this._font_id = _font_id;
    }

    public String get_from() {
        return _from;
    }

    public void set_from(String _from) {
        this._from = _from;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public String get_ref_glyph_id() {
        return _ref_glyph_id;
    }

    public void set_ref_glyph_id(String _ref_glyph_id) {
        this._ref_glyph_id = _ref_glyph_id;
    }

    public Frame_Zi getFrame_zi() {
        if(frame_zi == null){
            if(_frame!=null && !"".equals(_frame)){
                String[] frames = _frame.split(",");
                Frame_Zi frame_zi = new Frame_Zi(Float.parseFloat(frames[0]),Float.parseFloat(frames[1]),Float.parseFloat(frames[2]),Float.parseFloat(frames[3]));
                setFrame_zi(frame_zi);
            }
        }
        return frame_zi;
    }

    public void setFrame_zi(Frame_Zi frame_zi) {
        this.frame_zi = frame_zi;
    }

    public class Frame_Zi {
        private float left;
        private float top;
        private float width;
        private float height;

        public Frame_Zi(float left, float top, float width, float height) {
            this.left = left;
            this.top = top;
            this.width = width;
            this.height = height;
        }

        public float getLeft() {
            return left;
        }

        public void setLeft(float left) {
            this.left = left;
        }

        public float getTop() {
            return top;
        }

        public void setTop(float top) {
            this.top = top;
        }

        public float getWidth() {
            return width;
        }

        public void setWidth(float width) {
            this.width = width;
        }

        public float getHeight() {
            return height;
        }

        public void setHeight(float height) {
            this.height = height;
        }
    }



    public String getUrl(){
        if(!TextUtils.isEmpty(_color_image)){
            return _color_image;
        }
        if(!TextUtils.isEmpty(_color_thumb)){
            return _color_thumb;
        }
        if(!TextUtils.isEmpty(_clear_image)){
            return _clear_image;
        }
        if(!TextUtils.isEmpty(_clear_thumb)){
            return _clear_thumb;
        }
        return "";
    }

    public String getUrlThumb(){
        if(!TextUtils.isEmpty(_color_thumb)){
            return _color_thumb;
        }
        if(!TextUtils.isEmpty(_color_image)){
            return _color_image;
        }
        if(!TextUtils.isEmpty(_clear_thumb)){
            return _clear_thumb;
        }
        if(!TextUtils.isEmpty(_clear_image)){
            return _clear_image;
        }
        return "";
    }

    public String getUrlClear(){
        if(!TextUtils.isEmpty(_clear_image)){
            return _clear_image;
        }
        if(!TextUtils.isEmpty(_clear_thumb)){
            return _clear_thumb;
        }
        if(!TextUtils.isEmpty(_color_image)){
            return _color_image;
        }
        if(!TextUtils.isEmpty(_color_thumb)){
            return _color_thumb;
        }
        return "";
    }

    public String getUrlClearThumb(){
        if(!TextUtils.isEmpty(_clear_thumb)){
            return _clear_thumb;
        }
        if(!TextUtils.isEmpty(_clear_image)){
            return _clear_image;
        }
        if(!TextUtils.isEmpty(_color_thumb)){
            return _color_thumb;
        }
        if(!TextUtils.isEmpty(_color_image)){
            return _color_image;
        }
        return "";
    }
}
