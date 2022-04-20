package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZilibBean;

/**
 * 描述：登录
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZiLibSetView extends IBaseView<ZilibBean> {
    public void updateFail(String msg);
    public void updateSuccess(String msg);
}
