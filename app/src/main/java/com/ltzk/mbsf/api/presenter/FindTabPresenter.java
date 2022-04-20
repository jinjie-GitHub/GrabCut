package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.BannerViewsInfo;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;
import java.util.List;

/**
 * Created by JinJie on 2020/5/25
 */
public class FindTabPresenter extends BasePresenterImp<IBaseView> {

    public void glyph_video_author_zities(String uid, int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("uid", uid);
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_author_zities(requestBean.getParams()), this, "glyph_video_author_zities", true);
    }

    public void slider_items_discovery() {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("place_id", "7f45f53d");
        modelImpl.getDataFromHttp(serviceApi.ad_query(requestBean.getParams()), this, "slider_items_discovery", false);
    }

    public void disc_items() {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("place_id", "9ea5f71a");
        modelImpl.getDataFromHttp(serviceApi.ad_query(requestBean.getParams()), this, "disc_items", true);
    }

    /**
     * 开屏广告
     */
    public void ad_splash() {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("place_id", "17955293");
        modelImpl.getDataFromHttp(serviceApi.ad_query(requestBean.getParams()), this, "disc_items", false);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        view.disimissProgress();
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("slider_items_discovery")) {
                view.loadDataSuccess((List<BannerViewsInfo>) callBack.getData());
            } else if (flag.equals("disc_items")) {
                view.loadDataSuccess((List<BannerViewsInfo>) callBack.getData());
            }
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