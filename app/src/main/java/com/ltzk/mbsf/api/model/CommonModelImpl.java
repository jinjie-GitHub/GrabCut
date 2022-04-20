package com.ltzk.mbsf.api.model;

import android.text.TextUtils;
import android.util.Log;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BaseModel;
import com.ltzk.mbsf.base.IBaseRequestCallBack;
import com.ltzk.mbsf.bean.Bus_LoginOut;
import com.ltzk.mbsf.bean.Bus_LoginOutTip;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述：
 * 作者： on 2017/11/20 14:52
 * 邮箱：499629556@qq.com
 */
public class CommonModelImpl extends BaseModel implements CommonModel {
    private static final String ERROR_MSG = "Null is not a valid element";

    private CompositeDisposable mCompositeSubscription;

    public CommonModelImpl() {
        super();
        mCompositeSubscription = new CompositeDisposable();
    }

    /**
     * 数据的回调接口
     */
    @Override
    public void getDataFromHttp(Observable observable, final IBaseRequestCallBack iBaseRequestCallBack, final String flag, final boolean isShow) {
        if (mCompositeSubscription == null || mCompositeSubscription.isDisposed()) {
            mCompositeSubscription = new CompositeDisposable();
        }

        observable  //将subscribe添加到subscription，用于注销subscribe
                .observeOn(AndroidSchedulers.mainThread())//指定事件消费线程
                .subscribeOn(Schedulers.io())  //指定 subscribe() 发生在 IO 线程
                .subscribe(new Observer<ResponseData>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (isShow)
                            iBaseRequestCallBack.beforeRequest();
                        mCompositeSubscription.add(d);
                    }

                    @Override
                    public void onComplete() {
                        iBaseRequestCallBack.requestComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("--->", "onError:" + e.toString());
                        final String msg = e.getMessage();
                        if (!TextUtils.isEmpty(msg) && msg.contains(ERROR_MSG)) {

                        } else {
                            //回调接口：请求异常
                            iBaseRequestCallBack.requestError(e, flag);
                        }
                    }

                    @Override
                    public void onNext(ResponseData responseData) {
                        if (responseData == null) {
                            return;
                        }

                        if (!SPUtils.get(MainApplication.getInstance(), "appversion", "").equals(responseData.getAppversion())) {
                            SPUtils.put(MainApplication.getInstance(), "appversion", responseData.getAppversion());
                        }

                        if (responseData.getStat() != Constan.response_sussess && "1001".equals(responseData.getError().getCode())) { //登录被踢
                            MainApplication.getInstance().quit();
                            if ("glyph_query".equals(flag)) {//首页字典列表
                                EventBus.getDefault().post(new Bus_LoginOut(flag, responseData.getError().getMessage()));
                            } else if ("jizi_list".equals(flag)) {//首页集字列表
                                EventBus.getDefault().post(new Bus_LoginOut(flag));
                            } else if ("logout".equals(flag)) {//退出登录
                                iBaseRequestCallBack.requestError(null, flag);
                            } else {//其他接口检测到被退出
                                EventBus.getDefault().post(new Bus_LoginOut(flag));
                                //非首页检测到为登录
                                Bus_LoginOutTip bus_loginOutTip = new Bus_LoginOutTip(responseData.getError().getTitle(), responseData.getError().getMessage());
                                EventBus.getDefault().post(bus_loginOutTip);
                            }
                        } else {
                            //回调接口：请求成功，获取实体类对象
                            iBaseRequestCallBack.requestSuccess(responseData, flag);
                        }
                    }
                });
    }

    @Override
    public void onUnsubscribe() {
        //判断状态
        if (mCompositeSubscription != null) {
            mCompositeSubscription.dispose();
            mCompositeSubscription = null;
        }
    }

    @Override
    public void dispose() {
        //判断状态
        if (mCompositeSubscription != null) {
            mCompositeSubscription.dispose();
        }
    }
}