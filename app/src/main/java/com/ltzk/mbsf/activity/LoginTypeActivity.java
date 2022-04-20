package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.LoginByWechatPresenterImpl;
import com.ltzk.mbsf.api.view.LoginByWechatView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginCannel;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_wechatLogin;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.ToastUtil;
import com.tencent.mm.opensdk.modelmsg.SendAuth;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;

/**
 * 描述：登录方式选择
 * 作者： on 2017/11/26 17:38
 * 邮箱：499629556@qq.com
 */

public class LoginTypeActivity extends MyBaseActivity<LoginByWechatView,LoginByWechatPresenterImpl> implements LoginByWechatView {

    @OnClick(R2.id.tv_wechat)
    public void tv_wechat(View view){
        // IWXAPI 是第三方app和微信通信的openapi接口

        if (!MainApplication.getInstance().api.isWXAppInstalled()) {
            ToastUtil.showToast(activity,"您还未安装微信客户端");
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "mbsf_wx_login";
        MainApplication.getInstance().api.sendReq(req);
    }

    @OnClick(R.id.login_phone)
    public void login_phone(View view) {
        startActivity(new Intent(activity, LoginByCodeActivity.class));
    }

    @OnClick(R.id.login_user)
    public void login_user(View view) {
        startActivity(new Intent(activity, LoginByPwdActivity.class));
    }

    @Override
    public void initView() {
        topBar.setTitle("用户登录");
        topBar.setRightTxtListener("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Bus_LoginCannel());
            }
        });
        topBar.setLeftButtonNoPic();

        requestBean = new RequestBean();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login_type;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    //登录取消
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginCannel messageEvent) {
        finish();
    }

    //登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        finish();
    }


    //微信登录授权成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_wechatLogin messageEvent) {
        Logger.e("--->login");
        synchronized (this) {
            if (!requestBean.containsKey("code")) {
                requestBean.addParams("code", messageEvent.getCode());
                presenter.loginByWechat(requestBean, true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected LoginByWechatPresenterImpl getPresenter() {
        return new LoginByWechatPresenterImpl();
    }

    RequestBean requestBean;

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataSuccess(UserBean userBean) {
        MainApplication.getInstance().putToken(userBean.get_token());
        MainApplication.getInstance().putUser(userBean);
        EventBus.getDefault().post(new Bus_LoginSucces());
    }

    @Override
    public void loadDataError(String errorMsg) {

    }
}
