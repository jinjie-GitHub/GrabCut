package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.base.MyBaseAdapter;
import com.ltzk.mbsf.bean.ZitieBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;
import com.ltzk.mbsf.widget.RotateTextView;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class ChaodaiAdapter extends MyBaseAdapter {

    Activity activity;

    int indexSelect = -1;

    public int getIndexSelect() {
        return indexSelect;
    }

    public void setIndexSelect(int indexSelect) {
        this.indexSelect = indexSelect;
    }

    private LayoutInflater inflater;
    public ChaodaiAdapter(Activity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        list = new ArrayList<String>();
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
        final String bean = (String)list.get(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_chaodai, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv_name.setText(bean);
        if(indexSelect == position){
            holder.tv_name.setBackgroundColor(activity.getResources().getColor(R.color.whiteSmoke));
        }else {
            holder.tv_name.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
        }
        holder.tv_name.getPaint().setFakeBoldText(true);
        return convertView;
    }

    static class ViewHolder {
        @BindView(R2.id.tv_name)
        TextView tv_name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
