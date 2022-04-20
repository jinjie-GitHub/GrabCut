package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.BigZitieView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.ZitieBean;


/**
 * 描述：作者列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class BigZitiePresenterImpl extends BasePresenterImp<BigZitieView>{


    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("zitie_hd_url")) {
                view.loadDataSuccess((String) callBack.getData());
            }else if (flag.equals("zitie_page_fav")) {
                view.favResult((String) callBack.getData());
            }else if (flag.equals("zitie_page_unfav")) {
                view.unfavResult((String) callBack.getData());
            }else if (flag.equals("zitie_details")) {
                view.getDetailSuccess((ZitieBean) callBack.getData());
            }
        } else {
            if (flag.equals("zitie_page_fav") || flag.equals("zitie_page_unfav")) {
                view.favAndUnFav(callBack.getError().getMessage());
            } else {
                view.loadDataError(callBack.getError().getTitle() + "" + callBack.getError().getMessage());
            }
        }
    }

    public void getDetail(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_details(requestBean.getParams()),this,"zitie_details",isShow);
    }

    public void zitie_hd_url(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_hd_url(requestBean.getParams()),this,"zitie_hd_url",isShow);
    }

    public void zitie_page_fav(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_fav(requestBean.getParams()),this,"zitie_page_fav",isShow);
    }

    public void zitie_page_unfav(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_unfav(requestBean.getParams()),this,"zitie_page_unfav",isShow);
    }
}
