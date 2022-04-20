package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;

import java.util.List;


/**
 * 描述：字库字形详细列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZiLibZiListView extends IBaseView<RowBean<ZiLibDetailBean>> {
    void getfont_charsetSuccess(List<String> list);
    void getfont_charsetFail(String msg);
    void deleteSuccess(String msg);
}
