package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.GlyphDetailView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.FontListBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;
import java.util.List;

/**
 * Created by JinJie on 2020/6/7
 */
public class GlyphDetailPresenter extends BasePresenterImp<GlyphDetailView> {

    public void details(String gid, boolean isShow) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("gid", gid);
        modelImpl.getDataFromHttp(serviceApi.details(requestBean.getParams()), this, "details", isShow);
    }

    /*public void glyphs(String zid, int page) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("zid", zid);
        requestBean.addParams("page", page);
        modelImpl.getDataFromHttp(serviceApi.glyphs(requestBean.getParams()), this, "glyphs", true);
    }*/

    public void query(int loaded) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("subset", "my");
        //requestBean.addParams("kind", "");
        //requestBean.addParams("font", "");
        //requestBean.addParams("key", "");
        requestBean.addParams("loaded", loaded);
        modelImpl.getDataFromHttp(serviceApi.query(requestBean.getParams()), this, "query", false);
    }

    public void add(String fid, String[] chars, String[] gids) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("fid", fid);
        requestBean.addParams("chars", chars);
        requestBean.addParams("gids", gids);
        modelImpl.getDataFromHttp(serviceApi.add(requestBean.getParams()), this, "add", false);
    }

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("details")) {
                view.loadDataSuccess(callBack.getData());
            } else if (flag.equals("query")) {
                FontListBean bean = (FontListBean) callBack.getData();
                view.fontList(bean);
            } else if (flag.equals("add")) {
                view.add(callBack.getStat());
            } else if (flag.equals("glyphs")) {
                List<DetailsBean> bean = (List<DetailsBean>) callBack.getData();
                view.glyphs(bean);
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