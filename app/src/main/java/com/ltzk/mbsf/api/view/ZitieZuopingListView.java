package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.GalleryDetailBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieHomeBean;

/**
 * 描述：字帖选辑作品列表
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface ZitieZuopingListView extends IBaseView<RowBean<ZitieHomeBean.ListBeanX.ListBean>> {
    public void getGalleryItemsResult(RowBean<ZitieHomeBean.ListBeanX.ListBeanAndType> bean);
    public void getGalleryDetailResult(GalleryDetailBean bean);
    public void getGalleryDetailResultFail(String msg);
}

