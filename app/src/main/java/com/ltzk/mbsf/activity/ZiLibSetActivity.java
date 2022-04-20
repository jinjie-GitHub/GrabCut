package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.presenter.ZiLibSetPresenterImpl;
import com.ltzk.mbsf.api.view.ZiLibSetView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_ZilibDel;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 字库编辑 update on 2021/06/10
 * 和新建字库【ZiLibAddActivity】一样的界面
 */
public class ZiLibSetActivity extends MyBaseActivity<ZiLibSetView, ZiLibSetPresenterImpl> implements ZiLibSetView {

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

    //私有
    @BindView(R2.id.rg_access)
    RadioGroup mRg_access;

    @BindView(R2.id.tv_btn)
    TextView tv_btn;

    @OnClick(R2.id.tv_btn)
    public void tv_btn(View view) {
        startActivity(new Intent(activity, ZiLibDelActivity.class).putExtra("zilibBean", zilibBean));
    }

    private boolean isInited = false;
    private void update() {
        if (isInited) {
            presenter.font_update(requestBean, true);
        }
    }

    private ZilibBean zilibBean;
    public void initView() {
        topBar.setTitle("字库设置");
        topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBar.setRightTxtListener("简介", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(activity, ZiLibDescripActivity.class).putExtra("fid", zilibBean.get_id()));
            }
        });
        et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    ActivityUtils.closeSyskeyBroad(activity);
                    if (ActivityUtils.editTextValueIsNull(et_name)) {
                        requestBean.addParams("title", et_name.getText().toString());
                        update();
                    }
                    return true;
                }
                return false;
            }
        });
        et_user.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    ActivityUtils.closeSyskeyBroad(activity);
                    if (ActivityUtils.editTextValueIsNull(et_user)) {
                        requestBean.addParams("author", et_user.getText().toString());
                        update();
                    }
                    return true;
                }
                return false;
            }
        });
        initKidView();
        initZiFontView();
        initAccessView();

        et_name.setImeOptions(EditorInfo.IME_ACTION_SEND);
        et_name.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        et_name.setSingleLine(true);

        et_user.setImeOptions(EditorInfo.IME_ACTION_SEND);
        et_user.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        et_user.setSingleLine(true);

        requestBean.addParams("fid", getIntent().getStringExtra("fid"));
        presenter.font_details(requestBean, true);
    }

    private void initAccessView() {
        mRg_access.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_access_1) {
                    requestBean.addParams("access", "0");
                } else if (checkedId == R.id.rb_access_2) {
                    requestBean.addParams("access", "1");
                }
                update();
            }
        });
    }

    private void initKidView() {
        mRg_kind.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_kind_1) {
                    requestBean.addParams("kind", "1");
                } else if (checkedId == R.id.rb_kind_2) {
                    requestBean.addParams("kind", "2");
                }
                update();
            }
        });
    }

    private void initZiFontView() {
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
                update();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zilib_set;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    //字库删除
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_ZilibDel messageEvent) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private RequestBean requestBean;
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
        if (tData == null) {
            return;
        }
        zilibBean = tData;
        et_name.setText(tData.get_title());
        et_user.setText(tData.get_author());
        if (tData.get_kind() == 1) {
            mRg_kind.check(R.id.rb_kind_1);
        } else {
            mRg_kind.check(R.id.rb_kind_2);
        }

        switch (tData.get_font()) {
            case "楷":
                mRg_font.check(R.id.rb_1);
                break;
            case "行":
                mRg_font.check(R.id.rb_2);
                break;
            case "草":
                mRg_font.check(R.id.rb_3);
                break;
            case "隶":
                mRg_font.check(R.id.rb_4);
                break;
            case "篆":
                mRg_font.check(R.id.rb_5);
                break;
        }

        if (tData.get_access() == 1) {
            mRg_access.check(R.id.rb_access_2);
        } else {
            mRg_access.check(R.id.rb_access_1);
        }

        requestBean.addParams("title", et_name.getText().toString());
        requestBean.addParams("author", et_user.getText().toString());
        isInited = true;
    }

    @Override
    public void updateSuccess(String msg) {

    }

    @Override
    public void updateFail(String msg) {
        isInited = false;
        ToastUtil.showToast(activity, msg);
        if (mRg_access.getCheckedRadioButtonId() == R.id.rb_access_1) {
            mRg_access.check(R.id.rb_access_2);
        } else {
            mRg_access.check(R.id.rb_access_1);
        }
        isInited = true;
    }

    @Override
    public void loadDataError(String msg) {
        ToastUtil.showToast(activity, msg);
    }
}