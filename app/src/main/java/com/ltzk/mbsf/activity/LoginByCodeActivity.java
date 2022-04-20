package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.LoginPresenterImpl;
import com.ltzk.mbsf.api.view.LoginView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginCannel;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_PhoneSetCannel;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：登录和绑定手机号
 * 作者： on 2017/11/20 15:57
 * 邮箱：499629556@qq.com
 */
public class LoginByCodeActivity extends MyBaseActivity<LoginView,LoginPresenterImpl> implements LoginView {

    @BindView(R2.id.et_phone)
    EditText et_phone;
    @BindView(R2.id.iv_delete)
    ImageView iv_delete;
    @OnClick(R2.id.iv_delete)
    public void iv_delete(View view){
        et_phone.setText("");
    }

    @BindView(R2.id.et_code)
    EditText et_code;
    @BindView(R2.id.iv_delete_pwd)
    ImageView iv_delete_pwd;
    @OnClick(R2.id.iv_delete_pwd)
    public void iv_delete_pwd(View view){
        et_code.setText("");
    }
    @BindView(R2.id.tv_getCode)
    TextView tv_getCode;
    @OnClick(R2.id.tv_getCode)
    public void tv_getCode(View view){
        ActivityUtils.closeSyskeyBroad(activity);
        if (!ActivityUtils.editTextValueIsNull(et_phone) || et_phone.getText().toString().trim().length() < 11) {
            ToastUtil.showToast(activity, "请输入正确的手机号!");
            return;
        }
        RequestBean requestBean = new RequestBean();
        requestBean.addParams("nation","86");
        requestBean.addParams("phone",et_phone.getText().toString().trim());
        requestBean.addParams("type",type);
        presenter.getCode(requestBean,true);
    }
    private CountDownTimer countDownTimer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long l) {
            long time = l / 1000;
            tv_getCode.setText(""+ (time > 9 ?time:("0"+time)) + "s后重发");
        }

        @Override
        public void onFinish() {
            tv_getCode.setEnabled(true);
            if(type.equals("sms")){
                tv_getCode.setText("获取短信验证码");
            }else {
                tv_getCode.setText("获取语音验证码");
            }
        }
    };

    @BindView(R.id.tv_hint)
    TextView tv_hint;
    @BindView(R2.id.tv_btn)
    TextView tv_btn;
    @OnClick(R2.id.tv_btn)
    public void tv_btn(View view){
        ActivityUtils.closeSyskeyBroad(activity);

        if(!ActivityUtils.editTextValueIsNull(et_phone) || et_phone.getText().toString().trim().length()<11){
            ToastUtil.showToast(activity,"请输入正确手机号");
            return;
        }
        if(!ActivityUtils.editTextValueIsNull(et_code) || et_code.getText().toString().trim().length()<4){
            ToastUtil.showToast(activity,"请输入正确验证码");
            return;
        }
        requestBean.addParams("phone",et_phone.getText().toString().trim());
        requestBean.addParams("code",et_code.getText().toString().trim());
        presenter.loginByCode(requestBean,true);

    }

    @OnClick(R2.id.tv_1)
    public void tv_1(View view){
        startActivityForResult(new Intent(activity,LoginByPwdActivity.class),1);
        finish();
    }

    protected String type = "sms";
    @BindView(R2.id.tv_2)
    TextView tv_2;
    private boolean isEdit;

    public static void safeStart(Context c, boolean isEdit) {
        Intent intent = new Intent(c, LoginByCodeActivity.class);
        intent.putExtra("isEdit", isEdit);
        c.startActivity(intent);
    }

    @Override
    public void initView() {
        isEdit=getIntent().getBooleanExtra("isEdit",false);
        topBar.setTitle(isEdit?"短信验证":"短信验证码登录");
        tv_btn.setText(isEdit?"绑定":"登录");
        tv_hint.setVisibility(isEdit?View.GONE:View.VISIBLE);
        topBar.setRightTxtListener("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEdit){
                    EventBus.getDefault().post(new Bus_PhoneSetCannel());
                }else{
                    EventBus.getDefault().post(new Bus_LoginCannel());
                }
            }
        });
        type = "sms";
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_phone.getText().toString().trim() != null && et_phone.getText().toString().trim().length() == 11){
                    tv_getCode.setEnabled(true);
                    if (et_code.getText().toString().trim() != null && et_code.getText().toString().trim().length() == 4) {
                        tv_btn.setEnabled(true);
                    } else {
                        tv_btn.setEnabled(false);
                    }
                }else {
                    tv_getCode.setEnabled(false);
                    tv_btn.setEnabled(false);
                }
                if (s != null && s.length() > 0) {
                    iv_delete.setVisibility(View.VISIBLE);
                } else {
                    iv_delete.setVisibility(View.GONE);
                }
            }
        });


        et_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_phone.getText().toString().trim() != null && et_phone.getText().toString().trim().length() == 11){
                    if (et_code.getText().toString().trim() != null && et_code.getText().toString().trim().length() == 4) {
                        tv_btn.setEnabled(true);
                    } else {
                        tv_btn.setEnabled(false);
                    }
                }else {
                    tv_getCode.setEnabled(false);
                    tv_btn.setEnabled(false);
                }

                if (s != null && s.length() > 0) {
                    iv_delete_pwd.setVisibility(View.VISIBLE);
                } else {
                    iv_delete_pwd.setVisibility(View.GONE);
                }
            }
        });
        et_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if (et_phone.getText().toString() != null && et_phone.getText().toString().length() > 0) {
                        iv_delete.setVisibility(View.VISIBLE);
                    } else {
                        iv_delete.setVisibility(View.GONE);
                    }
                }else {
                    iv_delete.setVisibility(View.GONE);
                }
            }
        });
        et_code.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    if (et_code.getText().toString() != null && et_code.getText().toString().length() > 0) {
                        iv_delete_pwd.setVisibility(View.VISIBLE);
                    } else {
                        iv_delete_pwd.setVisibility(View.GONE);
                    }
                }else {
                    iv_delete_pwd.setVisibility(View.GONE);
                }
            }
        });
        et_phone.setText((String) SPUtils.get(activity,"phone",""));
        mHandler.sendEmptyMessageDelayed(0,500);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if(et_code.isFocused()){
                ActivityUtils.showInput(activity,et_code);
            }else {
                ActivityUtils.showInput(activity,et_phone);
            }

        }
    };

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_login_by_code;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            et_phone.setText(data.getStringExtra("phone")+"");
        }
    }

    @Override
    protected LoginPresenterImpl getPresenter() {
        return new LoginPresenterImpl();
    }

    RequestBean requestBean;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestBean = new RequestBean();
    }

    //登录取消
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginCannel messageEvent) {
        finish();
    }

    @Override
    public void onBackPressedSupport() {
        EventBus.getDefault().post(new Bus_LoginCannel());
        super.onBackPressedSupport();
    }

    //取消
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_PhoneSetCannel messageEvent) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
    public void LoginResult(UserBean userBean) {
        SPUtils.put(activity,"phone",et_phone.getText().toString());
        MainApplication.getInstance().putToken(userBean.get_token());
        MainApplication.getInstance().putUser(userBean);
        EventBus.getDefault().post(new Bus_LoginSucces());
        finish();
    }

    @Override
    public void loadDataSuccess(String tData) {
        ToastUtil.showToast(activity,"获取验证码成功！");
        requestBean.addParams("sid",tData+"");
        countDownTimer.start();
        tv_getCode.setEnabled(false);
    }

    @Override
    public void update_pwdResult(String bean) {
    }

    @Override
    public void loadDataError(String msg) {
        ToastUtil.showToast(activity,msg);
    }
}
