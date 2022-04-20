package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.activity.JiziEditNewActivity;
import com.ltzk.mbsf.activity.JiziTitleUpdateActivity;
import com.ltzk.mbsf.bean.JiziBean;
import com.ltzk.mbsf.fragment.OnItemPositionListener;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JiziListAdapter extends RecyclerView.Adapter<JiziListAdapter.ViewHolder> implements OnItemPositionListener {


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
        callBack.onSelectChange(set);
    }
    CallBack callBack;
    Fragment fragment;
    List<JiziBean> list;

    public List<JiziBean> getList() {
        return list;
    }

    public void setList(List<JiziBean> list) {
        this.list = new ArrayList<>(list);
    }

    public JiziListAdapter(Fragment fragment, CallBack callBack) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        this.callBack = callBack;
    }


    @Override
    public JiziListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_jizi, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(JiziListAdapter.ViewHolder holder, final int position) {
        holder.setPosition(position);

        JiziBean bean = list.get(position);
        //设置标题
        holder.tv_author.setText(bean.get_title());

        //设置图片
        setUrlPic(bean.get_thumbs(),holder,position);

        //判断是否是编辑状态
        if(isEdit){
            holder.lay.setVisibility(View.VISIBLE);
        }else {
            holder.lay.setVisibility(View.GONE);
        }

        //判断集合中是否已有复选框
        if(set.contains(position)){
            holder.cb.setBackgroundResource(R.drawable.shape_solid_blue);
        }else {
            holder.cb.setBackgroundResource(R.drawable.shape_solid_gray);
        }
    }

    private void setImagePic(MyLoadingImageView imageView, int index, List<String> listUrl) {
        if (listUrl.size() > index) {
            final String url = listUrl.get(index);
            if (url != null && url.contains("http")) {
                imageView.setData(fragment, listUrl.get(index), R.color.whiteSmoke, R.color.whiteSmoke);
            } else {
                imageView.setTextData(TextUtils.isEmpty(url) ? "" : url);
            }
        } else {
            imageView.setData(fragment, "", R.color.whiteSmoke, R.color.whiteSmoke);
        }
    }

    private void setUrlPic(List<String> listUrl,ViewHolder holder,int postion){
        if(listUrl==null){
            listUrl = new ArrayList<>();
        }
        setImagePic(holder.iv_thumb_url0,0,listUrl);
        setImagePic(holder.iv_thumb_url1,1,listUrl);
        setImagePic(holder.iv_thumb_url2,2,listUrl);
        setImagePic(holder.iv_thumb_url3,3,listUrl);
        setImagePic(holder.iv_thumb_url4,4,listUrl);
        setImagePic(holder.iv_thumb_url5,5,listUrl);
        setImagePic(holder.iv_thumb_url6,6,listUrl);
        setImagePic(holder.iv_thumb_url7,7,listUrl);
        setImagePic(holder.iv_thumb_url8,8,listUrl);

    }

    @Override
    public int getItemCount() {
        if(list == null){
            return 0;
        }
        return list.size();
    }

    /**
     * 计算列数
     */
    static final String TAG = "JiziListAdapter";
    static final int PHONE = 108;
    static final int TABLET = 108;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;
    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(8);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        width = itemWidth / 3;
        int spacing = ViewUtil.dpToPx(4);
        int colCount = totalWidth / itemWidth;

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

    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R2.id.iv_thumb_url0)
        MyLoadingImageView iv_thumb_url0;
        @BindView(R2.id.iv_thumb_url1)
        MyLoadingImageView iv_thumb_url1;
        @BindView(R2.id.iv_thumb_url2)
        MyLoadingImageView iv_thumb_url2;

        @BindView(R2.id.iv_thumb_url3)
        MyLoadingImageView iv_thumb_url3;
        @BindView(R2.id.iv_thumb_url4)
        MyLoadingImageView iv_thumb_url4;
        @BindView(R2.id.iv_thumb_url5)
        MyLoadingImageView iv_thumb_url5;

        @BindView(R2.id.iv_thumb_url6)
        MyLoadingImageView iv_thumb_url6;
        @BindView(R2.id.iv_thumb_url7)
        MyLoadingImageView iv_thumb_url7;
        @BindView(R2.id.iv_thumb_url8)
        MyLoadingImageView iv_thumb_url8;

        @BindView(R2.id.tv_author)
        TextView tv_author;
        @BindView(R2.id.cb)
        ImageView cb;
        @BindView(R2.id.lay)
        LinearLayout lay;

        int position;
        public void setPosition(int position) {
            this.position = position;
        }

        private void setMyLayoutParam(MyLoadingImageView view,int width,int height){
            TableRow.LayoutParams layoutParam =  (TableRow.LayoutParams)view.getLayoutParams();
            layoutParam.width = width;
            layoutParam.height = height;
            view.setLayoutParams(layoutParam);
        }

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setMyLayoutParam(iv_thumb_url0,width,width);
            setMyLayoutParam(iv_thumb_url1,width,width);
            setMyLayoutParam(iv_thumb_url2,width,width);
            setMyLayoutParam(iv_thumb_url3,width,width);
            setMyLayoutParam(iv_thumb_url4,width,width);
            setMyLayoutParam(iv_thumb_url5,width,width);
            setMyLayoutParam(iv_thumb_url6,width,width);
            setMyLayoutParam(iv_thumb_url7,width,width);
            setMyLayoutParam(iv_thumb_url8,width,width);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)lay.getLayoutParams();
            layoutParams.height = width*3+ViewUtil.dpToPx(12);
            lay.setLayoutParams(layoutParams);

            //点击复选框
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!set.contains(position)){
                        set.add(position);
                        callBack.onSelectChange(set);
                        cb.setBackgroundResource(R.drawable.shape_solid_blue);
                    }else {
                        set.remove(position);
                        callBack.onSelectChange(set);
                        cb.setBackgroundResource(R.drawable.shape_solid_gray);
                    }
                }
            });

            //点击名称 跳转到修改名称界面
            tv_author.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startActivity(new Intent(activity, JiziTitleUpdateActivity.class).putExtra("jiziBean",list.get(position)));
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(XClickUtil.isFastDoubleClick(v)){
                        return;
                    }
                    //activity.startActivity(new Intent(activity, JiziEditActivity.class).putExtra("jiziBean",list.get(position)));
                    JiziEditNewActivity.safeStart(activity, list.get(position));
                }
            });
        }
    }
    public interface CallBack{
        public void onSelectChange(HashSet<Integer> set);
        void onItemSwap(int from, int target);
    }

    @Override
    public void onItemSwap(int from, int target) {
        //Collections.swap(mDatas, from, target);
        //交换数据
        JiziBean s = list.get(from);
        list.remove(from);
        list.add(target,s);
        Log.e("onItemSwap","from:"+from+" target:"+target);
        notifyItemMoved(from, target);
    }

    @Override
    public void onItemMoved(int position) {
        list.remove(position);
        Log.e("onItemSwap","position:"+position);
        notifyItemRemoved(position);
    }

    @Override
    public void onUpdate(int from, int target) {
        callBack.onItemSwap(from, target);
    }
}
