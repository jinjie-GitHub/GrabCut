package com.ltzk.mbsf.api.presenter;

import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.view.AuthorListView;
import com.ltzk.mbsf.base.BasePresenterImp;
import com.ltzk.mbsf.bean.AuthorBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;

import java.util.List;


/**
 * 描述：作者列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public class AuthorListPresenterImpl extends BasePresenterImp<AuthorListView>{

    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            if (flag.equals("author_query")) {
                view.loadDataSuccess((List<AuthorBean>) callBack.getData());
            }else if (flag.equals("dylist")) {
                view.dylist((List<String>) callBack.getData());
            }
        } else {
            if (flag.equals("author_query")) {
                view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
            }else if (flag.equals("dylist")) {
                view.dylistFail("");
            }
        }
    }

    @Override
    public void requestError(Throwable throwable, String flag) {
        if (flag.equals("author_query")) {
            view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        }else if (flag.equals("dylist")) {
            view.dylistFail("");
        }
        view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
    }

    public void getAuthorList(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.author_query(requestBean.getParams()),this,"author_query",isShow);
    }

    public void dylist(RequestBean requestBean, boolean isShow) {
        modelImpl.getDataFromHttp(serviceApi.dylist(requestBean.getParams()),this,"dylist",isShow);
    }

}
