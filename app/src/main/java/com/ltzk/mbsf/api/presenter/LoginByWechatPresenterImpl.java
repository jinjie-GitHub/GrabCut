package com.ltzk.mbsf.api.presenter;


import com.ltzk.mbsf.api.view.LoginByWechatView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;


/**
 * 描述：登录
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class LoginByWechatPresenterImpl extends BasePresenterImp<LoginByWechatView>{

    public void loginByWechat(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.login_weixin(requestBean.getParams()),this,"login_weixin",isShow);
    }
}
