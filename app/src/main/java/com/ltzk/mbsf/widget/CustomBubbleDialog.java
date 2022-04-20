package com.ltzk.mbsf.widget;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.utils.ViewUtil;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * 描述：
 * 作者： on 2019/1/25 13:32
 * 邮箱：499629556@qq.com
 */

public class CustomBubbleDialog extends BubbleDialog implements View.OnClickListener {

    public CustomBubbleDialog(Context context) {
        super(context);
    }
    public CustomBubbleDialog(Context context, OnClickCustomButtonListener mListener) {
        super(context);
        setOffsetY(-10);
        calBar(true);
        setTransParentBackground();
        setPosition(Position.BOTTOM);
        View rootView = LayoutInflater.from(context).inflate(R.layout.widgets_del_popview, null);
        mViewHolder = new ViewHolder(rootView);
        addContentView(rootView);
        BubbleLayout bl = new BubbleLayout(context);
        //箭头的长度
        bl.setLookLength(ViewUtil.dpToPx(15));
        //	气泡四角的圆弧
        bl.setBubbleRadius(ViewUtil.dpToPx(2));
        bl.setShadowRadius(0);
        setBubbleLayout(bl);
        mViewHolder.tv.setOnClickListener(this);

        this.mListener = mListener;
    }
    ViewHolder mViewHolder;
    private static class ViewHolder
    {
        TextView tv;
        public ViewHolder(View rootView)
        {
            tv = rootView.findViewById(R.id.tv_del);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (mListener != null)
        {
            dismiss();
            mListener.onClick();
        }
    }

    OnClickCustomButtonListener mListener;
    public void setClickListener(OnClickCustomButtonListener l)
    {
        this.mListener = l;
    }

    public interface OnClickCustomButtonListener
    {
        void onClick();
    }
}
