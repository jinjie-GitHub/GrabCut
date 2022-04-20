package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZiLibDetailListView;
import com.ltzk.mbsf.api.view.ZiLibListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;

import java.util.List;


/**
 * 描述：字列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZiLibDetailListPresenterImpl extends BasePresenterImp<ZiLibDetailListView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("font_glyphs_char")) {
                view.loadDataSuccess((RowBean<ZiBean>) callBack.getData());
            }else if (flag.equals("font_glyphs_upload")) {
                view.uploadSuccess((ZiBean) callBack.getData());
            }else if (flag.equals("font_glyphs_add")) {
                view.addSuccess((String) callBack.getData());
            }else if (flag.equals("font_glyphs_delete")) {
                view.deleteSuccess((String) callBack.getData());
            }else if (flag.equals("font_glyphs_prefer")) {
                view.preferSuccess((String) callBack.getData());
            }
        } else {
            if (flag.equals("font_glyphs_char")) {
                view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }else if (flag.equals("font_glyphs_upload")) {
                view.uploadFail(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }else if (flag.equals("font_glyphs_add")) {
                view.addFail(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }else if (flag.equals("font_glyphs_delete")) {
                view.deleteFail(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }else if (flag.equals("font_glyphs_prefer")) {
                view.preferFail(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }
        }
    }


    public void font_glyphs_char(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_glyphs_char(requestBean.getParams()),this,"font_glyphs_char",isShow);
    }

    public void font_glyphs_add(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_glyphs_add(requestBean.getParams()),this,"font_glyphs_add",isShow);
    }

    public void font_glyphs_upload(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_glyphs_upload(requestBean.getParams()),this,"font_glyphs_upload",isShow);
    }

    public void font_glyphs_delete(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_glyphs_delete(requestBean.getParams()),this,"font_glyphs_delete",isShow);
    }
    public void font_glyphs_prefer(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_glyphs_prefer(requestBean.getParams()),this,"font_glyphs_prefer",isShow);
    }

}
