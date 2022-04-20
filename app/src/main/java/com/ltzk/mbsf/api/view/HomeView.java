package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.BannerViewsInfo;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.TodayBean;

import java.util.List;

/**
 * Created by JinJie on 2020/5/25
 */
public interface HomeView extends IBaseView<TodayBean> {
    void setCalendar(ResponseData data);
    void slider_items_home(List<BannerViewsInfo> data);
}