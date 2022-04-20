package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.view.ZiListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;


/**
 * 描述：字帖注解
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZitieZhujiePresenterImpl extends BasePresenterImp<ZiListView>{

    public void zitie_page_glyphs(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_glyphs(requestBean.getParams()),this,"zitie_page_glyphs",isShow);
    }

}
