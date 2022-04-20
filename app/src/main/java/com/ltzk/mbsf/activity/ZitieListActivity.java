package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.ltzk.mbsf.adapter.SearchAdapter;
import com.ltzk.mbsf.adapter.ZitieListAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.ZitieListPresenterImpl;
import com.ltzk.mbsf.api.view.ZitieListView;
import com.ltzk.mbsf.base.MyBaseActivity;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.bean.ZuopinDetailBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.MySPUtils;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MyListView;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 描述：
 * 作者： on 2018/9/15 14:32
 * 邮箱：499629556@qq.com
 */

public class ZitieListActivity extends MyBaseActivity<ZitieListView,ZitieListPresenterImpl> implements OnRefreshListener,ZitieListView {
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
    @BindView(R2.id.iv_delete)
    ImageView iv_delete;
    @OnClick(R2.id.iv_delete)
    public void iv_delete(View view){
        et_key.setText("");
    }
    @BindView(R2.id.tv_cannel)
    TextView tv_cannel;
    @OnClick(R2.id.tv_cannel)
    public void tv_cannel(View view){
        if(list!=null && list.size()>0){
            cannel();
        }else {
            finish();
        }
    }

    private void cannel(){
        et_key.clearFocus();
        iv_delete.setVisibility(View.GONE);
        tv_cannel.setVisibility(View.GONE);
        lay_search_key.setVisibility(View.GONE);
        ActivityUtils.closeSyskeyBroad(activity);
    }

    @BindView(R2.id.tv_clear)
    TextView tv_clear;
    @OnClick(R2.id.tv_clear)
    public void tv_clear(View view){
        list_key.clear();
        adapter_key.setData(list_key);
        adapter_key.notifyDataSetChanged();
        tv_clear.setVisibility(View.GONE);
        SPUtils.put(activity,Constan.Key_type.KEY_ZITIE,"");
    }


