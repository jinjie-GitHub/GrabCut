package com.ltzk.mbsf.fragment;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.HistoryActivity;
import com.ltzk.mbsf.api.presenter.HistoryPresenter;
import com.ltzk.mbsf.api.view.HistoryView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.HistoryBean;
import com.ltzk.mbsf.bean.TodayBean;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import cn.tseeey.justtext.JustTextView;

/**
 * Created by JinJie on 2020/5/31
 */
public class HistoryFragment extends MyBaseFragment<HistoryView, HistoryPresenter> implements HistoryView, OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    private HistoryAdapter mAdapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_history;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            ((HistoryActivity) _mActivity).mCurrent = "HistoryFragment";
            ((HistoryActivity) _mActivity).mTopBar.setTitle("每日书论");
            ((HistoryActivity) _mActivity).mTopBar.setRightTextGone();
        }
    }

    @Override
    protected void initView(View rootView) {
        ((HistoryActivity) _mActivity).mTopBar.setRightTextGone();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mAdapter = new HistoryAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mRefreshListener.onRefresh(mRefreshLayout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setRunnable(() -> {
            presenter.history(mRefreshLayout.getLoaded());
        });
    }

    private OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.setLoaded(0);
        mAdapter.setNewData(null);
        presenter.history(0);
    };

    @Override
    protected HistoryPresenter getPresenter() {
        return new HistoryPresenter();
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
        ToastUtil.showToast(activity, errorMsg);
    }

    @Override
    public void loadDataSuccess(HistoryBean tData) {
        mRefreshLayout.setTotal(tData.total);
        if (mRefreshLayout.getLoaded() == 0) {
            mAdapter.setNewData(tData.list);
            mRefreshLayout.finishRefresh(true);
        } else {
            mRefreshLayout.finishLoadMore(true);
            if (mRefreshLayout.getLoaded() < tData.total) {
                mAdapter.addData(tData.list);
            }
        }
        mRefreshLayout.setEnableLoadMore(mAdapter.getItemCount() < tData.total);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        start(HistoryDetailFragment.newInstance((TodayBean) adapter.getItem(position)));
    }

    private static class HistoryAdapter extends BaseQuickAdapter<TodayBean, BaseViewHolder> {
        public HistoryAdapter() {
            super(R.layout.item_history);
        }

        @Override
        protected void convert(BaseViewHolder holder, TodayBean bean) {
            ((JustTextView) holder.getView(R.id.tv_content)).setText(bean._content);
            ((TextView) holder.getView(R.id.tv_author)).setText("《" + bean._src + "》" + bean._dynasty + "・" + bean._author);
            ((TextView) holder.getView(R.id.tv_data)).setText(bean._push_date);
        }
    }
}