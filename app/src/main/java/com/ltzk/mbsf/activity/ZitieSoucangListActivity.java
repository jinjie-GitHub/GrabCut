package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.adapter.ZitieListAdapter;
import com.ltzk.mbsf.api.presenter.ZitieSoucangListPresenterImpl;
import com.ltzk.mbsf.api.view.ZitieSoucangListView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.SoucangCannelBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：作品收藏列表
 * 作者： on 2018/9/15 14:32
 * 邮箱：499629556@qq.com
 */

public class ZitieSoucangListActivity extends MyBaseActivity<ZitieSoucangListView,ZitieSoucangListPresenterImpl> implements OnRefreshListener,ZitieSoucangListView {

    @BindView(R2.id.iv_left)
    ImageView left_button;
    @OnClick(R2.id.iv_left)
    public void left_button(View view){
        finish();
    }
    @BindView(R2.id.tv_left)
    TextView left_txt;
    @OnClick(R2.id.tv_left)
    public void left_txt(View view){
        if(adapter.isEdit()){//可编辑
            if(list_select == null){
                list_select = new ArrayList<>();
            }else {
                list_select.clear();
            }

            SoucangCannelBean bean ;
            for (Integer index : adapter.getSet()) {
                bean = new SoucangCannelBean();
                bean.setPage(list.get(index).get_focus_page());
                bean.setZid(list.get(index).get_zitie_id());
                list_select.add(bean);
            }
            if(list_select.size() == 0){
                ToastUtil.showToast(activity,"请选择要取消收藏的作品");
                return;
            }

            if(tipPopView_del == null){
                tipPopView_del = new TipPopView(activity, "", "从收藏列表中删除所选作品？","删除", new TipPopView.TipListener() {
                    @Override
                    public void ok() {
                        requestBean.addParams("pages",list_select);
                        presenter.zitie_page_unfav(requestBean,true);
                    }
                });
            }
            tipPopView_del.showPopupWindow(left_txt);
        }
    }

    @BindView(R2.id.tv_right)
    TextView right_txt;
    List<SoucangCannelBean> list_select;
    @OnClick(R2.id.tv_right)
    public void right_txt(View view){
        if(adapter.isEdit()){//取消编辑
            adapter.setEdit(false);
            adapter.setClear();
            adapter.notifyDataSetChanged();

            right_txt.setText("编辑");
            left_button.setVisibility(View.VISIBLE);
            left_txt.setVisibility(View.GONE);
        }else {//开始编辑
            adapter.setEdit(true);
            adapter.notifyDataSetChanged();

            right_txt.setText("取消");
            left_button.setVisibility(View.GONE);
            left_txt.setVisibility(View.VISIBLE);
        }
    }
    TipPopView tipPopView_del;
    @BindView(R2.id.nodata)
    ImageView nodata;
    @OnClick(R2.id.nodata)
    public void nodata(View view){
        onRefresh(swipeToLoadLayout);
    }
    @BindView(R2.id.swipeToLoadLayout)
    MySmartRefreshLayout swipeToLoadLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView swipe_target;
    ZitieListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zitie_soucang_list;
    }

    @Override
    public void initView() {
        requestBean = new RequestBean();
        requestBean.addParams("_token", MainApplication.getInstance().getToken());
        swipeToLoadLayout.setOnRefreshListener(this);

        adapter = new ZitieListAdapter(activity, new ZitieListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ZitieBean bean, int position) {
                if(XClickUtil.isFastDoubleClick(swipe_target)){
                    return;
                }
                if(bean.get_hd()!=null && bean.get_hd().equals("1")){
                    if(MainApplication.getInstance().isCanGo(activity,swipe_target,bean.get_free(),requestBean.getParams())){
                        startActivityFromBottom(new Intent(activity, BigZitieActivity.class).putExtra("zid",bean.get_zitie_id()));
                    }
                }else {
                    startActivityFromBottom(new Intent(activity, ZitieActivity.class).putExtra("zid",bean.get_zitie_id()).putExtra("index",bean.get_focus_page()));
                }
            }
        });
        adapter.setCallBack(new ZitieListAdapter.CallBack() {
            @Override
            public void onSelectChange(HashSet<Integer> set) {
                if(set.size()>0){
                    left_txt.setEnabled(true);
                }else {
                    left_txt.setEnabled(false);
                }
            }
        });
        swipe_target.setAdapter(adapter);
        swipe_target.setLayoutManager(new GridLayoutManager(activity,adapter.calculate(activity)));
        swipe_target.addItemDecoration(new SpaceItemDecoration(ViewUtil.dpToPx(4), ViewUtil.dpToPx(8),0, SpaceItemDecoration.GRIDLAYOUT));
        list = new ArrayList<>();

        onRefresh(swipeToLoadLayout);

        swipeToLoadLayout.setRunnable(() -> {
            if (swipeToLoadLayout.getLoaded() >= swipeToLoadLayout.getTotal()) {
                swipeToLoadLayout.finishLoadMore();
                ToastUtil.showToast(activity, "没有更多了！");
                return;
            }
            requestBean.addParams("loaded", swipeToLoadLayout.getLoaded());
            presenter.getList(requestBean, true);
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        swipeToLoadLayout.finishRefresh(false);
        swipeToLoadLayout.setLoaded(0);
        left_txt.setEnabled(false);
        adapter.setEdit(true);
        adapter.setData(null);
        right_txt(right_txt);
        requestBean.addParams("loaded", 0);
        presenter.getList(requestBean, true);
    }

    @Override
    protected ZitieSoucangListPresenterImpl getPresenter() {
        return new ZitieSoucangListPresenterImpl();
    }

    RequestBean requestBean;
    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        swipeToLoadLayout.finishRefresh();
        swipeToLoadLayout.finishLoadMore();
    }

    List<ZitieBean> list;

    @Override
    public void loadDataSuccess(RowBean<ZitieBean> tData) {
        if(tData!=null && tData.getList()!=null && tData.getList().size()>0){
            swipeToLoadLayout.setTotal(tData.getTotal());
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                list.clear();
                nodata.setVisibility(View.GONE);
            }
            list.addAll(tData.getList());
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        }else {
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                list.clear();
                adapter.setData(list);
                adapter.notifyDataSetChanged();
                nodata.setVisibility(View.VISIBLE);
            }
        }
        swipeToLoadLayout.setEnableLoadMore(adapter.getItemCount() < tData.getTotal());
    }

    @Override
    public void unfavResult(String bean) {
        onRefresh(swipeToLoadLayout);
    }

    @Override
    public void loadDataError(String errorMsg) {
        ToastUtil.showToast(activity,errorMsg+"");
    }

}
