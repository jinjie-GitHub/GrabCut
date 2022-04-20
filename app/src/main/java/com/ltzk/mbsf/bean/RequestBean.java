package com.ltzk.mbsf.bean;


import com.google.gson.Gson;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.utils.EncryptUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：网络请求实体
 */
public class RequestBean implements Serializable {


    private Map<String, Object> params;

    public Object getParam(String key){
        if (params != null) {
            return params.get(key);
        }
        return null;
    }

    public String getParams() {
        try{
            params.put("_token", ""+ MainApplication.getInstance().getToken());
            Gson gson = new Gson();
            String jsonStr = gson.toJson(params);
            return EncryptUtil.encrypt(jsonStr);
        }catch (Exception e){
            return "";
        }
    }
    public RequestBean() {
        if (params == null) {
            params = new HashMap<String, Object>();
            params.put("_channel", ""+ MainApplication.getChannel());
            params.put("_brand", ""+ MainApplication.brand);
            params.put("_plat", "android");
            params.put("_version",MainApplication.getVersionName());
        }
    }

    /**
     * 添加请求数据
     * @param key
     * @param value
     */
    public void addParams(String key, Object value) {
        params.put(key, value);
    }

    public boolean containsKey(String key) {
        return params.containsKey(key);
    }
}
