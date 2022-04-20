package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.view.AuthorListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;


/**
 * 描述：作者列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class AuthorDetailPresenterImpl extends BasePresenterImp<AuthorListView>{

    public void author_details(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.author_details(requestBean.getParams()),this,"author_details",isShow);
    }
}
