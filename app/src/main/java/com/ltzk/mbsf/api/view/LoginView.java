package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.UserBean;

/**
 * 描述：登录
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface LoginView extends IBaseView<String> {
    void LoginResult(UserBean bean);
    void update_pwdResult(String bean);
}
