package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2018/8/25 12:26
 * 邮箱：499629556@qq.com
 */

public class Bus_LoginOut extends BaseBean {
    private String flag;
    private String msg;
    public Bus_LoginOut(String flag) {
        this.flag = flag;
    }

    public Bus_LoginOut(String flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
