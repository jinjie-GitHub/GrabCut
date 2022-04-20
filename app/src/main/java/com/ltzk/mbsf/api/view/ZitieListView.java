package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.bean.ZuopinDetailBean;

/**
 * 描述：字帖列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZitieListView extends IBaseView<RowBean<ZitieBean>> {
    public void getZuopinDetailResult(ZuopinDetailBean bean);
    public void getZuopinDetailResultFail(String msg);
}

