package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.AuthorFamous;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import java.util.List;

/**
 * 描述：字帖列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */
public interface ZitiejiListView extends IBaseView<ZitieHomeBean> {
    //void setBannerViewsInfo(List<BannerViewsInfo> data);
    void setAuthorFamous(AuthorFamous data);
    void setDynastyList(List<String> data);
}
