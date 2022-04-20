package com.ltzk.mbsf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.classic.common.MultipleStatusView;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.GlyphDetailActivity;
import com.ltzk.mbsf.activity.LoginTypeActivity;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.adapter.ZiListAdapter;
import com.ltzk.mbsf.adapter.ZiListAuthorAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.ZiListPresenterImpl;
import com.ltzk.mbsf.api.view.ZiListView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.Bus_LoginOut;
import com.ltzk.mbsf.bean.DetailsBean;
import com.ltzk.mbsf.bean.KindType;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZiAuthorBean;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.popupview.TipPopView;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.SpaceItemDecoration;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
import com.ltzk.mbsf.widget.UnderLineRadioButton;
import com.qmuiteam.qmui.widget.popup.QMUINormalPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopup;
import com.qmuiteam.qmui.widget.popup.QMUIPopups;
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
 * update on 2021/6/16
 */
public class ZiListFragment extends MyBaseFragment<ZiListView, ZiListPresenterImpl> implements OnRefreshListener, ZiListView {
    public static final int REQ_SEARCH_KEY = 1;
    public static final int REQ_SEARCH_AUTHOR = 2;

    @OnClick(R2.id.iv_back)
    public void iv_back(View view) {
        mZiListFragmentCallBack.onBack();
    }

