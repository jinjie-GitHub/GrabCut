package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.view.View;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.UserDelPresenterImpl;
import com.ltzk.mbsf.api.view.UserDelView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginOut;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.ToastUtil;
import org.greenrobot.eventbus.EventBus;
import butterknife.OnClick;

/**
 * update on 2021/4/13
 */
public class UserDelActivity extends MyBaseActivity<UserDelView, UserDelPresenterImpl> implements UserDelView {

    private final RequestBean requestBean = new RequestBean();

    @OnClick(R2.id.tv_btn)
    public void tv_btn(View view) {
        TipPopView tipPopView = new TipPopView(activity, "再次确认", "注销操作无法恢复。", "注销", new TipPopView.TipListener() {
            @Override
            public void ok() {
                presenter.del(requestBean, true);
            }
        });
        tipPopView.showPopupWindow(topBar);
    }

    @Override
    public void initView() {
        topBar.setTitle("注销账号");
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_del;
    }

    @Override
    protected UserDelPresenterImpl getPresenter() {
        return new UserDelPresenterImpl();
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
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg + "");
    }

    @Override
    public void loadDataSuccess(String tData) {
        MainApplication.getInstance().quit();
        startActivity(new Intent(activity, MainActivity.class));
        EventBus.getDefault().post(new Bus_LoginOut(""));
        finish();
    }
}