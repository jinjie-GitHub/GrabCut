package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.bean.ZiAuthorBean;

/**
 * 描述：字作者
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class ZiListAuthorAdapter extends BaseQuickAdapter<ZiAuthorBean, BaseViewHolder> {

    String itemNameSelect = "";

    public String getItemNameSelect() {
        return itemNameSelect;
    }

    public void setItemNameSelect(String itemNameSelect) {
        this.itemNameSelect = itemNameSelect;
    }

    Activity activity;
    Fragment fragment;

    public ZiListAuthorAdapter(Activity activity) {
        super(R.layout.adapter_zi_author);
        this.activity = activity;
    }

    public ZiListAuthorAdapter(Fragment fragment) {
        super(R.layout.adapter_zi_author);
        this.fragment = fragment;
        this.activity = fragment.getActivity();
    }

    @Override
    protected void convert(BaseViewHolder holder, ZiAuthorBean bean) {
        if(bean == null){
            return;
        }
        TextView tv_author = holder.getView(R.id.tv_author);
        tv_author.setText(bean.get_name()+"");
        if(bean.get_num()>0){
            holder.setText(R.id.tv_num,bean.get_num()+"");
        }else {
            holder.setText(R.id.tv_num,"");
        }

        if(!TextUtils.isEmpty(bean.get_name()) && bean.get_name().equals(itemNameSelect)){
            tv_author.setBackground(activity.getResources().getDrawable(R.drawable.selector_layer_list_blue));
            tv_author.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
            holder.setVisible(R.id.iv_right,true);
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.white));
            tv_author.getPaint().setFakeBoldText(true);
        }else {
            tv_author.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
            tv_author.setTextColor(activity.getResources().getColor(R.color.black));
            holder.setVisible(R.id.iv_right,false);
            holder.itemView.setBackgroundColor(activity.getResources().getColor(R.color.transparent));
            tv_author.getPaint().setFakeBoldText(false);
        }

        //收藏的或者选中的都是蓝色
        final boolean state = bean.isFav() || (!TextUtils.isEmpty(bean.get_name()) && bean.get_name().equals(itemNameSelect));
        tv_author.setTextColor(ContextCompat.getColor(activity, state ? R.color.colorPrimary : R.color.black));
    }
}
