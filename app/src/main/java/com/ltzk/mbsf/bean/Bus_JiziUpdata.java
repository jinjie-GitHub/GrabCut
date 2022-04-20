package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2018/8/25 12:26
 * 邮箱：499629556@qq.com
 */

public class Bus_JiziUpdata extends BaseBean {

    private JiziBean jiziBean;

    public JiziBean getJiziBean() {
        return jiziBean;
    }

    public void setJiziBean(JiziBean jiziBean) {
        this.jiziBean = jiziBean;
    }

    public Bus_JiziUpdata() {

    }

    public Bus_JiziUpdata(JiziBean jiziBean) {
        this.jiziBean = jiziBean;
    }
}
