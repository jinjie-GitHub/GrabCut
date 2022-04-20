package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.JiziNewView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;


/**
 * 描述：字帖集列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class JiziNewPresenterImpl extends BasePresenterImp<JiziNewView> {


    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("jizi_add")) {
                view.loadDataSuccess((JiziBean) callBack.getData());
            }else if (flag.equals("jizi_update_text")) {
                view.jizi_updateResult((JiziBean) callBack.getData());
            } else if (flag.equals("text_s2t") || flag.equals("text_t2s")) {
                if (callBack.getData() instanceof String) {
                    view.setUp((String) callBack.getData());
                }
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }


    public void jizi_add(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_add(requestBean.getParams()),this,"jizi_add",isShow);
    }

    public void jizi_update(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_update_text(requestBean.getParams()),this,"jizi_update_text",isShow);
    }

    public void text_s2t(String text) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("text", text);
        modelImpl.getDataFromHttp(serviceApi.text_s2t(requestBean.getParams()), this, "text_s2t", true);
    }

    public void text_t2s(String text) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("text", text);
        modelImpl.getDataFromHttp(serviceApi.text_t2s(requestBean.getParams()), this, "text_t2s", true);
    }
}
