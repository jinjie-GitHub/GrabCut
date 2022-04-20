package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;

import java.io.Serializable;

/**
 * 描述：返回数据结构
 */
public class ResponseData<T> implements Serializable {
    //提示信息
    private Error error;
    //1表示成功
    private int stat;
    //数据结果
    private T data;

    private String appversion;

    private boolean showad;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public int getStat() {
        return stat;
    }

    public void setStat(int stat) {
        this.stat = stat;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getAppversion() {
        if(appversion == null){
            appversion = "";
        }
        return appversion;
    }

    public void setAppversion(String appversion) {
        this.appversion = appversion;
    }

    public boolean isShowAd() {
        return showad;
    }

    public class Error extends BaseBean {
        private String code;
        private String title;
        private String message;
        private String custom;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getCustom() {
            return custom;
        }

        public void setCustom(String custom) {
            this.custom = custom;
        }
    }
}
