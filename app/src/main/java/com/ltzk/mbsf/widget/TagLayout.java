package com.ltzk.mbsf.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.ltzk.mbsf.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class TagLayout extends ViewGroup {
    List<Rect> childrenBounds = new ArrayList<>();
    List<List<Rect>> childrenBoundsGroup = new ArrayList<>();
    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /*if(getChildCount() == 0){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            return;
        }*/

        //已用宽度
        int widthUsed = 0;
        //已用高度
        int heightUsed = 0;
        //当前行已用宽度
        int lineWidthUsed = 0;
        //当前行最大高度
        int lineMaxHeight = 0;

        //获取宽度模式
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        //获取可用最大宽度值
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        Log.e("10","onLayout>>getChildCount:"+getChildCount());
        //遍历子view
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            //measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            if (specMode != MeasureSpec.UNSPECIFIED &&
                    lineWidthUsed + child.getMeasuredWidth()+ViewUtil.dpToPx(10) > (specWidth - getPaddingLeft() - getPaddingRight())) {//宽度模式为不指定 并且 大于可用宽度时 换行
                lineWidthUsed = 0;//重置当前行已用宽度为0
                heightUsed = heightUsed + lineMaxHeight + ViewUtil.dpToPx(10); //计算已用高度
                lineMaxHeight = 0; //重置该行现在最大高度为0
                //measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, heightUsed);
                measureChild(child,widthMeasureSpec,heightMeasureSpec);
            }
            Rect childBound;
            if (childrenBounds.size() <= i) {
                childBound = new Rect();
                childrenBounds.add(childBound);
            } else {
                childBound = childrenBounds.get(i);
            }
            childBound.set(getPaddingLeft() + lineWidthUsed, heightUsed + getPaddingTop(), getPaddingLeft() + lineWidthUsed + child.getMeasuredWidth()+ViewUtil.dpToPx(10), heightUsed + child.getMeasuredHeight() + getPaddingTop());
            lineWidthUsed += child.getMeasuredWidth()+ViewUtil.dpToPx(10);//计算该行已用宽度
            widthUsed = Math.max(widthUsed, lineWidthUsed);//每次更新所有行的最大宽度，用来指定大布局的宽度
            lineMaxHeight = Math.max(lineMaxHeight, child.getMeasuredHeight());//每次更新所有行的最大高度，用来指定最大布局的高度
        }
        int width = widthUsed;
        width = Math.max(width, specWidth );
        int height = heightUsed + lineMaxHeight + getPaddingTop() + getPaddingBottom() + ViewUtil.dpToPx(10);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(childrenBounds.size() == 0 ){
            return;
        }

        int index = 0;
        int topTemp = childrenBounds.get(0).top;
        int width = getMeasuredWidth() - getPaddingRight();

        List<Rect> rectList = new ArrayList<>();
        childrenBoundsGroup.clear();
        for (int i = 0; i < getChildCount(); i++) {
            Rect childBounds = childrenBounds.get(i);
            if(childBounds.top == topTemp){
                rectList.add(childBounds);
            }else {
                //整行加入
                if(!rectList.isEmpty()){
                    childrenBoundsGroup.add(rectList);
                }
                rectList = new ArrayList<>();
                rectList.add(childBounds);
                //换行
                topTemp = childBounds.top;
            }
        }
        //整行加入
        if(!rectList.isEmpty()){
            childrenBoundsGroup.add(rectList);
        }

        for(int j = 0;j<childrenBoundsGroup.size();j++){
            List<Rect> childs = childrenBoundsGroup.get(j);
            int size = childs.size();
            float margin = 0;
            if(size>1 && j!=childrenBoundsGroup.size()-1){
                margin = (width - childs.get(size-1).right)/(size-1);
            }
            for (int i = 0; i < childs.size(); i++) {
                View child = getChildAt(index);
                if(child == null){
                    return;
                }
                index++;
                Rect childBounds = childs.get(i);
                int marginCur =(int) margin * i;
                child.layout( marginCur + childBounds.left+ViewUtil.dpToPx(5), childBounds.top, childBounds.right + marginCur-ViewUtil.dpToPx(5), childBounds.bottom);
            }
        }
        childrenBounds.clear();
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }
}
