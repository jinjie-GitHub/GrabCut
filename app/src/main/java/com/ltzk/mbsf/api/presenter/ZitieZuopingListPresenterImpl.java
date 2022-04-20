package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZitieZuopingListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.GalleryDetailBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import com.ltzk.mbsf.utils.ExceptionUtil;


/**
 * 描述：字帖作品列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZitieZuopingListPresenterImpl extends BasePresenterImp<ZitieZuopingListView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("gallery_items")) {
                view.getGalleryItemsResult((RowBean<ZitieHomeBean.ListBeanX.ListBeanAndType>) callBack.getData());
            }else if (flag.equals("author_zplist") || flag.equals("zuopin_query")){
                view.loadDataSuccess((RowBean<ZitieHomeBean.ListBeanX.ListBean>) callBack.getData());
            }else if (flag.equals("gallery_details")){
                view.getGalleryDetailResult((GalleryDetailBean) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }
    @Override
    public void requestError(Throwable throwable, String flag) {
        if (flag.equals("gallery_items") || flag.equals("author_zplist") || flag.equals("zuopin_query")){
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        }else if (flag.equals("zuopin_details")){
            view.getGalleryDetailResultFail(ExceptionUtil.getExceptionMsg(throwable));
        }
        view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
    }

    public void getList(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.gallery_items(requestBean.getParams()),this,"gallery_items",isShow);
    }

    public void author_zplist(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.author_zplist(requestBean.getParams()),this,"author_zplist",isShow);
    }

    public void zuopin_query(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zuopin_query(requestBean.getParams()),this,"zuopin_query",isShow);
    }

    public void gallery_details(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.gallery_details(requestBean.getParams()),this,"gallery_details",isShow);
    }

}
