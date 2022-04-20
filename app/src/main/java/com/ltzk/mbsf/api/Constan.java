package com.ltzk.mbsf.api;

/**
 * 描述：常量
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */
public class Constan {

    public static final String wechat_APPID = "wxe545b5e5fdefb996";
    public static final String QQ_APPID = "1106128572";
    /**
     * 描述：网络接口成功状态码
     */
    public static final int response_sussess = 0;
    /**
     * 描述：网络接口成功状态码
     */
    public static final int pensize = 0;

    public interface Key_type{
        public static final String KEY_ZI = "key_zi"; //字
        public static final String KEY_AUTHOR = "key_author"; //书法家
        public static final String KEY_ZIKU = "key_ziku"; //字库
        public static final String KEY_AUTHOR_PERFER = "key_author_perfer"; //集字里所选的书法家
        public static final String KEY_ZITIE = "key_zitie"; //字帖

        public static final String KEY_AUTHOR_ZILIST = "key_author_zilist"; //字典列表书法家
        public static final String KEY_AUTHOR_JIZI = "key_author_jizi"; //集字书法家

        public static final String KEY_AUTHOR_ZIKU = "key_author_zilib"; //集字书法家
        public static final String KEY_WEB_JIZI = "key_web_jizi";
        public static final String KEY_ZIDIAN_TAB_POS = "key_zidian_tab_pos";
        public static final String KEY_ZILIB_TAB_POS = "key_zilib_tab_pos";

        public static final String KEY_ZIDIAN_TYPE = "key_zidian_type";
        public static final String KEY_ZIDIAN_FONT = "key_zidian_font";
        public static final String KEY_ZIDIAN_ORDERBY = "key_zidian_orderby";
    }

}
