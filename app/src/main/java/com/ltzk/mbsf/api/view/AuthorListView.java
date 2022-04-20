package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.AuthorBean;

import java.util.List;

/**
 * 描述：作者列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface AuthorListView extends IBaseView<List<AuthorBean>> {
    public void dylist(List<String> list);
    public void dylistFail(String s);
}

