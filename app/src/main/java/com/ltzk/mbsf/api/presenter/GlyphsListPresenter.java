package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.GlyphsListBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;

/**
 * Created by JinJie on 2020/5/25
 */
public class GlyphsListPresenter extends BasePresenterImp<IBaseView> {

    public void glyphsQuery(String zid, String key, String author, int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("zid", zid);
        requestBean.addParams("key", key);
        requestBean.addParams("author", author);
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.glyphsQuery(requestBean.getParams()), this, "glyphsQuery", true);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        view.disimissProgress();
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("glyphsQuery")) {
                if (callBack.getData() instanceof GlyphsListBean) {
                    view.loadDataSuccess((GlyphsListBean) callBack.getData());
                }
            }
        } else {
            if (flag.equals("glyphsQuery")) {
                view.loadDataError(callBack.getError().getTitle() + ":" + callBack.getError().getMessage());
            }
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        view.disimissProgress();
        if (flag.equals("glyphsQuery")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        }
    }
}