package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.classic.common.MultipleStatusView;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.adapter.ZiListAdapter;
import com.ltzk.mbsf.api.presenter.ZiFavListPresenterImpl;
import com.ltzk.mbsf.api.view.ZiFavListView;
import com.ltzk.mbsf.base.BasePopupWindow;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.CustomBubbleDialog;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zy on 2016/3/25.
 *  集字选择单字
 */
public class JiziFavPopView extends BasePopupWindow implements OnRefreshListener, ZiFavListView {
    private View conentView;
    TipListener tipListener;

    //字典列表
    @BindView(R2.id.status_view)
    MultipleStatusView mStatus_view;
    @BindView(R2.id.refresh_layout)
    MySmartRefreshLayout mRefresh_layout;
    @BindView(R.id.recyclerView)
    RecyclerView mRv_zi;
    ZiListAdapter mAdapter;


    public JiziFavPopView(final Activity activity, TipListener tipListener) {
        this.activity = activity;
        this.tipListener = tipListener;
        init();
    }

    /**
     * 初始化
     */
    private void init(){
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widget_jizi_list_fav_popview, null);
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        ButterKnife.bind(this, conentView);
        widthS = (int)Math.min(ViewUtil.getWidth(),ViewUtil.getHeight()/2) - ViewUtil.dpToPx(60);
        setWidth(widthS);
        setHeight(widthS);
        setTouchable(true);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });
        initView();
        setBackgroundDrawable(new ColorDrawable(0x00000000));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                darkenBackgroud(activity, 1.0f);
            }
        });

        mRefresh_layout.setRunnable(new Runnable() {
            public void run() {
                loaded = mAdapter.getData().size();
                requestBean.addParams("loaded", loaded);
                presenter.glyph_favlist_hanzi(requestBean,false);
            }
        });
    }

    public void setData(String key){
        requestBean.addParams("hanzi", key);
        onRefresh(null);
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent,int x,int y) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            darkenBackgroud(activity, 1.0f);
            this.showAtLocation(parent, Gravity.RIGHT|Gravity.TOP, x, y);
        } else {
            this.dismiss();
        }
    }

    public interface TipListener{
        public void ok(ZiBean ziBean);
    }

    public void initView() {
        presenter = new ZiFavListPresenterImpl();
        presenter.subscribe(this);
        requestBean = new RequestBean();
        initZiView();
        mStatus_view.setOnRetryClickListener(mRetryClickListener);
    }

    /**
     * 点击"空空如也"重新发送请求
     */
    private View.OnClickListener mRetryClickListener = (View v) -> {
        onRefresh(mRefresh_layout);
    };

    public void destory(){
        presenter.unSubscribe();
    }
    int widthS = 0;
    final int spce = ViewUtil.dpToPx(4);
    final int spaceEdge = ViewUtil.dpToPx(4);
    final int tempSize = 150;
    private void initZiView(){
        mRefresh_layout.setOnRefreshListener(this);
        int[] item = ViewUtil.getItemWidth(widthS - ViewUtil.dpToPx(12),tempSize,spce, spaceEdge);
        mAdapter = new ZiListAdapter(activity,item[1]);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                if(XClickUtil.isFastDoubleClick(mRv_zi)){
                    return;
                }
                ZiBean bean = mAdapter.getData().get(position);

                if (isShowing()) {
                    dismiss();
                }

                tipListener.ok(bean);
            }
        });
        mAdapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                CustomBubbleDialog customBubbleDialog = new CustomBubbleDialog(activity,new CustomBubbleDialog.OnClickCustomButtonListener() {
                        @Override
                        public void onClick() {
                            ZiBean bean = mAdapter.getData().get(position);
                            requestBean.addParams("gid",bean.get_id());
                            //取消收藏
                            presenter.glyph_unfav_aid(requestBean,false);
                            mAdapter.getData().remove(position);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                customBubbleDialog.setClickedView(view).show();
                return false;
            }
        });
        mRv_zi.setAdapter(mAdapter);
        mRv_zi.setLayoutManager(new GridLayoutManager(activity,item[0]));
        mRv_zi.addItemDecoration(new SpaceItemDecoration(spce, spaceEdge,0, SpaceItemDecoration.GRIDLAYOUT));

    }
    int loaded = 0;

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mRefresh_layout.finishRefresh(false);
        mAdapter.setNewData(null);

        loaded = 0;
        mRefresh_layout.setLoaded(0);
        requestBean.addParams("loaded", loaded);
        presenter.glyph_favlist_hanzi(requestBean,true);
    }


    ZiFavListPresenterImpl presenter;
    RequestBean requestBean;
    @Override
    public void showProgress() {
        mStatus_view.showLoading();
    }

    private final View.OnClickListener mCancelClick = (View v) -> {
        if (presenter != null) {
            presenter.disSubscribe();
        }
    };

    @Override
    public void disimissProgress() {
        mRefresh_layout.finishRefresh();
        mRefresh_layout.finishLoadMore();
    }

    int total = 0;
    @Override
    public void loadDataSuccess(RowBean<ZiBean> tData) {

        if(tData!=null && tData.getList()!=null && tData.getList().size()>0){
            total = tData.getTotal();
            mRefresh_layout.setTotal(tData.getTotal());
            mStatus_view.showContent();
            mAdapter.addData(tData.getList());
            mAdapter.notifyDataSetChanged();

            if(mAdapter.getData().size()>=total) {
                mRefresh_layout.setEnableLoadMore(false);
            }else {
                mRefresh_layout.setEnableLoadMore(true);
            }
        }else {
            if(loaded == 0){//刷新
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
        mStatus_view.showError();
        ToastUtil.showToast(activity,errorMsg+"");
    }
}
