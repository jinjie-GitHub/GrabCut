package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.presenter.ZiLibSetPresenterImpl;
import com.ltzk.mbsf.api.view.ZiLibSetView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import butterknife.BindView;

/**
 * 字库简介 update on 2021/06/10
 */
public class ZiLibDescripActivity extends MyBaseActivity<ZiLibSetView, ZiLibSetPresenterImpl> implements ZiLibSetView {

    @BindView(R2.id.et_descrip)
    EditText et_descrip;

    //字体
    @BindView(R2.id.tv_count)
    TextView mTvCount;

    private ZilibBean zilibBean;
    private boolean isChange() {
        if (zilibBean.get_descrip().equals(et_descrip.getText().toString())) {
            return false;
        }
        return true;
    }

    @Override
    public void initView() {
        topBar.setTitle("字库简介");
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBar.setRightTxtListener("预览", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChange()) {
                    requestBean.addParams("descrip", et_descrip.getText().toString());
                    presenter.font_update(requestBean, true);
                } else {
                    startActivity(new Intent(activity, MyWebActivity.class).putExtra("url", RetrofitManager.RES_URL + "font/webpage?fid=" + zilibBean.get_id()));
                }
            }
        });
        et_descrip.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTvCount.setText(et_descrip.getText().toString().length() + "/500");
                if (isChange()) {
                    topBar.setRightTxt("保存");
                } else {
                    topBar.setRightTxt("预览");
                }
            }
        });
        et_descrip.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    ActivityUtils.closeSyskeyBroad(activity);
                    if (ActivityUtils.editTextValueIsNull(et_descrip)) {
                        requestBean.addParams("descrip", et_descrip.getText().toString());
                        presenter.font_update(requestBean, true);
                    }
                    return true;
                }
                return false;
            }
        });
        requestBean.addParams("fid", getIntent().getStringExtra("fid"));
        presenter.font_details(requestBean, true);
        mHandler.sendEmptyMessageDelayed(0, 500);
    }

    private final Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActivityUtils.showInput(activity, et_descrip);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_zilib_descrip_set;
    }

    private RequestBean requestBean;

    @Override
    protected ZiLibSetPresenterImpl getPresenter() {
        requestBean = new RequestBean();
        return new ZiLibSetPresenterImpl();
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
    public void loadDataSuccess(ZilibBean tData) {
        zilibBean = tData;
        et_descrip.setText(tData.get_descrip());
    }

    @Override
    public void updateSuccess(String msg) {
        topBar.setRightTxt("预览");
        zilibBean.set_descrip(et_descrip.getText().toString());
    }

    @Override
    public void updateFail(String msg) {

    }

    @Override
    public void loadDataError(String msg) {
        ToastUtil.showToast(activity, msg);
    }
}