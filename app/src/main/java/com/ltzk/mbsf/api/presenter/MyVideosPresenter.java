package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;
import java.util.List;

/**
 * Created by JinJie on 2020/5/25
 */
public class MyVideosPresenter extends BasePresenterImp<IBaseView> {

    public void glyph_video_author_zities(String uid, int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("uid", uid);
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_author_zities(requestBean.getParams()), this, "glyph_video_author_zities", true);
    }

    public void author_favlist() {
        final RequestBean requestBean = new RequestBean();
        modelImpl.getDataFromHttp(serviceApi.author_favlist(requestBean.getParams()), this, "author_favlist", true);
    }

    public void author_fav_update(List<String> names) {
        if (names == null || names.size() == 0) {
            return;
        }
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("names", names);
        modelImpl.getDataFromHttp(serviceApi.author_fav_update(requestBean.getParams()), this, "author_fav_update", true);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        view.disimissProgress();
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("glyph_video_author_zities")) {
                view.loadDataSuccess(callBack.getData());
            } else if (flag.equals("author_favlist")) {
                view.loadDataSuccess(callBack.getData());
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