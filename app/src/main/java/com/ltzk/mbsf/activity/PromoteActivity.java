package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.PromotePresenter;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.TopBar;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/03/09
 */
public class PromoteActivity extends MyBaseActivity<IBaseView, PromotePresenter> implements IBaseView<String> {

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.editText)
    EditText editText;

    @BindView(R.id.hint)
    TextView hint;

    @BindView(R.id.clear)
    View clear;

    private boolean needUpdate;

    public static void safeStart(BaseActivity activity, boolean isLink) {
        Intent intent = new Intent(activity, PromoteActivity.class);
        intent.putExtra("isLink", isLink);
        activity.startActivityFromBottom(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_promote;
    }

    @Override
    public void initView() {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                final String condition = editText.getText().toString().trim();
                editText.setSelection(condition.length());
                if (TextUtils.isEmpty(condition)) {
                    hint.setVisibility(View.VISIBLE);
                    clear.setVisibility(View.GONE);
                    mTopBar.post(() -> {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
                        lp.width = ViewUtil.dpToPx(1);
                        lp.rightMargin = ViewUtil.dpToPx(0);
                        editText.setLayoutParams(lp);
                    });
                } else {
                    hint.setVisibility(View.GONE);
                    clear.setVisibility(View.VISIBLE);
                    mTopBar.post(() -> {
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editText.getLayoutParams();
                        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        lp.rightMargin = ViewUtil.dpToPx(50);
                        editText.setLayoutParams(lp);
                    });
                }
            }
        });

        final boolean isLink = getIntent().getBooleanExtra("isLink", false);
        final UserBean userBean = MainApplication.getInstance().getUser();
        if (isLink) {
            mTopBar.setTitle("设置推广链接");
            hint.setText("请输入链接地址");
            if (!TextUtils.isEmpty(userBean._ad_link)) {
                editText.setText(userBean._ad_link);
            }
        } else {
            mTopBar.setTitle("设置推广文案");
            hint.setText("请输入推广文案");
            if (!TextUtils.isEmpty(userBean._ad_title)) {
                editText.setText(userBean._ad_title);
            }
        }

        mTopBar.setLeftButtonNoPic();
        mTopBar.setLeftTxtListener("取消", v -> {
            finish();
        });

        mTopBar.setRightTxtListener("保存", v -> {
            final String condition = editText.getText().toString().trim();
            if (isLink) {
                presenter.user_update_adlink(condition);
            } else {
                presenter.user_update_adtitle(condition);
            }
        });

        clear.setOnClickListener(v -> {
            editText.setText("");
        });

        hint.setOnClickListener(v -> {
            ActivityUtils.showInput(activity, editText);
        });

        mTopBar.postDelayed(() -> {
            ActivityUtils.showInput(activity, editText);
        }, 300);
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
        needUpdate = true;
        finish();
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg);
    }

    @Override
    protected PromotePresenter getPresenter() {
        return new PromotePresenter();
    }

    @Override
    public void finish() {
        ActivityUtils.closeSyskeyBroad(activity);
        super.finish();
        if (needUpdate) {
            EventBus.getDefault().post(new Bus_LoginSucces());
        }
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }
}