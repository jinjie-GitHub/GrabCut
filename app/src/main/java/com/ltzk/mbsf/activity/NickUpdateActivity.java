package com.ltzk.mbsf.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.NickPresenterImpl;
import com.ltzk.mbsf.api.view.NickUpdateView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_UserUpdate;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 描述：修改昵称
 * 作者： on 2017/11/29 15:14
 * 邮箱：499629556@qq.com
 */

public class NickUpdateActivity extends MyBaseActivity<NickUpdateView,NickPresenterImpl> implements NickUpdateView {
    @BindView(R2.id.et_key)
    EditText et_key;
    @BindView(R2.id.iv_delete)
    ImageView iv_delete;
    @OnClick(R2.id.iv_delete)
    public void iv_delete(View view){
        et_key.setText("");
    }
    @Override
    public void initView() {
        et_key.setText(MainApplication.getInstance().getUser().get_nickname()+"");
        requestBean = new RequestBean();

        topBar.setTitle("修改昵称");
        topBar.setLeftButtonListener(R.mipmap.close2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBar.setRightTxtListener("保存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtils.closeSyskeyBroad(activity);
                submit();
            }
        });

        et_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s != null && s.length() > 0) {
                    iv_delete.setVisibility(View.VISIBLE);
                } else {
                    iv_delete.setVisibility(View.GONE);
                }
            }
        });
        et_key.setSelection(et_key.getText().toString().length());
        mHandler.sendEmptyMessageDelayed(0,500);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActivityUtils.showInput(activity,et_key);
        }
    };

    /**
     * 提交
     */
    private void submit(){
        String key = et_key.getText().toString().trim();
        if("".equals(key)){
            ToastUtil.showToast(activity,"您还未输入昵称！");
        }else {
            requestBean.addParams("nickname",key);
            presenter.update_nickname(requestBean,true);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_jizi_title_update;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected NickPresenterImpl getPresenter() {
        return new NickPresenterImpl();
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
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity,errorMsg+"");
    }

    @Override
    public void loadDataSuccess(String tData) {
        UserBean userBean = MainApplication.getInstance().getUser();
        userBean.set_nickname(et_key.getText().toString());
        MainApplication.getInstance().putUser(userBean);
        EventBus.getDefault().post(new Bus_UserUpdate());
        finish();
    }

}
