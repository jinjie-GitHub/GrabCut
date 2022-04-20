package com.ltzk.mbsf.utils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述：
 * 作者： on 2018/12/28 11:17
 * 邮箱：499629556@qq.com
 */

public class UrlUtil {

    public static class UrlEntity {
        /**
         * 基础url
         */
        public String baseUrl;
        /**
         * url参数
         */
        public Map<String, String> params;
    }

    /**
     * 解析url
     *
     * @param url
     * @return
     */
    public static UrlEntity parse(String url) {
        UrlEntity entity = new UrlEntity();
        if (url == null) {
            return entity;
        }
        url = url.trim();
        if (url.equals("")) {
            return entity;
        }
        String[] urlParts = url.split("\\?");
        entity.baseUrl = urlParts[0];
        //没有参数
        if (urlParts.length == 1) {
            return entity;
        }
        //有参数
        String[] params = urlParts[1].split("&");
        entity.params = new HashMap<>();
        for (String param : params) {
            String[] keyValue = param.split("=");
            entity.params.put(keyValue[0], java.net.URLDecoder.decode(keyValue[1]));
        }

        return entity;
    }

    public static String getUrl(UrlEntity entity) {
        StringBuffer buffer = new StringBuffer(entity.baseUrl);
        buffer.append("?");
        for(Map.Entry<String,String> entry : entity.params.entrySet()){
            buffer.append(entry.getKey());
            buffer.append("=");
            buffer.append(entry.getValue());
            buffer.append("&");

        }
        return buffer.toString();
    }

    public static String getBaseUrl(String url){
            if (url == null) {
                return "";
            }
            url = url.trim();
            if (url.equals("")) {
                return "";
            }
            String[] urlParts = url.split("\\?");
            return urlParts[0];
    }
}
