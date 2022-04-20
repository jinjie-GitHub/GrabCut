package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.view.ZiLibAddView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;


/**
 * 描述：删字库
 * 邮箱：499629556@qq.com
 */

public class ZiLibAddPresenterImpl extends BasePresenterImp<ZiLibAddView>{

    public void font_add(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_add(requestBean.getParams()),this,"font_add",isShow);
    }

}
