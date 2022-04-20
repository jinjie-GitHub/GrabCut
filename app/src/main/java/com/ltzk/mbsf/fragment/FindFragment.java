package com.ltzk.mbsf.fragment;

import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.FindTabPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.BannerViewsInfo;
import com.ltzk.mbsf.utils.IntentHelper;
import com.ltzk.mbsf.utils.RoundViewOutlineProvider;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.FixGridLayoutManager;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.XBanner;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.stx.xhb.androidx.transformers.Transformer;

import java.util.List;

import butterknife.BindView;

/**
 * Created by JinJie on 2020/7/30
 */
public class FindFragment extends MyBaseFragment<IBaseView, FindTabPresenter> implements IBaseView<List<BannerViewsInfo>>, OnItemClickListener {

    @BindView(R.id.banner)
    XBanner mXBanner;

    @BindView(R.id.loading)
    View mLoading;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    private MenuAdapter mAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_find;
    }

    @Override
    protected void initView(View rootView) {
        initBanner(mXBanner);
        mAdapter = new MenuAdapter();
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new FixGridLayoutManager(activity, 3));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GridItemDecoration.Builder(activity)
                .setHorizontalSpan(R.dimen.item_decoration_span)
                .setVerticalSpan(R.dimen.item_decoration_span)
                .setColorResource(R.color.whiteSmoke)
                .build());

        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(true);
        mRefreshLayout.setEnableOverScrollDrag(true);
        mRefreshLayout.setOnRefreshListener(refreshLayout -> {
            mRefreshLayout.finishRefresh(false);
            mAdapter.setNewData(null);
            requestData();
        });
    }

    @Override
    protected void requestData() {
        super.requestData();
        presenter.disc_items();
    }

    private void initBanner(XBanner xBanner) {
        xBanner.setAllowUserScrollable(true);
        xBanner.setPageTransformer(Transformer.Default);
        //设置广告图片点击事件
        xBanner.setOnItemClickListener((XBanner banner, Object model, View view, int position) -> {
            BannerViewsInfo bannerInfo = ((BannerViewsInfo) model);
            IntentHelper.processAction(activity, bannerInfo);
        });
        //加载广告图片
        xBanner.loadImage((XBanner banner, Object model, View view, int position) -> {
            view.setBackgroundColor(Color.WHITE);
            BannerViewsInfo bannerInfo = ((BannerViewsInfo) model);
            applyRoundCorner(view, ViewUtil.dpToPx(6));
            Glide.with(activity).load(bannerInfo.img).into((ImageView) view);
            updateBannerHeight(view);
        });
    }

    private boolean isUpdate;
    private void updateBannerHeight(View target) {
        synchronized (FindFragment.this) {
            if (isUpdate) {
                return;
            }
            isUpdate = true;
            mXBanner.post(() -> {
                final int w = target.getMeasuredWidth();
                if (w > 0) {
                    ViewGroup.LayoutParams params = mXBanner.getLayoutParams();
                    params.height = (int) ((9f * w) / 16);
                    mXBanner.setLayoutParams(params);
                }
            });
        }
    }

    private RoundViewOutlineProvider mProvider = new RoundViewOutlineProvider(ViewUtil.dpToPx(6));
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void applyRoundCorner(View target, float radius) {
        if (target == null) {
            return;
        }
        target.setElevation(20);
        target.setClipToOutline(true);//用outline裁剪内容区域
        target.setOutlineProvider(mProvider);
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
        mLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        mLoading.setVisibility(View.GONE);
    }

    @Override
    public void loadDataSuccess(List<BannerViewsInfo> tData) {
        if (tData == null || tData.size() == 0) {
            return;
        }

        if (mAdapter.getData().size() == 0) {
            presenter.slider_items_discovery();
            mAdapter.setNewData(tData);
        } else {
            mXBanner.setBannerData(tData);
            mXBanner.setIsClipChildrenMode(true);
            mXBanner.setAutoPlayAble(tData.size() > 1);
            mXBanner.setBannerCurrentItem(0, true);
        }
    }

    @Override
    public void loadDataError(String errorMsg) {

    }

    @Override
    protected FindTabPresenter getPresenter() {
        return new FindTabPresenter();
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        IntentHelper.processAction(activity, mAdapter.getItem(position));
    }

    private class MenuAdapter extends BaseQuickAdapter<BannerViewsInfo, BaseViewHolder> {
        public MenuAdapter() {
            super(R.layout.item_find_menu);
        }

        @Override
        protected void convert(BaseViewHolder holder, BannerViewsInfo name) {
            holder.setText(R.id.name, name.title);
            final ImageView imageView = holder.getView(R.id.icon);
            Glide.with(getContext()).load(name.img).into(imageView);
        }
    }
}