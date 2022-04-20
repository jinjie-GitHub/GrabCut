package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;
import java.util.List;

/**
 * Created by JinJie on 2020/6/14
 */
public class FontListBean extends BaseBean {
    public int total;
    public List<FontList> list;
    public static final class FontList {
        public String _id;
        public String _title;
        public String _font;
        public String _author;
        public String _own;
        //public List<String> _thumbs;
    }
}