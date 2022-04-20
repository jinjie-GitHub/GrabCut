package com.ltzk.mbsf.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.base.BaseActivity;
import com.ltzk.mbsf.fragment.DictionaryFragment;

/**
 * update on 2021/06/08
 */
public class DictionaryActivity extends BaseActivity {
    private DictionaryFragment dictionaryFragment;

    public static void safeStart(Context c, String key) {
        Intent intent = new Intent(c, DictionaryActivity.class);
        intent.putExtra("key", key);
        c.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dictionary;
    }

    @Override
    public void initView() {
        dictionaryFragment = new DictionaryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("key", getIntent().getStringExtra("key"));
        dictionaryFragment.setArguments(bundle);
        dictionaryFragment.setDictionaryFragmentCallBack(new DictionaryFragment.DictionaryFragmentCallBack() {
            @Override
            public void onBack() {
                finish();
            }
        });
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fl, dictionaryFragment);
        fragmentTransaction.show(dictionaryFragment);
        fragmentTransaction.commit();
    }
}