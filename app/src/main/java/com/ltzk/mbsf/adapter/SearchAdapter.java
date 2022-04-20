package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.base.MyBaseAdapter;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.SwipeListLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：搜索关键字列表
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class SearchAdapter extends MyBaseAdapter {

    String mKey = "";

    public void setKey(String key) {
        this.mKey = key;
    }

    Activity activity;
    int width;
    private LayoutInflater inflater;
    OnItemDelCallBack onItemDelCallBack;
    public SearchAdapter(Activity activity,OnItemDelCallBack onItemDelCallBack) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<String>();
        this.onItemDelCallBack = onItemDelCallBack;
        width = (activity.getWindowManager().getDefaultDisplay().getWidth() - ViewUtil.dpToPx(35))/6;
    }

    @Override
    public int getCount() {
        return list!=null?list.size():0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final String bean = (String)list.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_search, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int start = bean.indexOf(mKey);
        int end = start+mKey.length();

        if (start != end) {
            try {
                if (start < 0) {
                    start = 0;
                }
                if (end > bean.length()) {
                    end = bean.length();
                }
                SpannableString spannableString = new SpannableString(bean + "");
                ForegroundColorSpan span = new ForegroundColorSpan(activity.getResources().getColor(R.color.colorPrimary));
                spannableString.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.tv_key.setText(spannableString);
            } catch (Exception e) {
                Log.d("--->", "Search:" + e.getMessage());
            }
        } else {
            holder.tv_key.setText(bean + "");
        }

        holder.tv_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelCallBack.click(bean,position);
            }
        });
        holder.iv_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelCallBack.to(bean);
                notifyDataSetChanged();
            }
        });
        holder.sll_main.setOnSwipeStatusListener(new MyOnSlipStatusListener(holder.sll_main));
        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemDelCallBack.del(bean);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @BindView(R2.id.tv_key)
        TextView tv_key;
        @BindView(R2.id.iv_to)
        ImageView iv_to;
        @BindView(R2.id.sll_main)
        SwipeListLayout sll_main;
        @BindView(R2.id.tv_delete)
        TextView tv_delete;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    private Set<SwipeListLayout> sets = new HashSet();

    public interface OnItemDelCallBack{
        public void del(String string);
        public void click(String string,int position);
        public void to(String string);
    }

    class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }
    }
}
