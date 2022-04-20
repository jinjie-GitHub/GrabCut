package com.ltzk.mbsf.api.view;


import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.bean.JiziBean;

import java.util.List;

/**
 * 描述：集字详细 新建 编辑
 * 作者： on 2017/11/20 15:48
 * 邮箱：499629556@qq.com
 */

public interface JiziEditView extends IBaseView<String> {
    public void jizi_autoResult(JiziBean jiziBean);
    public void jizi_update(boolean success);
    public void jizi_tmpl_list(Object data);
    public void jizi_glyphs(Object data);
}
