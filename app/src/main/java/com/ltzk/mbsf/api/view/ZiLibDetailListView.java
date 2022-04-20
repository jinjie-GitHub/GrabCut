package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;
import com.ltzk.mbsf.bean.ZilibBean;


/**
 * 描述：字库字形详细列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZiLibDetailListView extends IBaseView<RowBean<ZiBean>> {
    public void uploadSuccess(ZiBean ziBean);
    public void uploadFail(String msg);
    public void addSuccess(String msg);
    public void addFail(String msg);
    public void deleteSuccess(String msg);
    public void deleteFail(String msg);
    public void preferSuccess(String msg);
    public void preferFail(String msg);
}
