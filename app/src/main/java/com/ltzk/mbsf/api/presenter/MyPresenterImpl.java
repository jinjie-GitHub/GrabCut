package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.MyView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.utils.ExceptionUtil;

import org.json.JSONObject;


/**
 * 描述：个人中心
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class MyPresenterImpl extends BasePresenterImp<MyView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("getinfo")) {
                view.loadDataSuccess((UserBean) callBack.getData());
            }else if (flag.equals("logout")) {
                view.logoutResult((String) callBack.getData());
            }else if (flag.equals("checkin")) {
                view.checkinResult((JSONObject) callBack.getData());
            }else if (flag.equals("login_weixin")) {
                view.loadDataSuccess((UserBean) callBack.getData());
            }else if (flag.equals("unbind_weixin")) {
                view.unbind_weixinResult((String) callBack.getData());
            }else if (flag.equals("unbind_phone")) {
                view.unbind_phoneResult((String) callBack.getData());
            }else if (flag.equals("update_avatar")) {
                view.update_avatarResult((UserBean) callBack.getData());
            }else if (flag.equals("xiaoe_sdk_logout")) {
                view.xiaoe_sdk_logout();
            }
        } else {
            if (flag.equals("logout")) {
                view.logoutError("");
            }else {
                view.loadDataError(callBack.getError().getTitle() + "" + callBack.getError().getMessage());
            }
        }
    }
    @Override
    public void requestError(Throwable throwable, String flag) {
        if (flag.equals("logout")) {
            view.logoutError("");
            view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
        }else {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
            view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
        }
    }
    public void getinfo(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.getinfo(requestBean.getParams()),this,"getinfo",isShow);
    }

    public void update_avatar(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.update_avatar(requestBean.getParams()),this,"update_avatar",isShow);
    }

    public void logout(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.logout(requestBean.getParams()),this,"logout",isShow);
    }

    public void xiaoe_sdk_logout() {
        final RequestBean requestBean = new RequestBean();
        modelImpl.getDataFromHttp(serviceApi.xiaoe_sdk_logout(requestBean.getParams()), this, "xiaoe_sdk_logout", false);
    }

    public void checkin(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.checkin(requestBean.getParams()),this,"checkin",isShow);
    }

    public void login_weixin(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.login_weixin(requestBean.getParams()),this,"login_weixin",isShow);
    }

    public void unbind_weixin(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.unbind_weixin(requestBean.getParams()),this,"unbind_weixin",isShow);
    }

    public void unbind_phone(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.unbind_phone(requestBean.getParams()),this,"unbind_phone",isShow);
    }

}
