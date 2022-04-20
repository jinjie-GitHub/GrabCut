package com.ltzk.mbsf.popupview;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.adapter.ZiListAuthorAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.ZiListPresenterImpl;
import com.ltzk.mbsf.api.view.ZiListView;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiAuthorBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.StatusBarUtil;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MultipleStatusView;
import com.ltzk.mbsf.widget.MyLoadingImageView;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.qmuiteam.qmui.util.QMUIResHelper;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zy on 2016/3/25.
 * 集字选择单字
 */
public class JiziSelectPopView extends QMUIMyPopup<JiziSelectPopView> implements OnRefreshListener, ZiListView {
    private static final String KEY_JIZI_KIND    = "pop_jizi_kind";
    private static final String KEY_JIZI_TYPE    = "pop_jizi_type";
    private static final String KEY_JIZI_FONT    = "pop_jizi_font";
    private static final String KEY_JIZI_ORDERBY = "pop_jizi_orderby";

    Activity activity;
    @BindView(R.id.rb_orderby_author_1)
    RadioButton rbOrderbyAuthor1;
    @BindView(R.id.rb_orderby_author_2)
    RadioButton rbOrderbyAuthor2;
    @BindView(R.id.rb_orderby_1)
    RadioButton rbOrderby1;
    @BindView(R.id.rb_orderby_2)
    RadioButton rbOrderby2;
    @BindView(R.id.rb_orderby_3)
    RadioButton rbOrderby3;
    private View conentView;
    TipListener tipListener;

    //笔类型
    @BindView(R2.id.rg_kid)
    RadioGroup mRg_kid;

    //类型
    @BindView(R2.id.rg_type)
    RadioGroup mRg_type;


    //最近
    JiziFavPopView mJiziFavPopView;
    @BindView(R2.id.tv_lately)
    TextView mTv_lately;

    View p;
    int xFav = 0, yFav = 0;
    @OnClick(R2.id.tv_lately)
    public void tv_lately(View view) {
        if (mJiziFavPopView == null) {
            mJiziFavPopView = new JiziFavPopView(activity, new JiziFavPopView.TipListener() {
                @Override
                public void ok(ZiBean ziBean) {
                    dismiss();
                    tipListener.ok(ziBean, index);
                }
            });
        }
        mJiziFavPopView.setData(key);
        mJiziFavPopView.showPopupWindow(p, xFav, yFav);
    }

    @BindView(R2.id.tv_author)
    TextView mTv_author;
    @OnClick(R2.id.tv_author)
    public void tv_author(View view) {
        String author = mTv_author.getText().toString();
        if (author.equals("全部")) {
            SearchActivity.safeStart(activity, Constan.Key_type.KEY_AUTHOR, "", "书法家", 2);
            activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        } else {
            selectAuthor("");
        }
    }

    @BindView(R2.id.iv_author_clean)
    ImageView mIv_author_clear;
    @OnClick(R2.id.iv_author_clean)
    public void iv_author_clean(View view) {
        if (mTv_author.getText().toString().equals("全部")) {
            SearchActivity.safeStart(activity, Constan.Key_type.KEY_AUTHOR,"", "书法家",2);
            activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        }else {
            selectAuthor("");
        }
    }

