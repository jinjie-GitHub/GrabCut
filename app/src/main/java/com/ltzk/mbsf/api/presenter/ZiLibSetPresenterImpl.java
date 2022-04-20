package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZiLibAddView;
import com.ltzk.mbsf.api.view.ZiLibSetView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;
import com.ltzk.mbsf.bean.ZilibBean;

import java.util.List;


/**
 * 描述：新增字库
 * 邮箱：499629556@qq.com
 */

public class ZiLibSetPresenterImpl extends BasePresenterImp<ZiLibSetView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("font_details")) {
                view.loadDataSuccess((ZilibBean) callBack.getData());
            }else if (flag.equals("font_update")) {
                view.updateSuccess("");
            }
        } else {
            if(flag.equals("font_update")){
                view.updateFail(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }else {
                view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }
        }
    }

    public void font_details(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_details(requestBean.getParams()),this,"font_details",isShow);
    }

    public void font_delete(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_delete(requestBean.getParams()),this,"font_delete",isShow);
    }

    public void font_update(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_update(requestBean.getParams()),this,"font_update",isShow);
    }

}
