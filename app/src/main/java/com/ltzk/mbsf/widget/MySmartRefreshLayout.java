package com.ltzk.mbsf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ltzk.mbsf.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

/**
 * Created by JinJie on 2021/5/7
 */
public class MySmartRefreshLayout extends SmartRefreshLayout {

    private Runnable mRunnable;
    private RecyclerView mRecyclerView;
    private ClassicsFooter mClassicsFooter;

    private int loaded = 0;
    private int mTotal = 0;

    public MySmartRefreshLayout(Context context) {
        super(context);
        //init();
    }

    public MySmartRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    private void init() {
        setFooterInsetStart(0);
        setFooterTriggerRate(1);
        setEnableAutoLoadMore(false);
        setEnableOverScrollBounce(true);//是否启用越界回弹
        //setEnableOverScrollDrag(true);//是否启用越界拖动（仿苹果效果）
        setEnableLoadMore(false);
        mClassicsFooter = findViewById(R.id.footer);
        if (mClassicsFooter == null) {
            mClassicsFooter = (ClassicsFooter) getRefreshFooter();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        if (mRecyclerView == null) {
            final int count = getChildCount();
            for (int i = 0; i < count; i++) {
                if (getChildAt(i) instanceof RecyclerView) {
                    mRecyclerView = (RecyclerView) getChildAt(i);
                    break;
                }
            }
        }

        if (mClassicsFooter != null) {
            setOnMultiPurposeListener(mMultiPurposeListener);
        }
    }

    public int getLoaded() {
        return loaded;
    }

    public void setLoaded(int loaded) {
        this.loaded = loaded;
    }

    public int getTotal() {
        return mTotal;
    }

    public void setTotal(int total) {
        this.mTotal = total;
    }

    public void setRunnable(Runnable runnable) {
        this.mRunnable = runnable;
    }

    private final SimpleMultiPurposeListener mMultiPurposeListener = new SimpleMultiPurposeListener() {
        private static final String FOOTER_MOVING = "上拉加载更多：";
        private static final String FOOTER_LOADING = "松开立即加载：";
        private float value = 0;

        public void onFooterMoving(RefreshFooter footer, boolean isDragging, float percent, int offset, int footerHeight, int maxDragHeight) {
            if (mRecyclerView != null) {
                if (mRecyclerView.getAdapter() instanceof BaseQuickAdapter) {
                    loaded = ((BaseQuickAdapter) mRecyclerView.getAdapter()).getData().size();
                } else {
                    loaded = mRecyclerView.getAdapter().getItemCount();
                }
            }
            if (isDragging) {
                mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_PROGRESS).setVisibility(View.GONE);
                if (offset <= footerHeight) {
                    ((TextView) mClassicsFooter.findViewById(ClassicsFooter.ID_TEXT_TITLE)).setText(FOOTER_MOVING + loaded + "/" + mTotal);
                    mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_ARROW).setVisibility(View.VISIBLE);
                    if (value == 0) {
                        value = 180;
                        mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_ARROW).animate().rotation(value);
                    }
                    finishLoadMoreWithNoMoreData();
                } else {
                    ((TextView) mClassicsFooter.findViewById(ClassicsFooter.ID_TEXT_TITLE)).setText(FOOTER_LOADING + loaded + "/" + mTotal);
                    mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_ARROW).setVisibility(View.VISIBLE);
                    if (value == 180) {
                        value = 0;
                        mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_ARROW).animate().rotation(value);
                    }
                }
            } else {
                if (offset >= footerHeight) {
                    synchronized (this) {
                        final boolean isVisible = mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_ARROW).getVisibility() == View.VISIBLE;
                        if (isVisible) {
                            mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_ARROW).setVisibility(View.GONE);
                            mClassicsFooter.findViewById(ClassicsFooter.ID_IMAGE_PROGRESS).setVisibility(View.VISIBLE);
                            closeHeaderOrFooter();
                            if (mRunnable != null) {
                                mRunnable.run();
                            }
                        }
                    }
                } else {
                    finishLoadMoreWithNoMoreData();
                }
            }
        }
    };
}