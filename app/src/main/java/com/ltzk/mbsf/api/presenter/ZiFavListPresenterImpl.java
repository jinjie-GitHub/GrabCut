package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZiFavListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.utils.ExceptionUtil;


/**
 * 描述：字典收藏列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZiFavListPresenterImpl extends BasePresenterImp<ZiFavListView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("glyph_favlist_hanzi")) {
                view.loadDataSuccess((RowBean<ZiBean>) callBack.getData());
            }
        } else {
            if (flag.equals("glyph_favlist_hanzi")) {
                view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        if (flag.equals("glyph_favlist_hanzi")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
            view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
        }
    }

    public void glyph_favlist_hanzi(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.glyph_favlist_hanzi(requestBean.getParams()),this,"glyph_favlist_hanzi",isShow);
    }

    public void glyph_unfav_aid(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.glyph_unfav_aid(requestBean.getParams()),this,"glyph_unfav_aid",isShow);
    }

    public void disposeAll(){
        modelImpl.dispose();
    }

}
