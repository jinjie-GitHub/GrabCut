package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

import java.util.List;

/**
 * 描述：列表集合
 * 作者： on 2018/8/21 20:49
 * 邮箱：499629556@qq.com
 */

public class RowBean<T> extends BaseBean {
    private int total;
    private List<T> list;
    private int cur;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }
    public void setList(List<T> list) {
        this.list = list;
    }

    public int getCur() {
        return cur;
    }

    public void setCur(int cur) {
        this.cur = cur;
    }
}
