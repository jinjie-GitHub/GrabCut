package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZiLibDetailListView;
import com.ltzk.mbsf.api.view.ZiLibZiListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;

import java.util.List;


/**
 * 描述：字列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZiLibZiListPresenterImpl extends BasePresenterImp<ZiLibZiListView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("font_charset")) {
                view.getfont_charsetSuccess((List<String>) callBack.getData());
            } else if (flag.equals("font_glyphs_query")) {
                view.loadDataSuccess((RowBean<ZiLibDetailBean>) callBack.getData());
            } else if (flag.equals("delete")) {
                view.deleteSuccess("");
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    public void font_charset(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_charset(requestBean.getParams()),this,"font_charset",isShow);
    }

    public void font_glyphs_query(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_glyphs_query(requestBean.getParams()),this,"font_glyphs_query",isShow);
    }

    public void delete(String fid, String str, String gid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("fid", fid);
        requestBean.addParams("char", str);
        //requestBean.addParams("gid", gid);
        modelImpl.getDataFromHttp(serviceApi.glyphsDelete(requestBean.getParams()), this, "delete", false);
    }

    public void disposeAll() {
        modelImpl.dispose();
    }
}
