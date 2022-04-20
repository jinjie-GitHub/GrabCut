package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.classic.common.MultipleStatusView;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.adapter.ZiLibListNewAdapter;
import com.ltzk.mbsf.api.presenter.FontQueryPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.Bus_ZilibAdd;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.widget.FixGridLayoutManager;
import com.ltzk.mbsf.widget.GridItemDecoration;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.TopBar;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by JinJie on 2021/8/2
 */
public class ZiLibMyActivity extends MyBaseActivity<IBaseView, FontQueryPresenter> implements IBaseView<RowBean<ZilibBean>>, OnItemClickListener {
    private static final String EDIT = "编辑";
    private static final String CANCEL = "取消";

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    @BindView(R.id.tv_add_font)
    View tv_add_font;

    @BindView(R.id.multipleStatusView)
    MultipleStatusView mMultipleStatusView;

    private String subset = "my";
    private ZiLibListNewAdapter mAdapter;
    private List<ZilibBean> mList = new ArrayList<>();
    private String mRightTxt = EDIT;

    public static void safeStart(Context c, String subset) {
        Intent intent = new Intent(c, ZiLibMyActivity.class);
        intent.putExtra("subset", subset);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_fav_videos;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private final boolean isPrivate() {
        return "my".equals(subset);
    }

    @OnClick(R.id.tv_add_font)
    public void tv_add_font(View view) {
        startActivityFromBottom(new Intent(activity, ZiLibAddActivity.class));
    }

    @Override
    public void initView() {
        subset = getIntent().getStringExtra("subset");
        mTopBar.setTitle(isPrivate() ? "个人字库" : "字库收藏");
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
            finish();
        });

        if (isPrivate()) {
            /*mTopBar.setRightButtonListener(R.mipmap.add_lib, v -> {
                startActivityFromBottom(new Intent(activity, ZiLibAddActivity.class));
            });*/
        } else {
            mTopBar.setRightTxtListener(EDIT, v -> {
                mRightTxt = EDIT.equals(mRightTxt) ? CANCEL : EDIT;
                mTopBar.setRightTxt(mRightTxt);
                mTopBar.setLeftTxtColor(getResources().getColor(R.color.silver));
                resetTopBarState(mRightTxt);
            });
        }

        tv_add_font.setVisibility(isPrivate() ? View.VISIBLE : View.GONE);
        mAdapter = new ZiLibListNewAdapter(activity, (ZilibBean zilibBean) -> {
                Intent intent = new Intent(activity, ZiLibZiListActivity.class);
                intent.putExtra("zilibBean", zilibBean);
                activity.startActivity(intent);
            }
        );

        mAdapter.setRunnable(() -> mTopBar.setLeftTxtColor(getResources().getColor(mAdapter.getSet().size() > 0 ?
                R.color.red : R.color.silver)));
        mRecyclerView.setLayoutManager(new FixGridLayoutManager(activity, mAdapter.calculate(activity)));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new GridItemDecoration.Builder(this)
                .setHorizontalSpan(R.dimen.activity_horizontal_margin)
                .setVerticalSpan(R.dimen.activity_vertical_margin)
                .setColorResource(R.color.whiteSmoke)
                .build());

        mMultipleStatusView.setOnRetryClickListener(mRetryClickListener);
        mRefreshListener.onRefresh(mRefreshLayout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        mRefreshLayout.setRunnable(() -> {
            presenter.font_query(subset, mRefreshLayout.getLoaded());
        });
    }

    private void deleteFavZiLib() {
        if (mAdapter.getSet().size() == 0) {
            return;
        }
        TipPopView tipPopView_del = new TipPopView(activity, "", "从收藏列表中删除所选字库？", "删除", new TipPopView.TipListener() {
            @Override
            public void ok() {
                List<String> list = new ArrayList<>();
                Iterator<String> it = mAdapter.getSet().iterator();
                while (it.hasNext()) {
                    try {
                        final String fid = it.next();
                        list.add(fid);
                        for (int pos = 0; pos < mAdapter.getList().size(); pos++) {
                            if (fid.equals(mAdapter.getList().get(pos).get_id())) {
                                mAdapter.getList().remove(pos);
                                mAdapter.notifyItemRemoved(pos);
                                mAdapter.notifyItemRangeChanged(pos, mAdapter.getList().size() - pos);
                            }
                        }
                        it.remove();
                    } catch (Exception e) {
                        mAdapter.notifyDataSetChanged();
                    }
                }
                mTopBar.setLeftTxtColor(getResources().getColor(R.color.silver));
                presenter.libUnFav(list);
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
        mList.clear();
        mAdapter.setList(mList);
        mRightTxt = EDIT;
        mTopBar.setRightTxt(EDIT);
        resetTopBarState(EDIT);
        presenter.font_query(subset, 0);
    };

    private View.OnClickListener mRetryClickListener = (View v) -> {
        mRefreshListener.onRefresh(mRefreshLayout);
    };

    @Override
    protected FontQueryPresenter getPresenter() {
        return new FontQueryPresenter();
    }

    @Override
    public void showProgress() {
        showProgressDialog("");
        mMultipleStatusView.showContent();
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        mRefreshLayout.finishRefresh(true);
        mRefreshLayout.finishLoadMore(true);
    }

    //字库有新增
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_ZilibAdd messageEvent) {
        mRefreshListener.onRefresh(mRefreshLayout);
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
        mRefreshLayout.setTotal(tData.getTotal());
        mRefreshLayout.setEnableLoadMore(mAdapter.getItemCount() < tData.getTotal());
        if (tData.getTotal() == 0) {
            mMultipleStatusView.showEmpty();
        } else {
            mMultipleStatusView.showContent();
        }
    }

    @Override
    public void loadDataError(String errorMsg) {
        mMultipleStatusView.showError();
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