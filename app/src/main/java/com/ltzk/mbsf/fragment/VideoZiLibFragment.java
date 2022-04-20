package com.ltzk.mbsf.fragment;

import android.view.View;
import android.view.ViewStub;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.VideoListActivity;
import com.ltzk.mbsf.adapter.ZiLibListNewAdapter;
import com.ltzk.mbsf.api.presenter.FontQueryPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.widget.FixGridLayoutManager;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/8/2
 */
public class VideoZiLibFragment extends MyBaseFragment<IBaseView, FontQueryPresenter> implements IBaseView<RowBean<ZilibBean>>, OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    private UserBean mUserBean;
    private ZiLibListNewAdapter mAdapter;
    private List<ZilibBean> mList = new ArrayList<>();

    @BindView(R.id.view_empty_data)
    ViewStub mViewStubNoData;
    private View mNoDataView;

    public static VideoZiLibFragment newInstance() {
        return new VideoZiLibFragment();
    }

    @Override
    protected int getLayoutRes() {
        mUserBean = MainApplication.getInstance().getUser();
        return R.layout.fragment_my_videos;
    }

    @Override
    protected void initView(View rootView) {
        mAdapter = new ZiLibListNewAdapter(activity, (ZilibBean ziTie) -> {
            VideoListActivity.safeStart(activity, "", "", ziTie.get_id(), mUserBean._id, DetailsBean.newDetails(ziTie.get_title()), new ArrayList<>());
           }
        );

        mRecyclerView.setLayoutManager(new FixGridLayoutManager(activity, mAdapter.calculate(activity)));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new GridItemDecoration.Builder(activity)
                .setHorizontalSpan(R.dimen.activity_horizontal_margin)
                .setVerticalSpan(R.dimen.activity_vertical_margin)
                .setColorResource(R.color.whiteSmoke)
                .build());

        mRefreshListener.onRefresh(mRefreshLayout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setRunnable(() -> {
            presenter.glyph_video_author_fonts(mUserBean._id, mRefreshLayout.getLoaded());
        });
    }

    private final OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mList.clear();
        mAdapter.setList(mList);
        mAdapter.notifyDataSetChanged();
        presenter.glyph_video_author_fonts(mUserBean._id, 0);
    };

    @Override
    protected FontQueryPresenter getPresenter() {
        return new FontQueryPresenter();
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        mRefreshLayout.finishRefresh(true);
        mRefreshLayout.finishLoadMore(true);
    }

    @Override
    public void loadDataSuccess(RowBean<ZilibBean> tData) {
        if (tData == null) {
            return;
        }
        if (mList.size() < tData.getTotal()) {
            mList.addAll(tData.getList());
            mAdapter.setList(mList);
            mAdapter.notifyDataSetChanged();
        }
        showNoDataView(tData.getTotal() <= 0);
        mRefreshLayout.setTotal(tData.getTotal());
        mRefreshLayout.setEnableLoadMore(mAdapter.getItemCount() < tData.getTotal());
    }

    private void showNoDataView(boolean show) {
        if (mNoDataView == null) {
            mNoDataView = mViewStubNoData.inflate();
            mNoDataView.findViewById(R.id.empty_retry_view).setOnClickListener(view -> {
                mRefreshListener.onRefresh(mRefreshLayout);
            });
        }
        mNoDataView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void loadDataError(String errorMsg) {
        if ("0".equals(errorMsg)) {
            //mRefreshListener.onRefresh(mRefreshLayout);
        } else {
            ToastUtil.showToast(activity, errorMsg);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

    }
}