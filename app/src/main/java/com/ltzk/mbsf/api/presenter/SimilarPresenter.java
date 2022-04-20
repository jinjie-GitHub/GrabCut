package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.SimilarView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.SimilarBean;
import com.ltzk.mbsf.utils.ExceptionUtil;

/**
 * Created by JinJie on 2020/5/25
 */
public class SimilarPresenter extends BasePresenterImp<SimilarView> {

    public void similar(final String gid, final int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("gid", gid);
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.similar(requestBean.getParams()), this, "similar", true);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("similar")) {
                view.loadDataSuccess((SimilarBean)callBack.getData());
            }
        } else {
            if (flag.equals("similar")) {
                view.loadDataError(callBack.getError().getTitle() + ":" + callBack.getError().getMessage());
            }
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        view.disimissProgress();
        if (flag.equals("similar")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        }
    }
}