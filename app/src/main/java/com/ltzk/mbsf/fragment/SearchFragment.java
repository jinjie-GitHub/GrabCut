package com.ltzk.mbsf.fragment;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.activity.SearchActivity;
import com.ltzk.mbsf.adapter.SearchAdapter;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.api.presenter.SearchPresenterImpl;
import com.ltzk.mbsf.api.view.SearchView;
import com.ltzk.mbsf.base.MyBaseFragment;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.utils.ActivityUtils;
import com.ltzk.mbsf.utils.SPUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索
 */
public class SearchFragment extends MyBaseFragment<SearchView, SearchPresenterImpl> implements SearchView {

	String mKeyType = Constan.Key_type.KEY_ZI;

	SearchFragmentCallBack mSearchFragmentCallBack;
	public void setSearchFragmentCallBack(SearchFragmentCallBack searchFragmentCallBack){
		mSearchFragmentCallBack = searchFragmentCallBack;
	}

	@BindView(R.id.et_key)
	EditText etKey;
	@BindView(R.id.iv_delete_key)
	ImageView ivDeleteKey;
	@BindView(R.id.lv_key)
	ListView lvKey;
	@BindView(R.id.tv_search)
	TextView tvSearch;

	//关键字列表
	SearchAdapter adapter_key;
	List<String> list_key = new ArrayList<>();

	@OnClick(R.id.iv_delete_key)
	public void onIvDeleteKeyClicked() {
		etKey.setText("");
	}

	@OnClick(R.id.iv_back)
	public void onTvCannelClicked() {
		mSearchFragmentCallBack.onBack();
	}

	@OnClick(R.id.iv_clear)
	public void onTvClearClicked() {
		list_key.clear();
		adapter_key.setData(list_key);
		adapter_key.notifyDataSetChanged();
		SPUtils.put(activity, mKeyType, "");
	}

	@OnClick(R.id.tv_search)
	public void onTvSearchClicked() {
		search(etKey.getText().toString());
	}

	private Handler mHandler = new MyHandler(this);

	private void handleMessage(Message msg){
		ActivityUtils.showInput(activity,etKey);
	}

	private static final class MyHandler extends Handler {
		private WeakReference<SearchFragment> mReference;
		private MyHandler(SearchFragment fragment) {
			mReference = new WeakReference<>(fragment);
		}
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			SearchFragment fragment = mReference.get();
			if (fragment != null) {
				fragment.handleMessage(msg);
			}
		}
	}

	/**
	 * 初始化搜索区域控件
	 */
	private void initKeyView() {
		adapter_key = new SearchAdapter(activity, new SearchAdapter.OnItemDelCallBack() {
			@Override
			public void click(String string, int position) {
				search(string);
			}

			@Override
			public void del(String string) {
				list_key.remove(string);
				saveKeys();
			}

			@Override
			public void to(String string) {
				etKey.setText(string);
				etKey.setSelection(string.length());
			}
		});
		lvKey.setAdapter(adapter_key);
		etKey.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
					search(etKey.getText().toString());
					return true;
				}
				return false;
			}
		});
		etKey.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				setListData();
			}
		});
		if (getArguments() != null) {
			mKeyType = getArguments().getString(SearchActivity.KEY_TYPE);
			if (!TextUtils.isEmpty(getArguments().getString(SearchActivity.KEY))) {
				etKey.setText(getArguments().getString(SearchActivity.KEY));
			}
			if (!TextUtils.isEmpty(getArguments().getString(SearchActivity.KEY_HINT))) {
				etKey.setHint(getArguments().getString(SearchActivity.KEY_HINT));
			}
		}
	}

	List<String> listAdapter = new ArrayList<>();
	private void setListData(){
		if(mKeyType.equals(Constan.Key_type.KEY_AUTHOR) || mKeyType.equals(Constan.Key_type.KEY_ZITIE) || mKeyType.equals(Constan.Key_type.KEY_ZIKU)){
			String key = etKey.getText().toString();
			if(TextUtils.isEmpty(key)){
				adapter_key.setKey(key);
				adapter_key.setData(list_key);
				adapter_key.notifyDataSetChanged();
			}else {
				adapter_key.setKey(key);
				requestBean.addParams("key",key);
				if(mKeyType.equals(Constan.Key_type.KEY_AUTHOR)){
					presenter.author_sug(requestBean,false);
				}else {
					presenter.zuopin_sug(requestBean,false);
				}
			}
		}

	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		initKeyData();
		adapter_key.setData(list_key);
		adapter_key.notifyDataSetChanged();
	}

	/**
	 * 初始化关键字数据
	 */
	private void initKeyData() {
		list_key.clear();
		String keys_str = (String) SPUtils.get(activity, mKeyType, "");
		String[] keys;
		if (!keys_str.equals("")) {
			keys = keys_str.split("#");
			for (int i = 0; i < keys.length; i++) {
				list_key.add(keys[i]);
			}
		}
	}

	/**
	 * 加字
	 * @param key
	 */
	private void putKeys(String key) {
		if (list_key.contains(key)) {
			list_key.remove(key);
		}
		list_key.add(0, key);
	}

	/**
	 * 存字
	 */
	private void saveKeys() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < list_key.size(); i++) {
			buffer.append("#");
			buffer.append(list_key.get(i));
		}
		String keys_str = buffer.toString().replaceFirst("#", "");
		SPUtils.put(activity, mKeyType, keys_str);
	}

	/**
	 * 搜索
	 */
	private void search(String key) {
		if (!TextUtils.isEmpty(key)) {
			putKeys(key);
			saveKeys();
		}
		mSearchFragmentCallBack.onSearch(key);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if(hidden){
			ActivityUtils.closeSyskeyBroad(activity,etKey);
		}else {
			mHandler.sendEmptyMessageDelayed(0,300);
		}
	}

	public interface SearchFragmentCallBack{
		void onSearch(String key);
		void onBack();
	}

	@Override
	public void showProgress() {

	}

	@Override
	public void disimissProgress() {

	}

	@Override
	public void loadDataSuccess(List<String> tData) {
		if (tData != null) {
			listAdapter = tData;
			adapter_key.setData(listAdapter);
			adapter_key.notifyDataSetChanged();
		}
	}

	@Override
	public void loadDataError(String errorMsg) {

	}

	@Override
	protected int getLayoutRes() {
		return R.layout.fragment_search;
	}

	@Override
	protected void initView(View rootView) {
		mHandler.sendEmptyMessageDelayed(0,300);
		initKeyView();
		initData();
	}

	RequestBean requestBean = new RequestBean();
	@Override
	protected SearchPresenterImpl getPresenter() {
		return new SearchPresenterImpl();
	}
}

