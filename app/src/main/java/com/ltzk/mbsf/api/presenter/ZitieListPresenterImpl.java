package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZitieListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.bean.ZuopinDetailBean;
import com.ltzk.mbsf.utils.ExceptionUtil;


/**
 * 描述：字帖列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZitieListPresenterImpl extends BasePresenterImp<ZitieListView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("zuopin_ztlist") || flag.equals("zitie_query")){
                view.loadDataSuccess((RowBean<ZitieBean>) callBack.getData());
            }else if (flag.equals("zuopin_details")){
                view.getZuopinDetailResult((ZuopinDetailBean) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        if (flag.equals("zuopin_ztlist") || flag.equals("zitie_query")){
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        }else if (flag.equals("zuopin_details")){
            view.getZuopinDetailResultFail(ExceptionUtil.getExceptionMsg(throwable));
        }
        view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
    }

    public void zuopin_ztlist(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zuopin_ztlist(requestBean.getParams()),this,"zuopin_ztlist",isShow);
    }

    public void getListByKey(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_query(requestBean.getParams()),this,"zitie_query",isShow);
    }

    public void zuopin_details(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zuopin_details(requestBean.getParams()),this,"zuopin_details",isShow);
    }
}
