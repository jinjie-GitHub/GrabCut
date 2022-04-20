package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.HistoryView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.HistoryBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;

/**
 * Created by JinJie on 2020/5/25
 */
public class HistoryPresenter extends BasePresenterImp<HistoryView> {

    public void history(final int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.history(requestBean.getParams()), this, "history", true);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("history")) {
                view.loadDataSuccess((HistoryBean) callBack.getData());
            }
        } else {
            if (flag.equals("history")) {
                view.loadDataError(callBack.getError().getTitle() + ":" + callBack.getError().getMessage());
            }
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        view.disimissProgress();
        if (flag.equals("history")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        }
    }
}