package com.ltzk.mbsf.activity;

import android.view.View;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.ZiLibDelPresenterImpl;
import com.ltzk.mbsf.api.view.ZiLibDelView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_ZilibDel;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.ToastUtil;
import org.greenrobot.eventbus.EventBus;
import butterknife.OnClick;

/**
 * 删除字库 update on 2021/06/10
 */
public class ZiLibDelActivity extends MyBaseActivity<ZiLibDelView, ZiLibDelPresenterImpl> implements ZiLibDelView {

    private TipPopView tipPopView;
    @OnClick(R2.id.tv_btn)
    public void tv_btn(View view) {
        //弹窗提示
        if (tipPopView == null) {
            tipPopView = new TipPopView(activity, "", "请再次确认？", "删除", new TipPopView.TipListener() {
                @Override
                public void ok() {
                    requestBean.addParams("fid", zilibBean.get_id());
                    presenter.font_delete(requestBean, true);
                }
            });
        }
        tipPopView.showPopupWindow(activity.getWindow().getDecorView());
    }

    private ZilibBean zilibBean;
    public void initView() {
        zilibBean = (ZilibBean) getIntent().getSerializableExtra("zilibBean");
        if (zilibBean == null) {
            finish();
            return;
        }

        topBar.setTitle(zilibBean.get_title());
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zilib_del;
    }

    private RequestBean requestBean;
    protected ZiLibDelPresenterImpl getPresenter() {
        requestBean = new RequestBean();
        return new ZiLibDelPresenterImpl();
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataSuccess(String tData) {
        EventBus.getDefault().post(new Bus_ZilibDel());
        finish();
    }

    @Override
    public void loadDataError(String msg) {
        ToastUtil.showToast(activity, msg);
    }
}