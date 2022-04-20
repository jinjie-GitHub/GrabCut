package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.SimilarPresenter;
import com.ltzk.mbsf.api.view.SimilarView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.SimilarBean;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.TopBar;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by JinJie on 2020/5/31
 */
public class SimilarActivity extends MyBaseActivity<SimilarView, SimilarPresenter> implements SimilarView, OnItemClickListener {

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    private String gid = "";
    private SimilarAdapter mAdapter;

    public static void safeStart(Context c, String gid, String _hanzi) {
        Intent intent = new Intent(c, SimilarActivity.class);
        intent.putExtra("gid", gid);
        intent.putExtra("_hanzi", _hanzi);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_similar;
    }

    @Override
    public void initView() {
        mTopBar.setTitle(getIntent().getStringExtra("_hanzi"));
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
            finish();
        });

        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, calculate(activity)));
        mAdapter = new SimilarAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        GridItemDecoration divider = new GridItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.activity_horizontal_margin)
                .setVerticalSpan(R.dimen.activity_vertical_margin)
                .setColorResource(R.color.whiteSmoke)
                .build();
        mRecyclerView.addItemDecoration(divider);

        gid = getIntent().getStringExtra("gid");
        presenter.similar(gid, 0);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setRunnable(() -> {
            presenter.similar(gid, mRefreshLayout.getLoaded());
        });
    }

    /**
     * 计算列数
     */
    static final String TAG = "GlyphsListActivity";
    static final int PHONE = 60;
    static final int TABLET = 60;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;
    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(12);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        int spacing = ViewUtil.dpToPx(4);
        int colCount = totalWidth / itemWidth;

        //获取有效的列数
        while (true) {
            if (colCount == 0) {
                colCount = 1;
                break;
            } else {
                int result = totalWidth - (itemWidth * colCount + spacing * (colCount - 1));
                if (result > 3) {
                    break;
                }
                colCount--;
            }
        }
        Log.d(TAG, "colCount=" + colCount);
        return colCount;
    }

    private OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.setLoaded(0);
        mAdapter.setNewData(null);
        presenter.similar(gid, 0);
    };

    @Override
    protected SimilarPresenter getPresenter() {
        return new SimilarPresenter();
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
    public void loadDataSuccess(SimilarBean tData) {
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
        SimilarBean.Similar bean = mAdapter.getItem(position);
        final List<DetailsBean> list = new ArrayList<>();
        for (SimilarBean.Similar glyphsListBean : mAdapter.getData()) {
            if (!TextUtils.isEmpty(glyphsListBean._id)) {
                list.add(DetailsBean.newDetails(glyphsListBean._id, glyphsListBean._hanzi));
            }
        }
        GlyphDetailActivity.safeStart(activity, bean._id, list);
    }

    private class SimilarAdapter extends BaseQuickAdapter<SimilarBean.Similar, BaseViewHolder> {
        public SimilarAdapter() {
            super(R.layout.item_similar_list);
        }

        @Override
        protected void convert(BaseViewHolder holder, SimilarBean.Similar bean) {
            holder.setVisible(R.id.loading, true);
            holder.setVisible(R.id.miView, TextUtils.isEmpty(bean._color_image));
            holder.setTextColor(R.id.name, getResources().getColor(bean._video_count > 0 ? R.color.orange : R.color.black));
            holder.setText(R.id.name, bean._hanzi);
            holder.setText(R.id.author, bean._author);
            final ImageView color_image = holder.getView(R.id.icon);
            final View miView = holder.getView(R.id.miView);

            miView.post(() -> {
                ViewGroup.LayoutParams lp = miView.getLayoutParams();
                lp.width = color_image.getMeasuredWidth();
                lp.height = color_image.getMeasuredHeight();
                if (TextUtils.isEmpty(bean._color_image)) {
                    miView.setLayoutParams(lp);
                }
            });

            Glide.with(getApplication())
                    .asBitmap()
                    .load(!TextUtils.isEmpty(bean._color_image) ? bean._color_image : bean._clear_image)
                    .into(new ImageViewTarget<Bitmap>(color_image) {
                        @Override
                        protected void setResource(@Nullable Bitmap resource) {
                            if (resource != null) {
                                color_image.setImageBitmap(resource);
                                holder.setVisible(R.id.loading, false);
                            }
                        }
                    });
        }
    }
}