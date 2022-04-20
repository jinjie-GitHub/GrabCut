package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;
import com.ltzk.mbsf.widget.RotateTextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class ZitieListAdapter extends RecyclerView.Adapter<ZitieListAdapter.ViewHolder>  {

    Activity activity;
    int width;
    boolean isEdit = false;

    public boolean isEdit() {
        return isEdit;
    }
    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    HashSet<Integer> set=new HashSet<Integer>();

    public HashSet<Integer> getSet() {
        return set;
    }

    public void setClear() {
        set.clear();
    }

    List<ZitieBean> list;
    public ZitieListAdapter(Activity activity,OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.activity = activity;
        list = new ArrayList<ZitieBean>();
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
    public ZitieListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.adapter_zitie, parent, false);
        ZitieListAdapter.ViewHolder viewHolder = new ZitieListAdapter.ViewHolder(v,width);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ZitieListAdapter.ViewHolder holder, int position) {

        ZitieBean bean = (ZitieBean)list.get(position);


        if(bean.get_hd()!=null && bean.get_hd().equals("1")){
            holder.tv_author.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        }else {
            holder.tv_author.setTextColor(activity.getResources().getColor(R.color.black));
        }

        if(bean.get_subtitle()!=null && !bean.get_subtitle().equals("")){
            holder.tv_author.setText(bean.get_name()+"\n"+bean.get_subtitle());
        }else {
            holder.tv_author.setText(bean.get_name());
        }

        holder.tv_author.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
        holder.iv_thumb_url.setData(activity,bean.get_cover_url(),R.mipmap.ic_launcher,ImageView.ScaleType.FIT_CENTER);
        if ("1".equals(bean.get_free())){
            holder.rtv.setVisibility(View.GONE);
        }else {
            holder.rtv.setVisibility(View.VISIBLE);
        }
        if(isEdit){
            holder.lay.setVisibility(View.VISIBLE);
        }else {
            holder.lay.setVisibility(View.GONE);
        }

        if(set.contains(position)){
            holder.cb.setBackgroundResource(R.drawable.shape_solid_blue);
        }else {
            holder.cb.setBackgroundResource(R.drawable.shape_solid_gray);
        }

        holder.lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!set.contains(position)){
                    set.add(position);
                    holder.cb.setBackgroundResource(R.drawable.shape_solid_blue);
                    if(callBack!=null){
                        callBack.onSelectChange(set);
                    }
                }else {
                    set.remove(position);
                    holder.cb.setBackgroundResource(R.drawable.shape_solid_gray);
                    if(callBack!=null){
                        callBack.onSelectChange(set);
                    }
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(bean,position);
            }
        });
    }

    /**
     * 计算列数
     */
    static final String TAG = "ZitieListAdapter";
    static final int PHONE = 108;
    static final int TABLET = 108;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;
    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(8);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        int spacing = ViewUtil.dpToPx(4);
        int colCount = totalWidth / itemWidth;
        width = itemWidth;

        //获取有效的列数
        while (true) {
            if (colCount == 0) {
                colCount = 1;
                break;
            } else {
                int result = totalWidth - (itemWidth * colCount + spacing * (colCount - 1));
                if (result >= 1) {
                    break;
                }
                colCount--;
            }
        }
        Log.d(TAG, "colCount=" + colCount);
        return colCount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R2.id.iv_thumb_url)
        MyLoadingImageView iv_thumb_url;
        @BindView(R2.id.tv_author)
        TextView tv_author;
        @BindView(R2.id.rtv)
        RotateTextView rtv;
        @BindView(R2.id.cb)
        ImageView cb;
        @BindView(R2.id.lay)
        LinearLayout lay;

        ViewHolder(View view, int width) {
            super(view);
            ButterKnife.bind(this, view);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv_thumb_url.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = width;
            iv_thumb_url.setLayoutParams(layoutParams);
            lay.setLayoutParams(layoutParams);

            /*tv_author.post(() -> {
                LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) tv_author.getLayoutParams();
                layoutParams2.width = ViewGroup.LayoutParams.MATCH_PARENT;
                tv_author.setLayoutParams(layoutParams2);
            });*/
        }
    }
    CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack{
        public void onSelectChange(HashSet<Integer> set);
    }

    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        public void onItemClick(ZitieBean bean, int position);
    }
}
