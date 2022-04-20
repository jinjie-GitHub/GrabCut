package com.ltzk.mbsf.base;


import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.ServiceApi;
import com.ltzk.mbsf.api.model.CommonModel;
import com.ltzk.mbsf.api.model.CommonModelImpl;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.ExceptionUtil;

/**
 * 描述：
 *  * 代理对象的基础实现 ：  一些基础的方法
 *
 * @param <V> 视图接口对象(view) 具体业务各自继承自IBaseView
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */
public class BasePresenterImp<V extends IBaseView> implements IBaseRequestCallBack {

    //retrofit请求数据的管理类
    public RetrofitManager retrofitManager;
    public ServiceApi serviceApi;
    protected V view = null;  //基类视图
    protected CommonModel modelImpl;

    /**
     * @param iBaseView
     */
    public void subscribe(V iBaseView) {
        view = iBaseView;
        modelImpl = new CommonModelImpl();
        retrofitManager = RetrofitManager.builder();
        serviceApi = RetrofitManager.builder().getService();
    }

    /**
     *
     */
    public void unSubscribe() {
        modelImpl.onUnsubscribe();
        view = null;
    }

    /**
     * 取消网络请求
     */
    public void disSubscribe() {
        if (modelImpl != null) {
            modelImpl.dispose();
        }
    }

    /**
     * @descriptoin	请求之前显示progress
     * @date 2017/2/16 15:13
     */
    @Override
    public void beforeRequest() {
        view.showProgress();
    }

    /**
     * @descriptoin	请求异常显示异常信息
     * @param throwable 异常信息
     */
    @Override
    public void requestError(Throwable throwable, String flag) {
        view.loadDataError(ExceptionUtil.getExceptionMsg(throwable));
        view.disimissProgress(); //请求错误，提示错误信息之后隐藏progress
    }

    /**
     * @descriptoin	请求完成之后隐藏progress
     */
    @Override
    public void requestComplete() {
        view.disimissProgress();
    }

    /**
     * @descriptoin	请求成功获取成功之后的数据信息
     * @param callBack 回调的数据
     */
    @Override
    public void requestSuccess(ResponseData callBack, String flag) {
        if(Constan.response_sussess==callBack.getStat()){
            view.loadDataSuccess(callBack.getData());
        }else{
            view.loadDataError(callBack.getError().getTitle()+""+callBack.getError().getMessage());
        }
    }

}
