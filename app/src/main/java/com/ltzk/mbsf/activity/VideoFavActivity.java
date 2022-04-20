package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.FontQueryPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.VideoListBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.FixGridLayoutManager;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.OpenVideoDialog;
import com.ltzk.mbsf.widget.TopBar;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/8/2
 */
public class VideoFavActivity extends MyBaseActivity<IBaseView, FontQueryPresenter> implements IBaseView<VideoListBean>, OnItemClickListener {
    private static final String EDIT = "编辑";
    private static final String CANCEL = "取消";

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    private VideoListAdapter mAdapter;
    private String mRightTxt = EDIT;

    public static void safeStart(Context c) {
        Intent intent = new Intent(c, VideoFavActivity.class);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_fav_videos;
    }

    @Override
    public void initView() {
        mTopBar.setTitle("视频收藏");
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
            finish();
        });

        mTopBar.setRightTxtListener(EDIT, v -> {
            mRightTxt = EDIT.equals(mRightTxt) ? CANCEL : EDIT;
            mTopBar.setRightTxt(mRightTxt);
            mTopBar.setLeftTxtColor(getResources().getColor(R.color.silver));
            resetTopBarState(mRightTxt);
        });

        mAdapter = new VideoListAdapter();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new FixGridLayoutManager(activity, 3));
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(this);
        mAdapter.setRunnable(() -> mTopBar.setLeftTxtColor(getResources().getColor(mAdapter.getSet().size() > 0 ?
                R.color.red : R.color.silver)));
        mRecyclerView.addItemDecoration(new GridItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.activity_horizontal_margin)
                .setVerticalSpan(R.dimen.activity_vertical_margin)
                .setColorResource(R.color.whiteSmoke)
                .build());

        mRefreshListener.onRefresh(mRefreshLayout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setRunnable(() -> {
            presenter.glyph_video_favlist(mRefreshLayout.getLoaded());
        });
    }

    private void deleteFavZiLib() {
        if (mAdapter.getSet().size() == 0) {
            return;
        }
        TipPopView tipPopView_del = new TipPopView(activity, "", "从收藏列表中删除所选视频？", "删除", new TipPopView.TipListener() {
            @Override
            public void ok() {
                List<String> list = new ArrayList<>();
                Iterator<String> it = mAdapter.getSet().iterator();
                while (it.hasNext()) {
                    try {
                        final String fid = it.next();
                        list.add(fid);
                        for (int pos = 0; pos < mAdapter.getData().size(); pos++) {
                            if (fid.equals(mAdapter.getData().get(pos)._id)) {
                                mAdapter.getData().remove(pos);
                                mAdapter.notifyItemRemoved(pos);
                                mAdapter.notifyItemRangeChanged(pos, mAdapter.getData().size() - pos);
                            }
                        }
                        it.remove();
                    } catch (Exception e) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
                mTopBar.setLeftTxtColor(getResources().getColor(R.color.silver));
                presenter.glyph_video_unfav(list);
            }
        });
        tipPopView_del.showPopupWindow(mTopBar);
    }

    private void resetTopBarState(String msg) {
        if (CANCEL.equals(msg)) {
            mAdapter.setEdit(true);
            mTopBar.setLeftTxtListener("删除", v -> {
                deleteFavZiLib();
            });
        } else {
            mAdapter.setEdit(false);
            mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
                finish();
            });
        }
    }

    private final OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mAdapter.setNewData(null);
        mRightTxt = EDIT;
        mTopBar.setRightTxt(EDIT);
        resetTopBarState(EDIT);
        presenter.glyph_video_favlist(0);
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Integer event) {
        if (event == 0x2000 || event == 0x2001) {
            mRefreshListener.onRefresh(mRefreshLayout);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

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
    public void loadDataSuccess(VideoListBean tData) {
        if (tData == null) {
            return;
        }
        if (mAdapter.getData().size() < tData.total) {
            mAdapter.addData(tData.list);
        }
        mAdapter.showEmptyView(tData.total);
        mRefreshLayout.setTotal(tData.total);
        mRefreshLayout.setEnableLoadMore(mAdapter.getItemCount() < tData.total);
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
        VideoListBean.Videos videos = mAdapter.getItem(position);
        OpenVideoDialog.openVideo(videos._id, "").show(getSupportFragmentManager(), null);
    }

    private class VideoListAdapter extends BaseQuickAdapter<VideoListBean.Videos, BaseViewHolder> {
        private boolean isEdit = false;
        private HashSet<String> set = new HashSet<>();
        private Runnable runnable;

        public VideoListAdapter() {
            super(R.layout.item_video_fav_list);
        }

        public final void setEdit(boolean edit) {
            isEdit = edit;
            if (!edit) {
                set.clear();
            }
            notifyDataSetChanged();
        }

        public HashSet<String> getSet() {
            return set;
        }

        public void setRunnable(Runnable r) {
            runnable = r;
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
        protected void convert(BaseViewHolder holder, VideoListBean.Videos bean) {
            final ImageView imageView = holder.getView(R.id.imageView);
            Glide.with(getContext()).load(bean._cover).into(imageView);
            imageView.post(() -> {
                ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                lp.height = imageView.getMeasuredWidth();
                imageView.setLayoutParams(lp);
            });

            holder.setVisible(R.id.vip, bean._free == 0 || bean._disabled == 1);
            final ImageView avatar = holder.getView(R.id.avatar);
            Glide.with(getContext()).load(bean._glyph._color_image).into(avatar);

            final TextView nickname = holder.getView(R.id.nickname);
            nickname.setTextColor(activity.getResources().getColor(R.color.orange));
            nickname.setText(bean._glyph._hanzi);

            final ImageView cb = holder.getView(R.id.cb);
            final View ll_check = holder.getView(R.id.ll_check);
            ll_check.post(() -> {
                ViewGroup.LayoutParams lp = ll_check.getLayoutParams();
                lp.height = imageView.getMeasuredWidth() + ViewUtil.dpToPx(34);
                ll_check.setLayoutParams(lp);
            });

            if (isEdit) {
                ll_check.setVisibility(View.VISIBLE);
                if (set.contains(bean._id)) {
                    cb.setBackgroundResource(R.drawable.shape_solid_blue);
                } else {
                    cb.setBackgroundResource(R.drawable.shape_solid_gray);
                }
            } else {
                ll_check.setVisibility(View.GONE);
            }

            ll_check.setOnClickListener(v -> {
                if (!set.contains(bean._id)) {
                    set.add(bean._id);
                    cb.setBackgroundResource(R.drawable.shape_solid_blue);
                } else {
                    set.remove(bean._id);
                    cb.setBackgroundResource(R.drawable.shape_solid_gray);
                }
                if (runnable != null) {
                    runnable.run();
                }
            });

            final View.OnClickListener clickListener = v -> {
                switch (v.getId()) {
                    case R.id.avatar:
                    case R.id.nickname:
                        final List<DetailsBean> list = new ArrayList<>();
                        for (VideoListBean.Videos videosBean : mAdapter.getData()) {
                            if (!TextUtils.isEmpty(videosBean._glyph._id)) {
                                list.add(DetailsBean.newDetails(videosBean._glyph._id, videosBean._glyph._hanzi));
                            }
                        }
                        GlyphDetailActivity.safeStart(activity, bean._glyph._id, list);
                        break;
                    default:
                        break;
                }
            };
            avatar.setOnClickListener(clickListener);
            nickname.setOnClickListener(clickListener);
        }
    }
}