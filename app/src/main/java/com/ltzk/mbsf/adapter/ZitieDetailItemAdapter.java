package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.bean.ZitieDetailitemBean;
import com.ltzk.mbsf.widget.MyLoadingImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * update
 */
public class ZitieDetailItemAdapter extends RecyclerView.Adapter<ZitieDetailItemAdapter.ViewHolder> {

    int select = 0;

    public void setSelect(int select) {
        notifyItemChanged(this.select);
        this.select = select;
        notifyItemChanged(select);
    }

    private ItemClickListener clickListener;
    public void setOntiemmClickListener(ItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    Activity activity;
    Fragment fragment;
    List<ZitieDetailitemBean> list;

    public ZitieDetailItemAdapter(Activity activity) {
        this.activity = activity;
        list = new ArrayList<>();
    }

    public void setData(List<ZitieDetailitemBean> list) {
        this.list = list;
    }

    /**
     * 收藏的字帖
     */
    public final void setFavs(final List<Integer> favs) {
        try {
            if (favs != null && list != null) {
                final int count = list.size();
                for (Integer fav : favs) {
                    final int realPos = count - fav;
                    if (realPos >= 0 && realPos < list.size()) {
                        list.get(realPos).pos = realPos;
                    }
                }
            }
        } catch (Exception e) {}
    }

    /**
     * 选中/反选
     */
    public void setChecked(int page, boolean checked) {
        try {
            final int realPos = getItemCount() - page;
            list.get(realPos).pos = checked ? realPos : -1;
            notifyItemChanged(realPos);
        } catch (Exception e) {}
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_zitie_detail, viewGroup, false);
        return new ViewHolder(this, v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        ZitieDetailitemBean bean = list.get(position);
        holder.setPosition(position);
        if (fragment == null) {
            holder.iv_thumb_url.setData(activity, bean.get_thumb(), R.mipmap.ic_launcher, ImageView.ScaleType.CENTER_CROP);
        } else {
            holder.iv_thumb_url.setData(fragment, bean.get_thumb(), R.mipmap.ic_launcher, ImageView.ScaleType.CENTER_CROP);
        }

        if (position == select) {
            holder.rel_bg.setBackgroundColor(Color.RED);
        } else {
            holder.rel_bg.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
        }

        final int count = list.size();
        holder.mIndex.setText(String.valueOf(count - position));
        if (position == bean.pos) {
            holder.tv_mark.setVisibility(View.VISIBLE);
        } else {
            holder.tv_mark.setVisibility(View.GONE);
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R2.id.iv_thumb_url)
        MyLoadingImageView iv_thumb_url;
        @BindView(R2.id.rel_bg)
        RelativeLayout rel_bg;
        @BindView(R2.id.tv_mark)
        View tv_mark;
        @BindView(R2.id.index)
        TextView mIndex;
        int position;

        public void setPosition(int position) {
            this.position = position;
        }

        public ViewHolder(final ZitieDetailItemAdapter adapter, View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.clickListener.onClick(v, position, false);
                }
            });
        }
    }

    public interface ItemClickListener {
        void onClick(View view, int position, boolean isLongClick);
    }
}