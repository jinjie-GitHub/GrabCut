package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.ZitieSoucangListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieBean;


/**
 * 描述：字帖收藏列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class ZitieSoucangListPresenterImpl extends BasePresenterImp<ZitieSoucangListView>{

    /**
     * @param callBack 回调的数据
     * @descriptoin 请求成功获取成功之后的数据信息
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("zitie_page_favlist")) {
                view.loadDataSuccess((RowBean<ZitieBean>) callBack.getData());
            }else if (flag.equals("zitie_page_unfav")) {
                view.unfavResult((String) callBack.getData());
            }
        } else {
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

    public void getList(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_favlist(requestBean.getParams()),this,"zitie_page_favlist",isShow);
    }

    public void zitie_page_unfav(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.zitie_page_unfav(requestBean.getParams()),this,"zitie_page_unfav",isShow);
    }

}
