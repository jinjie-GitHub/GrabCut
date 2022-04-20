package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.HomeView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.BannerViewsInfo;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.TodayBean;
import com.ltzk.mbsf.utils.ExceptionUtil;

import java.util.List;

/**
 * Created by JinJie on 2020/5/25
 */
public class HomePresenter extends BasePresenterImp<HomeView> {

    public void today() {
        modelImpl.getDataFromHttp(serviceApi.today(), this, "today", false);
    }

    public void calendar() {
        modelImpl.getDataFromHttp(serviceApi.calendar(), this, "calendar", false);
    }

    public void slider_items_home() {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("place_id", "7ea63487");
        modelImpl.getDataFromHttp(serviceApi.ad_query(requestBean.getParams()), this, "slider_items_home", false);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("today")) {
                view.loadDataSuccess((TodayBean) callBack.getData());
            } else if (flag.equals("calendar")) {
                view.setCalendar(callBack);
            } else if (flag.equals("slider_items_home")) {
                view.slider_items_home((List<BannerViewsInfo>) callBack.getData());
            }
        } else {
            if (flag.equals("today")) {
                view.loadDataError(callBack.getError().getTitle() + ":" + callBack.getError().getMessage());
            }
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        view.disimissProgress();
        if (flag.equals("today")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        }
    }
}