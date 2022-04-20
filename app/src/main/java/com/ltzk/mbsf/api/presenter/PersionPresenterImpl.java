package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.PersionView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.UserBean;


/**
 * 描述：我的
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class PersionPresenterImpl extends BasePresenterImp<PersionView>{


    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("getinfo")) {
                view.getUserInfoSuccess((UserBean) callBack.getData());
            } else if (flag.equals("qqun")) {
                //view.setupQQ((QQGroupBean) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle() + "" + callBack.getError().getMessage());
        }
    }

    public void getinfo(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.getinfo(requestBean.getParams()),this,"getinfo",isShow);
    }

    /*public void qqun() {
        final RequestBean requestBean = new RequestBean();
        modelImpl.getDataFromHttp(serviceApi.qqun(requestBean.getParams()), this, "qqun", false);
    }*/

}
