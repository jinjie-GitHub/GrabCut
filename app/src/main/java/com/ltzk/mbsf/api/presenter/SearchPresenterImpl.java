package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.view.AuthorListView;
import com.ltzk.mbsf.api.view.SearchView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;


/**
 * 描述：作者列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class SearchPresenterImpl extends BasePresenterImp<SearchView>{

    public void author_sug(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.author_sug(requestBean.getParams()),this,"author_sug",isShow);
    }

    public void zuopin_sug(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zuopin_sug(requestBean.getParams()),this,"zuopin_sug",isShow);
    }
}
