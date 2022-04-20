package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.LoginView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.UserBean;


/**
 * 描述：登录
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class LoginPresenterImpl extends BasePresenterImp<LoginView>{


    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("get_code")) {
                view.loadDataSuccess((String) callBack.getData());
            }else if (flag.equals("login_code")) {
                view.LoginResult((UserBean) callBack.getData());
            }else if (flag.equals("login_pwd")) {
                view.LoginResult((UserBean) callBack.getData());
            }else if (flag.equals("register")) {
                view.LoginResult((UserBean) callBack.getData());
            }else if (flag.equals("update_pwd")) {
                view.update_pwdResult((String) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    public void getCode(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.get_code(requestBean.getParams()),this,"get_code",isShow);
    }

    public void loginByCode(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.login_code(requestBean.getParams()),this,"login_code",isShow);
    }

    public void loginByPwd(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.login_pwd(requestBean.getParams()),this,"login_pwd",isShow);
    }

    public void register(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.register(requestBean.getParams()),this,"register",isShow);
    }

    public void update_pwd(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.update_pwd(requestBean.getParams()),this,"update_pwd",isShow);
    }

}
