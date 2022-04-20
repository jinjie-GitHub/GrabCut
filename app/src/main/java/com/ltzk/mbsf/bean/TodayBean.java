package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * Created by JinJie on 2020/5/31
 */
public class TodayBean extends BaseBean {
    public String _id;
    public String _content;
    public String _src;
    public String _author;
    public String _dynasty;
    public String _push_date;

    @Override
    public String toString() {
        return "TodayBean{" +
                "_id='" + _id + '\'' +
                ", _content='" + _content + '\'' +
                ", _src='" + _src + '\'' +
                ", _author='" + _author + '\'' +
                ", _dynasty='" + _dynasty + '\'' +
                ", _push_date='" + _push_date + '\'' +
                '}';
    }
}
