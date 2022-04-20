package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieBean;

/**
 * 描述：字帖收藏列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZitieSoucangListView extends IBaseView<RowBean<ZitieBean>> {
    public void unfavResult(String bean);
}

