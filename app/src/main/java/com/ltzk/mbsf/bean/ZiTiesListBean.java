package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;
import java.util.List;

/**
 * Created by JinJie on 2020/6/14
 */
public class ZiTiesListBean extends BaseBean {
    public int total;
    public List<ZiTie> list;

    public static final class ZiTie {
        public String _zitie_id;
        public String _name;
        public String _cover_url;
        public String _author;
    }
}