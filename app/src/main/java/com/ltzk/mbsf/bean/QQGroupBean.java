package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * Created by JinJie on 2020/5/31
 */
public class QQGroupBean extends BaseBean {
    public String id;
    public String url;

    @Override
    public String toString() {
        return "QQGroupBean{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}