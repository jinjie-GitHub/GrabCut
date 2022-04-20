package com.ltzk.mbsf.base;


import com.ltzk.mbsf.bean.ResponseData;

/**
 * 描述：请求数据的回调接口
 * Presenter用于接受model获取（加载）数据后的回调
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */
public interface IBaseRequestCallBack {

    /**
     * @descriptoin	请求之前的操作
     */
    void beforeRequest();

    /**
     * @descriptoin	请求异常
     * @param throwable 异常类型
     */
    void requestError(Throwable throwable, String flag);

    /**
     * @descriptoin	请求完成
     */
    void requestComplete();

    /**
     * @descriptoin	请求成功
     * @param responseData 根据业务返回相应的数据
     */
    void requestSuccess(ResponseData responseData, String flag);
}
