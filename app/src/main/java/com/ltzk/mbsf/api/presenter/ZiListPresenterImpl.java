package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZiListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiAuthorBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.utils.ExceptionUtil;

import java.util.Arrays;


/**
 * 描述：字列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZiListPresenterImpl extends BasePresenterImp<ZiListView>{

    @Override
    public void requestError(Throwable throwable, String flag) {
        if (flag.equals("glyph_query")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
            view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
        }else if (flag.equals("glyph_favlist_hanzi")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
            view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
        }else if(flag.equals("glyph_authors")){
            view.glyph_authorsFail("");
        }
    }

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if (Constan.response_sussess == callBack.getStat()) {
            if (flag.equals("glyph_query") || flag.equals("glyph_picker")) {
                view.loadDataSuccess((RowBean<ZiBean>) callBack.getData());
            } else if (flag.equals("glyph_authors")) {
                view.glyph_authorsSucess((RowBean<ZiAuthorBean>) callBack.getData());
            } else if (flag.equals("author_fav") || flag.equals("author_unfav")) {
                view.author_fav_unfav();
            }
        } else {
            if (flag.equals("glyph_query") || flag.equals("glyph_picker")) {
                view.loadDataError(callBack.getError().getTitle() + "" + callBack.getError().getMessage());
            } else if (flag.equals("glyph_favlist_hanzi")) {
                view.loadDataError(callBack.getError().getTitle() + "" + callBack.getError().getMessage());
            } else if (flag.equals("glyph_authors")) {
                view.glyph_authorsFail(callBack.getError().getMessage());
            }
        }
    }

    public void glyph_fav_aid(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.glyph_fav_aid(requestBean.getParams()),this,"glyph_fav_aid",isShow);
    }

    public void getList(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.glyph_query(requestBean.getParams()),this,"glyph_query",isShow);
    }

    public void glyph_authors(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.glyph_authors(requestBean.getParams()),this,"glyph_authors",isShow);
    }

    public void getListPick(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.glyph_picker(requestBean.getParams()),this,"glyph_picker",isShow);
    }

    public void disposeAll(){
        modelImpl.dispose();
    }

    public void author_fav(String name) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("name", name);
        modelImpl.getDataFromHttp(serviceApi.author_fav(requestBean.getParams()), this, "author_fav", false);
    }

    public void author_unfav(String... name) {
        final RequestBean requestBean = new RequestBean();
        requestBean.addParams("names", Arrays.asList(name));
        modelImpl.getDataFromHttp(serviceApi.author_unfav(requestBean.getParams()), this, "author_unfav", false);
    }
}
