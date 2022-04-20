package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：注册
 * 作者： on 2017/11/20 15:57
 * 邮箱：499629556@qq.com
 */

public class RegistActivity extends MyBaseActivity<LoginView,LoginPresenterImpl> implements LoginView {

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

    @BindView(R2.id.tv_btn)
    TextView tv_btn;
    @OnClick(R2.id.tv_btn)
    public void tv_btn(View view){
        ActivityUtils.closeSyskeyBroad(activity);

        if(!ActivityUtils.editTextValueIsNull(et_phone)){
            ToastUtil.showToast(activity,"请输入用户名");
            return;
        }
        if(!ActivityUtils.editTextValueIsNull(et_code)){
            ToastUtil.showToast(activity,"请输入密码");
            return;
        }

        RequestBean requestBean = new RequestBean();
        requestBean.addParams("name",et_phone.getText().toString());
        requestBean.addParams("pwd",et_code.getText().toString());
        presenter.register(requestBean,true);

    }


    @Override
    public void initView() {
        topBar.setTitle("新用户注册");
        topBar.setRightTxtListener("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tv_btn.setText("注册");
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!et_phone.getText().toString().trim().equals("") && !et_code.getText().toString().equals("")){
                    tv_btn.setEnabled(true);
                }else {
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
                if (!et_phone.getText().toString().trim().equals("") && !et_code.getText().toString().equals("")){
                    tv_btn.setEnabled(true);
                }else {
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
        mHandler.sendEmptyMessageDelayed(0,500);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActivityUtils.showInput(activity,et_phone);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_change_username;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            et_phone.setText(data.getStringExtra("username")+"");
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected LoginPresenterImpl getPresenter() {
        return new LoginPresenterImpl();
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
        SPUtils.put(activity,"username",et_phone.getText().toString());
        MainApplication.getInstance().putToken(userBean.get_token());
        MainApplication.getInstance().putUser(userBean);
        EventBus.getDefault().post(new Bus_LoginSucces());
        finish();
    }

    @Override
    public void loadDataSuccess(String tData) {

    }

    @Override
    public void update_pwdResult(String bean) {

    }

    @Override
    public void loadDataError(String msg) {
        ToastUtil.showToast(activity,msg);
    }
}
