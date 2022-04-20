package com.ltzk.mbsf.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ltzk.mbsf.MainApplication;
import com.ltzk.mbsf.R;
import com.ltzk.mbsf.R2;
import com.ltzk.mbsf.bean.ZitieHomeBean;
import com.ltzk.mbsf.utils.ViewUtil;
import com.ltzk.mbsf.widget.MyLoadingImageView;
import com.ltzk.mbsf.widget.RotateTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 描述：
 * 作者： on 2018/8/18 14:27
 * 邮箱：499629556@qq.com
 */

public class ZitiejiListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private SparseArray<ZitieHomeBean.ListBeanX.ListBeanAndType> titles = new SparseArray<>();
    List<ZitieHomeBean.ListBeanX.ListBeanAndType> list;

    public List<ZitieHomeBean.ListBeanX.ListBeanAndType> getList() {
        return list;
    }

    public void setList(List<ZitieHomeBean.ListBeanX.ListBeanAndType> list) {
        this.list = list;
    }

    Fragment fragment;
    Activity activity;
    public ZitiejiListAdapter(Fragment fragment) {
        this.fragment = fragment;
        this.activity = fragment.getActivity();
        list = new ArrayList<ZitieHomeBean.ListBeanX.ListBeanAndType>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 1){
            View v = LayoutInflater.from(activity).inflate(R.layout.adapter_zitie_home_item_title, parent, false);
            TitleViewHolder titleViewHolder = new TitleViewHolder(v);
            return titleViewHolder;
        }else {
            View v = LayoutInflater.from(activity).inflate(R.layout.adapter_zitieji_home, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(v);
            return myViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int index) {
        Log.e("onBindViewHolder","size:"+list.size()+"===positon:"+index);
        if(isTitle(index)){
            TitleViewHolder titleViewHolder = (TitleViewHolder) holder;
            titleViewHolder.setIndex(index);
            titleViewHolder.tv_name.setText(list.get(index).getTitle());
            return;
        }

        ZitieHomeBean.ListBeanX.ListBeanAndType listBeanAndType = list.get(index);
        String type_cur = listBeanAndType.get_type();
        ZitieHomeBean.ListBeanX.ListBean bean = listBeanAndType.get_data();

        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.setIndex(index);
        myViewHolder.tv_name.getPaint().setFakeBoldText(true);
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
                myViewHolder.tv_author.setVisibility(View.GONE);
                myViewHolder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                break;
            case ZitieHomeBean.type_gallery:
                name = bean.get_title();
                url = bean.get_cover_url();
                myViewHolder.tv_author.setVisibility(View.GONE);
                myViewHolder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                break;
            case ZitieHomeBean.type_zuopin:
                name = bean.get_name();
                url = bean.get_cover_url();
                myViewHolder.tv_author.setText(bean.get_author()+"");
                myViewHolder.tv_author.setVisibility(View.VISIBLE);
                myViewHolder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                break;
            case ZitieHomeBean.type_zitie:
                name = bean.get_name();
                url = bean.get_cover_url();
                if (bean._video == 1) {
                    myViewHolder.tv_name.setTextColor(activity.getResources().getColor(bean._video == 1 ? R.color.orange : R.color.dimGray));
                } else {
                    myViewHolder.tv_name.setTextColor(activity.getResources().getColor((!TextUtils.isEmpty(bean.get_hd()) && "1".equals(bean.get_hd())) ? R.color.colorPrimary : R.color.dimGray));
                }
                myViewHolder.tv_author.setVisibility(TextUtils.isEmpty(bean.get_author()) ? View.GONE : View.VISIBLE);
                myViewHolder.tv_author.setText(bean.get_author() + "");
                break;
        }
        if(bean.get_subtitle()!=null && !bean.get_subtitle().equals("")){
            name = name +"\n"+bean.get_subtitle();
        }
        myViewHolder.tv_name.setText(name);
        if (bean.get_free()==null || bean.get_free().equals("")|| "1".equals(bean.get_free())){
            myViewHolder.rtv.setVisibility(View.GONE);
        }else {
            myViewHolder.rtv.setVisibility(View.VISIBLE);
        }
        if(fragment!=null){
            myViewHolder.iv_thumb_url.setData(fragment,url,-1, ImageView.ScaleType.CENTER_CROP);
        }else {
            myViewHolder.iv_thumb_url.setData(activity,url,-1, ImageView.ScaleType.CENTER_CROP);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getAdapterType();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        //如果是title就占据设置的spanCount个单元格
        final GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
        //Sets the source to get the number of spans occupied by each item in the adapter.
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(isTitle(position)){
                    return layoutManager.getSpanCount();
                }
                return 1;
            }
        });

    }

    private boolean isTitle(int position){
        if(list.get(position).getAdapterType() ==1){
            return true;
        }else {
            return false;
        }
        //return titles.get(position) == null ? false : true;
    }

    /**
     * @param position 插入item的位置，注意这里的下标是包含title的（title算一个item，并且所有item随着插入的title的增多而改变），
     *                 即该position参数可以理解为包含title的所有item中title所处于的插入的位置
     * @param title
     */
    public void addTitle(int position, ZitieHomeBean.ListBeanX.ListBeanAndType title){
        titles.put(position, title);
    }

    /**
     * 计算列数
     */
    static final String TAG  = "ZitiejiListAdapter";
    static final int PHONE   = 108;
    static final int TABLET  = 108;
    final int mWidth = MainApplication.isTablet ? TABLET : PHONE;
    public final int calculate(final Context ctx) {
        int totalWidth = ViewUtil.getScreenWidth(ctx) - ViewUtil.dpToPx(16);
        int itemWidth = ViewUtil.dpToPx(mWidth);
        int spacing = ViewUtil.dpToPx(5);
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

    public class TitleViewHolder extends RecyclerView.ViewHolder{
        @BindView(R2.id.tv_name)
        TextView tv_name;
        @BindView(R2.id.tv_more)
        TextView tv_more;
        private int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public TitleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tv_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZitieHomeBean.ListBeanX.ListBeanAndType bean = list.get(index);
                    zitieHomeCallBack.more(bean.getTitle(),bean.get_type());
                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.iv_thumb_url)
        MyLoadingImageView iv_thumb_url;
        @BindView(R2.id.tv_author)
        TextView tv_author;
        @BindView(R2.id.tv_name)
        TextView tv_name;
        @BindView(R2.id.rtv)
        RotateTextView rtv;

        private int index;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            int width = ViewUtil.dpToPx(mWidth);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,width);
            iv_thumb_url.setLayoutParams(layoutParams);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ZitieHomeBean.ListBeanX.ListBeanAndType bean = list.get(index);
                    switch (bean.get_type()) {
                        case ZitieHomeBean.type_author:
                            zitieHomeCallBack.author(bean.get_data());
                            break;
                        case ZitieHomeBean.type_gallery:
                            zitieHomeCallBack.xuanji(bean.get_data());
                            break;
                        case ZitieHomeBean.type_zuopin:
                            zitieHomeCallBack.zuopin(bean.get_data());
                            break;
                        case ZitieHomeBean.type_zitie:
                            zitieHomeCallBack.zitie(bean.get_data());
                            break;
                    }
                }
            });
        }
    }


    ZitieHomeCallBack zitieHomeCallBack;
    public void setZitieHomeCallBack(ZitieHomeCallBack zitieHomeCallBack) {
        this.zitieHomeCallBack = zitieHomeCallBack;
    }

    public interface ZitieHomeCallBack{
        void zuopin(ZitieHomeBean.ListBeanX.ListBean bean);
        void xuanji(ZitieHomeBean.ListBeanX.ListBean bean);
        void author(ZitieHomeBean.ListBeanX.ListBean bean);
        void zitie(ZitieHomeBean.ListBeanX.ListBean bean);

        void more_soucang();
        void more(String title,String gid);
        void more_auhor();
    }
}
