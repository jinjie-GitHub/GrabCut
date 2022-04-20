package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.ZitieBean;

/**
 * 描述：WebView
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface BigZitieView extends IBaseView<String> {
    public void favResult(String bean);
    public void unfavResult(String bean);
    public void getDetailSuccess(ZitieBean bean);
    public void getDetailFail(String bean);

    public void favAndUnFav(String bean);
}
