package com.ltzk.mbsf.fragment;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.VideoListActivity;
import com.ltzk.mbsf.api.presenter.MyVideosPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.UserBean;
import com.ltzk.mbsf.bean.ZiTiesListBean;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.widget.FixGridLayoutManager;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/7/29
 */
public class VideoZiTieFragment extends MyBaseFragment<IBaseView, MyVideosPresenter> implements IBaseView<ZiTiesListBean>, OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    private UserBean mUserBean;
    private MyVideosAdapter mAdapter;

    public static VideoZiTieFragment newInstance() {
        return new VideoZiTieFragment();
    }

    @Override
    protected int getLayoutRes() {
        mUserBean = MainApplication.getInstance().getUser();
        return R.layout.fragment_my_videos;
    }

    @Override
    protected void initView(View rootView) {
        mAdapter = new MyVideosAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new FixGridLayoutManager(activity, 3));
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
            presenter.glyph_video_author_zities(mUserBean._id, mRefreshLayout.getLoaded());
        });
    }

    private final OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mAdapter.setNewData(null);
        mRefreshLayout.setLoaded(0);
        presenter.glyph_video_author_zities(mUserBean._id, 0);
    };

    @Override
    protected MyVideosPresenter getPresenter() {
        return new MyVideosPresenter();
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
    public void loadDataSuccess(ZiTiesListBean data) {
        if (data == null) {
            return;
        }
        if (mRefreshLayout.getLoaded() < data.total) {
            mAdapter.addData(data.list);
        }
        mRefreshLayout.setTotal(data.total);
        mAdapter.showEmptyView(data.total);
        mRefreshLayout.setEnableLoadMore(mAdapter.getItemCount() < data.total);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        final ZiTiesListBean.ZiTie ziTie = mAdapter.getItem(position);
        VideoListActivity.safeStart(activity, "", ziTie._zitie_id, "", mUserBean._id, DetailsBean.newDetails(ziTie._name), new ArrayList<>());
    }

    private class MyVideosAdapter extends BaseQuickAdapter<ZiTiesListBean.ZiTie, BaseViewHolder> {
        public MyVideosAdapter() {
            super(R.layout.item_my_videos);
        }

        private void showEmptyView(final int total) {
            if (total <= 0) {
                View emptyView = getLayoutInflater().inflate(R.layout.empty_data, null);
                mAdapter.setEmptyView(emptyView);
                mAdapter.notifyDataSetChanged();
                emptyView.findViewById(R.id.empty_retry_view).setOnClickListener(view -> {
                    mRefreshListener.onRefresh(mRefreshLayout);
                });
            }
        }

        @Override
        protected void convert(BaseViewHolder holder, ZiTiesListBean.ZiTie bean) {
            holder.setText(R.id.name, bean._name);
            holder.setText(R.id.author, bean._author);
            final ImageView imageView = holder.getView(R.id.icon);
            Glide.with(getContext()).load(bean._cover_url).into(imageView);
            imageView.post(() -> {
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                lp.height = imageView.getMeasuredWidth();
                imageView.setLayoutParams(lp);
            });
        }
    }
}