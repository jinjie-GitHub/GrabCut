package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.bean.ZiBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;

/**
 * 描述：字列表
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class ZiListAdapter extends BaseQuickAdapter<ZiBean, BaseViewHolder> {

    /**
     * 是否是印章
     */
    boolean isYin = false;
    public void setYin(boolean yin) {
        isYin = yin;
    }

    /**
     * 当前选中的作者
     */
    String mAuthorSelect;
    public void setmAuthorSelect(String mAuthorSelect) {
        this.mAuthorSelect = mAuthorSelect;
    }

    /**
     * 是否大图
     */
    boolean isBigItem;
    public void setBigItem(boolean bigItem) {
        isBigItem = bigItem;
    }

    Activity activity;
    Fragment fragment;
    int width;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ZiListAdapter(Fragment fragment,int width) {
        super(R.layout.adapter_zi_home);
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.width = width;
    }

    public ZiListAdapter(Activity activity,int width) {
        super(R.layout.adapter_zi_home);
        this.activity = activity;
        this.width = width;
    }

    @Override
    protected void convert(BaseViewHolder holder, ZiBean bean) {
        holder.setTextColor(R.id.tv_name, activity.getResources().getColor(bean._video_count > 0 ? R.color.orange : R.color.black));

        TextView tv_name = holder.getView(R.id.tv_name);
        LinearLayout linearLayout = holder.getView(R.id.lay_bottom);
        LinearLayout.LayoutParams layoutParams_lin = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        ((TextView)holder.getView(R.id.tv_name)).setSingleLine(true);
        if(isBigItem){//大图
            tv_name.setTextSize(11);
            layoutParams_lin.height = ViewUtil.dpToPx(28);
        }else {//小图
            tv_name.setTextSize(9);
            layoutParams_lin.height = ViewUtil.dpToPx(20);
        }
        linearLayout.setLayoutParams(layoutParams_lin);

        if(TextUtils.isEmpty(bean.get_author()) || (!TextUtils.isEmpty(mAuthorSelect) && !mAuthorSelect.equals("书法家"))){
            //无作者或者单作者
            if(isYin){//印章
                holder.setText(R.id.tv_name,bean.get_hanzi()+"");
            }else {
                holder.setText(R.id.tv_name,bean.get_from()+"");
            }
            holder.setGone(R.id.tv_author,true);

        }else {
            if(isBigItem){//大图
                if(isYin){
                    holder.setText(R.id.tv_name,bean.get_hanzi()+"");
                }else {
                    holder.setText(R.id.tv_name,bean.get_from()+"");
                }
                holder.setText(R.id.tv_author,bean.get_author()+"");
                holder.setGone(R.id.tv_author,false);

            }else {//小图
                holder.setText(R.id.tv_name,bean.get_author()+"");
                holder.setGone(R.id.tv_author,true);
            }
        }


        MyLoadingImageView myLoadingImageView = holder.getView(R.id.iv_thumb_url);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)myLoadingImageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        myLoadingImageView.setLayoutParams(layoutParams);

        if (TextUtils.isEmpty(bean.get_color_image())) {
            View miView = holder.getView(R.id.miView);
            miView.setLayoutParams(layoutParams);
            holder.setVisible(R.id.miView, true);
        } else {
            holder.setVisible(R.id.miView, false);
        }

        if(fragment == null){
            myLoadingImageView.setData(activity,bean.getUrlThumb(),-1);
        }else {
            myLoadingImageView.setData(fragment,bean.getUrlThumb(),-1);
        }
    }
}