    /**
     * 切换书法家
     *
     * @param name
     */
    private void selectAuthor(String name) {

        /*if(name.equals(mTv_author.getText().toString())){
            return;
        }*/

        if (TextUtils.isEmpty(name)) {
            mZiListAuthorAdapter.setItemNameSelect("");
            mTv_author.setText("全部");
            mIv_author_clear.setImageResource(R.mipmap.arrow_down_small);
            mTv_author.setTextColor(mContext.getResources().getColor(R.color.gray));
            mIv_author_clear.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.gray)));
        } else {
            mZiListAuthorAdapter.setItemNameSelect(name);
            List<ZiAuthorBean> list = mZiListAuthorAdapter.getData();
            int count = list.size();
            for(int i = 0;i<count;i++){
                if(list.get(i).get_name().equals(name)){
                    mRv_author.scrollToPosition(i);
                }
            }
            mTv_author.setText(name);
            mIv_author_clear.setImageResource(R.mipmap.close2);
            mTv_author.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            mIv_author_clear.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorPrimary)));
        }
        //保存上次所选
        SPUtils.put(activity, Constan.Key_type.KEY_AUTHOR_JIZI, name);

        mZiListAuthorAdapter.notifyDataSetChanged();
        if (isInitFinish) {
            onRefresh(mRefresh_layout);
        }
    }

    //字体
    @BindView(R2.id.rg_font)
    RadioGroup mRg_font;

    //作者排序
    @BindView(R2.id.rg_orderby_author)
    RadioGroup mRg_orderby_author;

    //字典排序
    @BindView(R2.id.rg_orderby)
    RadioGroup mRg_orderby;

    //隐藏作者列表按钮
    @BindView(R2.id.iv_author)
    ImageView mIv_author;

    @OnClick(R2.id.iv_author)
    public void iv_author(View view) {
        if (mRv_author.getVisibility() == View.VISIBLE) {
            mIv_author.setImageResource(R.mipmap.list_show);
            authorWidth = 0;
            mRv_author.setVisibility(View.GONE);
            mRg_orderby_author.setVisibility(View.GONE);
        } else {
            mIv_author.setImageResource(R.mipmap.list_hide);
            authorWidth = ViewUtil.dpToPx(80);
            mRv_author.setVisibility(View.VISIBLE);
            mRg_orderby_author.setVisibility(View.VISIBLE);
        }
        changeItemSize(false);
    }

    int widthS = 0, heightS = 0;
    int authorWidth = ViewUtil.dpToPx(80);
    final int spce = ViewUtil.dpToPx(4);
    final int spaceEdge = ViewUtil.dpToPx(4);
    final int tempSize = 150;
    final int tempSizeBig = 250;
    boolean isBigItem = false;

    //切换字典九宫格数量按钮
    @BindView(R2.id.iv_change)
    ImageView mIv_change;

    @OnClick(R2.id.iv_change)
    public void iv_change(View view) {
        changeItemSize(true);
    }

    /**
     * @param isChange 是否改变item大小级别
     */
    private void changeItemSize(boolean isChange) {
        int[] item;
        if (!isChange) {
            isBigItem = !isBigItem;
        }
        if (isBigItem) {
            mIv_change.setImageResource(R.mipmap.item_large);
            isBigItem = false;
            item = ViewUtil.getItemWidth(widthS - authorWidth, tempSize, spce, spaceEdge);
        } else {
            mIv_change.setImageResource(R.mipmap.item_small);
            isBigItem = true;
            item = ViewUtil.getItemWidth(widthS - authorWidth, tempSizeBig, spce, spaceEdge);
        }
        mAdapter.setBigItem(isBigItem);
        mAdapter.setWidth(item[1]);
        mRv_zi.setLayoutManager(new GridLayoutManager(activity, item[0]));
        mAdapter.notifyDataSetChanged();
    }

    //作者列表
    @BindView(R2.id.rv_author)
    RecyclerView mRv_author;
    ZiListAuthorAdapter mZiListAuthorAdapter;
    //字典列表
    @BindView(R2.id.status_view)
    MultipleStatusView mStatus_view;
    @BindView(R2.id.refresh_layout)
    MySmartRefreshLayout mRefresh_layout;
    @BindView(R.id.recyclerView)
    RecyclerView mRv_zi;
    ZiListAdapter mAdapter;

    private boolean isInitFinish;
    public JiziSelectPopView(final Activity activity, TipListener tipListener) {
        super(activity, (int) ViewUtil.getWidth() - ViewUtil.dpToPx(20), ((int) ViewUtil.getHeight() - ViewUtil.dpToPx(20)) / 2);
        widthS = mInitWidth;
        heightS = mInitHeight;
        this.activity = activity;
        this.tipListener = tipListener;
        view(createContentView());
        mStatus_view.setOnRetryClickListener(mRetryClickListener);
        mRefresh_layout.setRunnable(() -> {
            requestBean.addParams("loaded", mAdapter.getData().size());
            presenter.getList(requestBean, false);
        });
        isInitFinish = true;
    }

    /**
     * 点击"空空如也"重新发送请求
     */
    private View.OnClickListener mRetryClickListener = (View v) -> {
        onRefresh(mRefresh_layout);
    };

    /**
     * 初始化
     */
    private View createContentView() {
        LayoutInflater inflater = activity.getLayoutInflater();
        conentView = inflater.inflate(R.layout.widget_jizi_list_select_popview, null);
        // 设置SelectPicPopupWindow的View
        ButterKnife.bind(this, conentView);
        initView();
        shadowElevation(5,0.9f);
        borderColor(activity.getResources().getColor(R.color.whiteSmoke));
        borderWidth(5);
        shadow(true);
        return conentView;
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow(View parent, int gravity, int x, Rect rect) {
        this.p = parent;
        int y = 0;
        if (rect.top > heightS) {
            y = rect.top - heightS - QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_popup_arrow_height);
            preferredDirection(DIRECTION_TOP);
        } else if (ViewUtil.getHeight() - rect.bottom > heightS) {
            y = rect.bottom + QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_popup_arrow_height);
            preferredDirection(DIRECTION_BOTTOM);
        } else if ((rect.top - StatusBarUtil.getStatusBarHeight(activity)) > (ViewUtil.getHeight() - rect.bottom)) {
            y = 0;
            int height = rect.height();
            rect.top = heightS;
            rect.bottom = rect.top + height - StatusBarUtil.getStatusBarHeight(activity);
            preferredDirection(DIRECTION_TOP);
        } else {
            int height = rect.height();
            y = (int) ViewUtil.getHeight() - heightS + StatusBarUtil.getStatusBarHeight(activity);
            rect.bottom = y;
            rect.top = rect.bottom - height;
            preferredDirection(DIRECTION_BOTTOM);
        }
        //计算子popView弹出位置
        yFav = y + ViewUtil.dpToPx(40);
        xFav = ViewUtil.dpToPx(12);

        show(parent,rect);
    }

    /**
     * 显示popupWindow
     */
    public void showPopupWindow2(View parent, Rect rect) {
        this.p = parent;
        int y = 0;
        if (rect.top > heightS) {
            y = rect.top - heightS - QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_popup_arrow_height);
            preferredDirection(DIRECTION_TOP);
        } else if (ViewUtil.getHeight() - rect.bottom > heightS) {
            y = rect.bottom + QMUIResHelper.getAttrDimen(mContext, R.attr.qmui_popup_arrow_height);
            preferredDirection(DIRECTION_BOTTOM);
        } else if ((rect.top - StatusBarUtil.getStatusBarHeight(activity)) > (ViewUtil.getHeight() - rect.bottom)) {
            y = 0;
            int height = rect.height();
            rect.top = heightS;
            rect.bottom = rect.top + height - StatusBarUtil.getStatusBarHeight(activity);
            preferredDirection(DIRECTION_TOP);
        } else {
            int height = rect.height();
            y = (int) ViewUtil.getHeight() - heightS + StatusBarUtil.getStatusBarHeight(activity);
            rect.bottom = y;
            rect.top = rect.bottom - height;
            preferredDirection(DIRECTION_BOTTOM);
        }
        //计算子popView弹出位置
        yFav = y + ViewUtil.dpToPx(40);
        xFav = ViewUtil.dpToPx(12);

        show(parent,rect);
    }

    public interface TipListener {
        void ok(ZiBean ziBean, int index);
    }

    private void initData(){
        if(presenter != null){
            return;
        }

        presenter = new ZiListPresenterImpl();
        presenter.subscribe(this);

        requestBean = new RequestBean();
        mRequestBeanAuthor = new RequestBean();
        setup();
    }

    /**
     * 读取缓存数据，设置View
     */
    private void setup() {
        final String kind = (String) SPUtils.get(activity, KEY_JIZI_KIND, "1");
        requestBean.addParams("kind", kind);
        mRequestBeanAuthor.addParams("kind", kind);
        if (kind.equals("1")) {
            ((RadioButton) mRg_kid.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton) mRg_kid.getChildAt(1)).setChecked(true);
        }

        final String type = (String) SPUtils.get(activity, KEY_JIZI_TYPE, "2");
        requestBean.addParams("type", type);
        mRequestBeanAuthor.addParams("type", type);
        if (type.equals("2")) {
            ((RadioButton) mRg_type.getChildAt(0)).setChecked(true);
        } else if (type.equals("3")) {
            ((RadioButton) mRg_type.getChildAt(1)).setChecked(true);
        } else {
            ((RadioButton) mRg_type.getChildAt(2)).setChecked(true);
        }

        final String font = (String) SPUtils.get(activity, KEY_JIZI_FONT, "楷");
        requestBean.addParams("font", font);
        mRequestBeanAuthor.addParams("font", font);
        for (int i = 0; i < mRg_font.getChildCount(); i++) {
            if (mRg_font.getChildAt(i) instanceof RadioButton) {
                if (((RadioButton) mRg_font.getChildAt(i)).getText().toString().equals(font)) {
                    ((RadioButton) mRg_font.getChildAt(i)).setChecked(true);
                    break;
                }
            }
        }

        final String orderby = (String) SPUtils.get(activity, KEY_JIZI_ORDERBY, "hot");
        requestBean.addParams("orderby", orderby);
        mRequestBeanAuthor.addParams("orderby", "name");
        if (orderby.equals("zuopin")) {
            ((RadioButton) mRg_orderby.getChildAt(2)).setChecked(true);
        } else if (orderby.equals("author")) {
            ((RadioButton) mRg_orderby.getChildAt(1)).setChecked(true);
        } else {
            ((RadioButton) mRg_orderby.getChildAt(0)).setChecked(true);
        }
    }

    public void initView() {
        initData();
        initKidView();
        initZiTypeView();
        initZiFontView();
        initAuthorSortView();
        initSortView();
        initZiView();
        initZiAuthorView();
    }

    private RadioButton mLastSelectItemKind;
    private void initKidView() {
        mRg_kid.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(mLastSelectItemKind!=null){
                    mLastSelectItemKind.getPaint().setFakeBoldText(false);
                }
                mLastSelectItemKind = mRg_kid.findViewById(mRg_kid.getCheckedRadioButtonId());
                mLastSelectItemKind.getPaint().setFakeBoldText(true);

                if (checkedId == R.id.rb_kid_1) {
                    requestBean.addParams("kind", "1");
                    mRequestBeanAuthor.addParams("kind", "1");
                } else if (checkedId == R.id.rb_kid_2) {
                    requestBean.addParams("kind", "2");
                    mRequestBeanAuthor.addParams("kind", "2");
                }
                SPUtils.put(activity, KEY_JIZI_KIND, requestBean.getParam("kind"));
                getAuthor();
            }
        });
        if(mLastSelectItemKind!=null){
            mLastSelectItemKind.getPaint().setFakeBoldText(false);
        }
        mLastSelectItemKind = mRg_kid.findViewById(mRg_kid.getCheckedRadioButtonId());
        mLastSelectItemKind.getPaint().setFakeBoldText(true);
    }

    private RadioButton mLastSelectItemType;
    private void initZiTypeView() {
        mRg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(mLastSelectItemType!=null){
                    mLastSelectItemType.getPaint().setFakeBoldText(false);
                }
                mLastSelectItemType = mRg_type.findViewById(mRg_type.getCheckedRadioButtonId());
                mLastSelectItemType.getPaint().setFakeBoldText(true);


                if (checkedId == R.id.rb_type_1) {
                    requestBean.addParams("type", "2");
                    mRequestBeanAuthor.addParams("type", "2");
                } else if (checkedId == R.id.rb_type_2) {
                    requestBean.addParams("type", "3");
                    mRequestBeanAuthor.addParams("type", "3");
                } else if (checkedId == R.id.rb_type_3) {
                    requestBean.addParams("type", "1");
                    mRequestBeanAuthor.addParams("type", "1");
                }
                SPUtils.put(activity, KEY_JIZI_TYPE, requestBean.getParam("type"));
                getAuthor();
            }
        });
        if(mLastSelectItemType!=null){
            mLastSelectItemType.getPaint().setFakeBoldText(false);
        }
        mLastSelectItemType = mRg_type.findViewById(mRg_type.getCheckedRadioButtonId());
        mLastSelectItemType.getPaint().setFakeBoldText(true);
    }

    private void setBold(TextView view) {
        if (view != null) {
            if (view.getPaint() != null) {
                view.getPaint().setFakeBoldText(true);
            }
        }
    }

    private RadioButton mLastSelectItemFont;
    private void initZiFontView() {
        mRg_font.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(mLastSelectItemFont!=null){
                    mLastSelectItemFont.getPaint().setFakeBoldText(false);
                }
                mLastSelectItemFont = mRg_font.findViewById(mRg_font.getCheckedRadioButtonId());
                mLastSelectItemFont.getPaint().setFakeBoldText(true);

                if (checkedId == R.id.rb_1) {
                    requestBean.addParams("font", "楷");
                    mRequestBeanAuthor.addParams("font", "楷");
                } else if (checkedId == R.id.rb_2) {
                    requestBean.addParams("font", "行");
                    mRequestBeanAuthor.addParams("font", "行");
                } else if (checkedId == R.id.rb_3) {
                    requestBean.addParams("font", "草");
                    mRequestBeanAuthor.addParams("font", "草");
                } else if (checkedId == R.id.rb_4) {
                    requestBean.addParams("font", "隶");
                    mRequestBeanAuthor.addParams("font", "隶");
                } else if (checkedId == R.id.rb_5) {
                    requestBean.addParams("font", "篆");
                    mRequestBeanAuthor.addParams("font", "篆");
                }
                SPUtils.put(activity, KEY_JIZI_FONT, requestBean.getParam("font"));
                getAuthor();
            }
        });
        if(mLastSelectItemFont!=null){
            mLastSelectItemFont.getPaint().setFakeBoldText(false);
        }
        mLastSelectItemFont = mRg_font.findViewById(mRg_font.getCheckedRadioButtonId());
        mLastSelectItemFont.getPaint().setFakeBoldText(true);
    }

    private void initAuthorSortView() {
        setBold(rbOrderbyAuthor1);
        setBold(rbOrderbyAuthor2);
        mRg_orderby_author.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_orderby_author_1) {
                    mRequestBeanAuthor.addParams("orderby", "name");
                } else if (checkedId == R.id.rb_orderby_author_2) {
                    mRequestBeanAuthor.addParams("orderby", "num");
                }
                getAuthor();
            }
        });
    }

    private void initSortView() {
        setBold(rbOrderby1);
        setBold(rbOrderby2);
        setBold(rbOrderby3);
        mRg_orderby.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_orderby_1) {
                    requestBean.addParams("orderby", "hot");
                } else if (checkedId == R.id.rb_orderby_2) {
                    requestBean.addParams("orderby", "author");
                } else if (checkedId == R.id.rb_orderby_3) {
                    requestBean.addParams("orderby", "zuopin");
                }
                SPUtils.put(activity, KEY_JIZI_ORDERBY, requestBean.getParam("orderby"));
                onRefresh(mRefresh_layout);
            }
        });
    }

    private void initZiAuthorView() {
        mZiListAuthorAdapter = new ZiListAuthorAdapter(activity);
        mZiListAuthorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final String name = mZiListAuthorAdapter.getItem(position).get_name();
                selectAuthor((mTv_author.getText().toString().equals("全部") || !mTv_author.getText().toString().equals(name)) ? name : "");
            }
        });
        //读取上次选的书法家
        selectAuthor((String) SPUtils.get(activity,Constan.Key_type.KEY_AUTHOR_JIZI,""));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRv_author.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity, R.drawable.shape_divider_line));
        mRv_author.addItemDecoration(dividerItemDecoration);
        mRv_author.setAdapter(mZiListAuthorAdapter);
    }

    private void initZiView() {
        mRefresh_layout.setOnRefreshListener(this);
        int[] item = ViewUtil.getItemWidth(widthS - authorWidth - ViewUtil.dpToPx(12), tempSize, spce, spaceEdge);
        mAdapter = new ZiListAdapter(activity, item[1]);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                if (XClickUtil.isFastDoubleClick(mRv_zi)) {
                    return;
                }
                ZiBean bean = mAdapter.getData().get(position);
                bean.setFirst(mZiBean.isFirst());
                dismiss();
                tipListener.ok(bean, index);

                //收藏
                RequestBean requestBean = new RequestBean();
                requestBean.addParams("gid",bean.get_id());
                //加入收藏
                presenter.glyph_fav_aid(requestBean,false);
            }
        });
        mRv_zi.setAdapter(mAdapter);
        mRv_zi.setLayoutManager(new GridLayoutManager(activity, item[0]));
        mRv_zi.addItemDecoration(new SpaceItemDecoration(spce, spaceEdge, 0, SpaceItemDecoration.GRIDLAYOUT));
    }


    ZiBean mZiBean;
    String key = "";
    int index = 0;

    public void setData(ZiBean bean, int index) {
        this.index = index;
        this.key = bean.get_key();
        mZiBean = bean;
        requestBean.addParams("key", key);
        requestBean.addParams("hanzi", key);
        mRequestBeanAuthor.addParams("hanzi", key);
        mRequestBeanAuthor.addParams("key", key);
        getAuthor();
    }

    /**
     * 设置选中的作者
     *
     * @param author
     */
    public void setAuthor(String author) {
        selectAuthor(author);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mRefresh_layout.finishRefresh(false);
        mAdapter.setNewData(null);

        mRefresh_layout.setLoaded(0);
        requestBean.addParams("loaded", 0);
        requestBean.addParams("author", mZiListAuthorAdapter.getItemNameSelect());
        presenter.getList(requestBean, true);
    }

    private void getAuthor() {
        presenter.glyph_authors(mRequestBeanAuthor, false);
    }

    ZiListPresenterImpl presenter;
    RequestBean requestBean;
    RequestBean mRequestBeanAuthor;

    @Override
    public void showProgress() {
        mStatus_view.showLoading();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
        mRefresh_layout.setVisibility(View.VISIBLE);
        mStatus_view.setVisibility(View.VISIBLE);
        mRv_zi.setVisibility(View.GONE);
        if (mStatus_view != null) {
            mStatus_view.resetStatus();
        }
    }

    /**
     * 允许用户取消api请求
     */
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

    @Override
    public void glyph_authorsSucess(RowBean<ZiAuthorBean> tData) {
        if (tData != null && tData.getList() != null && tData.getList().size() > 0) {
            mRv_author.setNestedScrollingEnabled(false);
            mZiListAuthorAdapter.setItemNameSelect("");
            mZiListAuthorAdapter.setNewData(tData.getList());
            int size = tData.getList().size();
            for (int i = 0; i < size; i++) {
                if (tData.getList().get(i).get_name().equals(mTv_author.getText().toString())) {
                    mZiListAuthorAdapter.setItemNameSelect(tData.getList().get(i).get_name());
                    mZiListAuthorAdapter.notifyDataSetChanged();
                    break;
                }
            }
        } else {
            noAuthors();
        }
        onRefresh(mRefresh_layout);
    }

    @Override
    public void glyph_authorsFail(String string) {
        noAuthors();
        onRefresh(mRefresh_layout);
    }

    @Override
    public void author_fav_unfav() {
        //do nothing
    }

    private void noAuthors() {
        mStatus_view.showEmpty();
        mRv_author.setNestedScrollingEnabled(false);
        mZiListAuthorAdapter.setItemNameSelect("");
        mZiListAuthorAdapter.setNewData(null);
    }

    @Override
    public void loadDataSuccess(RowBean<ZiBean> tData) {
        if (tData != null && tData.getList() != null && tData.getList().size() > 0) {
            mRefresh_layout.setTotal(tData.getTotal());
            mStatus_view.showContent();
            mAdapter.addData(tData.getList());

            if (mAdapter.getData().size() >= tData.getTotal()) {
                mRefresh_layout.setEnableLoadMore(false);
            } else {
                mRefresh_layout.setEnableLoadMore(true);
            }
        } else {
            if (tData == null || tData.getTotal() == 0) {//刷新
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
        ToastUtil.showToast(activity, errorMsg + "");
    }

    private final class ZiListAdapter extends BaseQuickAdapter<ZiBean, BaseViewHolder> {

        /**
         * 是否是印章
         */
        boolean isYin = false;
        public void setYin(boolean yin) {
            isYin = yin;
        }

        /**
         * 当前选中的作者
         */
        String mAuthorSelect;
        public void setmAuthorSelect(String mAuthorSelect) {
            this.mAuthorSelect = mAuthorSelect;
        }

        /**
         * 是否大图
         */
        boolean isBigItem;
        public void setBigItem(boolean bigItem) {
            isBigItem = bigItem;
        }

        Activity activity;
        Fragment fragment;
        int width;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public ZiListAdapter(Fragment fragment,int width) {
            super(R.layout.adapter_zi_home);
            this.fragment = fragment;
            this.activity = fragment.getActivity();
            this.width = width;
        }

        public ZiListAdapter(Activity activity,int width) {
            super(R.layout.adapter_zi_home);
            this.activity = activity;
            this.width = width;
        }

        @Override
        protected void convert(BaseViewHolder holder, ZiBean bean) {
            holder.setTextColor(R.id.tv_name, activity.getResources().getColor(bean._video_count > 0 ? R.color.orange : R.color.black));

            TextView tv_name = holder.getView(R.id.tv_name);
            LinearLayout linearLayout = holder.getView(R.id.lay_bottom);
            LinearLayout.LayoutParams layoutParams_lin = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            ((TextView)holder.getView(R.id.tv_name)).setSingleLine(true);
            if(isBigItem){//大图
                tv_name.setTextSize(11);
                layoutParams_lin.height = ViewUtil.dpToPx(28);
            }else {//小图
                tv_name.setTextSize(9);
                layoutParams_lin.height = ViewUtil.dpToPx(20);
            }
            linearLayout.setLayoutParams(layoutParams_lin);

            if(isYin){
                holder.setText(R.id.tv_name,bean.get_hanzi()+"");
            }else {
                holder.setText(R.id.tv_name,bean.get_from()+"");
            }
            holder.setGone(R.id.tv_author,true);

            MyLoadingImageView myLoadingImageView = holder.getView(R.id.iv_thumb_url);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)myLoadingImageView.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = width;
            myLoadingImageView.setLayoutParams(layoutParams);

            if (TextUtils.isEmpty(bean.get_color_image())) {
                View miView = holder.getView(R.id.miView);
                miView.setLayoutParams(layoutParams);
                holder.setVisible(R.id.miView, true);
            } else {
                holder.setVisible(R.id.miView, false);
            }

            if(fragment == null){
                myLoadingImageView.setData(activity,bean.getUrlThumb(),-1);
            }else {
                myLoadingImageView.setData(fragment,bean.getUrlThumb(),-1);
            }
        }
    }
}
