package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.bean.RequestBean;
import com.ltzk.mbsf.fragment.SearchFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


/**
 * 描述：首页
 * 作者： on 2017/11/26 17:38
 * 邮箱：499629556@qq.com
 */

public class SearchActivity extends BaseActivity {

    public static final String KEY = "key";
    public static final String KEY_TYPE = "key_type";
    public static final String KEY_HINT = "key_hint";
    public static final String Arguments = "Arguments";
    public static void safeStart(Fragment fragment, String keyType, String key,String hint,int requestCode){
        Intent intent = new Intent(fragment.getActivity(), SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE,keyType);
        bundle.putString(KEY,key);
        bundle.putString(KEY_HINT,hint);
        intent.putExtra(Arguments,bundle);
        fragment.startActivityForResult(intent,requestCode);
    }

    public static void safeStart(Activity context, String keyType, String key,String hint,int requestCode){
        Intent intent = new Intent(context, SearchActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TYPE,keyType);
        bundle.putString(KEY,key);
        bundle.putString(KEY_HINT,hint);
        intent.putExtra(Arguments,bundle);
        context.startActivityForResult(intent,requestCode);
    }

    SearchFragment mSearchFragment;


    @Override
    public void initView() {
        mSearchFragment = new SearchFragment();
        mSearchFragment.setArguments(getIntent().getBundleExtra(Arguments));
        mSearchFragment.setSearchFragmentCallBack(new SearchFragment.SearchFragmentCallBack() {
            @Override
            public void onSearch(String key) {
                Intent intent = new Intent();
                intent.putExtra(KEY,key);
                setResult(Activity.RESULT_OK,intent);
                finish();
            }

            @Override
            public void onBack() {
                finish();
            }
        });

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, mSearchFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_search;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    @Override
    public void finish() {
        super.finishFromTop();
    }
}
