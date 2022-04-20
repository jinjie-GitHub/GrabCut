package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZitieDetailView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.bean.ZitieShiwenBean;

import java.util.List;


/**
 * 描述：字帖详细
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZitieDetailPresenterImpl extends BasePresenterImp<ZitieDetailView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("zitie_details")) {
                view.loadDataSuccess((ZitieBean) callBack.getData());
            }else if (flag.equals("zitie_page_text")) {
                view.getShiwenResult((ZitieShiwenBean) callBack.getData());
            }else if (flag.equals("zitie_page_fav")) {
                view.favResult((String) callBack.getData());
            }else if (flag.equals("zitie_page_unfav")) {
                view.unfavResult((String) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    public void getDetail(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_details(requestBean.getParams()),this,"zitie_details",isShow);
    }

    /**
     * 获取释文
     * @param requestBean
     * @param isShow
     */
    public void zitie_page_text(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_text(requestBean.getParams()),this,"zitie_page_text",isShow);
    }

    public void zitie_page_fav(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_fav(requestBean.getParams()),this,"zitie_page_fav",isShow);
    }

    public void zitie_page_unfav(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_unfav(requestBean.getParams()),this,"zitie_page_unfav",isShow);
    }


}
