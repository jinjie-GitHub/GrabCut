package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class ZiSelectPopAdapter extends RecyclerView.Adapter<ZiSelectPopAdapter.ViewHolder> {

    Activity activity;
    int width;
    List<ZiBean> list;
    public ZiSelectPopAdapter(Activity activity, ZiSelectPopAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.activity = activity;
        list = new ArrayList<ZiBean>();
        width = ((int)ViewUtil.getWidth() - ViewUtil.dpToPx(55))/6;
    }

    public void setData(List list) {
        if (list != null) {
            this.list = list;
        }else{
            this.list.clear();
        }
    }

    public void addData(List list) {
        if (list != null) {
            this.list.addAll(list);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ZiSelectPopAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.adapter_zi_popview, parent, false);
        ZiSelectPopAdapter.ViewHolder viewHolder = new ZiSelectPopAdapter.ViewHolder(v,width);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ZiSelectPopAdapter.ViewHolder holder, int position) {
        ZiBean bean = list.get(position);
        holder.tv_author.setText(bean.get_hanzi());
        holder.iv_thumb_url.setData(activity,bean.getUrlThumb(),0);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(bean,position);
            }
        });
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R2.id.iv_thumb_url)
        MyLoadingImageView iv_thumb_url;
        @BindView(R2.id.tv_author)
        TextView tv_author;
        ViewHolder(View view,int width) {
            super(view);
            ButterKnife.bind(this, view);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width,width);
            iv_thumb_url.setLayoutParams(layoutParams);
        }
    }

    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        public void onItemClick(ZiBean bean,int position);
    }
}
