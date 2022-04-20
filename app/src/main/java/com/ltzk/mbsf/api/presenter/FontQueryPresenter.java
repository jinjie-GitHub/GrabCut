package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by JinJie on 2020/5/25
 */
public class FontQueryPresenter extends BasePresenterImp<IBaseView> {

    public void font_query(String subset, int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("subset", subset);
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.font_query(requestBean.getParams()), this, "font_query", true);
    }

    public void libUnFav(List<?> list) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("fids", list);
        modelImpl.getDataFromHttp(serviceApi.libUnFav(requestBean.getParams()), this, "libUnFav", false);
    }

    public void glyph_video_unfav(List<?> list) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("vids", list);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_unfav(requestBean.getParams()), this, "glyph_video_unfav", false);
    }

    public void glyph_video_fav(String vid) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("vid", vid);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_fav(requestBean.getParams()), this, "glyph_video_fav", false);
    }

    public void glyph_video_favlist(int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_favlist(requestBean.getParams()), this, "glyph_video_favlist", true);
    }

    public void glyph_video_author_fonts(String uid, int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("uid", uid);
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.glyph_video_author_fonts(requestBean.getParams()), this, "glyph_video_author_fonts", true);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        view.disimissProgress();
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("font_query")) {
                view.loadDataSuccess(callBack.getData());
            } else if (flag.equals("glyph_video_favlist")) {
                view.loadDataSuccess(callBack.getData());
            } else if (flag.equals("glyph_video_author_fonts")) {
                view.loadDataSuccess(callBack.getData());
            } else if (flag.equals("libUnFav")) {
                //view.loadDataError("0");
            } else if (flag.equals("glyph_video_fav")) {
                EventBus.getDefault().post(new Integer(0x2000));
            } else if (flag.equals("glyph_video_unfav")) {
                EventBus.getDefault().post(new Integer(0x2001));
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