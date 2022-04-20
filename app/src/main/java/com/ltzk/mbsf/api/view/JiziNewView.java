package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.JiziBean;

/**
 * 描述：集字 新建 编辑
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface JiziNewView extends IBaseView<JiziBean> {
    public void jizi_updateResult(JiziBean tData);
    public void setUp(String data);
}
