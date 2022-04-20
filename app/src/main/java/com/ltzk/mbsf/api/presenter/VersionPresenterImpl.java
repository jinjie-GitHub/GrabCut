package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.VersionView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.VersionBean;
import com.ltzk.mbsf.bean.XeLoginBean;


/**
 * 描述：版本
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class VersionPresenterImpl extends BasePresenterImp<VersionView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("checkupdate")) {
                view.loadDataSuccess((VersionBean) callBack.getData());
            }else if (flag.equals("getinfo")) {
                view.getUserInfoSuccess((UserBean) callBack.getData());
            }else if (flag.equals("user_sdk_login")) {
                view.user_sdk_login((XeLoginBean) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    public void getinfo(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.getinfo(requestBean.getParams()),this,"getinfo",isShow);
    }

    public void user_sdk_login() {
        final RequestBean requestBean = new RequestBean();
        //requestBean.addParams("login_type", "");
        //requestBean.addParams("redirect_uri", "");
        modelImpl.getDataFromHttp(serviceApi.user_sdk_login(requestBean.getParams()), this, "user_sdk_login", false);
    }
}
