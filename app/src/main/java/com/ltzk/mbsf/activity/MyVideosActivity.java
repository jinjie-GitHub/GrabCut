package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.fragment.VideoZiLibFragment;
import com.ltzk.mbsf.fragment.VideoZiTieFragment;

import butterknife.BindView;

/**
 * Created by JinJie on 2021/7/29
 */
public class MyVideosActivity extends BaseActivity {

    @BindView(R.id.radioGroup)
    RadioGroup mRadioGroup;

    public static void safeStart(Context c) {
        Intent intent = new Intent(c, MyVideosActivity.class);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_videos;
    }

    @Override
    public void initView() {
        findViewById(R.id.left_button_).setOnClickListener(v -> {
            finish();
        });
        mRadioGroup.setOnCheckedChangeListener(mChangeListener);
        handleSelectTab();
    }

    private void handleSelectTab() {
        mRadioGroup.clearCheck();
        final View checkView = mRadioGroup.findViewById(R.id.rb_type_zitie);
        if (checkView instanceof RadioButton) {
            ((RadioButton) checkView).setChecked(true);
        } else {
            mRadioGroup.check(R.id.rb_type_zitie);
        }
    }

    private RadioGroup.OnCheckedChangeListener mChangeListener = (RadioGroup group, int checkedId) -> {
        if (checkedId == R.id.rb_type_zitie) {
            loadRootFragment(R.id.container, VideoZiTieFragment.newInstance(), false, true);
        } else if (checkedId == R.id.rb_type_zilib) {
            loadRootFragment(R.id.container, VideoZiLibFragment.newInstance(), false, true);
        }
    };
}