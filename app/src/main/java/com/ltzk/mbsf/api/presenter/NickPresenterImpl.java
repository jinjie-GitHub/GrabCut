package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.NickUpdateView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;


/**
 * 描述：昵称编辑
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class NickPresenterImpl extends BasePresenterImp<NickUpdateView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("update_nickname")) {
                view.loadDataSuccess((String) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }


    public void update_nickname(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.update_nickname(requestBean.getParams()),this,"update_nickname",isShow);
    }


}
