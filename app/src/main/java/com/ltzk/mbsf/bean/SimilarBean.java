package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;
import java.util.List;

/**
 * Created by JinJie on 2020/5/31
 */
public class SimilarBean extends BaseBean {
    public int total;
    public List<Similar> list;

    public static class Similar {
        public String _id;
        public String _hanzi;
        public String _author;
        public String _page;
        public String _frame;
        public String _color_image;
        public String _clear_image;
        public int _video_count;
    }
}
