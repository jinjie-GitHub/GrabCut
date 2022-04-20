package com.ltzk.mbsf.api.presenter;


import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.JiziListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;


/**
 * 描述：集字详细 新建 编辑
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class JiziListPresenterImpl extends BasePresenterImp<JiziListView>{


    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        view.disimissProgress();
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("jizi_list")) {
                view.loadDataSuccess((RowBean<JiziBean>) callBack.getData());
            }else if (flag.equals("jizi_delete")) {
                view.jizi_deleteResult((String) callBack.getData());
            }else if (flag.equals("update_order")) {
                view.update_orderResult((String) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    public void getList(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_list(requestBean.getParams()),this,"jizi_list",isShow);
    }

    public void jizi_delete(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.jizi_delete(requestBean.getParams()),this,"jizi_delete",isShow);
    }

    public void update_order(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.update_order(requestBean.getParams()),this,"update_order",isShow);
    }
}