    @BindView(R2.id.tv_key)
    TextView tv_key;
    @OnClick(R2.id.tv_key)
    public void tv_key(View view) {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_ZI, "", "", REQ_SEARCH_KEY);
    }

    //书法家搜索
    @BindView(R2.id.lay_author)
    LinearLayout lay_author;
    @BindView(R2.id.tv_author)
    TextView mTv_author;
    @OnClick(R2.id.tv_author)
    public void tv_author(View view) {
        if (mTv_author.getText().toString().equals("书法家")) {
            SearchActivity.safeStart(this, Constan.Key_type.KEY_AUTHOR, "", "书法家", REQ_SEARCH_AUTHOR);
            activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        } else {
            setAuthor("", true);
        }
    }

    @BindView(R2.id.iv_author_clean)
    ImageView mIv_author_clear;
    @OnClick(R2.id.iv_author_clean)
    public void iv_author_clean(View view) {
        if (mTv_author.getText().toString().equals("书法家")) {
            SearchActivity.safeStart(this, Constan.Key_type.KEY_AUTHOR, "", "书法家", REQ_SEARCH_AUTHOR);
            activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
        } else {
            setAuthor("", true);
        }
    }

    /**
     * 切换书法家
     */
    private void setAuthor(String name, boolean isRefresh) {
        if (TextUtils.isEmpty(name)) {
            mTv_author.setText("书法家");
            mIv_author_clear.setImageResource(R.mipmap.arrow_down_small);
            mIv_author_clear.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.black)));
            mTv_author.setTextColor(ContextCompat.getColor(activity, R.color.black));
            mTv_author.getPaint().setFakeBoldText(false);
        } else {
            mTv_author.setText(name);
            mIv_author_clear.setImageResource(R.mipmap.close2);
            mIv_author_clear.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.colorPrimary)));
            mTv_author.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
            mTv_author.getPaint().setFakeBoldText(true);
        }

        //保存上次所选
        SPUtils.put(activity, Constan.Key_type.KEY_AUTHOR_ZILIST + mKind, name);

        if (isRefresh) {
            selectAuthor(mTv_author.getText().toString());
            onRefresh(mRefresh_layout);
        }
    }

    //类型选中
    @BindView(R2.id.lay_type)
    LinearLayout lay_type;
    @BindView(R2.id.tv_type)
    TextView mTv_type;
    private QMUIPopup mNormalPopup;
    @OnClick(R2.id.tv_type)
    public void tv_type(View view) {
        RadioGroup typeView = (RadioGroup) LayoutInflater.from(activity).inflate(R.layout.adapter_item_type_pop, null);
        final KindType kindType = KindType.getKindTypeByKind(mKind);
        if (kindType == KindType.YINGBI) {
            typeView.findViewById(R.id.tv_2).setVisibility(View.GONE);
        }
        RadioGroup.OnCheckedChangeListener listener = (RadioGroup group, int checkedId) -> {
            final String text = ((RadioButton) typeView.findViewById(checkedId)).getText().toString();
            mTv_type.setText(text);
            switch (text) {
                case "真迹":
                    requestBean.addParams("type", 2);
                    mRequestBeanAuthor.addParams("type", 2);
                    mAdapter.setYin(false);
                    break;
                case "字典":
                    requestBean.addParams("type", 3);
                    mRequestBeanAuthor.addParams("type", 3);
                    mAdapter.setYin(false);
                    break;
                case "字库":
                    requestBean.addParams("type", 1);
                    mRequestBeanAuthor.addParams("type", 1);
                    mAdapter.setYin(true);
                    break;
            }
            SPUtils.put(activity, Constan.Key_type.KEY_ZIDIAN_TYPE + mKind, requestBean.getParam("type"));
            search();
            if (mNormalPopup != null) {
                mNormalPopup.dismiss();
            }
        };
        final int type = (int) requestBean.getParam("type");
        if (type == 3) {
            ((RadioButton) typeView.findViewById(R.id.tv_2)).setChecked(true);
        } else if (type == 1) {
            ((RadioButton) typeView.findViewById(R.id.tv_3)).setChecked(true);
        } else {
            ((RadioButton) typeView.findViewById(R.id.tv_1)).setChecked(true);
        }
        typeView.setOnCheckedChangeListener(listener);
        mNormalPopup = QMUIPopups.popup(activity, ViewUtil.dpToPx(88))
                .view(typeView)
                .bgColor(ContextCompat.getColor(activity, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(activity, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(ViewUtil.dpToPx(2))
                .offsetYIfBottom(ViewUtil.dpToPx(-8))
                .offsetX(ViewUtil.dpToPx(-4))
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .preferredDirection(QMUIPopup.DIRECTION_BOTTOM)
                .shadow(true)
                .shadowElevation(10, 1f)
                .arrow(true)
                .arrowSize(40, 22);
        mNormalPopup.show(mTv_type);
    }

    //字体
    @BindView(R2.id.rg_font)
    RadioGroup mRg_font;

    //线
    @BindView(R2.id.view_line)
    View view_line;

    //排序
    @BindView(R2.id.lay_sort)
    LinearLayout lay_sort;

    //作者排序
    @BindView(R2.id.rg_orderby_author)
    RadioGroup mRg_orderby_author;

    //字典排序
    @BindView(R2.id.rg_orderby)
    RadioGroup mRg_orderby;

    //是否显示作者栏
    private boolean mAuthorListShow = false;
    private boolean isBigItem = true;

    //隐藏作者列表按钮
    @BindView(R2.id.iv_author)
    ImageView mIv_author;
    @OnClick(R2.id.iv_author)
    public void iv_author(View view) {
        mAuthorListShow = !mAuthorListShow;
        changeShowAuthorList(mAuthorListShow);
        changeItemSize(false);
        SPUtils.put(activity, "AutorListShow" + mKind, mAuthorListShow);
    }

    //切换字典九宫格数量按钮
    @BindView(R2.id.iv_change)
    ImageView mIv_change;
    @OnClick(R2.id.iv_change)
    public void iv_change(View view) {
        changeItemSize(true);
        SPUtils.put(activity, "isBigItem" + mKind, isBigItem);
    }

    /**
     * 是否显示作者列表
     */
    private int authorWidth = 0;
    private void changeShowAuthorList(boolean isShow) {
        if (isShow) {
            mIv_author.setImageResource(R.mipmap.list_hide);
            authorWidth = ViewUtil.dpToPx(81);
            mRv_author.setVisibility(View.VISIBLE);
            mRg_orderby_author.setVisibility(View.VISIBLE);
        } else {
            mIv_author.setImageResource(R.mipmap.list_show);
            authorWidth = 0;
            mRv_author.setVisibility(View.GONE);
            mRg_orderby_author.setVisibility(View.GONE);
        }
    }

    final int space = ViewUtil.dpToPx(3);
    final int spaceEdge = ViewUtil.dpToPx(4);
    final int tempSize = ViewUtil.dpToPx(50);
    final int tempSizeBig = ViewUtil.dpToPx(100);
    private void changeItemSize(boolean isChange) {
        int[] item;
        if (!isChange) {
            isBigItem = !isBigItem;
        }
        if (isBigItem) {
            mIv_change.setImageResource(R.mipmap.item_large);
            isBigItem = false;
            item = ViewUtil.getItemWidth((int) ViewUtil.getWidth() - authorWidth, tempSize, space, spaceEdge);
        } else {
            mIv_change.setImageResource(R.mipmap.item_small);
            isBigItem = true;
            item = ViewUtil.getItemWidth((int) ViewUtil.getWidth() - authorWidth, tempSizeBig, space, spaceEdge);
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

    @BindView(R2.id.recyclerView)
    RecyclerView mRv_zi;
    ZiListAdapter mAdapter;

    @BindView(R.id.tagLayout)
    RelativeLayout mTagLayout;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_zi_list;
    }

    //登录被踢
    private TipPopView tipPopView;
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onShowMessageEvent(Bus_LoginOut messageEvent) {
        if (!messageEvent.getFlag().equals("glyph_query")) {
            return;
        }
        if (tipPopView == null) {
            tipPopView = new TipPopView(activity, "请登录", messageEvent.getMsg() + "", "登录", new TipPopView.TipListener() {
                @Override
                public void ok() {
                    startActivity(new Intent(activity, LoginTypeActivity.class));
                }
            });
        }
        tipPopView.showAtView(mRefresh_layout);
        mRefresh_layout.finishLoadMore();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            String condition = data.getStringExtra(SearchActivity.KEY);
            if (requestCode == REQ_SEARCH_AUTHOR) {
                setAuthor(condition, true);
            } else if (requestCode == REQ_SEARCH_KEY) {
                search(condition, mKind, true);
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initView(View rootView) {
        EventBus.getDefault().register(this);

        initZiView();
        initZiAuthorView();
        initZiFontView();
        initAuthorSortView();
        initSortView();

        mRefresh_layout.setRunnable(() -> {
            requestBean.addParams("loaded", mRefresh_layout.getLoaded());
            presenter.getList(requestBean, false);
        });
    }

    /**
     * 读取缓存数据，设置View
     */
    private void setup() {
        //读取上次选的书法家
        //final int pos = (int) SPUtils.get(activity, Constan.Key_type.KEY_ZIDIAN_TAB_POS, 0);
        //mKind = KindType.values()[pos].getKind();
        String name = (String) SPUtils.get(activity, Constan.Key_type.KEY_AUTHOR_ZILIST + mKind, "");
        setAuthor(name, false);

        final int type = (int) SPUtils.get(activity, Constan.Key_type.KEY_ZIDIAN_TYPE + mKind, 2);
        requestBean.addParams("type", type);
        mRequestBeanAuthor.addParams("type", type);
        if (type == 3) {
            mTv_type.setText("字典");
        } else if (type == 1) {
            mTv_type.setText("字库");
        } else {
            mTv_type.setText("真迹");
        }

        final String font = (String) SPUtils.get(activity, Constan.Key_type.KEY_ZIDIAN_FONT + mKind, "楷");
        requestBean.addParams("font", font);
        mRequestBeanAuthor.addParams("font", font);
        for (int i = 0; i < mRg_font.getChildCount(); i++) {
            if (mRg_font.getChildAt(i) instanceof RadioButton) {
                if (((RadioButton) mRg_font.getChildAt(i)).getText().toString().equals(font)) {
                    ((UnderLineRadioButton) mRg_font.getChildAt(i)).setChecked(true);
                    break;
                }
            }
        }

        final String orderby = (String) SPUtils.get(activity, Constan.Key_type.KEY_ZIDIAN_ORDERBY + mKind, "hot");
        requestBean.addParams("orderby", orderby);
        mRequestBeanAuthor.addParams("orderby", "name");
        if (orderby.equals("zuopin")) {
            ((UnderLineRadioButton) mRg_orderby.getChildAt(2)).setChecked(true);
        } else if (orderby.equals("author")) {
            ((UnderLineRadioButton) mRg_orderby.getChildAt(1)).setChecked(true);
        } else if (orderby.equals("video")) {
            ((UnderLineRadioButton) mRg_orderby.getChildAt(3)).setChecked(true);
        } else {
            ((UnderLineRadioButton) mRg_orderby.getChildAt(0)).setChecked(true);
        }
    }

    private void initZiView() {
        mRefresh_layout.setOnRefreshListener(this);
        int[] item = ViewUtil.getItemWidth((int) ViewUtil.getWidth() - authorWidth, tempSizeBig, space, spaceEdge);
        mAdapter = new ZiListAdapter(this, item[1]);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int position) {
                if (XClickUtil.isFastDoubleClick(mRv_zi)) {
                    return;
                }
                ZiBean bean = mAdapter.getData().get(position);
                final List<DetailsBean> list = new ArrayList<>();
                for (ZiBean glyphsListBean : mAdapter.getData()) {
                    if (!TextUtils.isEmpty(glyphsListBean.get_id())) {
                        list.add(DetailsBean.newDetails(glyphsListBean.get_id(), glyphsListBean.get_hanzi()));
                    }
                }
                GlyphDetailActivity.safeStart(activity, bean.get_id(), list);
            }
        });
        mRv_zi.setAdapter(mAdapter);
        mRv_zi.setLayoutManager(new GridLayoutManager(activity, item[0]));
        mRv_zi.addItemDecoration(new SpaceItemDecoration(space, spaceEdge, 0, SpaceItemDecoration.GRIDLAYOUT));
        mStatus_view.setOnRetryClickListener(mRetryClickListener);
    }

    private void initZiAuthorView() {
        mZiListAuthorAdapter = new ZiListAuthorAdapter(this);
        mZiListAuthorAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                final String name = mZiListAuthorAdapter.getItem(position).get_name();
                setAuthor((mTv_author.getText().toString().equals("书法家") || !mTv_author.getText().toString().equals(name)) ? name : "", true);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRv_author.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(activity, R.drawable.shape_divider_line));
        mRv_author.addItemDecoration(dividerItemDecoration);
        mRv_author.setAdapter(mZiListAuthorAdapter);
        mZiListAuthorAdapter.setOnItemLongClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            showAuthorFavState(view, mZiListAuthorAdapter.getItem(position));
            return false;
        });
    }

    private void initZiFontView() {
        mRg_font.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
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
                } else if (checkedId == R.id.rb_6) {
                    requestBean.addParams("font", "印");
                    mRequestBeanAuthor.addParams("font", "印");
                    mAdapter.setYin(true);
                }
                SPUtils.put(activity, Constan.Key_type.KEY_ZIDIAN_FONT + mKind, requestBean.getParam("font"));
                search();
            }
        });
    }

    private void initAuthorSortView() {
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
        mRg_orderby.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_orderby_1) {
                    requestBean.addParams("orderby", "hot");
                } else if (checkedId == R.id.rb_orderby_2) {
                    requestBean.addParams("orderby", "author");
                } else if (checkedId == R.id.rb_orderby_3) {
                    requestBean.addParams("orderby", "zuopin");
                } else if (checkedId == R.id.rb_orderby_4) {
                    requestBean.addParams("orderby", "video");
                }
                SPUtils.put(activity, Constan.Key_type.KEY_ZIDIAN_ORDERBY + mKind, requestBean.getParam("orderby"));
                onRefresh(mRefresh_layout);
            }
        });
    }

    private void showAuthorFavState(View anchor, ZiAuthorBean bean) {
        final TextView itemView = new TextView(activity);
        final boolean isFav = bean.isFav();
        itemView.setText(isFav ? "取消收藏" : "收藏作者");
        itemView.setTextSize(14);
        itemView.setPadding(30, 16, 30, 16);
        itemView.setTextColor(ContextCompat.getColor(activity, isFav ? R.color.red : R.color.colorPrimary));
        final QMUIPopup popup = QMUIPopups.popup(activity)
                .view(itemView)
                .bgColor(ContextCompat.getColor(activity, R.color.whiteSmoke))
                .borderColor(ContextCompat.getColor(activity, R.color.colorLine))
                .borderWidth(ViewUtil.dpToPx(1))
                .radius(8)
                .animStyle(QMUINormalPopup.ANIM_GROW_FROM_CENTER)
                .offsetYIfBottom(ViewUtil.dpToPx(0))
                .offsetX(0)
                .preferredDirection(QMUIPopup.DIRECTION_TOP)
                .shadow(true)
                .arrow(true)
                .arrowSize(40, 20)
                .show(anchor);
        itemView.setOnClickListener((View v) -> {
            popup.dismiss();
            if (isFav) {
                presenter.author_unfav(bean.get_name());
            } else {
                presenter.author_fav(bean.get_name());
            }
        });
    }

    private String mKey;
    private int mKind = 1;
    public void search(String msg, int kind, boolean isChanged) {
        final String key = msg.length() > 7 ? msg.substring(0, 7) : msg;
        mKey = key;
        mKind = kind;
        setup();
        if (isChanged) {
            tv_key.setText(key);
            handleInputString(key);
        }

        showProgress();
        requestBean.addParams("kind", kind);
        mRequestBeanAuthor.addParams("kind", kind);
        KindType kindType = KindType.getKindTypeByKind(kind);
        switch (kindType) {
            case YINGBI:
            case MAOBI:
                mAdapter.setYin(false);
                lay_author.setVisibility(View.VISIBLE);
                lay_type.setVisibility(View.VISIBLE);
                mRg_font.setVisibility(View.VISIBLE);
                view_line.setVisibility(View.VISIBLE);
                lay_sort.setVisibility(View.VISIBLE);

                mAuthorListShow = (boolean) SPUtils.get(activity, "AutorListShow" + mKind, false);
                changeShowAuthorList(mAuthorListShow);
                isBigItem = (boolean) SPUtils.get(activity, "isBigItem" + mKind, true);
                changeItemSize(false);
                break;
            case ZHUANGKE:
                requestBean.addParams("type", "");
                requestBean.addParams("font", "");

                mAdapter.setYin(true);
                lay_author.setVisibility(View.VISIBLE);
                lay_type.setVisibility(View.GONE);
                mRg_font.setVisibility(View.GONE);
                view_line.setVisibility(View.VISIBLE);
                lay_sort.setVisibility(View.VISIBLE);

                mAuthorListShow = (boolean) SPUtils.get(activity, "AutorListShow" + mKind, false);
                changeShowAuthorList(mAuthorListShow);
                isBigItem = (boolean) SPUtils.get(activity, "isBigItem" + mKind, true);
                changeItemSize(false);
                break;
        }

        search();
    }

    private void search() {
        getAuthor();
    }

    private void handleInputString(String msg) {
        if (msg.length() <= 1) {
            mTagLayout.setVisibility(View.GONE);
            return;
        }

        mTagLayout.removeAllViews();
        mTagLayout.setVisibility(View.VISIBLE);
        RadioGroup ll = new RadioGroup(activity);
        ll.setOrientation(LinearLayout.HORIZONTAL);

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewUtil.dpToPx(28), ViewUtil.dpToPx(28));
        params.gravity = Gravity.CENTER;
        params.leftMargin = ViewUtil.dpToPx(6);

        for (int i = 0; i < msg.length(); i++) {
            RadioButton button = new RadioButton(activity);
            button.setId(i);
            if (i == 0) {
                button.setChecked(true);
            }
            button.setButtonDrawable(null);
            button.setText(String.valueOf(msg.charAt(i)));
            setSelectorColor(button, ContextCompat.getColor(activity, R.color.gray), ContextCompat.getColor(activity, R.color.white));
            setSelectorDrawable(button, ContextCompat.getColor(activity, R.color.colorLine), ContextCompat.getColor(activity, R.color.colorPrimary));
            button.setLayoutParams(params);
            button.setGravity(Gravity.CENTER);
            ll.addView(button);
        }
        ll.setOnCheckedChangeListener((RadioGroup group, int checkedId) -> {
            for (int i = 0; i < msg.length(); i++) {
                if (i == group.getCheckedRadioButtonId()) {
                    search(String.valueOf(msg.charAt(i)), mKind, false);
                    break;
                }
            }
        });
        //ll.check(0);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTagLayout.addView(ll, lp);

        View line = new View(activity);
        line.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorLine));
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewUtil.dpToPx(0.6f));
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mTagLayout.addView(line, lp2);
    }

    private void setSelectorColor(RadioButton radioButton, int normal, int checked) {
        int[] colors = new int[]{normal, checked, normal};
        int[][] states = new int[3][];
        states[0] = new int[]{-android.R.attr.state_checked};
        states[1] = new int[]{android.R.attr.state_checked};
        states[2] = new int[]{};
        ColorStateList colorStateList = new ColorStateList(states, colors);
        radioButton.setTextColor(colorStateList);
    }

    private void setSelectorDrawable(RadioButton radioButton, int normal, int checked) {
        //new ColorDrawable(normal)
        int roundRadius = ViewUtil.dpToPx(4);
        GradientDrawable normalDrawable = new GradientDrawable();
        normalDrawable.setCornerRadius(roundRadius);
        normalDrawable.setColor(normal);

        GradientDrawable checkedDrawable = new GradientDrawable();
        checkedDrawable.setCornerRadius(roundRadius);
        checkedDrawable.setColor(checked);

        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_checked}, checkedDrawable);
        drawable.addState(new int[]{-android.R.attr.state_checked}, normalDrawable);
        radioButton.setBackground(drawable);
    }

    /**
     * 点击"空空如也"重新发送请求
     */
    private View.OnClickListener mRetryClickListener = (View v) -> {
        onRefresh(mRefresh_layout);
    };

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        mRefresh_layout.finishRefresh(false);
        mRefresh_layout.setLoaded(0);
        final int num = getAuthorWorksNum(mZiListAuthorAdapter.getItemNameSelect());
        mAdapter.setmAuthorSelect(num != 0 ? mZiListAuthorAdapter.getItemNameSelect() : "");
        mAdapter.setNewData(null);

        requestBean.addParams("loaded", 0);
        requestBean.addParams("key", mKey);

        if (TextUtils.isEmpty(mZiListAuthorAdapter.getItemNameSelect()) ||
                mZiListAuthorAdapter.getItemNameSelect().equals("书法家") || num == 0) {
            requestBean.addParams("author", "");
        } else {
            requestBean.addParams("author", mZiListAuthorAdapter.getItemNameSelect());
        }
        presenter.getList(requestBean, true);
    }

    private int getAuthorWorksNum(String author) {
        if (!TextUtils.isEmpty(author) && !author.equals("书法家")) {
            final List<ZiAuthorBean> list = mZiListAuthorAdapter.getData();
            for (ZiAuthorBean bean : list) {
                if (bean.get_name().equals(author)) {
                    return bean.get_num();
                }
            }
        }
        return 0;
    }

    private void getAuthor() {
        mRequestBeanAuthor.addParams("key", mKey);
        presenter.glyph_authors(mRequestBeanAuthor, false);
    }

    private RequestBean requestBean;
    private RequestBean mRequestBeanAuthor;
    protected ZiListPresenterImpl getPresenter() {
        requestBean = new RequestBean();
        mRequestBeanAuthor = new RequestBean();
        return new ZiListPresenterImpl();
    }

    @Override
    public void showProgress() {
        mStatus_view.showLoading();
        if (null != mStatus_view.findViewById(R.id.ll_loading)) {
            mStatus_view.findViewById(R.id.ll_loading).setOnClickListener(mCancelClick);
        }
    }

    /**
     * 允许用户取消api请求
     */
    private final View.OnClickListener mCancelClick = (View v) -> {
        cancel();
        mStatus_view.showEmpty();
        mRefresh_layout.finishRefresh();
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
            mZiListAuthorAdapter.setNewData(tData.getList());
            int size = tData.getList().size();
            String author = "";
            for (int i = 0; i < size; i++) {
                if (tData.getList().get(i).get_name().equals(mTv_author.getText().toString())) {
                    author = mTv_author.getText().toString();
                    break;
                }
            }

            selectAuthor(author);
        } else {
            noAuthors();
        }

        if (!authorOnly) {
            onRefresh(mRefresh_layout);
        }

        authorOnly = false;
    }

    @Override
    public void glyph_authorsFail(String string) {
        noAuthors();
        onRefresh(mRefresh_layout);
    }

    private void noAuthors() {
        mRv_author.setNestedScrollingEnabled(false);
        mZiListAuthorAdapter.setItemNameSelect("");
        mZiListAuthorAdapter.setNewData(null);
    }

    private void selectAuthor(String author) {
        mZiListAuthorAdapter.setItemNameSelect(author);
        mZiListAuthorAdapter.notifyDataSetChanged();
        if (!TextUtils.isEmpty(author) && !author.equals("书法家")) {
            List<ZiAuthorBean> list = mZiListAuthorAdapter.getData();
            int count = list.size();
            for (int i = 0; i < count; i++) {
                if (list.get(i).get_name().equals(author)) {
                    mRv_author.scrollToPosition(i);
                    break;
                }
            }
        }
    }

    @Override
    public void loadDataSuccess(RowBean<ZiBean> tData) {
        mRefresh_layout.setTotal(tData.getTotal());
        if (tData != null && tData.getList() != null && tData.getList().size() > 0) {
            mStatus_view.showContent();
            mAdapter.addData(tData.getList());

            if (mAdapter.getData().size() >= tData.getTotal()) {
                mRefresh_layout.setEnableLoadMore(false);
            } else {
                mRefresh_layout.setEnableLoadMore(true);
            }
        } else {
            if (mRefresh_layout.getLoaded() == 0) {//刷新
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
        if (isVisible) {
            ToastUtil.showToast(activity, errorMsg);
        }
    }

    private boolean authorOnly;
    public void author_fav_unfav() {
        synchronized (ZiListFragment.this) {
            if (authorOnly) {
                return;
            }
            authorOnly = true;
            getAuthor();
        }
    }

    @Override
    public boolean onBackPressedSupport() {
        if (mZiListFragmentCallBack != null) {
            mZiListFragmentCallBack.onBack();
            return true;
        }
        return super.onBackPressedSupport();
    }

    private ZiListFragmentCallBack mZiListFragmentCallBack;
    public void setZiListFragmentCallBack(ZiListFragmentCallBack mZiListFragmentCallBack) {
        this.mZiListFragmentCallBack = mZiListFragmentCallBack;
    }

    public interface ZiListFragmentCallBack {
        void onBack();
    }
}