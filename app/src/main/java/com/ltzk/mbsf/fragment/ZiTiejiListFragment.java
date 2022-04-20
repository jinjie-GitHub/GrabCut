package com.ltzk.mbsf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.AuthorListActivity;
import com.ltzk.mbsf.activity.BigZitieActivity;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.activity.ZitieActivity;
import com.ltzk.mbsf.activity.ZitieListActivity;
import com.ltzk.mbsf.activity.ZitieSoucangListActivity;
import com.ltzk.mbsf.activity.ZitieZuopingListActivity;
import com.ltzk.mbsf.adapter.ZitiejiListAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.ZitiejiListPresenterImpl;
import com.ltzk.mbsf.api.view.ZitiejiListView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.AuthorFamous;
import com.ltzk.mbsf.bean.Bus_LoginOut;
import com.ltzk.mbsf.bean.Bus_LoginSucces;
import com.ltzk.mbsf.bean.Bus_UserUpdate;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import com.ltzk.mbsf.utils.GlideUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
 * 描述：
 * 作者： on 2018/8/18 14:20
 * 邮箱：499629556@qq.com
 */
public class ZiTiejiListFragment extends MyBaseFragment<ZitiejiListView,ZitiejiListPresenterImpl> implements OnRefreshListener,ZitiejiListView {

    public static final int REQ_SEARCH_KEY = 1;
    @BindView(R2.id.tv_key)
    TextView tv_key;
    @OnClick(R2.id.tv_key)
    public void tv_key(View view){
        SearchActivity.safeStart(this, Constan.Key_type.KEY_ZITIE,"", "",REQ_SEARCH_KEY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String key = data.getStringExtra(SearchActivity.KEY);
            if (requestCode == REQ_SEARCH_KEY) {
                startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type",0).putExtra("key",key));
            }
        }
    }

    @OnClick(R2.id.tv_all)
    public void tv_all(View view){
        startActivity(new Intent(activity, AuthorListActivity.class));
    }

    @BindView(R2.id.tv_nodata)
    TextView tv_nodata;
    @OnClick(R2.id.tv_nodata)
    public void tv_nodata(View view){
        onRefresh(swipeToLoadLayout);
    }
    @BindView(R2.id.tv_error)
    TextView tv_error;
    @OnClick(R2.id.tv_error)
    public void tv_error(View view){
        onRefresh(swipeToLoadLayout);
    }


    @BindView(R2.id.swipeToLoadLayout)
    SmartRefreshLayout swipeToLoadLayout;
    @BindView(R2.id.swipe_target)
    RecyclerView recyclerView;
    ZitiejiListAdapter adapter;

