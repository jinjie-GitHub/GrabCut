package com.ltzk.mbsf.utils;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 描述：
 * 作者： on 2018/8/18 14:20
 * 邮箱：499629556@qq.com
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    public static final int LINEARLAYOUT = 0;
    public static final int GRIDLAYOUT = 1;
    public static final int STAGGEREDGRIDLAYOUT = 2;

    //限定为LINEARLAYOUT,GRIDLAYOUT,STAGGEREDGRIDLAYOUT
    @IntDef({LINEARLAYOUT, GRIDLAYOUT, STAGGEREDGRIDLAYOUT})
    //表示注解所存活的时间,在运行时,而不会存在. class 文件.
    @Retention(RetentionPolicy.SOURCE)
    public @interface LayoutManager {
        public int type() default LINEARLAYOUT;
    }


    private int leftRight;
    private int edgel;
    private int edget;
    private int edger;
    private int edgeb;
    /**
     * 头布局个数
     */
    private int headItemCount;
    /**
     * 边距
     */
    private int spacev;
    private int spaceh;
    /**
     * 烈数
     */
    private int spanCount;

    private @LayoutManager
    int layoutManager;
    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param headItemCount
     * @param layoutManager
     */
    public SpaceItemDecoration(int spacev,int spaceh, int edgel,int edget,int edger,int edgeb,int headItemCount, @LayoutManager int layoutManager) {
        this.spacev = spacev;
        this.spaceh = spaceh;
        this.edgel = edgel;
        this.edget = edget;
        this.edger = edger;
        this.edgeb = edgeb;
        this.headItemCount = headItemCount;
        this.layoutManager = layoutManager;
    }


    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space
     * @param includeEdge
     * @param layoutManager
     */
    public SpaceItemDecoration(int space, boolean includeEdge, @LayoutManager int layoutManager) {
        this(space, 0, includeEdge, layoutManager);
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space
     * @param headItemCount
     * @param includeEdge
     * @param layoutManager
     */
    public SpaceItemDecoration(int space, int headItemCount, boolean includeEdge, @LayoutManager int layoutManager) {

        this.spaceh = space;
        this.spacev = space;
        if(includeEdge){
            this.edgel = space;
            this.edget = space;
            this.edger = space;
            this.edgeb = space;
        }else {
            this.edgel = 0;
            this.edget = 0;
            this.edger = 0;
            this.edgeb = 0;
        }
        this.headItemCount = headItemCount;
        this.layoutManager = layoutManager;

    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space
     * @param spaceEdge
     * @param layoutManager
     */
    public SpaceItemDecoration(int space, int spaceEdge,int headItemCount, @LayoutManager int layoutManager) {
        this(space,space,spaceEdge,spaceEdge,spaceEdge,spaceEdge,headItemCount,layoutManager);

    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space
     * @param headItemCount
     * @param layoutManager
     */
    public SpaceItemDecoration(int space, int headItemCount, @LayoutManager int layoutManager) {
        this(space, headItemCount, true, layoutManager);
    }


    /**
     * LinearLayoutManager or GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param space
     * @param layoutManager
     */
    public SpaceItemDecoration(int space, @LayoutManager int layoutManager) {
        this(space, 0, true, layoutManager);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        switch (layoutManager) {
            case LINEARLAYOUT:
                setLinearLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            case GRIDLAYOUT:
                GridLayoutManager gridLayoutManager = (GridLayoutManager) parent.getLayoutManager();
                //列数
                spanCount = gridLayoutManager.getSpanCount();
                setNGridLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            case STAGGEREDGRIDLAYOUT:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
                //列数
                spanCount = staggeredGridLayoutManager.getSpanCount();
                setNGridLayoutSpaceItemDecoration(outRect, view, parent, state);
                break;
            default:
                break;
        }
    }

    /**
     * LinearLayoutManager spacing
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    private void setLinearLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = spaceh;
        outRect.right = spaceh;
        outRect.bottom = spaceh;
        if (parent.getChildLayoutPosition(view) == 0) {
            outRect.top = spaceh;
        } else {
            outRect.top = 0;
        }
    }

    /**
     * GridLayoutManager or StaggeredGridLayoutManager spacing
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    private void setNGridLayoutSpaceItemDecoration(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view) - headItemCount;
        if (headItemCount != 0 && position == -headItemCount) {
            return;
        }
        int column = position % spanCount;

        if(column == 0){
            outRect.left = edgel;
        }else {
            outRect.left = spacev - column * spacev / spanCount;
        }
        if(column == spanCount-1){
            outRect.right = edger;
        }else {
            outRect.right = (column + 1) * spacev / spanCount;
        }

        if (position < spanCount) {
            outRect.top = edget;
        }

        //
        int lastCount = parent.getAdapter().getItemCount()%spanCount;
        if(lastCount == 0){
            lastCount = spanCount;
        }
        if(parent.getAdapter().getItemCount() - lastCount <= position){
            outRect.bottom = edgeb;
        }else {
            outRect.bottom = spaceh;
        }
    }

}
