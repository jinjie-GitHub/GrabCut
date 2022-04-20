package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;
import java.util.List;

/**
 * Created by JinJie on 2020/6/14
 */
public class GlyphsListBean extends BaseBean {
    public int total;
    public List<Glyphs> list;

    public static final class Glyphs {
        public String _id;
        public String _hanzi;
        public String _font;
        public String _author;
        public int _page;
        public String _frame;
        public String _color_image;
        public int _video_count;
    }
}