    private int column = 3;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_zitieji_list;
    }

    @Override
    protected void initView(View rootView) {
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void requestData() {
        super.requestData();
        presenter.getList(requestBean, true);
        presenter.dylist();
        presenter.author_famous();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void cancel() {
        super.cancel();
        disimissProgress();
    }

    public void initView() {
        initDynastyAndAuthor();
        requestBean = new RequestBean();
        requestBean.addParams("_token", MainApplication.getInstance().getToken());
        swipeToLoadLayout.setEnableLoadMore(false);
        swipeToLoadLayout.setOnRefreshListener(this);

        adapter = new ZitiejiListAdapter(this);
        column = adapter.calculate(activity);
        requestBean.addParams("colnum", column);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, column);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            int space = ViewUtil.dpToPx(5);
            private Paint mPaint;

            {
                this.mPaint = new Paint();
                mPaint.setColor(getResources().getColor(R.color.whiteSmoke));
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view);
                RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(view);
                if (viewHolder.getItemViewType() == 1) {
                    if (position == 0) {
                        outRect.top = ViewUtil.dpToPx(8);
                    } else {
                        outRect.top = ViewUtil.dpToPx(16);
                    }
                    outRect.bottom = space;
                } else {
                    outRect.left = space / 2;
                    outRect.right = space / 2;
                    outRect.bottom = space;
                }
            }

            // 在子视图上设置绘制范围，并绘制内容
            // 绘制图层在ItemView以下，所以如果绘制区域与ItemView区域相重叠，会被遮挡
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                super.onDraw(c, parent, state);
                // 获取RecyclerView的Child view的个数
                int childCount = parent.getChildCount();
                // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
                for (int i = 0; i < childCount; i++) {
                    View child = parent.getChildAt(i);
                    RecyclerView.ViewHolder viewHolder = parent.getChildViewHolder(child);
                    if (viewHolder.getItemViewType() == 1) {
                        int left = 0;
                        int top = child.getTop() - ViewUtil.dpToPx(16);
                        int right = child.getRight();
                        int bottom = child.getTop();
                        c.drawRect(left, top, right, bottom, mPaint);
                    }
                }
                View child = parent.getChildAt(childCount - 1);
                if (child != null) {
                    int left = 0;
                    int top = child.getBottom() + space;
                    int right = (int) ViewUtil.getWidth();
                    int bottom = top + ViewUtil.dpToPx(8);
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        });

        recyclerView.setAdapter(adapter);
        adapter.setZitieHomeCallBack(new ZitiejiListAdapter.ZitieHomeCallBack() {
            @Override
            public void zuopin(ZitieHomeBean.ListBeanX.ListBean bean) {
                startActivity(new Intent(activity, ZitieListActivity.class).putExtra("zid",bean.get_zuopin_id()).putExtra("title",bean.get_name()));
            }

            @Override
            public void xuanji(ZitieHomeBean.ListBeanX.ListBean bean) {
                startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type",2).putExtra("gid",bean.get_gallery_id()).putExtra("title",bean.get_title()));
            }

            @Override
            public void author(ZitieHomeBean.ListBeanX.ListBean bean) {
                startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type",1).putExtra("name",bean.get_name()).putExtra("title",bean.get_dynasty()+" • "+bean.get_name()));
            }

            @Override
            public void zitie(ZitieHomeBean.ListBeanX.ListBean bean) {
                if(bean.get_hd()!=null && bean.get_hd().equals("1")){
                    if(MainApplication.getInstance().isCanGo(activity,recyclerView,bean.get_free(),requestBean.getParams())){
                        startActivityFromBottom(new Intent(activity, BigZitieActivity.class).putExtra("zid",bean.get_zitie_id()));
                    }
                }else {
                    startActivityFromBottom(new Intent(activity, ZitieActivity.class).putExtra("zid",bean.get_zitie_id()).putExtra("index",bean.get_focus_page()));
                }
            }


            @Override
            public void more_soucang() {
                startActivity(new Intent(activity, ZitieSoucangListActivity.class));
            }

            @Override
            public void more(String title,String gid) {
                startActivity(new Intent(activity, ZitieZuopingListActivity.class).putExtra("type",2).putExtra("gid",gid).putExtra("title",title));
            }

            @Override
            public void more_auhor() {
                startActivity(new Intent(activity, AuthorListActivity.class));            }
        });
    }


    //登录被踢
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginOut messageEvent) {
        onRefresh(swipeToLoadLayout);
    }

    //登录成功
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginSucces messageEvent) {
        onRefresh(swipeToLoadLayout);
    }

    //用户信息更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_UserUpdate messageEvent) {
        onRefresh(swipeToLoadLayout);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        processRefresh();
        requestBean.addParams("_token", MainApplication.getInstance().getToken());
        requestData();
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
    protected ZitiejiListPresenterImpl getPresenter() {
        return new ZitiejiListPresenterImpl();
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
    List<ZitieHomeBean.ListBeanX.ListBeanAndType> list = new ArrayList<>();
    @Override
    public void loadDataSuccess(ZitieHomeBean tData) {
        list.clear();
        if(tData!=null && tData.getList()!=null && !tData.getList().isEmpty()){
            //有数据
            tv_nodata.setVisibility(View.GONE);
            tv_error.setVisibility(View.GONE);

            int adaterType = 1;
            ZitieHomeBean.ListBeanX.ListBeanAndType listBeanAndType;
            for(int i = 0;i<tData.getList().size();i++){
                ZitieHomeBean.ListBeanX listBeanX = tData.getList().get(i);
                List<ZitieHomeBean.ListBeanX.ListBeanAndType> listBeanAndTypes = listBeanX.get_list();
                listBeanAndType = new ZitieHomeBean().new ListBeanX().new ListBeanAndType();
                listBeanAndType.setTitle(listBeanX.get_title());
                listBeanAndType.set_type(listBeanX.get_gallery_id());
                adaterType = 1;
                listBeanAndType.setAdapterType(adaterType);
                list.add(listBeanAndType);
                int size = listBeanAndTypes.size();
                /*if(size>6){
                    size = 6;
                }*/

                for (int j = 0; j < size; j++) {
                    if ((j + 1) % column == 0) {
                        adaterType = 3;
                    } else {
                        adaterType = 2;
                    }
                    listBeanAndType = listBeanAndTypes.get(j);
                    listBeanAndType.setAdapterType(adaterType);
                    list.add(listBeanAndType);
                }
            }
        }else {
            //无数据
            tv_nodata.setVisibility(View.VISIBLE);
            tv_error.setVisibility(View.GONE);
        }
        adapter.setList(list);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void loadDataError(String errorMsg) {
        tv_nodata.setVisibility(View.GONE);
        tv_error.setVisibility(View.VISIBLE);
        if (isVisible) {
            ToastUtil.showToast(activity, errorMsg);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 1.将需要悬浮的layout放到CollapsingToolbarLayout之外，AppBarLayout之内
     * 2.将CollapsingToolbarLayout的app:layout_scrollFlags设置为scroll
     * 3.给滚动的NestedScrollView设置 app:layout_behavior="@String/appbar_scrolling_view_behavior"
     */
    @BindView(R.id.rv_dynasty)
    RecyclerView rv_dynasty;
    DynastyAdapter dynastyAdapter;

    @BindView(R.id.rv_author)
    RecyclerView rv_author;
    AuthorAdapter authorAdapter;

    @Override
    public void setAuthorFamous(AuthorFamous data) {
        if (data == null) {
            return;
        }
        if (authorAdapter.getData().size() < data.total) {
            authorAdapter.addData(data.list);
        }
    }

    @Override
    public void setDynastyList(List<String> data) {
        if (data == null) {
            return;
        }
        data.remove(DynastyAdapter.Korea);
        data.remove(DynastyAdapter.Japan);
        dynastyAdapter.setNewData(data);
    }

    private void initDynastyAndAuthor() {
        dynastyAdapter = new DynastyAdapter();
        authorAdapter = new AuthorAdapter();

        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_dynasty.setLayoutManager(layoutManager);
        rv_dynasty.setAdapter(dynastyAdapter);

        LinearLayoutManager layoutManager2 = new LinearLayoutManager(activity);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_author.setLayoutManager(layoutManager2);
        rv_author.setAdapter(authorAdapter);

        dynastyAdapter.setOnItemClickListener((adapter, view, position) -> {
            AuthorListActivity.safeStart(activity, dynastyAdapter.getData().get(position));
        });

        authorAdapter.setOnItemClickListener((adapter, view, position) -> {
            startActivity(new Intent(activity, ZitieZuopingListActivity.class)
                    .putExtra("type", 0)
                    .putExtra("key", authorAdapter.getData().get(position)._name));
        });
    }

    private static class DynastyAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
        private static final String Korea = "朝鲜";
        private static final String Japan = "日本";

        public DynastyAdapter() {
            super(R.layout.item_dynasty_list);
        }

        @Override
        protected void convert(BaseViewHolder holder, String bean) {
            if (bean.length() == 1) {
                holder.setText(R.id.dynasty, bean + "朝");
            } else {
                holder.setText(R.id.dynasty, bean);
            }
        }
    }

    private static class AuthorAdapter extends BaseQuickAdapter<AuthorFamous.Author, BaseViewHolder> {
        public AuthorAdapter() {
            super(R.layout.item_author_list);
        }

        @Override
        protected void convert(BaseViewHolder holder, AuthorFamous.Author bean) {
            GlideUtils.loadCircleImage1(bean._head, holder.getView(R.id.logo));
            holder.setText(R.id.author, bean._name);
            holder.setText(R.id.data, "-" + bean._dynasty + "-");
            holder.setText(R.id.achieve, "共收录" + bean._zpnum + "篇作品");
        }
    }
}