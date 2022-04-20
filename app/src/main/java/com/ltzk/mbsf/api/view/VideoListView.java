package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.VideoListBean;
import java.util.List;

/**
 * Created by JinJie on 2021/4/13
 */
public interface VideoListView extends IBaseView<VideoListBean> {
    void glyphs(List<DetailsBean> data);
    void upload(VideoListBean.Videos data);
}