package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RowBean;

/**
 * 描述：集字列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface JiziListView extends IBaseView<RowBean<JiziBean>> {
    public void jizi_deleteResult(String bean);
    public void update_orderResult(String bean);
}
