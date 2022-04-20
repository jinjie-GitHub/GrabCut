package com.ltzk.mbsf.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * 描述：
 * 作者： on 2019/3/14 14:26
 * 邮箱：499629556@qq.com
 */

public class ParentViewPager extends ViewPager {
    public ParentViewPager(Context context) {
        super(context);
    }

    public ParentViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /** the last x position */
    private float   lastX;

    /** if the first swipe was from left to right (->), dont listen to swipes from the right */
    private boolean slidingLeft;

    /** if the first swipe was from right to left (<-), dont listen to swipes from the left */
    private boolean slidingRight;

    /*@Override
    public boolean onTouchEvent(final MotionEvent ev) {
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                // Disallow parent ViewPager to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(true);

                // save the current x position
                this.lastX = ev.getX();

                break;

            case MotionEvent.ACTION_UP:
                // Allow parent ViewPager to intercept touch events.
                this.getParent().requestDisallowInterceptTouchEvent(false);

                // save the current x position
                this.lastX = ev.getX();

                // reset swipe actions
                this.slidingLeft = false;
                this.slidingRight = false;

                break;

            case MotionEvent.ACTION_MOVE:
                *//*
                 * if this is the first item, scrolling from left to
                 * right should navigate in the surrounding ViewPager
                 *//*
                if (this.getCurrentItem() == 0) {
                    // swiping from left to right (->)?
                    if (this.lastX <= ev.getX() && !this.slidingRight) {
                        // make the parent touch interception active -> parent pager can swipe
                        this.getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        *//*
                         * if the first swipe was from right to left, dont listen to swipes
                         * from left to right. this fixes glitches where the user first swipes
                         * right, then left and the scrolling state gets reset
                         *//*
                        this.slidingRight = true;

                        // save the current x position
                        this.lastX = ev.getX();
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                } else
                *//*
                 * if this is the last item, scrolling from right to
                 * left should navigate in the surrounding ViewPager
                 *//*
                    if (this.getCurrentItem() == this.getAdapter().getCount() - 1) {
                        // swiping from right to left (<-)?
                        if (this.lastX >= ev.getX() && !this.slidingLeft) {
                            // make the parent touch interception active -> parent pager can swipe
                            this.getParent().requestDisallowInterceptTouchEvent(false);
                        } else {
                        *//*
                         * if the first swipe was from left to right, dont listen to swipes
                         * from right to left. this fixes glitches where the user first swipes
                         * left, then right and the scrolling state gets reset
                         *//*
                            this.slidingLeft = true;

                            // save the current x position
                            this.lastX = ev.getX();
                            this.getParent().requestDisallowInterceptTouchEvent(true);
                        }
                    }

                break;
        }

        super.onTouchEvent(ev);
        return true;
    }

    *//**
     * 重写onInterceptTouchEvent()方法来解决图片点击缩小时候的Crash问题
     *
     *//*
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            return super.onInterceptTouchEvent(event);
        } catch (IllegalArgumentException  e) {
            e.printStackTrace();
        }
        return false ;
    }*/

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // pointerIndex out of range
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            // pointerIndex out of range
        }
        return false;
    }



}
