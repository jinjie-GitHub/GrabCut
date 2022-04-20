package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZiLibListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZilibBean;

import java.util.Arrays;


/**
 * 描述：字库字形列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZiLibListPresenterImpl extends BasePresenterImp<ZiLibListView> {

    public void font_query(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_query(requestBean.getParams()), this, "font_query", isShow);
    }

    public void libFav(String fid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("fid", fid);
        modelImpl.getDataFromHttp(serviceApi.libFav(requestBean.getParams()), this, "libFav", false);
    }

    public void font_update(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_update(requestBean.getParams()), this, "font_update", false);
    }


    public void libUnFav(String fid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("fids", Arrays.asList(fid));
        modelImpl.getDataFromHttp(serviceApi.libUnFav(requestBean.getParams()), this, "libUnFav", false);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("font_query")) {
                view.loadDataSuccess((RowBean<ZilibBean>) callBack.getData());
            } else if (flag.equals("libFav")) {

            } else if (flag.equals("libUnFav")) {

            }else if(flag.equals("font_update")) {

            }
        } else {
            view.loadDataError(callBack.getError().getTitle() + ":" + callBack.getError().getMessage());
        }
    }

    public void disposeAll() {
        modelImpl.dispose();
    }
}
