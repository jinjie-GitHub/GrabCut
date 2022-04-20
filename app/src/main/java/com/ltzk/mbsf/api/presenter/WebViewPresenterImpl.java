package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.WebViewView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.PayWeichatBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.UserBean;


/**
 * 描述：webView
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class WebViewPresenterImpl extends BasePresenterImp<WebViewView>{


    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("iap_wxpay_unifiedorder")) {
                view.loadDataSuccess((PayWeichatBean) callBack.getData());
            }else if (flag.equals("redeem")) {
                view.redeemResult((UserBean) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    public void pay(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.iap_wxpay_unifiedorder(requestBean.getParams()),this,"iap_wxpay_unifiedorder",isShow);
    }

    public void redeem(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.redeem(requestBean.getParams()),this,"redeem",isShow);
    }
}
