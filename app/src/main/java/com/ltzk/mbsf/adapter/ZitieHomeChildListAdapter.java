package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;
import com.ltzk.mbsf.widget.RotateTextView;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class ZitieHomeChildListAdapter extends RecyclerView.Adapter<ZitieHomeChildListAdapter.ViewHolder> {
    Fragment fragment;
    Activity activity;
    List<Object> list;
    private boolean isOneLine = false;
    public boolean isOneLine() {
        return isOneLine;
    }
    public void setOneLine(boolean oneLine) {
        isOneLine = oneLine;
    }

    private String type;

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    int width;

    public ZitieHomeChildListAdapter(Activity activity,OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.activity = activity;
        init();
    }

    public ZitieHomeChildListAdapter(Fragment fragment,OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        init();
    }

    private void init(){
        list = new ArrayList<Object>();
    }

    /**
     * 计算列数
     */
    static final String TAG = "king";
    static final int PHONE = 108;
    static final int TABLET = 108;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;
    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(12);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        int spacing = ViewUtil.dpToPx(3);
        int colCount = totalWidth / itemWidth;
        width = itemWidth;

        //获取有效的列数
        while (true) {
            if (colCount == 0) {
                colCount = 1;
                break;
            } else {
                int result = totalWidth - (itemWidth * colCount + spacing * (colCount - 1));
                if (result > 3) {
                    break;
                }
                colCount--;
            }
        }
        Log.d(TAG, "colCount=" + colCount);
        return colCount;
    }

    @Override
    public int getItemCount() {
        if(isOneLine){
            return Math.min(6,list.size());
        }else {
            return list.size();
        }
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
    public ZitieHomeChildListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.adapter_zitieji_home, parent, false);
        ZitieHomeChildListAdapter.ViewHolder viewHolder = new ZitieHomeChildListAdapter.ViewHolder(v,width);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ZitieHomeBean.ListBeanX.ListBean bean;
        String type_cur = getType();
        if(TextUtils.isEmpty(type_cur)){//检测是否有强制item数据类型
            ZitieHomeBean.ListBeanX.ListBeanAndType listBeanAndType = (ZitieHomeBean.ListBeanX.ListBeanAndType)list.get(position);
            type_cur = listBeanAndType.get_type();
            bean = listBeanAndType.get_data();
        }else {
            bean = (ZitieHomeBean.ListBeanX.ListBean)list.get(position);
        }

        String name = "";
        String url = "";
        switch (type_cur) {
            case ZitieHomeBean.type_author:
                if(bean.get_dynasty()!=null){
                    name = bean.get_dynasty()+" • "+bean.get_name();
                }else {
                    name = bean.get_name();
                }
                url = bean.get_head();
                if(bean.get_head() == null || bean.get_head().equals("")){
                    url = bean.get_cover_url();
                }
                holder.tv_author.setVisibility(View.GONE);
                holder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                break;
            case ZitieHomeBean.type_gallery:
                name = bean.get_title();
                url = bean.get_cover_url();
                holder.tv_author.setVisibility(View.GONE);
                holder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                break;
            case ZitieHomeBean.type_zuopin:
                name = bean.get_name();
                url = bean.get_cover_url();
                holder.tv_author.setText(bean.get_author()+"");
                holder.tv_author.setVisibility(View.VISIBLE);
                holder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                break;
            case ZitieHomeBean.type_zitie:
                name = bean.get_name();
                url = bean.get_cover_url();
                if (bean._video == 1) {
                    holder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                } else {
                    holder.tv_name.setTextColor(activity.getResources().getColor((!TextUtils.isEmpty(bean.get_hd()) && "1".equals(bean.get_hd())) ? R.color.colorPrimary : R.color.dimGray));
                }
                holder.tv_author.setVisibility(TextUtils.isEmpty(bean.get_author()) ? View.GONE : View.VISIBLE);
                holder.tv_author.setText(bean.get_author() + "");
                break;
        }
        if(bean.get_subtitle()!=null && !bean.get_subtitle().equals("")){
            name = name +"\n"+bean.get_subtitle();
        }
        holder.tv_name.setText(name);
        if (bean.get_free()==null || bean.get_free().equals("")|| "1".equals(bean.get_free())){
            holder.rtv.setVisibility(View.GONE);
        }else {
            holder.rtv.setVisibility(View.VISIBLE);
        }
        if(fragment!=null){
            holder.iv_thumb_url.setData(fragment,url,R.mipmap.ic_launcher, ImageView.ScaleType.CENTER_CROP);
        }else {
            holder.iv_thumb_url.setData(activity,url,R.mipmap.ic_launcher, ImageView.ScaleType.CENTER_CROP);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(list.get(position),position);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R2.id.iv_thumb_url)
        MyLoadingImageView iv_thumb_url;
        @BindView(R2.id.tv_author)
        TextView tv_author;
        @BindView(R2.id.tv_name)
        TextView tv_name;
        @BindView(R2.id.rtv)
        RotateTextView rtv;
        ViewHolder(View view,int width) {
            super(view);
            ButterKnife.bind(this, view);
            tv_author.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)iv_thumb_url.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = width;
            iv_thumb_url.setLayoutParams(layoutParams);
        }
    }

    OnItemClickListener onItemClickListener;
    public interface OnItemClickListener{
        public void onItemClick(Object object,int position);
    }
}
