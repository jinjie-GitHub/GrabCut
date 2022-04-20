package com.ltzk.mbsf.api;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.ltzk.mbsf.utils.EncryptUtil;
import com.ltzk.mbsf.utils.HbLogUtil;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2017-03-28.
 */

public class JsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
    private final Gson gson;//gson对象
    private final TypeAdapter<T> adapter;

    /**
     * 构造器
     */
    public JsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    /**
     * 转换
     *
     * @param responseBody
     * @return
     * @throws IOException
     */
    @Override
    public T convert(ResponseBody responseBody) throws IOException {
        try {
            String result = EncryptUtil.decrypt(responseBody.bytes());
            HbLogUtil.getInstance().json("http:response"+result);
            return adapter.fromJson(result);
            /*return adapter.fromJson(responseBody.string());*/
        }catch (Exception e){
            return null;
        }
    }

}
