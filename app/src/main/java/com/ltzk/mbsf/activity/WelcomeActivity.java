package com.ltzk.mbsf.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import notchtools.geek.com.notchtools.NotchTools;

/**
 * update on 2021/03/09
 */
public class WelcomeActivity extends BaseActivity {
    private static final int DELAY_MILLIS = 1000;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotchTools.getFullScreenTools().fullScreenUseStatusForActivityOnCreate(this);
    }

    @Override
    public void initView() {
        mHandler.postDelayed(() -> {
            WelcomeActivity.this.startActivity(new Intent(this, PhotosActivity.class));
            WelcomeActivity.this.finish();
            WelcomeActivity.this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }, DELAY_MILLIS);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ||
                keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}