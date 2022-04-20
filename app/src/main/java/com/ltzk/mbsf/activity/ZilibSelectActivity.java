package com.ltzk.mbsf.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.fragment.ZiLibNewFragment;
import org.greenrobot.eventbus.EventBus;

/**
 * update on 2021/05/08
 */
public class ZilibSelectActivity extends BaseActivity {

    @Override
    public void initView() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, ZiLibNewFragment.getInstance(false));
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        post(() -> {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fl_content);
            if (fragment instanceof ZiLibNewFragment) {
                ((ZiLibNewFragment) fragment).ziLibSelect();
            }
        });
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
        EventBus.getDefault().post(new Boolean(true));
        super.finishFromTop();
    }
}