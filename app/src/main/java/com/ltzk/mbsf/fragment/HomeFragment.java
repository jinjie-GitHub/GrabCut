package com.ltzk.mbsf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.HistoryActivity;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.HomePresenter;
import com.ltzk.mbsf.api.view.HomeView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.BannerViewsInfo;
import com.ltzk.mbsf.bean.KindType;
import com.ltzk.mbsf.bean.ResponseData;
import com.ltzk.mbsf.bean.TodayBean;
import com.ltzk.mbsf.utils.IntentHelper;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.RoundViewOutlineProvider;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.XBanner;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.stx.xhb.androidx.transformers.Transformer;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.tseeey.justtext.JustTextView;

/**
 * Created by JinJie on 2020/5/25
 */
public class HomeFragment extends MyBaseFragment<HomeView, HomePresenter> implements HomeView {
    private static final int REQ_SEARCH_KEY = 1;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.tv_content)
    JustTextView mContent;

    @BindView(R.id.tv_author)
    TextView mAuthor;

    @BindView(R.id.btn_search)
    TextView mSearch;

    @BindView(R.id.book)
    TextView mBook;

    @BindView(R.id.tv_history)
    TextView mHistory;

    @BindView(R.id.ll_book)
    View mBookLayout;

    @BindView(R.id.calendar)
    TextView mCalendar;

    @BindView(R.id.loading)
    ProgressBar mLoading;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout mRefreshLayout;

    @BindView(R.id.banner)
    XBanner mXBanner;

    private TodayBean mBean;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View rootView) {
        addTabs();
        initBanner(mXBanner);
        mRefreshLayout.setEnableLoadMore(false);
        mRefreshLayout.setEnableOverScrollBounce(true);
        mRefreshLayout.setEnableOverScrollDrag(true);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        presenter.calendar();
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
            //updateBannerHeight(view);
        });
    }

    private boolean isUpdate;
    private void updateBannerHeight(View target) {
        synchronized (HomeFragment.this) {
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
    protected void requestData() {
        super.requestData();
        presenter.today();
        presenter.slider_items_home();
    }

    private OnRefreshListener mRefreshListener = refreshLayout -> {
        mLoading.setVisibility(View.VISIBLE);
        mRefreshLayout.finishRefresh(false);
        this.requestData();
    };

    private void addTabs() {
        final KindType[] titles = KindType.values();
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);
        for (int pos = 0; pos < titles.length; pos++) {
            View customView = View.inflate(activity, R.layout.tab_item_layout, null);
            TextView text = customView.findViewById(R.id.tab_item_text);
            text.setText(titles[pos].getName());
            text.setTextColor(ContextCompat.getColor(activity, R.color.gray));
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(customView), pos == 0);
        }
    }

    private TabLayout.OnTabSelectedListener mTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        public void onTabSelected(TabLayout.Tab tab) {
            onSelected(tab);
            SPUtils.put(activity, Constan.Key_type.KEY_ZIDIAN_TAB_POS, mTabLayout.getSelectedTabPosition());
        }

        public void onTabUnselected(TabLayout.Tab tab) {
            onSelected(tab);
            SearchActivity.safeStart(HomeFragment.this, Constan.Key_type.KEY_ZI, "", KindType.values()[mTabLayout.getSelectedTabPosition()].getName() + "查询", REQ_SEARCH_KEY);
        }

        public void onTabReselected(TabLayout.Tab tab) {
            SearchActivity.safeStart(HomeFragment.this, Constan.Key_type.KEY_ZI, "", KindType.values()[mTabLayout.getSelectedTabPosition()].getName() + "查询", REQ_SEARCH_KEY);
        }

        private void onSelected(final TabLayout.Tab tab) {
            final View customView = tab.getCustomView();
            TextView text = customView.findViewById(R.id.tab_item_text);
            View indicator = customView.findViewById(R.id.tab_item_indicator);
            text.setTextColor(tab.isSelected() ? ContextCompat.getColor(activity, R.color.colorPrimary) : ContextCompat.getColor(activity, R.color.gray));
            text.getPaint().setFakeBoldText(tab.isSelected());
            indicator.setVisibility(tab.isSelected() ? View.VISIBLE : View.INVISIBLE);
            mSearch.setText(KindType.values()[mTabLayout.getSelectedTabPosition()].getName() + "查询");
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_SEARCH_KEY && resultCode == Activity.RESULT_OK) {
            mHomeSearchCallBack.onSearch(KindType.values()[mTabLayout.getSelectedTabPosition()].getKind(), data.getStringExtra(SearchActivity.KEY));
        }
    }

    @Override
    protected HomePresenter getPresenter() {
        return new HomePresenter();
    }

    @Override
    public void showProgress() {
        //showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
    }

    @Override
    public void loadDataError(String errorMsg) {
        mLoading.setVisibility(View.GONE);
        if (isVisible) {
            ToastUtil.showToast(activity, errorMsg);
        }
    }

    @Override
    public void loadDataSuccess(TodayBean bean) {
        mBean = bean;
        mLoading.setVisibility(View.GONE);
        mContent.setText(bean._content);
        mAuthor.setText("《" + bean._src + "》" + bean._dynasty + "・" + bean._author);
    }

    @Override
    public void setCalendar(ResponseData data) {
        MySPUtils.setSplashADState(activity, data.isShowAd());
        mCalendar.getPaint().setFakeBoldText(true);
        if (data.getData() instanceof String) {
            mCalendar.setText((String) data.getData());
        }
    }

    @Override
    public void slider_items_home(List<BannerViewsInfo> data) {
        mXBanner.setBannerData(data);
        mXBanner.setIsClipChildrenMode(true);
        mXBanner.setAutoPlayAble(data.size() > 1);
        mXBanner.setBannerCurrentItem(0, true);
    }

    @OnClick({R.id.btn_search, R.id.tv_history, R.id.ll_book})
    public void OnClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.btn_search:
                SearchActivity.safeStart(HomeFragment.this, Constan.Key_type.KEY_ZI, "", KindType.values()[mTabLayout.getSelectedTabPosition()].getName() + "查询", REQ_SEARCH_KEY);
                break;
            case R.id.tv_history:
                activity.startActivity(new Intent(activity, HistoryActivity.class));
                break;
            case R.id.ll_book:
                if (mBean != null) {
                    HistoryActivity.safeStart(activity, mBean);
                }
                break;
        }
    }

    private HomeSearchCallBack mHomeSearchCallBack;
    public void setHomeSearchCallBack(HomeSearchCallBack homeSearchCallBack) {
        mHomeSearchCallBack = homeSearchCallBack;
    }

    public interface HomeSearchCallBack {
        void onSearch(int kindType, String key);
    }
}