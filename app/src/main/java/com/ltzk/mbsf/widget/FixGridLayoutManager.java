package com.ltzk.mbsf.widget;

import android.content.Context;
import android.util.AttributeSet;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.ltzk.mbsf.utils.Logger;

public class FixGridLayoutManager extends GridLayoutManager {

    public FixGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public FixGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public FixGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Throwable e) {
            Logger.d("onLayoutChildren:" + e);
        }
    }
}