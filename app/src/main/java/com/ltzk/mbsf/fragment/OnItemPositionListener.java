package com.ltzk.mbsf.fragment;

public interface OnItemPositionListener {
    //交换
    void onItemSwap(int from, int target);

    //侧滑
    void onItemMoved(int position);

    void onUpdate(int from, int target);
}