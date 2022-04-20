package com.ltzk.mbsf.fragment;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.classic.common.MultipleStatusView;
import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.activity.ZiLibAddActivity;
import com.ltzk.mbsf.activity.ZiLibZiListActivity;
import com.ltzk.mbsf.activity.ZilibSearchResutActivity;
import com.ltzk.mbsf.adapter.ZiLibListAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.ZiLibListPresenterImpl;
import com.ltzk.mbsf.api.view.ZiLibListView;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.Bus_LoginOutTip;
import com.ltzk.mbsf.bean.Bus_ZilibAdd;
import com.ltzk.mbsf.bean.Bus_ZilibDel;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.bean.RowBean;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.utils.SPUtils;
import com.ltzk.mbsf.utils.ToastUtil;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MySmartRefreshLayout;
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


public class ZiLibNewFragment extends MyBaseFragment<ZiLibListView, ZiLibListPresenterImpl> implements OnRefreshListener, ZiLibListView {

	public static final int REQ_SEARCH_KEY = 1;
	public static final int REQ_SEARCH = 2;

	@BindView(R.id.iv_left)
	ImageView mIvLeft;
	@OnClick(R2.id.iv_left)
	public void onLeftClick(View view) {
		if (mIsIndex) {
			if (getActivity() != null) {
				getActivity().finish();
			}
		} else {
			finishFromTop();
		}
	}

	@OnClick(R.id.tv_add_font)
	public void tv_add_font(View view) {
		startActivityFromBottom(new Intent(activity, ZiLibAddActivity.class));
	}

	@BindView(R.id.rg_type)
	RadioGroup rgType;
	@BindView(R.id.iv_search)
	ImageView ivSearch;
	@OnClick(R2.id.iv_search)
	public void iv_search(View view) {
		SearchActivity.safeStart(this, Constan.Key_type.KEY_ZIKU, "", "字库查询", REQ_SEARCH_KEY);

	}
	@BindView(R.id.rg_kid)
	RadioGroup rgKid;
	@BindView(R.id.rg_font)
	RadioGroup rgFont;
	@BindView(R.id.topLayout)
	View topLayout;

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//是否是首页
	private boolean mIsIndex = true;
	public void setIsIndex(boolean isIndex){
		mIsIndex = isIndex;
	}
	//字典列表
	@BindView(R2.id.status_view)
	MultipleStatusView mStatus_view;
	@BindView(R2.id.refresh_layout)
	MySmartRefreshLayout mRefresh_layout;
	@BindView(R2.id.recyclerView)
	RecyclerView mRv_zi;
	ZiLibListAdapter mAdapter;
	private ItemTouchHelper helper;

	@BindView(R.id.ll_kind_font)
	View ll_kind_font;
	@BindView(R.id.kind_font_line)
	View kind_font_line;
	@BindView(R.id.tv_add_font)
	View tv_add_font;

	public static ZiLibNewFragment getInstance(boolean isIndex) {
		final ZiLibNewFragment fragment = new ZiLibNewFragment();
		fragment.setIsIndex(isIndex);
		return fragment;
	}

