package com.ltzk.mbsf.utils;

import android.content.Context;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by JinJie on 2022/03/01
 */
public class RecycleViewUtils {
    public static void initVerticalRecycle(Context context, RecyclerView recyclerView) {
        if (null != recyclerView) {
            //解决卡顿
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            //解决自动滑动
            recyclerView.setFocusable(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    public static void initHorizontalRecycle(Context context, RecyclerView recyclerView) {
        if (null != recyclerView) {
            //解决卡顿
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(false);
            //解决自动滑动
            recyclerView.setFocusable(false);
            LinearLayoutManager ms = new LinearLayoutManager(context);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(ms);
        }
    }

    public static void initGridViewRecycle(Context context, RecyclerView recyclerView, int count) {
        //解决卡顿
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
        //解决自动滑动
        recyclerView.setFocusable(false);
        GridLayoutManager layoutManage = new GridLayoutManager(context, count);
        recyclerView.setLayoutManager(layoutManage);
    }
}