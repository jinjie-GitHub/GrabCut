package com.ltzk.mbsf.api.view;

import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.FontListBean;
import java.util.List;

/**
 * Created by JinJie on 2020/6/7
 */
public interface GlyphDetailView extends IBaseView {
    void fontList(FontListBean fontBean);
    void add(int stat);
    void glyphs(List<DetailsBean> data);
}