	@Override
	protected int getLayoutRes() {
		EventBus.getDefault().register(this);
		return R.layout.fragment_zi_lib_new;
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	protected void initView(View rootView) {
		init();
		mRefresh_layout.setOnRefreshListener(this);
		mAdapter = new ZiLibListAdapter(this, new ZiLibListAdapter.CallBack() {
			@Override
			public void onItemClick(ZilibBean zilibBean) {
				if (mIsIndex) {
					if (MainApplication.getInstance().isCanGo(activity, mRefresh_layout, String.valueOf(zilibBean._free), new RequestBean().getParams())) {
						Intent intent = new Intent(activity, ZiLibZiListActivity.class);
						intent.putExtra("zilibBean", zilibBean);
						activity.startActivity(intent);
					}
				} else {
					Intent intent = new Intent();
					intent.putExtra("zilibBean", zilibBean);
					activity.setResult(Activity.RESULT_OK, intent);
					((BaseActivity) activity).finishFromTop();
				}
			}

			@Override
			public void onItemNameClick(ZilibBean zilibBean) {
				if (mIsIndex) {
					if (MainApplication.getInstance().isCanGo(activity, mRefresh_layout, String.valueOf(zilibBean._free), new RequestBean().getParams())) {
						Intent intent = new Intent(activity, ZiLibZiListActivity.class);
						intent.putExtra("zilibBean", zilibBean);
						activity.startActivity(intent);
					}
				} else {
					Intent intent = new Intent();
					intent.putExtra("zilibBean", zilibBean);
					activity.setResult(Activity.RESULT_OK, intent);
					((BaseActivity) activity).finishFromTop();
				}
			}

			@Override
			public void onItemLongClick(View v, ZilibBean zilibBean, int position) {
				showFavState(v, zilibBean, position);
			}

			@Override
			public void onItemSwap(int from, int target) {
				RequestBean requestBean = new RequestBean();
				requestBean.addParams("fid",list.get(from).get_id());
				requestBean.addParams("order",target);
				presenter.font_update(requestBean, false);
			}
		});
		mRv_zi.setAdapter(mAdapter);
		mRv_zi.setLayoutManager(new GridLayoutManager(activity, mAdapter.calculate(activity)));
		//mRv_zi.addItemDecoration(new SpaceItemDecoration(ViewUtil.dpToPx(4), ViewUtil.dpToPx(8), 0, SpaceItemDecoration.GRIDLAYOUT));
		mStatus_view.setOnRetryClickListener(mRetryClickListener);

		requestBean.addParams("loaded", 0);
		presenter.font_query(requestBean, true);
		mRefresh_layout.setRunnable(() -> {
			requestBean.addParams("loaded", mRefresh_layout.getLoaded());
			presenter.font_query(requestBean, false);
		});
		changeSwap();
	}

	private void changeSwap(){
		if ("my".equals(requestBean.getParam("subset"))) {//字库 > 我的，这个页面有拖动item功能。
			if(helper == null){
				helper = new ItemTouchHelper(new DragSwipeCallback(mAdapter,activity));
			}
			helper.attachToRecyclerView(mRv_zi);
		}else if(helper != null){
			helper.attachToRecyclerView(null);
		}
	}

	/**
	 * 点击"空空如也"重新发送请求
	 */
	private View.OnClickListener mRetryClickListener = (View v) -> {
		onRefresh(mRefresh_layout);
	};

	private void showFavState(View anchor, ZilibBean bean, int pos) {
		final String subset = requestBean != null ? String.valueOf(requestBean.getParam("subset")) : "hot";
		if ("my".equals(subset)) {//字库 > 我的，这个页面长按时，不用显示“收藏”。
			return;
		}
		final TextView itemView = new TextView(activity);
		itemView.setText("fav".equals(subset) ? "取消收藏" : "收藏");
		itemView.setTextSize(16);
		itemView.setPadding(30, 16, 30, 16);
		itemView.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
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
			if ("fav".equals(subset)) {
				presenter.libUnFav(bean.get_id());
				removeItem(pos);
			} else {
				presenter.libFav(bean.get_id());
			}
		});
	}

	private void removeItem(int pos) {
		try {
			final List<ZilibBean> data = mAdapter.getList();
			data.remove(pos);
			mAdapter.notifyItemRemoved(pos);
			mAdapter.notifyItemRangeChanged(pos, data.size() - pos);
		} catch (Exception e) {
			mAdapter.notifyDataSetChanged();
		}
	}

	public final void ziLibSearch(String key) {
		topLayout.setVisibility(View.GONE);
		requestBean.addParams("key", key);
		onRefresh(mRefresh_layout);
	}

	public final void ziLibSelect() {
		requestData();
	}

	@Override
	public void onRefresh(@NonNull RefreshLayout refreshLayout) {
		processRefresh();
		if (presenter != null) {
			requestBean.addParams("loaded", 0);
			presenter.font_query(requestBean, true);
		}
	}

	/**
	 * 1、首先清空原列表内容；
	 * 2、下拉视图回弹至顶部；
	 * 3、发送请求，转圈提示；
	 */
	private final void processRefresh() {
		if (mRefresh_layout != null) {
			mRefresh_layout.finishRefresh(false);
			list.clear();
			mAdapter.setList(list);
			mAdapter.notifyDataSetChanged();
			mRefresh_layout.setLoaded(0);
		}
	}

	@Override
	protected ZiLibListPresenterImpl getPresenter() {
		return new ZiLibListPresenterImpl();
	}

	final RequestBean requestBean = new RequestBean();

	@Override
	public void showProgress() {
		mStatus_view.showLoading();
		if (null != mStatus_view.findViewById(R.id.ll_loading)) {
			mStatus_view.findViewById(R.id.ll_loading).setOnClickListener(mCancelClick);
		}
	}

	private final View.OnClickListener mCancelClick = (View v) -> {
		cancel();
	};

	@Override
	protected void cancel() {
		super.cancel();
		disimissProgress();
		mStatus_view.showEmpty();
	}

	//登录被踢
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onShowMessageEvent(Bus_LoginOutTip messageEvent) {
		if (mStatus_view != null && ("my".equals(requestBean.getParam("subset")) || !"my".equals(requestBean.getParam("fav")))) {
			mStatus_view.showEmpty();
			showRefreshByEmpty();
		}
	}

	//字库被删除
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onShowMessageEvent(Bus_ZilibDel messageEvent) {
		onRefresh(mRefresh_layout);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onShowMessageEvent(Boolean messageEvent) {
		if (messageEvent) {
			init();
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
	public void disimissProgress() {
		mRefresh_layout.finishRefresh();
		mRefresh_layout.finishLoadMore();
	}

	private final List<ZilibBean> list = new ArrayList<>();

	@Override
	public void loadDataSuccess(RowBean<ZilibBean> tData) {
		if (tData != null && tData.getList() != null && tData.getList().size() > 0) {
			mRefresh_layout.setTotal(tData.getTotal());
			mStatus_view.showContent();
			list.addAll(tData.getList());
			mAdapter.setList(list);
			mAdapter.notifyDataSetChanged();

			if (mAdapter.getList().size() >= tData.getTotal()) {
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

	@Override
	public void loadDataError(String errorMsg) {
		mStatus_view.showError();
		if (isVisible) {
			ToastUtil.showToast(activity, errorMsg);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private final void init() {

		if(mIsIndex){
			mIvLeft.setImageResource(R.mipmap.back);
		}else {
			mIvLeft.setImageResource(R.mipmap.close2);
		}

		requestBean.addParams("key", "");
		String subset = (String) SPUtils.get(activity,"subset","hot");
		setTypeValue(subset);
		requestBean.addParams("subset", subset);

		String font = (String) SPUtils.get(activity,"font","楷");
		setFontValue(font);
		requestBean.addParams("font", font);

		String kind = (String)SPUtils.get(activity,"kind","1");
		setKidValue(kind);
		requestBean.addParams("kind", kind);

		//我的和收藏
		if (subset.equals("my") || subset.equals("fav")) {
			requestBean.addParams("kind", "");
			requestBean.addParams("font", "");
		}

		initTypeView();
		initKidView();
		initZiFontView();
	}


    //TODO update
	private void getData() {
		if (isCanGetData) {
			onRefresh(mRefresh_layout);
		}
	}

    private RadioButton mLastSelectItemType;
	private void initTypeView() {
		rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(mLastSelectItemType!=null){
                    mLastSelectItemType.getPaint().setFakeBoldText(false);
                }
                mLastSelectItemType = rgType.findViewById(rgType.getCheckedRadioButtonId());
                mLastSelectItemType.getPaint().setFakeBoldText(true);
				processTabLayout(checkedId);

				final String font = (String) SPUtils.get(activity, "font", "楷");
				final String kind = (String) SPUtils.get(activity, "kind", "1");

				if (checkedId == R.id.rb_type_1) {
					requestBean.addParams("subset", "new");
					requestBean.addParams("font", font);
					requestBean.addParams("kind", kind);
				} else if (checkedId == R.id.rb_type_2) {
					requestBean.addParams("subset", "hot");
					requestBean.addParams("font", font);
					requestBean.addParams("kind", kind);
				} else if (checkedId == R.id.rb_type_3) {
					requestBean.addParams("subset", "my");
					requestBean.addParams("kind", "");
					requestBean.addParams("font", "");
				} else if (checkedId == R.id.rb_type_4) {
					requestBean.addParams("subset", "fav");
					requestBean.addParams("kind", "");
					requestBean.addParams("font", "");
				}
				getData();

				SPUtils.put(activity,"subset",requestBean.getParam("subset"));
				changeSwap();
			}
		});
        mLastSelectItemType = rgType.findViewById(rgType.getCheckedRadioButtonId());
        mLastSelectItemType.getPaint().setFakeBoldText(true);
		processTabLayout(rgType.getCheckedRadioButtonId());
	}

	private void processTabLayout(final int id) {
		if (id == R.id.rb_type_1 || id == R.id.rb_type_2) {
			ll_kind_font.setVisibility(View.VISIBLE);
			kind_font_line.setVisibility(View.VISIBLE);
		} else {
			ll_kind_font.setVisibility(View.GONE);
			kind_font_line.setVisibility(View.GONE);
		}
		tv_add_font.setVisibility(id == R.id.rb_type_3 ? View.VISIBLE : View.GONE);
	}

	private void initKidView() {
		setBoldText(rgKid.getCheckedRadioButtonId());
		rgKid.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_kid_1) {
					requestBean.addParams("kind", "1");
				} else if (checkedId == R.id.rb_kid_2) {
					requestBean.addParams("kind", "2");
				}
				setBoldText(R.id.rb_kid_1, R.id.rb_kid_2);
				getData();
				SPUtils.put(activity,"kind",requestBean.getParam("kind"));
			}
		});
	}

	private void setBoldText(int... rbId) {
		for (int cId : rbId) {
			RadioButton rb = rgKid.findViewById(cId);
			rb.getPaint().setFakeBoldText(rb.isChecked());
		}
	}

    private RadioButton mLastSelectItem;
	private void initZiFontView() {
		rgFont.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(mLastSelectItem!=null){
                    mLastSelectItem.getPaint().setFakeBoldText(false);
                }
                mLastSelectItem = rgFont.findViewById(rgFont.getCheckedRadioButtonId());
                mLastSelectItem.getPaint().setFakeBoldText(true);

				if (checkedId == R.id.rb_1) {
					requestBean.addParams("font", "楷");
				} else if (checkedId == R.id.rb_2) {
					requestBean.addParams("font", "行");
				} else if (checkedId == R.id.rb_3) {
					requestBean.addParams("font", "草");
				} else if (checkedId == R.id.rb_4) {
					requestBean.addParams("font", "隶");
				} else if (checkedId == R.id.rb_5) {
					requestBean.addParams("font", "篆");
				}
				getData();
				SPUtils.put(activity,"font",requestBean.getParam("font"));
			}
		});
        mLastSelectItem = rgFont.findViewById(rgFont.getCheckedRadioButtonId());
        mLastSelectItem.getPaint().setFakeBoldText(true);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if(resultCode == Activity.RESULT_OK){
			if(requestCode == REQ_SEARCH_KEY){
				startActivityForResult(new Intent(activity, ZilibSearchResutActivity.class)
						.putExtra("key",data.getStringExtra(SearchActivity.KEY))
						.putExtra("index",mIsIndex),REQ_SEARCH);
			}else if(REQ_SEARCH == requestCode){
				Intent intent = new Intent();
				intent.putExtra("zilibBean", data.getSerializableExtra("zilibBean"));
				activity.setResult(Activity.RESULT_OK, intent);
				((BaseActivity) activity).finishFromTop();
			}
		}
	}

	private void setTypeValue(String type){
		if (type.equals("new")) {
			rgType.check(R.id.rb_type_1);
		} else if (type.equals("hot")) {
			rgType.check(R.id.rb_type_2);
		} else if (type.equals("my")) {
			rgType.check(R.id.rb_type_3);
		} else if (type.equals("fav")) {
			rgType.check(R.id.rb_type_4);
		}
	}

	private void setFontValue(String font){
		if (font.equals("楷")) {
			rgFont.check(R.id.rb_1);
		} else if (font.equals("行")) {
			rgFont.check(R.id.rb_2);
		} else if (font.equals("草")) {
			rgFont.check(R.id.rb_3);
		} else if (font.equals("隶")) {
			rgFont.check(R.id.rb_4);
		} else if (font.equals("篆")) {
			rgFont.check(R.id.rb_5);
		}
	}

	private void setKidValue(String kind){
		if (kind.equals("1")) {
			rgKid.check(R.id.rb_kid_1);
		} else {
			rgKid.check(R.id.rb_kid_2);
		}
	}

	boolean isCanGetData = true;
	//字库有新增
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onShowMessageEvent(Bus_ZilibAdd messageEvent) {
		isCanGetData = false;
		String font = messageEvent.getFont();
		setFontValue(font);

		String kid = messageEvent.getKind();
		setKidValue(kid);

		rgType.check(R.id.rb_type_3);
		isCanGetData = true;

		getData();
	}
}