package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.BaseBean;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.UserBean;

/**
 * 描述：我的界面
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface PersionView extends IBaseView<BaseBean> {
    void getUserInfoSuccess(UserBean userBean);
    //void setupQQ(QQGroupBean obj);
}
