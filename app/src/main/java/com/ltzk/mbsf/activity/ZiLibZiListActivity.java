package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.classic.common.MultipleStatusView;
import com.google.android.material.tabs.TabLayout;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.adapter.ZiLibZiListAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.RetrofitManager;
import com.ltzk.mbsf.api.presenter.ZiLibZiListPresenterImpl;
import com.ltzk.mbsf.api.view.ZiLibZiListView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_ZilibDel;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZiLibDetailBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * update on 2021/06/11
 */

public class ZiLibZiListActivity extends MyBaseActivity<ZiLibZiListView, ZiLibZiListPresenterImpl> implements OnRefreshListener, ZiLibZiListView {
    public static final int REQ_SEARCH_KEY = 1;

    @OnClick(R.id.iv_back)
    public void onTvBackClicked() {
        finish();
    }

    @BindView(R2.id.et_key)
    EditText et_key;
    @OnClick(R.id.et_key)
    public void onTvSearchClicked() {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_ZI, "", "", REQ_SEARCH_KEY);
    }

    @OnClick({R.id.tv_more, R.id.iv_more})
    public void onTvMoreClicked() {
        if (zilibBean.get_own() == ZilibBean.OWN_ME) {
            startActivity(new Intent(activity, ZiLibSetActivity.class).putExtra("fid", zilibBean.get_id()));
        } else {
            startActivity(new Intent(activity, MyWebActivity.class).putExtra("url", RetrofitManager.RES_URL + "font/webpage?fid=" + zilibBean.get_id()));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_SEARCH_KEY) {
                View customView = mTabLayout.getTabAt(mTabLayout.getSelectedTabPosition()).getCustomView();
                TextView text = customView.findViewById(R.id.tab_item_text);
                View indicator = customView.findViewById(R.id.tab_item_indicator);
                text.setTextColor(getResources().getColor(R.color.black));
                indicator.setVisibility(View.INVISIBLE);

                requestBean.addParams("charset", "");
                et_key.setText(data.getStringExtra(SearchActivity.KEY));
                requestBean.addParams("char", et_key.getText().toString());
                onRefresh(null);
            }
        }
    }

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    //字典列表
    @BindView(R2.id.status_view)
    MultipleStatusView mStatus_view;

    @BindView(R2.id.refresh_layout)
    MySmartRefreshLayout mRefresh_layout;

    @BindView(R2.id.recyclerView)
    RecyclerView mRv_zi;
    ZiLibZiListAdapter mAdapter;

    @BindView(R2.id.rel_bottom)
    RelativeLayout rel_bottom;

    @BindView(R2.id.iv_prev)
    ImageView iv_prev;
    @OnClick(R.id.iv_prev)
    public void iv_prev() {
        loaded--;
        getData();
    }

    @BindView(R2.id.tv_page)
    TextView tv_page;
    @OnClick(R.id.tv_page)
    public void tv_page() {
        if (mNormalPopup == null) {
            getPopup();
        }
        adapter.clear();
        List<String> data = new ArrayList<>();
        for (int i = 0; i < total_page; i++) {
            data.add("" + (i + 1));
        }
        adapter.addAll(data);
        adapter.notifyDataSetChanged();
        mNormalPopup.show(tv_page);
    }

    private ArrayAdapter adapter;
    private QMUIPopup mNormalPopup;
    private GridView gv_menu;
    private void getPopup() {
        View view = getLayoutInflater().inflate(R.layout.widget_page_select, null);
        gv_menu = view.findViewById(R.id.gv_menu);
        List<String> data = new ArrayList<>();
        for (int i = 0; i < total_page; i++) {
            data.add("" + (i + 1));
        }
        adapter = new ArrayAdapter<>(activity, R.layout.adapter_item_jizi_new_menu, data);
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loaded = position;
                getData();
                if (mNormalPopup != null) {
                    mNormalPopup.dismiss();
                }
            }
        };
        gv_menu.setAdapter(adapter);
        gv_menu.setOnItemClickListener(onItemClickListener);

        mNormalPopup = QMUIPopups.popup(activity, (int) (ViewUtil.getWidth() * 2 / 3), (int) (ViewUtil.getWidth() * 2 / 3))
                .view(view)
                .bgColor(getResources().getColor(R.color.whiteSmoke))
                .borderColor(getResources().getColor(R.color.colorLine))
                .borderWidth(QMUIDisplayHelper.dp2px(activity, 1))
                .radius(0)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true);
    }

    @BindView(R2.id.iv_next)
    ImageView iv_next;
    @OnClick(R.id.iv_next)
    public void iv_next() {
        loaded++;
        getData();
    }

    private void check() {
        if (loaded <= 0) {
            loaded = 0;
            iv_prev.setEnabled(false);
        } else {
            iv_prev.setEnabled(true);
        }

        if (loaded >= (total_page - 1)) {
            loaded = total_page - 1;
            iv_next.setEnabled(false);
        } else {
            iv_next.setEnabled(true);
        }
    }

    private void addTabs(final List<String> titles) {
        final int tab = (int) SPUtils.get(activity, Constan.Key_type.KEY_ZILIB_TAB_POS, 0);
        mTabLayout.addOnTabSelectedListener(mTabSelectedListener);
        for (int pos = 0; pos < titles.size(); pos++) {
            View customView = View.inflate(activity, R.layout.tab_item_layout, null);
            TextView text = customView.findViewById(R.id.tab_item_text);
            text.setText(titles.get(pos));
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(customView), pos == tab);
        }
    }

    private TabLayout.OnTabSelectedListener mTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        public void onTabSelected(TabLayout.Tab tab) {
            SPUtils.put(activity, Constan.Key_type.KEY_ZILIB_TAB_POS, mTabLayout.getSelectedTabPosition());
            onSelected(tab);
            loaded = 0;
            onRefresh(null);
        }

        public void onTabUnselected(TabLayout.Tab tab) {
            onSelected(tab);
        }

        public void onTabReselected(TabLayout.Tab tab) {
        }

        private void onSelected(final TabLayout.Tab tab) {
            final View customView = tab.getCustomView();
            TextView text = customView.findViewById(R.id.tab_item_text);
            requestBean.addParams("char", "");
            requestBean.addParams("charset", text.getText());
            View indicator = customView.findViewById(R.id.tab_item_indicator);
            text.getPaint().setFakeBoldText(tab.isSelected());
            text.setTextColor(tab.isSelected() ? getResources().getColor(R.color.colorPrimary) : getResources().getColor(R.color.black));
            indicator.setVisibility(tab.isSelected() ? View.VISIBLE : View.INVISIBLE);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    //字库删除
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_ZilibDel messageEvent) {
        finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zi_lib_zi_list;
    }

    private ZilibBean zilibBean;
    public void initView() {
        zilibBean = (ZilibBean) getIntent().getSerializableExtra("zilibBean");
        if (zilibBean == null) {
            finish();
            return;
        }
        String key = getIntent().getStringExtra("key");
        requestBean.addParams("char", key);
        requestBean.addParams("fid", zilibBean.get_id());

        if (zilibBean.get_own() == 1) {
            findViewById(R.id.iv_more).setVisibility(View.VISIBLE);
            findViewById(R.id.tv_more).setVisibility(View.GONE);
        } else {
            findViewById(R.id.tv_more).setVisibility(View.VISIBLE);
            findViewById(R.id.iv_more).setVisibility(View.GONE);
        }

        mRefresh_layout.setOnRefreshListener(this);
        mRefresh_layout.setEnableLoadMore(false);
        mAdapter = new ZiLibZiListAdapter(this);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final ZiBean ziBean = mAdapter.getItem(position).getGlyph();
                if (zilibBean.get_own() == 1 || TextUtils.isEmpty(ziBean.get_id()) || mAdapter.getItem(position).getNum() > 1) {
                    Intent intent = new Intent(activity, ZiLibDetailListActivity.class);
                    intent.putExtra("zilibBean", zilibBean);
                    intent.putExtra("key", ziBean.get_key());
                    intent.putExtra("pos", position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("gids", (Serializable) mAdapter.getData());
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    final List<DetailsBean> list = new ArrayList<>();
                    for (ZiLibDetailBean ziLibDetailBean : mAdapter.getData()) {
                        if (ziLibDetailBean.getGlyph() != null && !TextUtils.isEmpty(ziLibDetailBean.getGlyph().get_id())) {
                            list.add(DetailsBean.newDetails(ziLibDetailBean.getGlyph().get_id(), ziLibDetailBean.getGlyph().get_hanzi()));
                        }
                    }
                    GlyphDetailActivity.safeStart(activity, ziBean.get_id(), list, zilibBean);
                }
            }
        });
        if (zilibBean.get_own() == 1) {
            setItemLongClickListener();
        }
        mRv_zi.setAdapter(mAdapter);
        mRv_zi.setLayoutManager(new GridLayoutManager(activity, mAdapter.calculate(activity)));
        mRv_zi.addItemDecoration(new SpaceItemDecoration(ViewUtil.dpToPx(4), ViewUtil.dpToPx(8), 0, SpaceItemDecoration.GRIDLAYOUT));
        mStatus_view.setOnRetryClickListener(mRetryClickListener);

        presenter.font_charset(requestBean, true);
    }

    private void setItemLongClickListener() {
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                final ZiBean bean = mAdapter.getItem(position).getGlyph();
                if (bean == null) {
                    return false;
                }
                final TextView itemView = new TextView(activity);
                itemView.setText("删除");
                itemView.setTextSize(16);
                itemView.setPadding(30, 16, 30, 16);
                itemView.setTextColor(Color.RED);
                final QMUIPopup popup = QMUIPopups.popup(activity)
                        .view(itemView)
                        .bgColor(ContextCompat.getColor(activity, R.color.whiteSmoke))
                        .borderColor(ContextCompat.getColor(activity, R.color.colorLine))
                        .borderWidth(ViewUtil.dpToPx(1))
                        .radius(8)
                        .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                        .offsetYIfBottom(ViewUtil.dpToPx(0))
                        .offsetX(0)
                        .preferredDirection(QMUIPopup.DIRECTION_TOP)
                        .shadow(true)
                        .arrow(true)
                        .arrowSize(40, 20)
                        .show(view);
                itemView.setOnClickListener((View v) -> {
                    popup.dismiss();
                    final String str = TextUtils.isEmpty(bean.get_hanzi()) ? bean.get_key() : bean.get_hanzi();
                    final String fId = zilibBean != null ? zilibBean.get_id() : bean.get_id();
                    TipPopView tipPopView = new TipPopView(activity, "您确定要删除字符『" + str + "』吗？", "该操作会删除该字符下的所有字形图片。", "删除", new TipPopView.TipListener() {
                        @Override
                        public void ok() {
                            presenter.delete(fId, str, bean.get_id());
                        }
                    });
                    tipPopView.showPopupWindow(view);
                });
                return true;
            }
        });
    }

    @Override
    public void deleteSuccess(String msg) {
        onRefresh(mRefresh_layout);
    }

    /**
     * 点击"空空如也"重新发送请求
     */
    private View.OnClickListener mRetryClickListener = (View v) -> {
        onRefresh(null);
    };

    private int loaded = 0;
    private RequestBean requestBean = new RequestBean();

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mRefresh_layout.finishRefresh(false);
        getData();
    }

    private void getData() {
        check();
        mAdapter.setNewData(null);
        requestBean.addParams("loaded", loaded * size);
        presenter.font_glyphs_query(requestBean, true);
    }

    @Override
    protected ZiLibZiListPresenterImpl getPresenter() {
        return new ZiLibZiListPresenterImpl();
    }

    @Override
    public void showProgress() {
        mStatus_view.showLoading();
        if (null != mStatus_view.findViewById(R.id.ll_loading)) {
            mStatus_view.findViewById(R.id.ll_loading).setOnClickListener(mCancelClick);
        }
    }

    /**
     * 允许用户取消api请求
     */
    private final View.OnClickListener mCancelClick = (View v) -> {
        cancel();
        mStatus_view.showEmpty();
        mRefresh_layout.finishRefresh();
    };

    @Override
    public void disimissProgress() {
        mRefresh_layout.finishRefresh();
        mRefresh_layout.finishLoadMore();
    }

    @Override
    public void getfont_charsetSuccess(List<String> list) {
        if (list != null && !list.isEmpty()) {
            addTabs(list);
        }
    }

    @Override
    public void getfont_charsetFail(String msg) {

    }

    private int size = 0;
    private int total = 0;
    private int total_page = 1;
    @Override
    public void loadDataSuccess(RowBean<ZiLibDetailBean> tData) {
        if (tData != null && tData.getList() != null && tData.getList().size() > 0) {
            total = tData.getTotal();
            mRefresh_layout.setTotal(tData.getTotal());
            mStatus_view.showContent();
            mAdapter.addData(tData.getList());
            mAdapter.notifyDataSetChanged();

            if (loaded == 0) {
                size = tData.getList().size();
                total_page = total / size;
                if (total % size > 0) {
                    total_page++;
                }
            }

            if (size < total) {
                rel_bottom.setVisibility(View.VISIBLE);
                tv_page.setText("第" + (loaded + 1) + "页");
                check();
            } else {
                rel_bottom.setVisibility(View.GONE);
            }
        } else {
            rel_bottom.setVisibility(View.GONE);
            if (loaded == 0) {//刷新
                mStatus_view.showEmpty();
                showRefreshByEmpty();
            }
        }
    }

    /**
     * 1.MultipleStatusView导致SmartRefreshLayout隐藏了无法刷新
     * 2.空空如也无法点击
     */
    private void showRefreshByEmpty() {
        mRefresh_layout.setVisibility(View.VISIBLE);
        mRefresh_layout.setEnableLoadMore(false);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity, errorMsg + "");
        if (errorMsg.contains("该字库非当前用户所有")) {
            onRefresh(mRefresh_layout);
        } else {
            mStatus_view.showError();
        }
    }
}