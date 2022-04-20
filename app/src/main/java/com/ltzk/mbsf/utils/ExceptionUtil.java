package com.ltzk.mbsf.utils;

import android.content.Context;


import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import retrofit2.HttpException;


/**
 * Created by Administrator on 2016-07-06.
 */
public class ExceptionUtil {

    /**
     * 异常处理
     * @param activity
     * @param throwable
     */
    public static void exception(Context activity, Throwable throwable){
        String msg = getExceptionMsg(throwable);
        ToastUtil.showToast(activity, msg);
    }

    public static String getExceptionMsg(Throwable throwable) {
        String msg = "";
        if(throwable instanceof ConnectException){
            msg = MainApplication.getInstance().getResources().getString(R.string.http_error);
        }else if (throwable instanceof TimeoutException || throwable instanceof SocketTimeoutException) {//超时
            msg = MainApplication.getInstance().getResources().getString(R.string.http_error_time_out);
        }else if (throwable instanceof IOException) {
            msg = MainApplication.getInstance().getResources().getString(R.string.http_error_time_out);
        }else if (throwable instanceof retrofit2.HttpException) {
            retrofit2.HttpException http = (retrofit2.HttpException) throwable;
            int errorCode = http.code();
            if (errorCode < 400 && errorCode > 299) {//重定向
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error300);
            } else if (errorCode < 500 && errorCode > 399) {//无效请求
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error400);
            } else if (errorCode == 504) {//服务器内部错
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error);
            }else if (errorCode < 600 && errorCode > 499) {//服务器内部错
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error500);
            }
        } else if (throwable instanceof HttpException) {
            HttpException http = (HttpException) throwable;
            int errorCode = http.code();
            if (errorCode < 400 && errorCode > 299) {//重定向
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error300);
            } else if (errorCode < 500 && errorCode > 399) {//无效请求
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error400);
            } else if (errorCode == 504) {//服务器内部错
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error);
            }else if (errorCode < 600 && errorCode > 499) {//服务器内部错
                msg = MainApplication.getInstance().getResources().getString(R.string.http_error500);
            }
        } else {
            msg = MainApplication.getInstance().getResources().getString(R.string.http_error_no_request);
        }
        return msg;
    }

    /**
     * 显示服务端反馈的错误msg
     * @param activity
     * @param msg
     */
    public static void showServerErrorMsg(Context activity, String msg){
        ToastUtil.showToast(activity, msg);
    }
}
