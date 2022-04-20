package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.bean.ZitieShiwenBean;

import java.util.List;

/**
 * 描述：字帖详细
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZitieDetailView extends IBaseView<ZitieBean> {
    public void getShiwenResult(ZitieShiwenBean zitieShiwenBean);
    public void favResult(String bean);
    public void unfavResult(String bean);
}

