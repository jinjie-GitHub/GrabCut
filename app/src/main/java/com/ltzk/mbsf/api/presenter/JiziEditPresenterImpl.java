package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.JiziEditView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;

/**
 * 描述：字帖集列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class JiziEditPresenterImpl extends BasePresenterImp<JiziEditView> {


    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("jizi_auto")) {
                view.jizi_autoResult((JiziBean) callBack.getData());
            } else if (flag.equals("getJiZiText")) {
                view.loadDataSuccess((String) callBack.getData());
            } else if (flag.equals("jiZiUnset") || flag.equals("updateGid")) {
                view.loadDataSuccess(flag.equals("jiZiUnset") ? "" : null);
            } else if (flag.equals("jizi_update_style")) {
                view.jizi_update(true);
            } else if (flag.equals("jizi_tmpl_list")) {
                view.jizi_tmpl_list(callBack.getData());
            } else if (flag.equals("jizi_glyphs")) {
                view.jizi_glyphs(callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle() + "" + callBack.getError().getMessage());
        }
    }

    public void getDetail(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_json(requestBean.getParams()),this,"jizi_json",isShow);
    }

    public void jizi_update_json_one(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_update_json(requestBean.getParams()),this,"jizi_update_json_one",isShow);
    }

    public void jizi_update_json(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_update_json(requestBean.getParams()),this,"jizi_update_json",isShow);
    }


    public void jizi_auto(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_auto(requestBean.getParams()),this,"jizi_auto",isShow);
    }

    public void updateGid(RequestBean requestBean) {
        modelImpl.getDataFromHttp(serviceApi.updateGid(requestBean.getParams()), this, "updateGid", false);
    }

    public void getJiZiText(String jid) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("jid", jid);
        modelImpl.getDataFromHttp(serviceApi.getJiZiText(requestBean.getParams()), this, "getJiZiText", false);
    }

    public void jiZiUnset(String jid, int row, int col) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("jid", jid);
        modelImpl.getDataFromHttp(serviceApi.jiZiUnset(requestBean.getParams()), this, "jiZiUnset", false);
    }

    public void jizi_update_style(String jid, String attr, String value) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("jid", jid);
        requestBean.addParams("attr", attr);
        requestBean.addParams("value", value);
        modelImpl.getDataFromHttp(serviceApi.jizi_update_style(requestBean.getParams()), this, "jizi_update_style", false);
    }

    public void jizi_tmpl_list(int loaded) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.jizi_tmpl_list(requestBean.getParams()), this, "jizi_tmpl_list", false);
    }

    public void jizi_glyphs(String jid) {
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("jid", jid);
        modelImpl.getDataFromHttp(serviceApi.jizi_glyphs(requestBean.getParams()), this, "jizi_glyphs", false);
    }
}