package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.bean.TodayBean;
import com.ltzk.mbsf.fragment.HistoryDetailFragment;
import com.ltzk.mbsf.fragment.HistoryFragment;
import com.ltzk.mbsf.widget.TopBar;

/**
 * Created by JinJie on 2020/5/31
 */
public class HistoryActivity extends BaseActivity {

    public TopBar mTopBar;
    public String mCurrent = "HistoryFragment";
    public TodayBean mTodayBean;

    public static void safeStart(Context c, TodayBean today) {
        Intent intent = new Intent(c, HistoryActivity.class);
        intent.putExtra("today", today);
        c.startActivity(intent);
    }

    @Override
    public void initView() {
        mTopBar = findViewById(R.id.top_bar);
        mTopBar.setTitle("每日书论");
        mTopBar.setLeftButtonListener(R.mipmap.back, mClickListener);
        mTopBar.setRightTxtListener("集字", v -> {
            HistoryDetailFragment fragment = findFragment(HistoryDetailFragment.class);
            if (fragment != null) {
                fragment.copy();
            }
        });

        mTodayBean = (TodayBean) getIntent().getSerializableExtra("today");
        if (mTodayBean != null) {
            mTopBar.setLeftButtonListener(R.mipmap.close2, mClickListener);
            if (TextUtils.isEmpty(mTodayBean._content)) {
                loadRootFragment(R.id.container, HistoryFragment.newInstance());
            } else {
                loadRootFragment(R.id.container, HistoryDetailFragment.newInstance(mTodayBean));
            }
        } else {
            loadRootFragment(R.id.container, HistoryFragment.newInstance());
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_history;
    }

    @Override
    public void finish() {
        super.finish();
        if (mTodayBean != null) {
            overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
        }
    }

    private View.OnClickListener mClickListener = v -> {
        if ("HistoryFragment".equals(mCurrent)) {
            finish();
        } else if (mTodayBean != null && !TextUtils.isEmpty(mTodayBean._content)) {
            finish();
        } else {
            pop();
        }
    };
}