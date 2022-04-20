package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.UserBean;

import org.json.JSONObject;

/**
 * 描述：个人中心
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface MyView extends IBaseView<UserBean> {
    public void logoutResult(String bean);
    public void logoutError(String bean);
    public void checkinResult(JSONObject bean);
    public void unbind_weixinResult(String bean);
    public void unbind_phoneResult(String bean);
    public void update_avatarResult(UserBean bean);
    public void xiaoe_sdk_logout();
}
