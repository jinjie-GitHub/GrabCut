package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.bean.ZilibBean;
import com.ltzk.mbsf.fragment.OnItemPositionListener;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.utils.XClickUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ZiLibListAdapter extends RecyclerView.Adapter<ZiLibListAdapter.ViewHolder> implements OnItemPositionListener {


    Activity activity;
    int width;
    CallBack mCallBack;
    Fragment fragment;
    List<ZilibBean> list;

    public List<ZilibBean> getList() {
        return list;
    }

    public void setList(List<ZilibBean> list) {
        this.list = new ArrayList<>(list);
    }

    public ZiLibListAdapter(Fragment fragment,CallBack callBack) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        mCallBack = callBack;
    }


    @Override
    public ZiLibListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_zilib, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ZiLibListAdapter.ViewHolder holder, final int position) {
        holder.setPosition(position);

        ZilibBean bean = list.get(position);

        if (TextUtils.isEmpty(bean.get_author())) {
            holder.author.setText("");
            holder.author_line.setVisibility(View.GONE);
        } else {
            holder.author.setText(bean.get_author());
            holder.author_line.setVisibility(View.VISIBLE);
        }

        //设置标题
        holder.title.setText(bean.get_title());
        holder.title.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.black));
        holder.vip.setVisibility(bean._free == 0 ? View.VISIBLE : View.GONE);
        //设置图片
        setUrlPic(bean.get_thumbs(), holder, position);

    }

    private void setImagePic(MyLoadingImageView imageView, int index, List<String> listUrl) {
        imageView.setTextData("");
        if (index < listUrl.size()) {
            final String url = listUrl.get(index);
            if (url.startsWith("https") || url.startsWith("http")) {
                imageView.setData(fragment, url, R.color.whiteSmoke, R.color.whiteSmoke);
            } else {
                imageView.setTextData(url);
            }
        } else {
            imageView.setData(fragment, "", R.color.whiteSmoke, R.color.whiteSmoke);
        }
    }

    private void setUrlPic(List<String> listUrl, ViewHolder holder, int postion) {
        if (listUrl == null) {
            listUrl = new ArrayList<>();
        }
        setImagePic(holder.iv_thumb_url0, 0, listUrl);
        setImagePic(holder.iv_thumb_url1, 1, listUrl);
        setImagePic(holder.iv_thumb_url2, 2, listUrl);
        setImagePic(holder.iv_thumb_url3, 3, listUrl);
        setImagePic(holder.iv_thumb_url4, 4, listUrl);
        setImagePic(holder.iv_thumb_url5, 5, listUrl);
        setImagePic(holder.iv_thumb_url6, 6, listUrl);
        setImagePic(holder.iv_thumb_url7, 7, listUrl);
        setImagePic(holder.iv_thumb_url8, 8, listUrl);
        setImagePic(holder.iv_thumb_url9, 9, listUrl);
        setImagePic(holder.iv_thumb_url10, 10, listUrl);
        setImagePic(holder.iv_thumb_url11, 11, listUrl);
        setImagePic(holder.iv_thumb_url12, 12, listUrl);
        setImagePic(holder.iv_thumb_url13, 13, listUrl);
        setImagePic(holder.iv_thumb_url14, 14, listUrl);
        setImagePic(holder.iv_thumb_url15, 15, listUrl);
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
    static final String TAG = "ZiLibListAdapter";
    static final int PHONE = 108;
    static final int TABLET = 108;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;
    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(8);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        width = itemWidth / 4;
        int spacing = ViewUtil.dpToPx(4);
        int colCount = totalWidth / itemWidth;

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
        @BindView(R2.id.iv_thumb_url9)
        MyLoadingImageView iv_thumb_url9;
        @BindView(R2.id.iv_thumb_url10)
        MyLoadingImageView iv_thumb_url10;
        @BindView(R2.id.iv_thumb_url11)
        MyLoadingImageView iv_thumb_url11;

        @BindView(R2.id.iv_thumb_url12)
        MyLoadingImageView iv_thumb_url12;
        @BindView(R2.id.iv_thumb_url13)
        MyLoadingImageView iv_thumb_url13;
        @BindView(R2.id.iv_thumb_url14)
        MyLoadingImageView iv_thumb_url14;
        @BindView(R2.id.iv_thumb_url15)
        MyLoadingImageView iv_thumb_url15;

        @BindView(R2.id.title)
        TextView title;
        @BindView(R2.id.lay)
        LinearLayout lay;

        @BindView(R.id.author)
        TextView author;
        @BindView(R.id.author_line)
        View author_line;
        @BindView(R.id.vip)
        View vip;

        int position;
        public void setPosition(int position) {
            this.position = position;
        }

        private void setMyLayoutParam(MyLoadingImageView view,int width,int height){
            view.setOnClickListener(mClickListener);
            view.setOnLongClickListener(mLongClickListener);

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
            setMyLayoutParam(iv_thumb_url9,width,width);
            setMyLayoutParam(iv_thumb_url10,width,width);
            setMyLayoutParam(iv_thumb_url11,width,width);
            setMyLayoutParam(iv_thumb_url12,width,width);
            setMyLayoutParam(iv_thumb_url13,width,width);
            setMyLayoutParam(iv_thumb_url14,width,width);
            setMyLayoutParam(iv_thumb_url15,width,width);

            //点击名称 跳转到修改名称界面
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(XClickUtil.isFastDoubleClick(v)){
                        return;
                    }
                    mCallBack.onItemNameClick(list.get(position));
                }
            });

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(XClickUtil.isFastDoubleClick(v)){
                        return;
                    }
                    mCallBack.onItemClick(list.get(position));
                }
            });*/
        }

        private View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onItemClick(list.get(position));
            }
        };

        private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCallBack.onItemLongClick(v, list.get(position), position);
                return false;
            }
        };
    }
    public interface CallBack{
        void onItemClick(ZilibBean zilibBean);
        void onItemNameClick(ZilibBean zilibBean);
        void onItemLongClick(View v, ZilibBean zilibBean, int position);
        void onItemSwap(int from, int target);
    }

    @Override
    public void onItemSwap(int from, int target) {
        //Collections.swap(mDatas, from, target);
        //交换数据
        ZilibBean s = list.get(from);
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
        mCallBack.onItemSwap(from,target);
    }
}