    @BindView(R2.id.lay_search_key)
    LinearLayout lay_search_key;
    @BindView(R2.id.lv_key)
    MyListView lv_key;
    SearchAdapter adapter_key;
    private void initSearch(){
        et_key.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    iv_delete.setVisibility(View.VISIBLE);
                    tv_cannel.setVisibility(View.VISIBLE);
                    lay_search_key.setVisibility(View.VISIBLE);
                    getData();
                }
            }
        });

        adapter_key = new SearchAdapter(activity, new SearchAdapter.OnItemDelCallBack() {

            @Override
            public void click(String string, int position) {
                et_key.setText(string);
                search();
            }

            @Override
            public void del(String string) {
                list_key.remove(string);
                saveKeys(Constan.Key_type.KEY_ZITIE,list_key);
            }

            @Override
            public void to(String string) {
                et_key.setText(string);
                et_key.setSelection(string.length());
            }
        });
        lv_key.setAdapter(adapter_key);
        getData();


        et_key.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId== EditorInfo.IME_ACTION_SEARCH||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)){
                    search();
                    return true;
                }
                return false;
            }
        });
        mHandler.sendEmptyMessageDelayed(0,500);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            ActivityUtils.showInput(activity,et_key);
        }
    };
    List<String> list_key;

    /**
     * 获取本地搜索记录
     */
    private void getData(){
        if(list_key == null){
            list_key = new ArrayList<>();
            String keys_str = (String) SPUtils.get(activity, Constan.Key_type.KEY_ZITIE,"");
            if(keys_str.equals("")){
                tv_clear.setVisibility(View.GONE);
                return;
            }
            tv_clear.setVisibility(View.VISIBLE);
            String[] keys = keys_str.split("#");
            for(int i = 0;i<keys.length;i++){
                list_key.add(keys[i]);
            }
        }
        if(list_key.size()>0){
            tv_clear.setVisibility(View.VISIBLE);
        }else {
            tv_clear.setVisibility(View.GONE);
        }
        adapter_key.setData(list_key);
        adapter_key.notifyDataSetChanged();
    }

    private void search(){
        if(!"".equals(et_key.getText().toString())){
            putKeys(list_key,et_key);
            saveKeys(Constan.Key_type.KEY_ZITIE,list_key);
            onRefresh(swipeToLoadLayout);
        }
        cannel();
    }

    /**
     * 加字
     * @param list
     * @param et
     */
    private void putKeys(List<String> list,EditText et) {
        if (et.getText().toString().equals("")) {
            return;
        }
        if (list.contains(et.getText().toString())) {
            list.remove(et.getText().toString());
        }
        list.add(0, et.getText().toString());
    }

    /**
     * 存字
     * @param type
     * @param list
     */
    private void saveKeys(String type,List<String> list){
        StringBuffer buffer = new StringBuffer();
        for(int i=0;i<list.size();i++){
            buffer.append("#");
            buffer.append(list.get(i));
        }
        String keys_str = buffer.toString().replaceFirst("#","");
        SPUtils.put(activity,type,keys_str);
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
    ZitieListAdapter adapter;

    private boolean isPick;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_zitie_list;
    }

    boolean isGid = true;
    @Override
    public void initView() {
        isPick = getIntent().getBooleanExtra("zitie_pick", false);
        String title = getIntent().getStringExtra("title");
        topBar.setTitle(title+"");
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
        String zid = getIntent().getStringExtra("zid");
        requestBean = new RequestBean();
        requestBean.addParams("_token", MainApplication.getInstance().getToken());
        swipeToLoadLayout.setOnRefreshListener(this);

        swipeToLoadLayout.setRunnable(() -> {
            if (swipeToLoadLayout.getLoaded() >= swipeToLoadLayout.getTotal()) {
                swipeToLoadLayout.finishLoadMore();
                ToastUtil.showToast(activity, "没有更多了！");
                return;
            }
            requestBean.addParams("loaded", swipeToLoadLayout.getLoaded());
            if (isGid) {
                presenter.zuopin_ztlist(requestBean, true);
            } else {
                presenter.getListByKey(requestBean, true);
            }
        });

        adapter = new ZitieListAdapter(activity,new ZitieListAdapter.OnItemClickListener(){
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
                    if (isPick) {
                        MySPUtils.setZiTieName(getApplication(), title);
                        MySPUtils.setZiTieId(getApplication(), bean.get_zitie_id());
                        EventBus.getDefault().post(new Boolean(true));
                        finish();
                    } else {
                        startActivityFromBottom(new Intent(activity, ZitieActivity.class).putExtra("zid", bean.get_zitie_id()).putExtra("index", bean.get_focus_page()));
                    }
                }
            }
        });
        swipe_target.setAdapter(adapter);
        swipe_target.setLayoutManager(new GridLayoutManager(activity,adapter.calculate(activity)));
        swipe_target.addItemDecoration(new SpaceItemDecoration(ViewUtil.dpToPx(4), ViewUtil.dpToPx(8),0, SpaceItemDecoration.GRIDLAYOUT));
        list = new ArrayList<>();
        if(zid!=null && !zid.equals("")){//点击字帖集进入
            isGid = true;
            requestBean.addParams("zid", zid);
            topBar.setVisibility(View.VISIBLE);
            lay_search.setVisibility(View.GONE);
            lay_search_key.setVisibility(View.GONE);
            onRefresh(swipeToLoadLayout);
            if(TextUtils.isEmpty(title)){
                presenter.zuopin_details(requestBean,false);
            }else {
                topBar.setTitle(title+"");
            }
        }else {//搜做进入
            isGid = false;
            topBar.setVisibility(View.GONE);
            lay_search.setVisibility(View.VISIBLE);
            lay_search_key.setVisibility(View.VISIBLE);
            initSearch();
        }
    }



    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        swipeToLoadLayout.finishRefresh(false);
        swipeToLoadLayout.setLoaded(0);
        adapter.setData(null);
        adapter.notifyDataSetChanged();
        requestBean.addParams("key", et_key.getText().toString());
        requestBean.addParams("loaded", 0);
        if (isGid) {
            presenter.zuopin_ztlist(requestBean, true);
        } else {
            presenter.getListByKey(requestBean, true);
        }
    }

    @Override
    protected ZitieListPresenterImpl getPresenter() {
        return new ZitieListPresenterImpl();
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

    @Override
    public void loadDataError(String errorMsg) {
        tv_nodata.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
        swipeToLoadLayout.setVisibility(View.GONE);

        ToastUtil.showToast(activity,errorMsg+"");
    }

    @Override
    public void getZuopinDetailResult(ZuopinDetailBean bean) {
        topBar.setTitle(bean.get_name()+"");
    }

    @Override
    public void getZuopinDetailResultFail(String msg) {

    }

    @Override
    protected void onDestroy() {
        presenter.unSubscribe();
        //EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
