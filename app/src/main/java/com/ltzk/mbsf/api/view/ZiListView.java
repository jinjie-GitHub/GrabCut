package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiAuthorBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZilibBean;


/**
 * 描述：字库列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZiListView extends IBaseView<RowBean<ZiBean>> {
    void glyph_authorsSucess(RowBean<ZiAuthorBean> bean);
    void glyph_authorsFail(String string);
    void author_fav_unfav();
}
