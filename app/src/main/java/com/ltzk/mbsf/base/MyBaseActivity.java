package com.ltzk.mbsf.base;

import android.os.Bundle;

/**
 * 描述：
 * 作者： on 2019/4/17 20:28
 * 邮箱：499629556@qq.com
 */

public abstract class MyBaseActivity<V extends IBaseView,T extends BasePresenterImp> extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initPresenter();
        super.onCreate(savedInstanceState);
    }

    protected T presenter;
    protected abstract T getPresenter();

    private void initPresenter(){
        presenter = getPresenter();
        presenter.subscribe((V)this);
    }

    @Override
    protected void onDestroy() {
        presenter.unSubscribe();
        super.onDestroy();
    }

    @Override
    protected void cancel() {
        super.cancel();
        if (presenter != null) {
            presenter.disSubscribe();
        }
    }
}
