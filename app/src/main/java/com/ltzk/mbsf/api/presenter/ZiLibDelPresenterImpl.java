package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.view.ZiLibAddView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;


/**
 * 描述：新增字库
 * 邮箱：499629556@qq.com
 */

public class ZiLibDelPresenterImpl extends BasePresenterImp<ZiLibAddView>{



    public void font_delete(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.font_delete(requestBean.getParams()),this,"font_delete",isShow);
    }

}
