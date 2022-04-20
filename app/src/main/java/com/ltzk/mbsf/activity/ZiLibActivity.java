package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.fragment.ZiLibNewFragment;
import com.ltzk.mbsf.widget.TopBar;

/**
 * Created by JinJie on 2021/7/31
 */
public class ZiLibActivity extends BaseActivity {

    private TopBar mTopBar;

    public static void safeStart(Context c) {
        Intent intent = new Intent(c, ZiLibActivity.class);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void initView() {
        mTopBar = findViewById(R.id.top_bar);
        mTopBar.setTitle("书法字库");
        mTopBar.setVisibility(View.GONE);
        mTopBar.setLeftButtonListener(R.mipmap.back, v -> {
            finish();
        });
        final ZiLibNewFragment ziLibFragment = new ZiLibNewFragment();
        ziLibFragment.setIsIndex(true);
        loadRootFragment(R.id.container, ziLibFragment);
    }
}