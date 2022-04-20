package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZitiejiListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.AuthorFamous;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import java.util.List;

public class ZitiejiListPresenterImpl extends BasePresenterImp<ZitiejiListView> {

    public void getList(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.gallery_list(requestBean.getParams()), this, "gallery_list", isShow);
    }

    public void slider_items_discovery() {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("place_id", "570ed561");
        //requestBean.addParams("place_id", "7f45f53d");
        modelImpl.getDataFromHttp(serviceApi.ad_query(requestBean.getParams()), this, "slider_items_discovery", false);
    }

    public void author_famous() {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("loaded", 0);
        modelImpl.getDataFromHttp(serviceApi.author_famous(requestBean.getParams()), this, "author_famous", false);
    }

    public void dylist() {
        final RequestBean requestBean = new RequestBean();
        modelImpl.getDataFromHttp(serviceApi.dylist(requestBean.getParams()), this, "dylist", false);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        view.disimissProgress();
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("gallery_list")) {
                view.loadDataSuccess((ZitieHomeBean) callBack.getData());
            } else if (flag.equals("slider_items_discovery")) {
                //view.setBannerViewsInfo((List<BannerViewsInfo>) callBack.getData());
            } else if (flag.equals("author_famous")) {
                view.setAuthorFamous((AuthorFamous) callBack.getData());
            } else if (flag.equals("dylist")) {
                view.setDynastyList((List<String>) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle() + ":" + callBack.getError().getMessage());
        }
    }
}