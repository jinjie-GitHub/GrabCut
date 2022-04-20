package com.ltzk.mbsf.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.JiziTitleUpdatePresenterImpl;
import com.ltzk.mbsf.api.view.JiziTitleUpdateView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_JiziUpdata;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * update on 2021/6/8
 */
public class JiziTitleUpdateActivity extends MyBaseActivity<JiziTitleUpdateView, JiziTitleUpdatePresenterImpl> implements JiziTitleUpdateView {

    @BindView(R2.id.et_key)
    EditText et_key;

    @BindView(R2.id.iv_delete)
    ImageView iv_delete;
    @OnClick(R2.id.iv_delete)
    public void iv_delete(View view) {
        et_key.setText("");
    }

    private JiziBean jiziBean;
    private RequestBean requestBean;

    @Override
    public void initView() {
        jiziBean = (JiziBean) getIntent().getSerializableExtra("jiziBean");
        requestBean = new RequestBean();
        requestBean.addParams("jid", jiziBean.get_id());

        topBar.setTitle("修改标题");
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

        et_key.setHint("请输入新标题");
        et_key.setText(jiziBean.get_title() + "");
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
        topBar.postDelayed(() -> ActivityUtils.showInput(activity, et_key), 400);
    }

    /**
     * 提交
     */
    private void submit() {
        String key = et_key.getText().toString().trim();
        if ("".equals(key)) {
            ToastUtil.showToast(activity, "您还未输入新标题内容！");
        } else {
            requestBean.addParams("title", key);
            presenter.jizi_update_title(requestBean, true);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_jizi_title_update;
    }

    @Override
    protected JiziTitleUpdatePresenterImpl getPresenter() {
        return new JiziTitleUpdatePresenterImpl();
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
        jiziBean.set_title(et_key.getText().toString());
        EventBus.getDefault().post(new Bus_JiziUpdata(jiziBean));
        finish();
    }
}