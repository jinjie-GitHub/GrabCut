package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.adapter.ZitieHomeChildListAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.ZitieZuopingListPresenterImpl;
import com.ltzk.mbsf.api.view.ZitieZuopingListView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.GalleryDetailBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：作品列表
 * 作者： on 2018/9/15 14:32
 * 邮箱：499629556@qq.com
 */

public class ZitieZuopingListActivity extends MyBaseActivity<ZitieZuopingListView, ZitieZuopingListPresenterImpl> implements OnRefreshListener, ZitieZuopingListView {
    public static final int REQ_SEARCH_KEY = 1;
    @BindView(R2.id.lay_search)
    LinearLayout lay_search;
    @BindView(R2.id.iv_back)
    ImageView iv_back;
    @OnClick(R2.id.iv_back)
    public void iv_back(View view){
        finish();
    }

    @BindView(R2.id.et_key)
    EditText et_key;
    @OnClick(R2.id.et_key)
    public void et_key(View view){
        SearchActivity.safeStart(this, Constan.Key_type.KEY_ZITIE,"", "",REQ_SEARCH_KEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String key = data.getStringExtra(SearchActivity.KEY);
            if (requestCode == REQ_SEARCH_KEY) {
                et_key.setText(key);
                requestBean.addParams("key", et_key.getText().toString());
                onRefresh(swipeToLoadLayout);
            }
        }
    }

    @BindView(R2.id.tv_nodata)
    TextView tv_nodata;
    @BindView(R2.id.tv_error)
    TextView tv_error;
    @OnClick({R2.id.tv_nodata,R2.id.tv_error})
    public void tv_error(View view){
        onRefresh(swipeToLoadLayout);
    }

