package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.base.MyBaseAdapter;
import com.ltzk.mbsf.bean.MenuItemBean;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class MenuAdapter extends MyBaseAdapter {

    Activity activity;


    private LayoutInflater inflater;
    public MenuAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<MenuItemBean>();
    }

    @Override
    public int getCount() {
        return list.size();
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
        final MenuItemBean bean = (MenuItemBean)list.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_menu, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(bean.getName());
        if (bean.getRes() > 0) {
            holder.iv_icon.setImageResource(bean.getRes());
        } else {
            holder.tv_name.setPadding(6,10,6,10);
            holder.tv_name.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary));
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R2.id.iv_icon)
        ImageView iv_icon;
        @BindView(R2.id.tv_name)
        TextView tv_name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
