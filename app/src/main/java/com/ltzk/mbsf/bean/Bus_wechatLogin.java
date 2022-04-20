package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

/**
 * 描述：
 * 作者： on 2018/8/25 12:26
 * 邮箱：499629556@qq.com
 */

public class Bus_wechatLogin extends BaseBean {
    private String code;

    public Bus_wechatLogin(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
