package com.ltzk.mbsf.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.api.Constan;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.fragment.ZiLibNewFragment;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * update on 2021/5/12
 */
public class ZilibSearchResutActivity extends BaseActivity {
    public static final int REQ_SEARCH_KEY = 1;

    @OnClick(R2.id.iv_back)
    public void onBackClick(View view) {
        finish();
    }

    @BindView(R.id.et_key)
    EditText mEtKey;

    @OnClick(R2.id.et_key)
    public void onKeyClick(View view) {
        SearchActivity.safeStart(this, Constan.Key_type.KEY_ZIKU, "", "字库查询", REQ_SEARCH_KEY);
    }

    @Override
    public void initView() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, ZiLibNewFragment.getInstance(getIntent().getBooleanExtra("index", false)));
        fragmentTransaction.commitAllowingStateLoss();
        search(getIntent().getStringExtra("key"));
    }

    private void search(String key) {
        mEtKey.postDelayed(new Runnable() {
            public void run() {
                mEtKey.setText(key);
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_content);
                if (fragment instanceof ZiLibNewFragment) {
                    ((ZiLibNewFragment) fragment).ziLibSearch(key);
                }
            }
        }, 0);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zilib_search_resulut;
    }

    @Override
    public void finish() {
        super.finishFromTop();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_SEARCH_KEY) {
                search(data.getStringExtra(SearchActivity.KEY));
            }
        }
    }
}