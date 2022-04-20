package com.ltzk.mbsf.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewStub;
import android.widget.TextView;

import com.heaven7.android.dragflowlayout.DragAdapter;
import com.heaven7.android.dragflowlayout.DragFlowLayout;
import com.heaven7.android.dragflowlayout.IViewObserver;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.api.presenter.MyVideosPresenter;
import com.ltzk.mbsf.base.IBaseView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.utils.Logger;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.TransitionProvider;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.TopBar;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/11/1
 */
public class AuthorFavActivity extends MyBaseActivity<IBaseView, MyVideosPresenter> implements IBaseView<List<String>> {
    private static final String EDIT = "编辑";
    private static final String CANCEL = "取消";

    @BindView(R.id.top_bar)
    TopBar mTopBar;

    @BindView(R.id.refreshLayout)
    MySmartRefreshLayout mRefreshLayout;

    @BindView(R.id.tagLayout)
    DragFlowLayout mTagLayout;

    @BindView(R.id.view_empty_data)
    ViewStub mViewStubNoData;
    private View mNoDataView;

    private List<String> mData;
    private String mRightTxt = EDIT;

    public static void safeStart(Context c) {
        Intent intent = new Intent(c, AuthorFavActivity.class);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_author_fav;
    }

    @Override
    public void initView() {
        mTopBar.setTitle("作者收藏");
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> finish());
        mTopBar.setRightTxtListener(EDIT, mClickListener);
        initDragFlowLayout();
        mRefreshListener.onRefresh(mRefreshLayout);
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
    }

    private View.OnClickListener mClickListener = v -> {
        if (EDIT.equals(mRightTxt)) {
            mTagLayout.beginDrag();
        } else {
            mTagLayout.finishDrag();
        }
        mRightTxt = EDIT.equals(mRightTxt) ? CANCEL : EDIT;
        mTopBar.setRightTxt(mRightTxt);
        resetTopBarState(mRightTxt);
    };

    private void resetTopBarState(String msg) {
        if (CANCEL.equals(msg)) {
            setShake(true);
            mTopBar.setLeftTxtListener("完成", v -> {
                mClickListener.onClick(v);
                final List<String> list = mTagLayout.getDragItemManager().getItems();
                presenter.author_fav_update(list);
            });
        } else {
            setShake(false);
            mTopBar.setLeftButtonListener(R.mipmap.back, v -> finish());
        }
    }

    private void setShake(boolean isShake) {
        for (int i = 0; i < mTagLayout.getChildCount(); i++) {
            final View target = mTagLayout.getChildAt(i);
            if (isShake) {
                if (!(target.getTag() instanceof ObjectAnimator)) {
                    target.setTag(TransitionProvider.shake(target));
                }
            } else {
                if (target.getTag() instanceof ObjectAnimator) {
                    ((ObjectAnimator) target.getTag()).cancel();
                    target.setRotation(0f);
                    target.setTag(null);
                }
            }
        }
    }

    private final OnRefreshListener mRefreshListener = refreshLayout -> {
        mRefreshLayout.finishRefresh(false);
        mRightTxt = EDIT;
        mTopBar.setRightTxt(mRightTxt);
        resetTopBarState(mRightTxt);
        mTagLayout.removeAllViews();
        presenter.author_favlist();
    };

    private void initDragFlowLayout() {
        /*mTagLayout.setLayoutTransition(TransitionProvider.createTransition(mTagLayout));
        mTagLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.iv_close) {
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
                Logger.d("--->onDeleteSuccess: " + data);
            }
        });*/

        mTagLayout.setOnItemClickListener((dragFlowLayout, view, motionEvent, i) -> {
            TextView tv = view.findViewById(R.id.tv_text);
            startActivity(new Intent(activity, AuthorDetailActivity.class)
                    .putExtra("name", (tv.getText().toString())));
            return false;
        });

        mTagLayout.setDragAdapter(new DragAdapter<String>() {
            @Override
            public int getItemLayoutId() {
                return R.layout.item_drag_flow;
            }

            @Override
            public void onBindData(View itemView, int dragState, String data) {
                TextView tv = itemView.findViewById(R.id.tv_text);
                tv.setText(data);
                itemView.findViewById(R.id.iv_close).setVisibility(
                        dragState != DragFlowLayout.DRAG_STATE_IDLE
                                ? View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public String getData(View itemView) {
                TextView tv = itemView.findViewById(R.id.tv_text);
                return tv.getText().toString();
            }
        });

        //设置拖拽状态监听器
        mTagLayout.setOnDragStateChangeListener(new DragFlowLayout.OnDragStateChangeListener() {
            @Override
            public void onDragStateChange(DragFlowLayout dfl, int dragState) {
                if (dragState == DragFlowLayout.DRAG_STATE_DRAGGING && !CANCEL.equals(mRightTxt)) {
                    Logger.d("--->onDragStateChange: " + dragState);
                    mRightTxt = CANCEL;
                    mTopBar.setRightTxt(mRightTxt);
                    resetTopBarState(mRightTxt);
                }
            }
        });

        //添加view观察者
        mTagLayout.addViewObserver(new IViewObserver() {
            @Override
            public void onAddView(View child, int index) {
                Logger.d("--->onAddView: " + index);
            }

            @Override
            public void onRemoveView(View child, int index) {
                Logger.d("--->onRemoveView: " + index);
            }
        });
    }

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
    public void loadDataSuccess(List<String> tData) {
        if (tData == null) {
            return;
        }
        mData = tData;
        showNoDataView(tData.size() <= 0);
        mTagLayout.removeAllViews();
        mTagLayout.getDragItemManager().addItems(mData);
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
        ToastUtil.showToast(activity, errorMsg);
    }
}