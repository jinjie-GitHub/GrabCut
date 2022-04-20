package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.VersionBean;
import com.ltzk.mbsf.bean.XeLoginBean;

/**
 * 描述：VersionView
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface VersionView extends IBaseView<VersionBean> {
    void getUserInfoSuccess(UserBean userBean);
    void user_sdk_login(XeLoginBean bean);
}
