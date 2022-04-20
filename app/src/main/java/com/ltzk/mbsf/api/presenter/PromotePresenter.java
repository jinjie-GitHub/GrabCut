package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;

/**
 * Created by JinJie on 2020/5/25
 */
public class PromotePresenter extends BasePresenterImp<IBaseView> {

    public void user_update_adtitle(String title) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("title", title);
        modelImpl.getDataFromHttp(serviceApi.user_update_adtitle(requestBean.getParams()), this, "title", false);
    }

    public void user_update_adlink(String link) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("link", link);
        modelImpl.getDataFromHttp(serviceApi.user_update_adlink(requestBean.getParams()), this, "link", false);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        view.disimissProgress();
        if (Constan.response_sussess == callBack.getStat()) {
            view.loadDataSuccess(callBack.getData());
        } else {
            view.loadDataError(callBack.getError().getTitle() + ":" + callBack.getError().getMessage());
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        view.disimissProgress();
        view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
    }
}