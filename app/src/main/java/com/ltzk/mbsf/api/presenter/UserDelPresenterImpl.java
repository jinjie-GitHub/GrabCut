package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.view.UserDelView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;


/**
 * 描述：注销
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class UserDelPresenterImpl extends BasePresenterImp<UserDelView>{

    public void del(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.delete(requestBean.getParams()),this,"delete",isShow);
    }

}
