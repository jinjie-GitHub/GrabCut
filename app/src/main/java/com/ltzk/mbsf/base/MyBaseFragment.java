package com.ltzk.mbsf.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import retrofit2.http.FieldMap;

/**
 * 描述：
 * 作者： on 2019/4/17 21:43
 * 邮箱：499629556@qq.com
 */

public abstract class MyBaseFragment<V extends IBaseView,T extends BasePresenterImp> extends BaseFragment {
    public static final String TAG = "MyBaseFragment";

    private View rootView = null;

    /**
     * 是否可见
     */
    protected boolean isVisible = false;
    /**
     * 视图是否加载完毕
     */
    public boolean isViewPrepare = false;

    /**
     * 是否加载过数据
     */
    public boolean hasLoadData = false;
    /**
     * 是否加载过数据
     */
    public boolean hasLoadDataAuto = false;
    /**
     * 加载数据
     */
    private void loadDate(){
        if(isVisible && isViewPrepare && !hasLoadData){
            requestData();
            hasLoadData = true;
        }
    }
    /**
     * 加载数据
     */
    private void loadDateAuto(){
        if(getUserVisibleHint() && isViewPrepare && !hasLoadDataAuto){
            requestDataAutoRefresh();
            hasLoadDataAuto = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        isVisible = !hidden;
        loadDate();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisible = isVisibleToUser;
        loadDate();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(getLayoutRes(), container, false);
        }
        ButterKnife.bind(this, rootView);
        initPresenter();
        initView(rootView);
        isViewPrepare = true;
        loadDate();
        loadDateAuto();
        return rootView;
    }


    /**
     * 只有在 Fragment 第一次对用户可见的时候才去请求
     */
    protected void requestData() {
    }

    /**
     * 每次 Fragment 对用户可见都会去请求
     */
    protected void requestDataAutoRefresh() {

    }

    /**
     * 当 Fragment 不可见的时候停止某些轮询请求的时候调用该方法停止请求
     */
    protected void stopRefresh() {

    }

    /**
     * 返回布局 resId
     *
     * @return layoutId
     */
    protected abstract int getLayoutRes();


    /**
     * 初始化view
     *
     * @param rootView
     */
    protected abstract void initView(View rootView);

    protected T presenter;
    protected abstract T getPresenter();
    private void initPresenter(){
        presenter = getPresenter();
        presenter.subscribe((V)this);
    }

    @Override
    public void onDestroyView() {
        presenter.unSubscribe();
        super.onDestroyView();
    }

    @Override
    protected void cancel() {
        super.cancel();
        if (presenter != null) {
            presenter.disSubscribe();
        }
    }
}
