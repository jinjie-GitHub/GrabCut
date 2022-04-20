package com.ltzk.mbsf.api.model;


import com.ltzk.mbsf.base.IBaseRequestCallBack;

import io.reactivex.Observable;


/**
 * 描述：
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface CommonModel {

    /**
     * @descriptoin
     * @author	dc
     * @param iBaseRequestCallBack 数据的回调接口
     * @date 2017/2/17 19:01
     */
    void getDataFromHttp(Observable observable, IBaseRequestCallBack iBaseRequestCallBack, String flag, boolean isShow);

    /**
     * @descriptoin	注销subscribe
     * @author
     * @param
     * @date 2017/2/17 19:02
     * @return
     */
    void onUnsubscribe();

    /**
     * @descriptoin	丢弃之前的请求
     * @author
     * @param
     * @date 2017/2/17 19:02
     * @return
     */
    void dispose();
}
