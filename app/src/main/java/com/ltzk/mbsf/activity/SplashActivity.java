package com.ltzk.mbsf.activity;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.FindTabPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseActivity;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/11/29
 */
public class SplashActivity extends MyBaseActivity<IBaseView, FindTabPresenter> implements IBaseView {
    private static final int DELAY_TOTAL_MILLIS = 10 * 1000;

    @BindView(R.id.lay)
    ImageView mImageView;

    private volatile boolean isOk;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {

        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public void initView() {
        mHandler.sendEmptyMessageDelayed(0, DELAY_TOTAL_MILLIS);
    }

    @Override
    protected FindTabPresenter getPresenter() {
        return new FindTabPresenter();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void disimissProgress() {

    }

    @Override
    public void loadDataSuccess(Object tData) {

    }

    @Override
    public void loadDataError(String errorMsg) {

    }
}