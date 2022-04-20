package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.ZiLibAddPresenterImpl;
import com.ltzk.mbsf.api.view.ZiLibAddView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_ZilibAdd;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 新建字库 update on 2021/06/10
 */
public class ZiLibAddActivity extends MyBaseActivity<ZiLibAddView, ZiLibAddPresenterImpl> implements ZiLibAddView {

    @BindView(R2.id.et_name)
    EditText et_name;

    @BindView(R2.id.iv_delete)
    ImageView iv_delete;
    @OnClick(R2.id.iv_delete)
    public void iv_delete(View view) {
        et_name.setText("");
    }

    @BindView(R2.id.et_user)
    EditText et_user;

    @BindView(R2.id.iv_delete_pwd)
    ImageView iv_delete_pwd;
    @OnClick(R2.id.iv_delete_pwd)
    public void iv_delete_pwd(View view) {
        et_user.setText("");
    }

    //字体
    @BindView(R2.id.rg_font)
    RadioGroup mRg_font;

    //笔类型
    @BindView(R2.id.rg_kind)
    RadioGroup mRg_kind;

    @BindView(R2.id.tv_btn)
    TextView tv_btn;

    @OnClick(R2.id.tv_btn)
    public void tv_btn(View view) {
        if (!ActivityUtils.editTextValueIsNull(et_name)) {
            ToastUtil.showToast(activity, "请输入字库名");
            return;
        }

        if (!ActivityUtils.editTextValueIsNull(et_user)) {
            ToastUtil.showToast(activity, "请输入作者");
            return;
        }

        requestBean.addParams("title", et_name.getText().toString());
        requestBean.addParams("author", et_user.getText().toString());
        presenter.font_add(requestBean, true);
    }

    private boolean canStart = true;

    @Override
    public void initView() {
        canStart = getIntent().getBooleanExtra("canStart", true);
        topBar.setTitle("新建字库");
        topBar.setLeftButtonListener(R.mipmap.close2, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initKidView();
        initZiFontView();
        mHandler.sendEmptyMessageDelayed(0, 300);
    }

    private void initKidView() {
        requestBean.addParams("kind", "1");
        mRg_kind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_kind_1) {
                    requestBean.addParams("kind", "1");
                } else if (checkedId == R.id.rb_kind_2) {
                    requestBean.addParams("kind", "2");
                }
            }
        });
    }

    private void initZiFontView() {
        requestBean.addParams("font", "楷");
        mRg_font.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_1) {
                    requestBean.addParams("font", "楷");
                } else if (checkedId == R.id.rb_2) {
                    requestBean.addParams("font", "行");
                } else if (checkedId == R.id.rb_3) {
                    requestBean.addParams("font", "草");
                } else if (checkedId == R.id.rb_4) {
                    requestBean.addParams("font", "隶");
                } else if (checkedId == R.id.rb_5) {
                    requestBean.addParams("font", "篆");
                }
            }
        });
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActivityUtils.showInput(activity, et_name);
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_zilib_add;
    }

    private RequestBean requestBean;

    @Override
    protected ZiLibAddPresenterImpl getPresenter() {
        requestBean = new RequestBean();
        return new ZiLibAddPresenterImpl();
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
        EventBus.getDefault().post(new Bus_ZilibAdd(requestBean.getParam("kind").toString(), requestBean.getParam("font").toString()));
        if (canStart) {
            activity.startActivity(new Intent(activity, ZiLibZiListActivity.class).putExtra("zilibBean", tData));
        }
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    @Override
    public void loadDataError(String msg) {
        ToastUtil.showToast(activity, msg);
    }
}