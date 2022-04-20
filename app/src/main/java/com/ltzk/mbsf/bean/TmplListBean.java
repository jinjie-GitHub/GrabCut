package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

import java.util.List;

/**
 * Created by JinJie on 2020/6/14
 */
public class TmplListBean extends BaseBean {
    public int total;
    public List<Tmpl> list;

    public static final class Tmpl {
        public String _name;
        public String _title;
        public String _icon;
    }
}