    @BindView(R2.id.swipeToLoadLayout)
    MySmartRefreshLayout swipeToLoadLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView swipe_target;
    ZitieHomeChildListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        EventBus.getDefault().register(this);
        return R.layout.activity_zitie_zuoping_list;
    }
    int type = 0;
    @Override
    public void initView() {
        String title = getIntent().getStringExtra("title");
        if(getIntent().getBooleanExtra("isJpush",false)){
            topBar.setLeftButtonListener(R.mipmap.close2, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishFromTop();
                }
            });
        }else {
            topBar.setLeftButtonListener(R.mipmap.back, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        requestBean = new RequestBean();
        requestBean.addParams("_token", MainApplication.getInstance().getToken());

        adapter = new ZitieHomeChildListAdapter(activity,new ZitieHomeChildListAdapter.OnItemClickListener(){
            @Override
            public void onItemClick(Object object, int position) {
                if(XClickUtil.isFastDoubleClick(swipe_target)){
                    return;
                }
                if(TextUtils.isEmpty(adapter.getType())){
                    ZitieHomeBean.ListBeanX.ListBeanAndType listBeanAndType = (ZitieHomeBean.ListBeanX.ListBeanAndType)object;
                    ZitieHomeBean.ListBeanX.ListBean bean = listBeanAndType.get_data();
                    switch (listBeanAndType.get_type()) {
                        case ZitieHomeBean.type_author:
                            startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type",1).putExtra("name",bean.get_name()).putExtra("title",bean.get_dynasty()+" • "+bean.get_name()));
                            break;
                        case ZitieHomeBean.type_gallery:
                            startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type",2).putExtra("gid",bean.get_gallery_id()).putExtra("title",bean.get_title()));
                            break;
                        case ZitieHomeBean.type_zuopin:
                            startActivity(new Intent(activity, ZitieListActivity.class).putExtra("zid",bean.get_zuopin_id()).putExtra("title",bean.get_name()));
                            break;
                        case ZitieHomeBean.type_zitie:
                            if(bean.get_hd()!=null && bean.get_hd().equals("1")){
                                if(MainApplication.getInstance().isCanGo(activity,swipe_target,bean.get_free(),requestBean.getParams())){
                                    startActivityFromBottom(new Intent(activity, BigZitieActivity.class).putExtra("zid",bean.get_zitie_id()));
                                }
                            }else {
                                startActivityFromBottom(new Intent(activity, ZitieActivity.class).putExtra("zid",bean.get_zitie_id()).putExtra("index",bean.get_focus_page()));
                            }
                            break;
                    }

                }else {
                    ZitieHomeBean.ListBeanX.ListBean bean = (ZitieHomeBean.ListBeanX.ListBean)object;
                    boolean isPick = getIntent().getBooleanExtra("zitie_pick", false);
                    startActivity(new Intent(activity, ZitieListActivity.class).putExtra("zid",bean.get_zuopin_id()).putExtra("title",bean.get_name()).putExtra("zitie_pick",isPick));
                }
            }
        });

        swipeToLoadLayout.setOnRefreshListener(this);
        swipe_target.setLayoutManager(new GridLayoutManager(activity,adapter.calculate(activity)));
        swipe_target.addItemDecoration(new SpaceItemDecoration(ViewUtil.dpToPx(3), ViewUtil.dpToPx(6),0, SpaceItemDecoration.GRIDLAYOUT));

        swipe_target.setAdapter(adapter);
        list = new ArrayList<>();
        list_gallery = new ArrayList<>();
        type = getIntent().getIntExtra("type",0);
        switch (type){
            case 1:
                topBar.setVisibility(View.VISIBLE);
                lay_search.setVisibility(View.GONE);

                String name = getIntent().getStringExtra("name");
                requestBean.addParams("name", name);
                adapter.setType(ZitieHomeBean.type_author);
                topBar.setRightTxtListener("简介", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(activity,AuthorDetailActivity.class).putExtra("name",name));
                    }
                });
                onRefresh(swipeToLoadLayout);
                break;
            case 2:
                topBar.setVisibility(View.VISIBLE);
                lay_search.setVisibility(View.GONE);

                String gid = getIntent().getStringExtra("gid");
                requestBean.addParams("gid", gid);
                //adapter.setType(ZitieHomeBean.type_zuopin);
                onRefresh(swipeToLoadLayout);
                break;
            default:
                topBar.setVisibility(View.GONE);
                lay_search.setVisibility(View.VISIBLE);
                et_key.setText(getIntent().getStringExtra("key"));
                requestBean.addParams("key", et_key.getText().toString());
                adapter.setType(ZitieHomeBean.type_zuopin);

                onRefresh(swipeToLoadLayout);
                break;
        }
        if(TextUtils.isEmpty(title)){
            if(type == 2){
                presenter.gallery_details(requestBean,false);
            }
        }else {
            topBar.setTitle(title+"");
        }

        swipeToLoadLayout.setRunnable(() -> {
            if (swipeToLoadLayout.getLoaded() >= swipeToLoadLayout.getTotal()) {
                swipeToLoadLayout.finishLoadMore();
                ToastUtil.showToast(activity, "没有更多了！");
                return;
            }
            requestBean.addParams("loaded", swipeToLoadLayout.getLoaded());
            switch (type) {
                case 1:
                    presenter.author_zplist(requestBean, true);
                    break;
                case 2:
                    presenter.getList(requestBean, true);
                    break;
                default:
                    presenter.zuopin_query(requestBean, true);
                    break;
            }
        });
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        swipeToLoadLayout.finishRefresh(false);
        swipeToLoadLayout.setLoaded(0);
        adapter.setData(null);
        adapter.notifyDataSetChanged();
        requestBean.addParams("loaded", 0);
        switch (type){
            case 1:
                presenter.author_zplist(requestBean,true);
                break;
            case 2:
                presenter.getList(requestBean,true);
                break;
            default:
                presenter.zuopin_query(requestBean,true);
                break;
        }
    }

    @Override
    protected ZitieZuopingListPresenterImpl getPresenter() {
        return new ZitieZuopingListPresenterImpl();
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

    List<ZitieHomeBean.ListBeanX.ListBean> list;

    @Override
    public void loadDataSuccess(RowBean<ZitieHomeBean.ListBeanX.ListBean> tData) {

        if(tData!=null && tData.getList()!=null && tData.getList().size()>0){
            swipeToLoadLayout.setTotal(tData.getTotal());
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                list.clear();
                //有数据
                tv_nodata.setVisibility(View.GONE);
                tv_error.setVisibility(View.GONE);
                swipeToLoadLayout.setVisibility(View.VISIBLE);
            }
            list.addAll(tData.getList());
            adapter.setData(list);
            adapter.notifyDataSetChanged();
        }else {
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                list.clear();
                adapter.setData(list);
                adapter.notifyDataSetChanged();
                //刷新无数据
                tv_nodata.setVisibility(View.VISIBLE);
                tv_error.setVisibility(View.GONE);
                swipeToLoadLayout.setVisibility(View.GONE);
            }
        }
        swipeToLoadLayout.setEnableLoadMore(adapter.getItemCount() < tData.getTotal());
    }

    List<ZitieHomeBean.ListBeanX.ListBeanAndType> list_gallery;
    @Override
    public void getGalleryItemsResult(RowBean<ZitieHomeBean.ListBeanX.ListBeanAndType> tData) {
        if(tData!=null && tData.getList()!=null && tData.getList().size()>0){
            swipeToLoadLayout.setTotal(tData.getTotal());
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                list_gallery.clear();
                //有数据
                tv_nodata.setVisibility(View.GONE);
                tv_error.setVisibility(View.GONE);
                swipeToLoadLayout.setVisibility(View.VISIBLE);
            }
            list_gallery.addAll(tData.getList());
            adapter.setData(list_gallery);
            adapter.notifyDataSetChanged();
        }else {
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                list_gallery.clear();
                adapter.setData(list_gallery);
                adapter.notifyDataSetChanged();
                //刷新无数据
                tv_nodata.setVisibility(View.VISIBLE);
                tv_error.setVisibility(View.GONE);
                swipeToLoadLayout.setVisibility(View.GONE);
            }
        }
        swipeToLoadLayout.setEnableLoadMore(adapter.getItemCount() < tData.getTotal());
    }

    @Override
    public void loadDataError(String errorMsg) {
        tv_nodata.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
        swipeToLoadLayout.setVisibility(View.GONE);

        ToastUtil.showToast(activity,errorMsg+"");
    }

    @Override
    public void getGalleryDetailResult(GalleryDetailBean bean) {
        topBar.setTitle(bean.get_title()+"");
    }

    @Override
    public void getGalleryDetailResultFail(String msg) {

    }


    @Override
    protected void onDestroy() {
        presenter.unSubscribe();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishEvent(Boolean isClose) {
        if (isClose) {
            finish();
        }
    }
}
