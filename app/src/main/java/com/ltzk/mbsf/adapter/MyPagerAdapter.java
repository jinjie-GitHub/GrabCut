package com.ltzk.mbsf.adapter;

import android.view.ViewGroup;
import com.ltzk.mbsf.bean.ZitieDetailitemBean;
import com.ltzk.mbsf.fragment.ZitieDetailFragment;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;

/**
 * 描述：
 * 作者： on 2019/3/12 15:36
 * 邮箱：499629556@qq.com
 */

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    List<ZitieDetailitemBean> list;

    public MyPagerAdapter(@NonNull FragmentManager fm, int behavior, List<ZitieDetailitemBean> list) {
        super(fm, behavior);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return ZitieDetailFragment.newInstance(position,list.get(position));
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
