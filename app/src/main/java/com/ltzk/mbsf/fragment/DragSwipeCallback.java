package com.ltzk.mbsf.fragment;

import android.content.Context;
import android.os.Vibrator;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class DragSwipeCallback extends ItemTouchHelper.Callback {

    private Context mContext;
    private OnItemPositionListener mItemPositionListener;
    private int mFromPosition = -1;

    public DragSwipeCallback(OnItemPositionListener itemPositionListener, Context context) {
        mItemPositionListener = itemPositionListener;
        mContext = context;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlag;
        int swipeFlags;
        //如果是表格布局，则可以上下左右的拖动，但是不能滑动
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            dragFlag = ItemTouchHelper.UP |
                    ItemTouchHelper.DOWN |
                    ItemTouchHelper.LEFT |
                    ItemTouchHelper.RIGHT;
            swipeFlags = 0;
        }
        //如果是线性布局，那么只能上下拖动，只能左右滑动
        else {
            dragFlag = ItemTouchHelper.UP |
                    ItemTouchHelper.DOWN;
            swipeFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        }

        //通过makeMovementFlags生成最终结果
        return makeMovementFlags(dragFlag, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //被拖动的item位置
        final int fromPosition = viewHolder.getLayoutPosition();
        if (mFromPosition == -1) {
            mFromPosition = fromPosition;
        }
        //他的目标位置
        final int targetPosition = target.getLayoutPosition();
        //为了降低耦合，使用接口让Adapter去实现交换功能
        mItemPositionListener.onItemSwap(fromPosition, targetPosition);
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //为了降低耦合，使用接口让Adapter去实现交换功能
        mItemPositionListener.onItemMoved(viewHolder.getLayoutPosition());
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            //viewHolder.itemView.setBackgroundColor(Color.RED);
            Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(50);
        }
    }

    //当手指松开的时候
    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //viewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
        final int from = mFromPosition;
        if (from != -1) {
            mItemPositionListener.onUpdate(from, viewHolder.getLayoutPosition());
        }
        mFromPosition = -1;
    }

    //禁止长按滚动交换，需要滚动的时候使用{@link ItemTouchHelper#startDrag(ViewHolder)}
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        //是否开启滑动删除
        return false;
    }
}