package com.ltzk.mbsf.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.HistoryActivity;
import com.ltzk.mbsf.activity.JiziNewActivity;
import com.ltzk.mbsf.activity.LoginTypeActivity;
import com.ltzk.mbsf.activity.WebCollectMainActivity;
import com.ltzk.mbsf.adapter.JiziListAdapter;
import com.ltzk.mbsf.api.presenter.JiziListPresenterImpl;
import com.ltzk.mbsf.api.view.JiziListView;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.Bus_JiziUpdata;
import com.ltzk.mbsf.bean.Bus_LoginOut;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.TodayBean;
import com.ltzk.mbsf.popupview.JiZiSettingPopView;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xujiaji.happybubble.BubbleLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class JiziListFragment extends MyBaseFragment<JiziListView,JiziListPresenterImpl> implements OnRefreshListener,JiziListView {

    @BindView(R2.id.left_txt)
    TextView left_txt;
    @OnClick(R2.id.left_txt)
    public void left_txt(View view){
        if (MainApplication.getInstance().isLogin()) {
            processMenuPos0();
        } else {
            tipLogin();
        }
    }

    private void processMenuPos0() {
        if (adapter.isEdit()) {//是编辑状态，点击需要删除
            if (list_select == null) {
                list_select = new ArrayList<>();
            } else {
                list_select.clear();
            }
            for (Integer index : adapter.getSet()) {
                list_select.add(list.get(index).get_id());
            }
            if (list_select.size() == 0) {
                ToastUtil.showToast(activity, "请选择要删除的项");
                return;
            }

            if (tipPopView_del == null) {
                tipPopView_del = new TipPopView(activity, "", "您确定要删除所选集字？", "删除", new TipPopView.TipListener() {
                    @Override
                    public void ok() {
                        requestBean.addParams("jids", list_select);
                        presenter.jizi_delete(requestBean, true);
                    }
                });
            }
            tipPopView_del.showPopupWindow(left_txt);
        } else {
            showMenu(left_txt);

        }
    }

    @BindView(R2.id.right_txt)
    TextView right_txt;
    List<String> list_select;
    @OnClick(R2.id.right_txt)
    public void right_txt(View view){
        if(adapter.isEdit()){//是编辑状态
            isEidt(false);
        }else {
            isEidt(true);
        }
    }
    TipPopView tipPopView_del;

    /**
     * 是否是编辑状态
     * @param is
     */
    private void isEidt(boolean is){
        if(is){
            //进入编辑状态
            adapter.setEdit(true);
            left_txt.setText("删除");
            left_txt.setEnabled(false);
            left_txt.setTextColor(getResources().getColor(R.color.gray));
            right_txt.setText("取消");
            right_txt.setEnabled(true);
        }else {
            //退出编辑状态
            adapter.setEdit(false);
            adapter.setClear();
            left_txt.setText("新建");
            left_txt.setEnabled(true);
            left_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
            right_txt.setText("编辑");
            if(list!=null && list.size()>0){
                right_txt.setEnabled(true);
            }else {
                right_txt.setEnabled(false);
            }
        }
        changeData(true);
    }

    @BindView(R2.id.lay_login)
    LinearLayout lay_login;
    @OnClick(R2.id.tv_login)
    public void tv_login(View view){
        startActivity(new Intent(activity, LoginTypeActivity.class));
    }
    @BindView(R2.id.rel_login_in)
    RelativeLayout rel_login_in;
    @BindView(R2.id.tv_nodata)
    TextView tv_nodata;
    @OnClick(R2.id.tv_nodata)
    public void tv_nodata(View view){
        if (MainApplication.getInstance().isLogin()) {
            startActivity(new Intent(activity, JiziNewActivity.class));
        } else {
            tipLogin();
        }
    }
    @BindView(R2.id.tv_error)
    TextView tv_error;
    @OnClick(R2.id.tv_error)
    public void tv_error(View view){
        onRefresh(swipeToLoadLayout);
    }

    @BindView(R2.id.swipeToLoadLayout)
    MySmartRefreshLayout swipeToLoadLayout;
    @BindView(R2.id.recyclerView)
    RecyclerView gv;
    JiziListAdapter adapter;
    private ItemTouchHelper helper;

    @Override
    protected int getLayoutRes() {
        EventBus.getDefault().register(this);
        return R.layout.fragment_jizi_list;
    }

    @Override
    protected void initView(View rootView) {
        initView();
    }

    @Override
    protected void requestData() {
        super.requestData();
        if (MainApplication.getInstance().isLogin()) {
            requestBean.addParams("loaded", 0);
            presenter.getList(requestBean, true);
        } else {
            onShowMessageEvent(new Bus_LoginOut(""));
        }
    }

    //登录被踢
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginOut messageEvent) {
        disimissProgress();
        lay_login.setVisibility(View.VISIBLE);
        rel_login_in.setVisibility(View.GONE);
    }

    //登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        lay_login.setVisibility(View.GONE);
        rel_login_in.setVisibility(View.VISIBLE);
        onRefresh(swipeToLoadLayout);
    }

    //数据被编辑刷新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_JiziUpdata messageEvent) {
        onRefresh(swipeToLoadLayout);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public void initView() {
        requestBean = new RequestBean();
        swipeToLoadLayout.setOnRefreshListener(this);
        adapter = new JiziListAdapter(this, new JiziListAdapter.CallBack() {
            @Override
            public void onSelectChange(HashSet<Integer> set) {
                if(adapter.isEdit() && set.size()==0){
                    left_txt.setEnabled(false);
                    left_txt.setTextColor(getResources().getColor(R.color.gray));
                }else {
                    left_txt.setEnabled(true);
                    left_txt.setTextColor(getResources().getColor(R.color.red));
                }
            }

            @Override
            public void onItemSwap(int from, int target) {
                RequestBean requestBean = new RequestBean();
                requestBean.addParams("jid",list.get(from).get_id());
                requestBean.addParams("order",target);
                presenter.update_order(requestBean, false);
            }
        });
        gv.setAdapter(adapter);
        gv.setLayoutManager(new GridLayoutManager(activity, adapter.calculate(activity)));
        //gv.addItemDecoration(new SpaceItemDecoration(ViewUtil.dpToPx(4), ViewUtil.dpToPx(8),0, SpaceItemDecoration.GRIDLAYOUT));
        list = new ArrayList<>();

        ((SimpleItemAnimator) gv.getItemAnimator()).setSupportsChangeAnimations(false);
        gv.getItemAnimator().setChangeDuration(0);

        swipeToLoadLayout.setRunnable(() -> {
            if (swipeToLoadLayout.getLoaded() >= swipeToLoadLayout.getTotal()) {
                swipeToLoadLayout.finishLoadMore();
                ToastUtil.showToast(activity, "没有更多了！");
                return;
            }
            requestBean.addParams("loaded", swipeToLoadLayout.getLoaded());
            presenter.getList(requestBean, false);
        });

        helper = new ItemTouchHelper(new DragSwipeCallback(adapter,activity));
        helper.attachToRecyclerView(gv);
    }


    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        processRefresh();
        swipeToLoadLayout.setLoaded(0);
        requestBean.addParams("loaded", 0);
        requestBean.addParams("_token", MainApplication.getInstance().getToken());
        presenter.getList(requestBean, true);
    }

    /**
     * 1、首先清空原列表内容；
     * 2、下拉视图回弹至顶部；
     * 3、发送请求，转圈提示；
     */
    private final void processRefresh() {
        swipeToLoadLayout.finishRefresh(false);
        list.clear();
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected JiziListPresenterImpl getPresenter() {
        return new JiziListPresenterImpl();
    }

    RequestBean requestBean;
    @Override
    public void showProgress() {
        showProgressDialog("");
    }

    @Override
    protected void cancel() {
        super.cancel();
        disimissProgress();
    }

    @Override
    public void disimissProgress() {
        closeProgressDialog();
        swipeToLoadLayout.finishRefresh();
        swipeToLoadLayout.finishLoadMore();
    }

    List<JiziBean> list = new ArrayList<>();

    @Override
    public void loadDataSuccess(RowBean<JiziBean> tData) {
        if(tData!=null && tData.getList()!=null && tData.getList().size()>0){

            swipeToLoadLayout.setTotal(tData.getTotal());
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                list.clear();
                //有数据
                tv_nodata.setVisibility(View.GONE);
                tv_error.setVisibility(View.GONE);
            }
            if(adapter.isEdit()){
                adapter.setEdit(false);
            }
            adapter.setClear();
            left_txt.setText("新建");
            left_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
            right_txt.setText("编辑");
            right_txt.setEnabled(true);
            list.addAll(tData.getList());
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        }else {
            if(swipeToLoadLayout.getLoaded() == 0){//刷新
                if(adapter.isEdit()){
                    adapter.setEdit(false);
                }
                adapter.setClear();
                left_txt.setText("新建");
                left_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
                right_txt.setEnabled(false);
                right_txt.setText("编辑");
                list.clear();
                adapter.setList(list);
                adapter.notifyDataSetChanged();
                //刷新无数据
                tv_nodata.setVisibility(View.VISIBLE);
                tv_error.setVisibility(View.GONE);
            }
        }
        swipeToLoadLayout.setEnableLoadMore(adapter.getItemCount() < tData.getTotal());
    }

    MyCallback myCallback;

    /**
     * @param isEditChange
     */
    private void changeData(boolean isEditChange){
        if(myCallback == null){
            myCallback = new MyCallback();
        }

        if(isEditChange){
            myCallback.setEditChange(isEditChange);
            myCallback.setList_old(adapter.getList());
            myCallback.setList_new(adapter.getList());
        }else {
            myCallback.setList_old(adapter.getList());
            myCallback.setList_new(list);
        }

        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(myCallback ,true);
        adapter.setList(myCallback.getList_new());
        diffResult.dispatchUpdatesTo(adapter);
    }

    class MyCallback extends DiffUtil.Callback{

        private boolean isEditChange = false;

        public boolean isEditChange() {
            return isEditChange;
        }

        public void setEditChange(boolean editChange) {
            isEditChange = editChange;
        }

        private List<JiziBean> list_old,list_new;

        public List<JiziBean> getList_old() {
            return list_old;
        }

        public void setList_old(List<JiziBean> list_old) {
            this.list_old = list_old;
        }

        public List<JiziBean> getList_new() {
            return list_new;
        }

        public void setList_new(List<JiziBean> list_new) {
            this.list_new = list_new;
        }

        public MyCallback() {
        }

        @Override
        public int getOldListSize() {
            if(list_old == null){
                return 0;
            }
            return list_old.size();
        }

        @Override
        public int getNewListSize() {
            if(list_new == null){
                return 0;
            }
            return list_new.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if(isEditChange){
                return true;
            }
            return list_old.get(oldItemPosition).get_id().equals(list_new.get(newItemPosition).get_id());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if(isEditChange){
                return false;
            }

            JiziBean jiziBean_old = list_old.get(oldItemPosition);
            JiziBean jiziBean_new = list_new.get(newItemPosition);

            if (!isEqual(jiziBean_old.get_title(),jiziBean_new.get_title())) {
                return false;
            }
            if (!isEqual(jiziBean_old.get_json(),jiziBean_new.get_json())) {
                return false;
            }
            if (!isEqual(jiziBean_old.getText(),jiziBean_new.getText())) {
                return false;
            }

            if (!isEqualsList(jiziBean_old.get_thumbs(),jiziBean_new.get_thumbs())) {
                return false;
            }
            return true;
        }

        private boolean isEqualsList(List aList, List bList) {

            if (aList == bList)
                return true;

            if (aList.size() != bList.size())
                return false;

            int n = aList.size();
            int i = 0;
            while (n-- != 0) {
                if (aList.get(i).equals(bList.get(i)))
                    return false;
                i++;
            }

            return true;
        }


        private boolean isEqual(String str1, String str2){
            if(str1 == null){
                if(str2 == null){
                    return true;
                }else if(str2!=null){
                    return false;
                }
            }else {
                if(str2 == null){
                    return false;
                }else if(str2.equals(str1)){
                    return true;
                }
            }

            return false;
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            Bundle bundle = new Bundle();
            if(isEditChange){
                bundle.putBoolean("isEidt",isEditChange);
                return bundle;
            }


            JiziBean jiziBean_old = list_old.get(oldItemPosition);
            JiziBean jiziBean_new = list_new.get(newItemPosition);

            if (!isEqual(jiziBean_old.get_title(),jiziBean_new.get_title())) {
                bundle.putString("title",jiziBean_new.get_title());
            }
            if (!isEqual(jiziBean_old.get_json(),jiziBean_new.get_json()) || !isEqualsList(jiziBean_old.get_thumbs(),jiziBean_new.get_thumbs())) {
                bundle.putStringArrayList("url",(ArrayList<String>) jiziBean_new.get_thumbs());
            }

            return bundle;
        }
    }

    @Override
    public void update_orderResult(String bean) {
        list.clear();
        list.addAll(adapter.getList());//更新交换后数据
    }

    @Override
    public void jizi_deleteResult(String bean) {
        for(String id : list_select){
            for (int i = list.size()-1; i >=0 ; i--) {
                if(id.equals(list.get(i).get_id())){
                    list.remove(i);
                    break;
                }
            }
        }
        adapter.setClear();
        list_select.clear();
        adapter.setList(list);
        adapter.notifyDataSetChanged();

        if (adapter.getItemCount() == 0) {
            adapter.setEdit(false);
            left_txt.setText("新建");
            left_txt.setEnabled(true);
            left_txt.setTextColor(getResources().getColor(R.color.colorPrimary));
            right_txt.setEnabled(false);
            right_txt.setText("编辑");
            tv_nodata.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void loadDataError(String errorMsg) {
        tv_nodata.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
        if (isVisible) {
            ToastUtil.showToast(activity, errorMsg);
        }
    }

    private void tipLogin() {
        final String msg = "登录后您才可以创建和管理个人集字。";
        TipPopView tipLoginPopView = new TipPopView(activity, "请您先登录。", msg, "登录", new TipPopView.TipListener() {
            @Override
            public void ok() {
                startActivity(new Intent(activity, LoginTypeActivity.class));
            }
        });
        tipLoginPopView.showPopupWindow(tv_nodata);
    }

    private void processMenuPos1() {
        if (getActivity() instanceof BaseActivity) {
            WebCollectMainActivity.safeStart((BaseActivity) getActivity(), 1);
        }
    }

    private void processMenuPos2() {
        if (getActivity() instanceof BaseActivity) {
            WebCollectMainActivity.safeStart((BaseActivity) getActivity(), 2);
        }
    }

    private void processMenuPos3() {
        HistoryActivity.safeStart(activity, new TodayBean());
        activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    private void showMenu(View anchor) {
        BubbleLayout bl = new BubbleLayout(activity);
        bl.setLookLength(ViewUtil.dpToPx(10));
        bl.setLookWidth(ViewUtil.dpToPx(20));
        bl.setBubbleRadius(2);
        JiZiSettingPopView jiZiSettingPopView = new JiZiSettingPopView(activity, null)
                .setClickedView(anchor)
                .setLayout(ViewUtil.dpToPx(180), WRAP_CONTENT, ViewUtil.dpToPx(0))
                .setOffsetY(-15)
                .setBubbleLayout(bl);
        jiZiSettingPopView.setOnItemClickListener((AdapterView<?> adapterView, View view, int i, long l) -> {
            jiZiSettingPopView.dismiss();
            switch (i) {
                case 0:
                    startActivityFromBottom(new Intent(activity, JiziNewActivity.class));
                    break;
                case 1:
                    processMenuPos1();
                    break;
                case 2:
                    processMenuPos2();
                    break;
                case 3:
                    processMenuPos3();
                    break;
            }
        });
        jiZiSettingPopView.show();
    }
}
