package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.GlyphsListPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.GlyphsListBean;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.TopBar;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by JinJie on 2020/11/11
 */
public class GlyphsListActivity extends MyBaseActivity<IBaseView, GlyphsListPresenter> implements IBaseView<GlyphsListBean>, OnItemClickListener {

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    private GlyphAdapter mAdapter;

    private String zid = "";
    private String key = "";
    private String author = "";

    public static void safeStart(Context c, String zid) {
        Intent intent = new Intent(c, GlyphsListActivity.class);
        intent.putExtra("zid", zid);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_glyphs_list;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void initView() {
        zid = getIntent().getStringExtra("zid");
        mRecyclerView.setLayoutManager(new GridLayoutManager(activity, calculate(activity)));
        mAdapter = new GlyphAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
        GridItemDecoration divider = new GridItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.activity_horizontal_margin)
                .setVerticalSpan(R.dimen.activity_vertical_margin)
                .setColorResource(R.color.whiteSmoke)
                .build();
        mRecyclerView.addItemDecoration(divider);

        mRefreshListener.onRefresh(mRefreshLayout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setRunnable(() -> {
            presenter.glyphsQuery(zid, key, author, mRefreshLayout.getLoaded());
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
        key = author = "";
        mAdapter.setNewData(null);
        presenter.glyphsQuery(zid, key, author, 0);
    };

    @Override
    protected GlyphsListPresenter getPresenter() {
        return new GlyphsListPresenter();
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.finishLoadMore(false);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg);
    }

    @OnClick(R2.id.iv_close)
    public void iv_close(View view) {
        finish();
    }

    @OnClick(R2.id.tv_search)
    public void tv_search(View view) {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_ZI, "", "请输入汉字", 0);
    }

    @OnClick(R2.id.tv_author)
    public void tv_author(View view) {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_AUTHOR, "", "书法家", 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                author = "";
                key = data.getStringExtra(SearchActivity.KEY);
            } else {
                key = "";
                author = data.getStringExtra(SearchActivity.KEY);
            }
            search();
        }
    }

    private void search() {
        mRefreshLayout.finishRefresh(false);
        mRefreshLayout.setLoaded(0);
        mAdapter.setNewData(null);
        presenter.glyphsQuery(zid, key, author, 0);
    }

    @Override
    public void loadDataSuccess(GlyphsListBean data) {
        mRefreshLayout.setTotal(data.total);
        if (mRefreshLayout.getLoaded() == 0) {
            mAdapter.setNewData(data.list);
            mRefreshLayout.finishRefresh(true);
        } else {
            mRefreshLayout.finishLoadMore(true);
            if (mRefreshLayout.getLoaded() < data.total) {
                mAdapter.addData(data.list);
            }
        }
        mRefreshLayout.setEnableLoadMore(mAdapter.getItemCount() < data.total);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        final GlyphsListBean.Glyphs item = mAdapter.getItem(position);
        final List<DetailsBean> list = new ArrayList<>();
        for (GlyphsListBean.Glyphs glyphsListBean : mAdapter.getData()) {
            if (!TextUtils.isEmpty(glyphsListBean._id)) {
                list.add(DetailsBean.newDetails(glyphsListBean._id, glyphsListBean._hanzi));
            }
        }
        GlyphDetailActivity.safeStart(activity, item._id, list);
    }

    private class GlyphAdapter extends BaseQuickAdapter<GlyphsListBean.Glyphs, BaseViewHolder> {
        public GlyphAdapter() {
            super(R.layout.item_glyphs_list);
        }

        @Override
        protected void convert(BaseViewHolder holder, GlyphsListBean.Glyphs bean) {
            holder.setTextColor(R.id.name, getResources().getColor(bean._video_count > 0 ? R.color.orange : R.color.black));
            final ImageView imageView = holder.getView(R.id.icon);
            Glide.with(getContext())
                    .load(bean._color_image)
                    .override(60, 60)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            holder.setText(R.id.name, bean._hanzi);
            holder.setText(R.id.author, bean._author);
        }

        @Override
        public void onViewRecycled(@NonNull BaseViewHolder holder) {
            super.onViewRecycled(holder);
            final ImageView imageView = holder.getView(R.id.icon);
            if (imageView != null) {
                Glide.with(activity).clear(imageView);
            }
        }
    }
}