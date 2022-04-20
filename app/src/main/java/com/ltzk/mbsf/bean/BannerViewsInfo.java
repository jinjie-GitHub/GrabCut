package com.ltzk.mbsf.bean;

import com.ltzk.mbsf.base.BaseBean;
import com.stx.xhb.androidx.entity.BaseBannerInfo;

public class BannerViewsInfo extends BaseBean implements BaseBannerInfo {
    public String target;
    public String title;
    public String icon;
    public String img;
    public String url;
    public String type;
    public Params params;

    public static final class Params {
        public String username;
        public String path;
        public String tag;
    }

    @Override
    public Object getXBannerUrl() {
        return img;
    }

    @Override
    public String getXBannerTitle() {
        return "";
    }
}